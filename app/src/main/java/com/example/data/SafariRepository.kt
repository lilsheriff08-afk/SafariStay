package com.example.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay

class SafariRepository(
    private val appDao: AppDao,
    private val sightingDao: SightingDao,
    private val journalDao: JournalDao
) {

    val allBookings: Flow<List<BookingEntity>> = appDao.getAllBookings()
    val allVouchers: Flow<List<VoucherEntity>> = appDao.getAllVouchers()
    val allFavorites: Flow<List<FavoriteEntity>> = appDao.getAllFavorites()
    val allSightings: Flow<List<SightingEntity>> = sightingDao.getAllSightings()
    val allNotifications: Flow<List<NotificationEntity>> = appDao.getAllNotifications()
    val allFeedback: Flow<List<FeedbackEntity>> = appDao.getAllFeedback()
    val allJournalEntries: Flow<List<JournalEntry>> = journalDao.getAllEntries()
    val allChecklistItems: Flow<List<ChecklistItem>> = appDao.getAllChecklistItems()

    suspend fun addJournalEntry(entry: JournalEntry) = withContext(Dispatchers.IO) {
        journalDao.insertEntry(entry)
    }

    suspend fun addChecklistItem(item: ChecklistItem) = withContext(Dispatchers.IO) {
        appDao.insertChecklistItem(item)
    }

    suspend fun updateChecklistItem(item: ChecklistItem) = withContext(Dispatchers.IO) {
        appDao.updateChecklistItem(item)
    }

    suspend fun deleteChecklistItem(item: ChecklistItem) = withContext(Dispatchers.IO) {
        appDao.deleteChecklistItem(item)
    }

    suspend fun updateJournalEntry(entry: JournalEntry) = withContext(Dispatchers.IO) {
        journalDao.updateEntry(entry)
    }

    suspend fun deleteJournalEntry(entry: JournalEntry) = withContext(Dispatchers.IO) {
        journalDao.deleteEntry(entry)
    }

    suspend fun syncJournalEntry(id: Int) = withContext(Dispatchers.IO) {
        // Simulate network call
        delay(800)
        val entries = journalDao.getAllEntries().first()
        val entry = entries.find { it.id == id }
        if (entry != null) {
            journalDao.updateEntry(entry.copy(isSynced = true))
        }
    }

    suspend fun addFeedback(feedback: FeedbackEntity) = withContext(Dispatchers.IO) {
        appDao.insertFeedback(feedback)
    }

    suspend fun syncFeedback(id: Int) = withContext(Dispatchers.IO) {
        // Simulate network call
        delay(1000)
        appDao.markFeedbackAsSynced(id)
    }

    suspend fun markNotificationAsRead(id: Int) = withContext(Dispatchers.IO) {
        appDao.markAsRead(id)
    }

    suspend fun checkAndGenerateNotifications() = withContext(Dispatchers.IO) {
        val now = System.currentTimeMillis()
        val twentyFourHoursFromNow = now + (24 * 60 * 60 * 1000)

        // Check Vouchers
        val vouchers = appDao.getAllVouchers().first()
        vouchers.forEach { voucher ->
            if (voucher.status == "Active" && voucher.expiryTimestamp > now && voucher.expiryTimestamp <= twentyFourHoursFromNow) {
                // Check if notification already exists
                val existing = appDao.getNotificationForRelated("VOUCHER", voucher.id)
                if (existing == null) {
                    appDao.insertNotification(
                        NotificationEntity(
                            title = "Voucher Expiring Soon!",
                            message = "Your '${voucher.title}' voucher with code ${voucher.code} expires in less than 24 hours. To redeem, show this code at any authorized Safari Outpost partner.",
                            type = "VOUCHER",
                            relatedId = voucher.id
                        )
                    )
                }
            }
        }

        // Check Bookings (Experiences)
        val bookings = appDao.getAllBookings().first()
        bookings.forEach { booking ->
            if (booking.status == "Confirmed" && booking.startDateTimestamp > now && booking.startDateTimestamp <= twentyFourHoursFromNow) {
                val existing = appDao.getNotificationForRelated("EXPERIENCE", booking.id)
                if (existing == null) {
                    appDao.insertNotification(
                        NotificationEntity(
                            title = "Upcoming Safari Experience!",
                            message = "Your booked experience '${booking.title}' starts in 24 hours at ${booking.location}. Please ensure you have your digital itinerary ready for check-in.",
                            type = "EXPERIENCE",
                            relatedId = booking.id
                        )
                    )
                }
            }
        }
    }

    suspend fun addSighting(sighting: SightingEntity) = withContext(Dispatchers.IO) {
        sightingDao.insertSighting(sighting)
    }

    suspend fun updateSighting(sighting: SightingEntity) = withContext(Dispatchers.IO) {
        sightingDao.updateSighting(sighting)
    }

    suspend fun getUnsyncedSightings(): List<SightingEntity> = withContext(Dispatchers.IO) {
        sightingDao.getUnsyncedSightings()
    }


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

    // --- WEATHER REPOSITORY LOGIC ---
    suspend fun getCachedWeather(locationName: String): WeatherCacheEntity? = withContext(Dispatchers.IO) {
        appDao.getWeatherForLocation(locationName)
    }

    suspend fun updateWeatherCache(weather: WeatherCacheEntity) = withContext(Dispatchers.IO) {
        appDao.insertWeatherCache(weather)
    }

    /**
     * Simulates fetching weather from an external API and updates cache.
     * In a real app, this would use Retrofit/Ktor to hit a weather API.
     */
    suspend fun fetchLatestWeather(locationName: String): WeatherCacheEntity = withContext(Dispatchers.IO) {
        // Deterministic simulation based on location name to make it feel "real"
        val seed = locationName.hashCode()
        val temp = 22 + (seed % 10)
        val condition = when (seed % 4) {
            0 -> "Sunny"
            1 -> "Partly Cloudy"
            2 -> "Clear Skies"
            else -> "Mild Breeze"
        }
        
        // Serengeti/Mara specific sunrise/sunset ranges (approximate for the region)
        val sunrise = "06:${30 + (seed % 15)} AM"
        val sunset = "06:${45 + (seed % 12)} PM"

        val weather = WeatherCacheEntity(
            locationName = locationName,
            temperature = "${temp}°C",
            condition = condition,
            sunrise = sunrise,
            sunset = sunset
        )
        appDao.insertWeatherCache(weather)
        weather
    }

    suspend fun cancelBooking(booking: BookingEntity) = withContext(Dispatchers.IO) {
        appDao.deleteBooking(booking)
    }

    suspend fun generateVoucher(title: String, description: String, amount: Double): VoucherEntity = withContext(Dispatchers.IO) {
        val randomSuffix = (1000..9999).random()
        val cleanTitle = title.take(5).uppercase().replace(" ", "")
        val code = "VCH-$cleanTitle-$randomSuffix"
        val expiryTs = System.currentTimeMillis() + (30L * 24 * 60 * 60 * 1000) // Default 30 days
        val voucher = VoucherEntity(
            code = code,
            title = title,
            description = description,
            originalValue = amount,
            remainingValue = amount,
            expiryDate = "Next Month",
            expiryTimestamp = expiryTs,
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
            val now = System.currentTimeMillis()
            val defaults = listOf(
                VoucherEntity(
                    code = "SERENGETI-VIP-500",
                    title = "Serengeti VIP Voucher",
                    description = "Pre-loaded voucher valid for premium game drives & luxury camps.",
                    originalValue = 500.0,
                    remainingValue = 500.0,
                    expiryDate = "Dec 15, 2026",
                    expiryTimestamp = now + (150L * 24 * 60 * 60 * 1000),
                    status = "Active"
                ),
                VoucherEntity(
                    code = "ZANZIBAR-STAY-250",
                    title = "Zanzibar Escape Voucher",
                    description = "Redeemable towards beachfront boutique stays and spice tours.",
                    originalValue = 250.0,
                    remainingValue = 250.0,
                    expiryDate = "Oct 30, 2026",
                    expiryTimestamp = now + (100L * 24 * 60 * 60 * 1000),
                    status = "Active"
                ),
                VoucherEntity(
                    code = "MARA-BALLOON-150",
                    title = "Maasai Mara Balloon Voucher",
                    description = "Use this voucher to book a magnificent hot air balloon flight at sunrise.",
                    originalValue = 150.0,
                    remainingValue = 150.0,
                    expiryDate = "Sep 20, 2026",
                    expiryTimestamp = now + (60L * 24 * 60 * 60 * 1000),
                    status = "Active"
                ),
                // EXPIRING SOON VOUCHER (FOR TESTING NOTIFICATIONS)
                VoucherEntity(
                    code = "LAST-CHANCE-100",
                    title = "Last Chance Bush Trek",
                    description = "A limited time voucher for a guided bush trek through the savanna.",
                    originalValue = 100.0,
                    remainingValue = 100.0,
                    expiryDate = "Expiring Tomorrow",
                    expiryTimestamp = now + (18L * 60 * 60 * 1000), // 18 hours from now
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
            val now = System.currentTimeMillis()
            val sampleBooking = BookingEntity(
                type = "STAY",
                title = "Savanna Horizon Eco-Lodge",
                location = "Maasai Mara, Kenya",
                dateRange = "Aug 12 - Aug 15, 2026",
                startDateTimestamp = now + (20L * 60 * 60 * 1000), // 20 hours from now (EXPIRING SOON/UPCOMING)
                price = 350.0,
                imageResName = "img_luxury_lodge",
                status = "Confirmed",
                voucherCodeUsed = "MARA-BALLOON-150"
            )
            appDao.insertBooking(sampleBooking)

            // Add Le Petit Village as an upcoming stop
            val ugandaStop = BookingEntity(
                type = "STAY",
                title = "Le Petit Village Hotel & Spa",
                location = "Kampala, Uganda",
                dateRange = "Sep 05 - Sep 07, 2026",
                startDateTimestamp = now + (45L * 24 * 60 * 60 * 1000), // 45 days away
                price = 195.0,
                imageResName = "img_luxury_lodge",
                status = "Confirmed"
            )
            appDao.insertBooking(ugandaStop)
        }

        // Prepopulate wildlife sightings
        val currentSightings = sightingDao.getAllSightings().first()
        if (currentSightings.isEmpty()) {
            val defaults = listOf(
                SightingEntity(
                    speciesName = "African Lion",
                    note = "A magnificent pride of lions resting under an acacia tree. Spotted near the Mara River crossing.",
                    locationTag = "Maasai Mara National Reserve, Kenya",
                    timestamp = System.currentTimeMillis() - 86400000 * 2, // 2 days ago
                    photoPlaceholder = "lion",
                    isSynced = true
                ),
                SightingEntity(
                    speciesName = "Leopard",
                    note = "Spotted this beautiful leopard sleeping on a high sausage tree branch in central Serengeti.",
                    locationTag = "Central Serengeti, Tanzania",
                    timestamp = System.currentTimeMillis() - 86400000, // 1 day ago
                    photoPlaceholder = "leopard",
                    isSynced = true
                ),
                SightingEntity(
                    speciesName = "African Elephant",
                    note = "A large herd of around 30 elephants crossing the road right in front of our Land Cruiser. Incredible experience!",
                    locationTag = "Ngorongoro Crater Rim, Tanzania",
                    timestamp = System.currentTimeMillis() - 3600000 * 4, // 4 hours ago
                    photoPlaceholder = "elephant",
                    isSynced = false
                )
            )
            for (s in defaults) {
                sightingDao.insertSighting(s)
            }
        }

        // Prepopulate checklist if empty
        if (appDao.getChecklistCount() == 0) {
            val defaults = listOf(
                ChecklistItem(title = "Passport (Valid for 6 months)", category = "DOCUMENT"),
                ChecklistItem(title = "Yellow Fever Certificate", category = "MEDICAL"),
                ChecklistItem(title = "E-Visa (Kenya/Tanzania)", category = "DOCUMENT"),
                ChecklistItem(title = "Travel Insurance Documents", category = "DOCUMENT"),
                ChecklistItem(title = "Malaria Prophylaxis", category = "MEDICAL"),
                ChecklistItem(title = "Anti-Diarrheal Medication", category = "MEDICAL"),
                ChecklistItem(title = "Sunscreen & Insect Repellent", category = "TRAVEL"),
                ChecklistItem(title = "Comfortable Safari Boots", category = "TRAVEL"),
                ChecklistItem(title = "Light Neutral Clothing", category = "TRAVEL")
            )
            for (item in defaults) {
                appDao.insertChecklistItem(item)
            }
        }
    }
}
