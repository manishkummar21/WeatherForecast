package com.weather.forecast.service

import android.Manifest
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ServiceInfo.FOREGROUND_SERVICE_TYPE_LOCATION
import android.location.Location
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.location.*
import com.weather.forecast.PreferenceManager
import com.weather.forecast.R
import com.weather.forecast.Utils.getName
import com.weather.forecast.data.location.repository.LocationRepo
import com.weather.forecast.data.weather.model.WeatherLocationData
import com.weather.forecast.data.weather.repository.WeatherRepository
import com.weather.forecast.helper.ResultsWrapper
import com.weather.forecast.ui.dashboard.activities.DashBoardActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.zip
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

@AndroidEntryPoint
class LocationService : LifecycleService() {

    private val TAG = "LocationService"

    @Inject
    lateinit var weatherRepository: WeatherRepository

    @Inject
    lateinit var locationRepo: LocationRepo

    @Inject
    lateinit var preferenceManager: PreferenceManager

    private val notificationManager by lazy {
        (getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager)
    }

    private val fusedLocationClient by lazy {
        LocationServices.getFusedLocationProviderClient(this)
    }

    private val locationRequest by lazy {
        LocationRequest.create().apply {
            this.priority = Priority.PRIORITY_HIGH_ACCURACY
            this.interval = 1 * 1000
        }
    }

    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            if (locationResult.locations.size > 0) {
                stopLocationUpdates()
                println("fetchWeatherDetails LocationCallback")
                fetchWeatherDetails(locationResult.locations[0])
            }
        }
    }

    override fun onCreate() {
        super.onCreate()
        startForeground(this, getString(R.string.notification_channel_id))
        startLocationUpdates()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)
        return START_STICKY
    }


    private fun startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            fusedLocationClient.requestLocationUpdates(
                locationRequest,
                locationCallback,
                null
            )
        }
    }

    private fun stopLocationUpdates() {
        fusedLocationClient.removeLocationUpdates(locationCallback)
    }

    private fun fetchWeatherDetails(location: Location) {
        println("fetchWeatherDetails called ${location.latitude}}")
        lifecycleScope.launch {
            weatherRepository.getWeatherForeCastList(
                location.latitude,
                location.longitude,
                preferenceManager.temperatureUnit
            ).zip(
                locationRepo.getLocationDetails(
                    location.latitude,
                    location.longitude
                )
            ) { weatherResponse, locationResponse ->
                return@zip WeatherLocationData(
                    getSuccessResponse(weatherResponse),
                    getSuccessResponse(locationResponse)
                )
            }
                .flowOn(Dispatchers.IO)
                .catch {
                    println("fetchWeatherDetails error")
                    stop()
                }.collect {
                    println("fetchWeatherDetails success")
                    showTempNotification(it)
                    stop()
                }
        }
    }

    private fun showTempNotification(it: WeatherLocationData) {
        if (it.weatherForeCastList != null && it.locationDetails != null)
            notificationManager.sendReminderNotification(
                this,
                getString(R.string.notification_channel_id),
                String.format(
                    getString(R.string.temp_notification_title),
                    it.weatherForeCastList.current.temp.toString(),
                    it.locationDetails.getName()
                ),
                it.weatherForeCastList.current.weather.last().description.replaceFirstChar {
                    if (it.isLowerCase()) it.titlecase(
                        Locale.getDefault()
                    ) else it.toString()
                }
            )
    }


    private fun startForeground(
        context: Context,
        channelId: String,
    ) {
        val builder = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle("Fetching the Current Location")
            .setAutoCancel(true)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            startForeground(
                Foreground_NOTIFICATION_ID,
                builder.build(),
                FOREGROUND_SERVICE_TYPE_LOCATION
            )
        } else
            startForeground(Foreground_NOTIFICATION_ID, builder.build())
    }

    private fun stop() {
        stopForeground(true)
        stopSelf()
    }

    private fun <T> getSuccessResponse(
        response: ResultsWrapper<T>,
    ): T? {
        return when (response) {
            is ResultsWrapper.Success -> return response.response
            else -> {
                null
            }
        }
    }


    companion object {
        const val Weather_NOTIFICATION_ID = 1
        const val Foreground_NOTIFICATION_ID = 2
    }

}


fun NotificationManager.sendReminderNotification(
    applicationContext: Context,
    channelId: String, title: String, subtitle: String
) {
    val contentIntent = Intent(applicationContext, DashBoardActivity::class.java)

    val pendingIntent = PendingIntent.getActivity(
        applicationContext,
        1,
        contentIntent,
        PendingIntent.FLAG_UPDATE_CURRENT
    )

    val builder = NotificationCompat.Builder(applicationContext, channelId)
        .setSmallIcon(R.mipmap.ic_launcher)
        .setContentTitle(title)
        .setContentText(subtitle)
        .setContentIntent(pendingIntent)
        .setAutoCancel(true)

    notify(LocationService.Weather_NOTIFICATION_ID, builder.build())
}