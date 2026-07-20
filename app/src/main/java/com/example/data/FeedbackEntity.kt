package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "feedback")
data class FeedbackEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val bookingId: Int,
    val bookingTitle: String,
    val rating: Int, // 1-5 stars
    val comment: String,
    val timestamp: Long = System.currentTimeMillis(),
    val isSynced: Boolean = false
)
