package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "wildlife_sightings")
data class SightingEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val speciesName: String,
    val note: String,
    val locationTag: String,
    val timestamp: Long,
    val photoPlaceholder: String, // name of the animal photo placeholder/asset
    val isSynced: Boolean = false
)
