package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "journal_entries")
data class JournalEntry(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val note: String,
    val timestamp: Long,
    val location: String,
    val isSynced: Boolean = false,
    val imageBase64: String? = null
)
