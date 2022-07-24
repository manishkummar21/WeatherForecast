package com.weather.forecast

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import java.util.*

object RemindersManager {


    private const val REMINDER_NOTIFICATION_REQUEST_CODE = 123

    fun startReminder(
        context: Context,
        reminderId: Int = REMINDER_NOTIFICATION_REQUEST_CODE
    ) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        val intent =
            Intent(context.applicationContext, AlarmReceiver::class.java).let { intent ->
                PendingIntent.getBroadcast(
                    context.applicationContext,
                    reminderId,
                    intent,
                    PendingIntent.FLAG_UPDATE_CURRENT
                )
            }

        val calendar: Calendar = Calendar.getInstance(Locale.ENGLISH).apply {
            set(Calendar.HOUR_OF_DAY, 6)
            set(Calendar.MINUTE, 0)
        }

        if (Calendar.getInstance(Locale.ENGLISH)
                .apply { add(Calendar.MINUTE, 1) }.timeInMillis - calendar.timeInMillis > 0
        ) {
            calendar.add(Calendar.DATE, 1)
        }

        alarmManager.setAlarmClock(
            AlarmManager.AlarmClockInfo(calendar.timeInMillis, intent),
            intent
        )
    }
}