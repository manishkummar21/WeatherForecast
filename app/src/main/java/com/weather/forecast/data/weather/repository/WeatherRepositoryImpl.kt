package com.weather.forecast.data.weather.repository

import androidx.lifecycle.LiveData
import com.hokart.android.database.WeatherDatabase
import com.weather.forecast.BuildConfig
import com.weather.forecast.data.weather.model.WeatherForeCastList
import com.weather.forecast.data.weather.remote.WeatherRemoteDataSource
import com.weather.forecast.entities.FavoriteCity
import com.weather.forecast.helper.ResultsWrapper
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class WeatherRepositoryImpl(
    private val db: WeatherDatabase,
    private val weatherRemoteDataSource: WeatherRemoteDataSource
) :
    WeatherRepository {

    override suspend fun getWeatherForeCastList(
        lat: Double,
        lng: Double,
        temperatureUnit: String
    ): Flow<ResultsWrapper<WeatherForeCastList>> = flow {
        emit(
            weatherRemoteDataSource.getWeatherForeCastList(
                BuildConfig.openWeatherApiKey,
                lat,
                lng,temperatureUnit
            )
        )
    }

    override fun getFavoriteCityID(): LiveData<List<String>> {
        return db.favCitiesDao().getAllFavID()
    }

    override suspend fun getAllFavoriteCity(): Flow<List<FavoriteCity>> = flow {
        emit(db.favCitiesDao().getAllFavItems())
    }

    override suspend fun insertOrDeleteFavCity(city: FavoriteCity): Flow<Unit> =
        flow {
            emit(db.favCitiesDao().insertOrDelete(city))
        }


}