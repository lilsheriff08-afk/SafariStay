package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "weather_cache")
data class WeatherCacheEntity(
    @PrimaryKey val locationName: String,
    val temperature: String,
    val condition: String,
    val sunrise: String,
    val sunset: String,
    val lastUpdated: Long = System.currentTimeMillis()
)
