package com.weather.forecast.data.weather.model

data class WeatherForeCastList(
    val lat:Double,
    val lon:Double,
    val current:WeatherData,
    val hourly:List<WeatherData>,
    val daily:List<DailyWeatherData>
)
