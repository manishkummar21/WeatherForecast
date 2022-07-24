package com.weather.forecast.data.base

import com.weather.forecast.Constants
import com.weather.forecast.data.weather.model.WeatherData

data class HourlyForeCastInfo(val data: List<WeatherData>) :
    Components(Constants.HourlyForeCastInfo)


