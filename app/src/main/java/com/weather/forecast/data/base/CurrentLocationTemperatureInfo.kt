package com.weather.forecast.data.base

import com.weather.forecast.Constants
import com.weather.forecast.data.location.model.LocationDetails
import com.weather.forecast.data.weather.model.WeatherData

data class CurrentLocationTemperatureInfo(
    val data: WeatherData,
    val locationDetails: LocationDetails,
    val unit: String
) :
    Components(Constants.CurrentTemperatureInfo)


