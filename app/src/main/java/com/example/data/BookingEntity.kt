package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "bookings")
data class BookingEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val bookingReference: String = "BK-" + System.currentTimeMillis().toString().takeLast(6),
    val title: String = "",
    val lodgeName: String = title,
    val checkInDate: String = "",
    val checkOutDate: String = "",
    val guestName: String = "Valued Guest",
    val type: String = "STAY", // "STAY" or "SAFARI"
    val location: String = "",
    val roomType: String = "Deluxe Luxury Safari Suite",
    val dateRange: String = "",
    val startDateTimestamp: Long = System.currentTimeMillis(), // Added for notification logic
    val price: Double = 0.0,
    val imageResName: String = "img_luxury_lodge",
    val status: String = "Confirmed", // "Confirmed", "Pending", "Held (Escrow)"
    val voucherCodeUsed: String? = null,
    val userId: String = "current_user",
    val timestamp: Long = System.currentTimeMillis()
)

