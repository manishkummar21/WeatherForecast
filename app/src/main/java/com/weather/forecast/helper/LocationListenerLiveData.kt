package com.weather.forecast.helper

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import androidx.core.app.ActivityCompat
import androidx.lifecycle.LiveData
import com.google.android.gms.location.*
import com.weather.forecast.data.LocationModel
import javax.inject.Inject

class LocationListenerLiveData @Inject constructor(
    private val context: Context,
) : LiveData<Event<LocationModel>>() {

    private var fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)

    private val locationRequest by lazy {
        LocationRequest.create().apply {
            this.priority = Priority.PRIORITY_HIGH_ACCURACY
            this.interval = 1 * 1000
        }
    }

    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            if (locationResult.locations.size > 0) {
                setLocationData(locationResult.locations[0])
                stopLocationUpdates()
            }
        }
    }

    private fun setLocationData(location: Location) {
        value = Event(LocationModel(
            longitude = location.longitude,
            latitude = location.latitude
        ))
    }

    private fun startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }

        fusedLocationClient.requestLocationUpdates(
            locationRequest,
            locationCallback,
            null
        )
    }

    override fun onActive() {
        super.onActive()
        startLocationUpdates()
    }

    override fun onInactive() {
        stopLocationUpdates()
    }

    private fun stopLocationUpdates() {
        fusedLocationClient.removeLocationUpdates(locationCallback)
    }

    fun startService() {
        onActive()
    }
}