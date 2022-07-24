package com.weather.forecast.data.weather.model

import com.weather.forecast.data.location.model.LocationDetails
import com.weather.forecast.helper.ResultsWrapper

data class WeatherLocationData(
    val weatherForeCastList: WeatherForeCastList?,
    val locationDetails: LocationDetails?
)
