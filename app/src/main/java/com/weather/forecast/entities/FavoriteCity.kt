package com.weather.forecast.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class FavoriteCity(
    @PrimaryKey val placeID: String,
    val title: String,
    val subtitle: String,
)
