package com.hokart.android.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.weather.forecast.dao.FavoriteCitiesDao
import com.weather.forecast.entities.FavoriteCity


@Database(
    entities = [FavoriteCity::class],
    version = 1,
    exportSchema = false
)

abstract class WeatherDatabase : RoomDatabase() {
    abstract fun favCitiesDao(): FavoriteCitiesDao
}