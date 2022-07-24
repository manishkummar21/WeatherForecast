package com.weather.forecast.data.weather.repository

import androidx.lifecycle.LiveData
import com.weather.forecast.data.weather.model.WeatherForeCastList
import com.weather.forecast.entities.FavoriteCity
import com.weather.forecast.helper.ResultsWrapper
import kotlinx.coroutines.flow.Flow

interface WeatherRepository {

    suspend fun getWeatherForeCastList(
        lat: Double,
        lng: Double,
        temperatureUnit: String
    ): Flow<ResultsWrapper<WeatherForeCastList>>

    suspend fun insertOrDeleteFavCity(city: FavoriteCity): Flow<Unit>

    fun getFavoriteCityID(): LiveData<List<String>>

    suspend fun getAllFavoriteCity(): Flow<List<FavoriteCity>>

}