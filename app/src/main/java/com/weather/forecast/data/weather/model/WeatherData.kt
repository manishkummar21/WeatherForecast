package com.weather.forecast.data.weather.model

data class WeatherData(
     val dt: Number,
     val temp: Number,
     val pressure: Number,
     val humidity: Number,
     val clouds: Number,
     val wind_speed: Number,
     val wind_deg: Number,
     val weather: List<WeatherDescription>
)

data class WeatherDescription(
     val id: Number,
     val main: String,
     val description: String,
     val icon: String
)

data class DailyWeatherData(
     val dt: Number,
     val temp: TemperatureDetails,
     val pressure: Number,
     val humidity: Number,
     val clouds: Number,
     val wind_speed: Number,
     val wind_deg: Number,
     val weather: List<WeatherDescription>
)

data class TemperatureDetails(
     val day: Number,
     val min: Number,
     val max: Number,
     val night: Number,
     val eve: Number,
     val morn: Number
)