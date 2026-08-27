package com.example.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface SyncDao {
    @Query("SELECT * FROM sync_queue WHERE status = 'PENDING_SYNC' ORDER BY timestamp ASC")
    fun getPendingActions(): Flow<List<SyncAction>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAction(action: SyncAction)

    @Query("UPDATE sync_queue SET status = :status WHERE id = :id")
    suspend fun updateStatus(id: Int, status: String)

    @Query("DELETE FROM sync_queue WHERE id = :id")
    suspend fun deleteAction(id: Int)

    @Query("SELECT COUNT(*) FROM sync_queue WHERE status = 'PENDING_SYNC'")
    fun getPendingCount(): Flow<Int>
}
