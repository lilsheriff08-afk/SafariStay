package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "local_bookings")
data class BookingEntity(
    @PrimaryKey val bookingReference: String,
    val lodgeName: String,
    val checkInDate: String,
    val checkOutDate: String,
    val guestName: String
)
