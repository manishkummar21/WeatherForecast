package com.weather.forecast.data.weather.remote

import com.weather.forecast.data.weather.model.WeatherForeCastList
import com.weather.forecast.helper.BaseRemoteDataSource
import com.weather.forecast.helper.ResultsWrapper
import javax.inject.Inject

class WeatherRemoteDataSourceImpl @Inject constructor(private val weatherService: WeatherService) :
    WeatherRemoteDataSource, BaseRemoteDataSource() {


    override suspend fun getWeatherForeCastList(
        appID: String,
        lat: Double,
        lng: Double,
        temperatureUnit: String
    ): ResultsWrapper<WeatherForeCastList> {
        return getResponse(
            request = { weatherService.getWeatherForeCastList(appID, lat,lng,temperatureUnit) })
    }

}