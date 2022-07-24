package com.weather.forecast.data.weather.remote

import com.weather.forecast.data.weather.model.WeatherForeCastList
import com.weather.forecast.helper.ResultsWrapper


interface WeatherRemoteDataSource {

    suspend fun getWeatherForeCastList(
        appID: String,
        lat: Double,
        lng: Double,
        temperatureUnit: String
    ): ResultsWrapper<WeatherForeCastList>
}
