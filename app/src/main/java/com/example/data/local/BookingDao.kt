package com.example.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface BookingDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBooking(booking: BookingEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllBookings(bookings: List<BookingEntity>)

    @Query("SELECT * FROM local_bookings ORDER BY checkInDate ASC")
    fun getAllBookings(): Flow<List<BookingEntity>>

    @Query("SELECT * FROM local_bookings WHERE bookingReference = :bookingReference LIMIT 1")
    suspend fun getBookingByReference(bookingReference: String): BookingEntity?

    @Delete
    suspend fun deleteBooking(booking: BookingEntity)

    @Query("DELETE FROM local_bookings WHERE bookingReference = :bookingReference")
    suspend fun deleteBookingByReference(bookingReference: String)

    @Query("DELETE FROM local_bookings")
    suspend fun clearAllBookings()
}
