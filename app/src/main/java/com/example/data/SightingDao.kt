package com.example.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface SightingDao {
    @Query("SELECT * FROM wildlife_sightings ORDER BY timestamp DESC")
    fun getAllSightings(): Flow<List<SightingEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSighting(sighting: SightingEntity)

    @Update
    suspend fun updateSighting(sighting: SightingEntity)

    @Query("SELECT * FROM wildlife_sightings WHERE isSynced = 0")
    suspend fun getUnsyncedSightings(): List<SightingEntity>
}
