package com.weather.forecast.data.weather.remote

import com.weather.forecast.data.weather.model.WeatherForeCastList
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherService {

    @GET("onecall")
    suspend fun getWeatherForeCastList(
        @Query("appid") appid: String,
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("units") units: String
    ): Response<WeatherForeCastList>
}