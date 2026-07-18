package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "bookings")
data class BookingEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val type: String, // "STAY" or "SAFARI"
    val title: String,
    val location: String,
    val dateRange: String,
    val price: Double,
    val imageResName: String,
    val status: String, // "Confirmed", "Pending"
    val voucherCodeUsed: String? = null,
    val timestamp: Long = System.currentTimeMillis()
)
