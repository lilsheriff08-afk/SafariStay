package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favorites")
data class FavoriteEntity(
    @PrimaryKey
    val itemId: String, // e.g. stay_mara_lodge, safari_great_migration
    val type: String,   // "STAY" or "SAFARI"
    val title: String,
    val timestamp: Long = System.currentTimeMillis()
)
