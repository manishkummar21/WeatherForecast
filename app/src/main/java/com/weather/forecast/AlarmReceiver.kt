package com.weather.forecast

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import com.weather.forecast.service.LocationService


class AlarmReceiver : BroadcastReceiver() {

    /**
     * sends notification when receives alarm
     * and then reschedule the reminder again
     * */
    override fun onReceive(context: Context, intent: Intent) {

        println("AlarmReceiver onReceive Called")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(Intent(context, LocationService::class.java))
        } else {
            context.startService(Intent(context, LocationService::class.java))
        }
        RemindersManager.startReminder(context.applicationContext)
    }
}