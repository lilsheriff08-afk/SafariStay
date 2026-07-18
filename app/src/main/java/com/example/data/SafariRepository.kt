package com.example.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import kotlinx.coroutines.Dispatchers

class SafariRepository(private val appDao: AppDao) {

    val allBookings: Flow<List<BookingEntity>> = appDao.getAllBookings()
    val allVouchers: Flow<List<VoucherEntity>> = appDao.getAllVouchers()
    val allFavorites: Flow<List<FavoriteEntity>> = appDao.getAllFavorites()

    suspend fun toggleFavorite(itemId: String, type: String, title: String) = withContext(Dispatchers.IO) {
        val favoritesList = appDao.getAllFavorites().first()
        val exists = favoritesList.any { it.itemId == itemId }
        if (exists) {
            appDao.deleteFavoriteById(itemId)
        } else {
            appDao.insertFavorite(FavoriteEntity(itemId = itemId, type = type, title = title))
        }
    }

    suspend fun getVoucherByCode(code: String): VoucherEntity? = withContext(Dispatchers.IO) {
        appDao.getVoucherByCode(code.trim().uppercase())
    }

    suspend fun createBooking(booking: BookingEntity) = withContext(Dispatchers.IO) {
        appDao.insertBooking(booking)
    }

    suspend fun cancelBooking(booking: BookingEntity) = withContext(Dispatchers.IO) {
        appDao.deleteBooking(booking)
    }

    suspend fun generateVoucher(title: String, description: String, amount: Double): VoucherEntity = withContext(Dispatchers.IO) {
        val randomSuffix = (1000..9999).random()
        val cleanTitle = title.take(5).uppercase().replace(" ", "")
        val code = "VCH-$cleanTitle-$randomSuffix"
        val voucher = VoucherEntity(
            code = code,
            title = title,
            description = description,
            originalValue = amount,
            remainingValue = amount,
            expiryDate = "Dec 31, 2026",
            status = "Active"
        )
        appDao.insertVoucher(voucher)
        voucher
    }

    suspend fun redeemVoucher(code: String, amountToDeduct: Double): Boolean = withContext(Dispatchers.IO) {
        val cleanCode = code.trim().uppercase()
        val voucher = appDao.getVoucherByCode(cleanCode) ?: return@withContext false
        if (voucher.status != "Active" || voucher.remainingValue < amountToDeduct) {
            return@withContext false
        }

        val updatedRemaining = voucher.remainingValue - amountToDeduct
        val updatedStatus = if (updatedRemaining <= 0.0) "Redeemed" else "Active"
        val updatedVoucher = voucher.copy(
            remainingValue = updatedRemaining,
            status = updatedStatus
        )
        appDao.updateVoucher(updatedVoucher)
        true
    }

    suspend fun prepopulateIfEmpty() = withContext(Dispatchers.IO) {
        // Check if vouchers table is empty
        val currentVouchers = appDao.getAllVouchers().first()
        if (currentVouchers.isEmpty()) {
            val defaults = listOf(
                VoucherEntity(
                    code = "SERENGETI-VIP-500",
                    title = "Serengeti VIP Voucher",
                    description = "Pre-loaded voucher valid for premium game drives & luxury camps.",
                    originalValue = 500.0,
                    remainingValue = 500.0,
                    expiryDate = "Dec 15, 2026",
                    status = "Active"
                ),
                VoucherEntity(
                    code = "ZANZIBAR-STAY-250",
                    title = "Zanzibar Escape Voucher",
                    description = "Redeemable towards beachfront boutique stays and spice tours.",
                    originalValue = 250.0,
                    remainingValue = 250.0,
                    expiryDate = "Oct 30, 2026",
                    status = "Active"
                ),
                VoucherEntity(
                    code = "MARA-BALLOON-150",
                    title = "Maasai Mara Balloon Voucher",
                    description = "Use this voucher to book a magnificent hot air balloon flight at sunrise.",
                    originalValue = 150.0,
                    remainingValue = 150.0,
                    expiryDate = "Sep 20, 2026",
                    status = "Active"
                )
            )
            for (v in defaults) {
                appDao.insertVoucher(v)
            }
        }

        // Prepopulate a sample past booking to showcase existing list
        val currentBookings = appDao.getAllBookings().first()
        if (currentBookings.isEmpty()) {
            val sampleBooking = BookingEntity(
                type = "STAY",
                title = "Savanna Horizon Eco-Lodge",
                location = "Maasai Mara, Kenya",
                dateRange = "Aug 12 - Aug 15, 2026",
                price = 350.0,
                imageResName = "img_luxury_lodge",
                status = "Confirmed",
                voucherCodeUsed = "MARA-BALLOON-150"
            )
            appDao.insertBooking(sampleBooking)
        }
    }
}
