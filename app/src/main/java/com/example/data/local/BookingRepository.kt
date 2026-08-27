package com.example.data.local

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class BookingRepository(
    private val bookingDao: BookingDao,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) {
    val allBookings: Flow<List<BookingEntity>> = bookingDao.getAllBookings()

    suspend fun insertBooking(booking: BookingEntity) = withContext(ioDispatcher) {
        bookingDao.insertBooking(booking)
    }

    suspend fun insertAllBookings(bookings: List<BookingEntity>) = withContext(ioDispatcher) {
        bookingDao.insertAllBookings(bookings)
    }

    suspend fun getBookingByReference(bookingReference: String): BookingEntity? = withContext(ioDispatcher) {
        bookingDao.getBookingByReference(bookingReference)
    }

    suspend fun deleteBooking(booking: BookingEntity) = withContext(ioDispatcher) {
        bookingDao.deleteBooking(booking)
    }

    suspend fun deleteBookingByReference(bookingReference: String) = withContext(ioDispatcher) {
        bookingDao.deleteBookingByReference(bookingReference)
    }

    suspend fun clearAllBookings() = withContext(ioDispatcher) {
        bookingDao.clearAllBookings()
    }
}
