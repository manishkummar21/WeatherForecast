package com.weather.forecast.dao

import com.weather.forecast.entities.FavoriteCity

import android.database.sqlite.SQLiteConstraintException

import androidx.lifecycle.LiveData
import androidx.room.*
import kotlinx.coroutines.flow.Flow


@Dao
interface FavoriteCitiesDao {

    @Insert
    fun insertAll(vararg cities: FavoriteCity)

    @Insert(onConflict = OnConflictStrategy.ABORT)
    fun insert(city: FavoriteCity): Long

    @Query("SELECT * FROM favoritecity")
    suspend fun getAllFavItems(): List<FavoriteCity>

    @Query("SELECT placeID FROM favoritecity")
    fun getAllFavID(): LiveData<List<String>>

    @Delete
    fun deleteFavCityByID(city: FavoriteCity)

    @Query("SELECT EXISTS(SELECT * FROM favoritecity WHERE placeID = :id)")
    fun isRowIsExist(id : String) : Boolean

    @Transaction
    suspend fun insertOrDelete(city: FavoriteCity) {
        if(isRowIsExist(city.placeID))
            deleteFavCityByID(city)
        else insert(city)
    }

}