package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "sync_queue")
data class SyncAction(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val actionType: String, // e.g., "BOOK_STAY", "BOOK_SAFARI"
    val payload: String,    // JSON payload
    val status: String = "PENDING_SYNC",
    val timestamp: Long = System.currentTimeMillis()
)
