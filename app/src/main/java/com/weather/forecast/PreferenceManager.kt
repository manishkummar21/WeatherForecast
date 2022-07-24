package com.weather.forecast

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.weather.forecast.data.LocationModel

class PreferenceManager(context: Context) {

    private var preferences: SharedPreferences = context.getSharedPreferences(
        BuildConfig.PreferenceName,
        Context.MODE_PRIVATE
    )

    private var gson: Gson = Gson()


    private val _lastKnowCurrentLocation = Pair("lastKnowCurrentLocation", null)

    private val _lastFetchedLocation = Pair("lastFetchedLocation", null)

    private val _temperatureUnit = Pair("settings_temperature_unit", "metric")

    private inline fun SharedPreferences.edit(operation: (SharedPreferences.Editor) -> Unit) {
        val editor = edit()
        operation(editor)
        editor.apply()
    }


    var lastKnowCurrentLocation: LocationModel?
        get() = gson.fromJson(
            preferences.getString(
                _lastKnowCurrentLocation.first, _lastKnowCurrentLocation.second
            ), LocationModel::class.java
        )
        set(value) = preferences.edit {
            it.putString(
                _lastKnowCurrentLocation.first, gson.toJson(value)
            )
        }

    var lastFetchedLocation: LocationModel?
        get() = gson.fromJson(
            preferences.getString(
                _lastFetchedLocation.first, _lastFetchedLocation.second
            ), LocationModel::class.java
        )
        set(value) = preferences.edit {
            it.putString(
                _lastFetchedLocation.first, gson.toJson(value)
            )
        }

    var temperatureUnit: String
        get() = preferences.getString(
            _temperatureUnit.first, _temperatureUnit.second
        )!!
        set(value) = preferences.edit {
            it.putString(_temperatureUnit.first, value)
        }
}