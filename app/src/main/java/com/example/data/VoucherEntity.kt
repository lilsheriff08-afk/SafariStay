package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "vouchers")
data class VoucherEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val code: String,
    val title: String,
    val description: String,
    val originalValue: Double,
    val remainingValue: Double,
    val expiryDate: String,
    val expiryTimestamp: Long, // Added for notification logic
    val status: String // "Active", "Redeemed", "Expired"
)
