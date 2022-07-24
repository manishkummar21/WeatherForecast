package com.weather.forecast.data.base

import com.weather.forecast.Constants
import com.weather.forecast.data.weather.model.DailyWeatherData
import com.weather.forecast.data.weather.model.WeatherData

data class DailyForeCastInfo(val data: List<DailyWeatherData>) :
    Components(Constants.DailyForeCastInfo)


