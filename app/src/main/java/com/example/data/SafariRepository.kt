package com.example.data

import android.content.Context
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay

import com.example.BuildConfig
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory

class SafariRepository(
    val context: Context,
    private val appDao: AppDao,
    private val sightingDao: SightingDao,
    private val journalDao: JournalDao,
    private val syncDao: SyncDao
) {

    val paymentRepository: PaymentRepository = PaymentRepository()

    private val moshi = Moshi.Builder().addLast(KotlinJsonAdapterFactory()).build()

    private val placesApi = Retrofit.Builder()
        .baseUrl("https://places.googleapis.com/")
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .build()
        .create(PlacesApiService::class.java)

    private val googleApiKey = BuildConfig.GOOGLE_PLACES_API_KEY

    // --- API INITIALIZATION ---
    
    // 1. M-Pesa Daraja API (Safaricom)
    private val mpesaApi = Retrofit.Builder()
        .baseUrl("https://sandbox.safaricom.co.ke/") // Switch to production URL if needed
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .build()
        .create(MpesaApiService::class.java)

    // 2. Africa's Talking SMS API
    private val africaTalkingApi = Retrofit.Builder()
        .baseUrl("https://api.africastalking.com/")
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .build()
        .create(AfricaTalkingApiService::class.java)

    // 3. Safari Stay Sync API (Offline-to-Online Batch Sync)
    private val syncApi = Retrofit.Builder()
        .baseUrl("https://api.safaristay.app/v1/")
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .build()
        .create(SafariStaySyncApiService::class.java)

    // 4. Gemini API for Custom Safari Itineraries
    private val okHttpClient = okhttp3.OkHttpClient.Builder()
        .connectTimeout(60, java.util.concurrent.TimeUnit.SECONDS)
        .readTimeout(60, java.util.concurrent.TimeUnit.SECONDS)
        .writeTimeout(60, java.util.concurrent.TimeUnit.SECONDS)
        .build()

    private val geminiApi = Retrofit.Builder()
        .baseUrl("https://generativelanguage.googleapis.com/")
        .client(okHttpClient)
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .build()
        .create(GeminiApiService::class.java)

    suspend fun generateCustomItinerary(
        lodgeLocation: String,
        interests: List<String>,
        pace: String = "Balanced & Immersive",
        country: String = "Kenya",
        durationDays: Int = 3,
        travelParty: String = "Couples / Explorers",
        budget: String = "Mid-Range Safari Lodges ($$)"
    ): String = withContext(Dispatchers.IO) {
        val apiKey = BuildConfig.GEMINI_API_KEY
        val interestsFormatted = if (interests.isNotEmpty()) interests.joinToString(", ") else "General Big Five Wildlife, Game Drives & Nature"
        val promptText = """
            You are a master African Safari Expedition Guide and Chief Lodge Naturalist specializing in East Africa (Kenya, Tanzania, Uganda, Rwanda).
            Generate a detailed $durationDays-day custom safari itinerary for travelers exploring "$lodgeLocation" in $country.
            
            Traveler Preferences:
            - Destination & Region: $lodgeLocation, $country
            - Duration: $durationDays Days
            - Travel Group / Style: $travelParty
            - Budget Level: $budget
            - Key Passions & Wildlife Interests: $interestsFormatted
            - Daily Expedition Pace: $pace

            Structure your response as clean, inspiring Markdown with distinct emoji-accented sections:
            # $durationDays-Day Safari Expedition: $lodgeLocation ($country)
            
            > **Expedition Summary**: Highlighting travel style ($travelParty), budget tier ($budget), and core highlights ($interestsFormatted).
            
            Provide day-by-day schedules for each of the $durationDays days:
            ## 🌅 Day 1: [Catchy Day Title]
            - **Morning (06:30 - 11:30 EAT)**: Arrival/dawn safari, lodge welcome ritual, orientation briefing.
            - **Afternoon (14:30 - 18:00 EAT)**: Targeted game drive/trekking activity tailored to $interestsFormatted.
            - **Evening (18:30 - 21:30 EAT)**: Golden hour sundowners, savanna banquet, campfire naturalist tales.
            
            [Include detailed sections for each day from Day 1 up to Day $durationDays tailored to the wildlife, terrain, and culture of $lodgeLocation, $country]
            
            ## 🦁 Target Wildlife & Prime Hotspots
            Key species to look for in this region (e.g., Big Five, Mountain Gorillas, Great Migration, Tree-climbing Lions, endemic birds) and optimal viewing spots.
            
            ## 💡 Ranger AI Bush Tip
            An exclusive insider naturalist tip specific to $lodgeLocation and $country (e.g., best time for predator hunts, photography lighting, local terrain secrets).
            
            ## 🎒 East Africa Packing & Safari Gear
            4-5 tailored packing essentials for this $durationDays-day trip (neutral colors, insect protection, camera gear, thermal layers).
            
            ## 🗣️ Swahili & Local Safari Phrase of the Journey
            2-3 essential Swahili or regional phrases with phonetic pronunciation and meaning.
            
            ## 🌿 Conservation & Community Pledge
            How this itinerary supports local conservation and community empowerment in $country.
        """.trimIndent()

        val request = GeminiRequest(
            contents = listOf(
                GeminiContent(
                    parts = listOf(GeminiPart(text = promptText))
                )
            ),
            generationConfig = GeminiGenerationConfig(temperature = 0.7f, maxOutputTokens = 3072)
        )

        try {
            if (apiKey.isNotBlank() && !apiKey.contains("MY_GEMINI_API_KEY") && !apiKey.contains("PLACEHOLDER")) {
                val response = geminiApi.generateContent(apiKey, request)
                val text = response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text
                if (!text.isNullOrBlank()) {
                    return@withContext text
                }
            }
        } catch (e: Exception) {
            android.util.Log.e("GeminiError", "Failed to reach Gemini API, generating localized fallback", e)
        }

        buildFallbackItinerary(lodgeLocation, country, durationDays, travelParty, budget, interestsFormatted, pace)
    }

    private fun buildFallbackItinerary(
        lodgeLocation: String,
        country: String,
        durationDays: Int,
        travelParty: String,
        budget: String,
        interests: String,
        pace: String
    ): String {
        val sb = java.lang.StringBuilder()
        sb.append("# $durationDays-Day Custom Safari: $lodgeLocation ($country)\n\n")
        sb.append("> **Travel Style**: $travelParty • **Budget Tier**: $budget\n")
        sb.append("> **Key Interests**: $interests • **Expedition Pace**: $pace\n\n")
        sb.append("---\n\n")

        sb.append("## 🌅 Day 1: Welcome to the Savanna & Afternoon Orientation Drive\n")
        sb.append("- **Morning (07:30 - 11:30 EAT)**: Scenic 4x4 transfer / bush airstrip arrival at $lodgeLocation. Welcome hibiscus refreshment, check-in, and safety briefing with lead naturalist.\n")
        sb.append("- **Afternoon (15:00 - 18:30 EAT)**: Initial open-roof 4x4 game drive across the golden plains, tracking resident prides and herbivores ($interests).\n")
        sb.append("- **Evening (19:30 - 22:00 EAT)**: Sunset bush sundowners followed by a boma barbecue dinner under the Southern Cross stars.\n\n")

        sb.append("## 🦁 Day 2: Dawn Predator Tracking & Great Savanna Exploration\n")
        sb.append("- **Morning (06:00 - 10:30 EAT)**: Early morning dawn game drive when big cats (lions, cheetahs, leopards) are on the prowl.\n")
        sb.append("- **Afternoon (13:00 - 16:30 EAT)**: Bush picnic beneath an acacia tree, followed by specialized wildlife tracking focusing on $interests.\n")
        sb.append("- **Evening (18:30 - 21:30 EAT)**: Night game safari with infrared spotlights searching for elusive nocturnal species (aardvark, bushbabies, genets).\n\n")

        if (durationDays >= 3) {
            sb.append("## 🌿 Day 3: Guided Bush Walk & Cultural Immersion\n")
            sb.append("- **Morning (06:30 - 09:30 EAT)**: Walking safari accompanied by an armed ranger, discovering micro-ecosystems, animal spoor, and medicinal flora.\n")
            sb.append("- **Afternoon (14:00 - 17:30 EAT)**: Authentic local community and conservation visit with traditional elders.\n")
            sb.append("- **Evening (19:00 - 21:30 EAT)**: Special chef-prepared dinner overlooking the illuminated waterhole.\n\n")
        }

        if (durationDays >= 5) {
            sb.append("## 🎈 Day 4: Aerial Hot Air Balloon Safari & River Crossing Hotspots\n")
            sb.append("- **Morning (05:15 - 09:30 EAT)**: Sunrise hot air balloon flight with panoramic views of the migration herds, concluding with a champagne bush breakfast.\n")
            sb.append("- **Afternoon (14:30 - 18:00 EAT)**: Deep-savanna river loop tracking hippo pods, basking Nile crocodiles, and herds gathering at water crossings.\n")
            sb.append("- **Evening (19:30 - 21:30 EAT)**: Swahili-themed culinary feast and storytelling by veteran safari trackers.\n\n")

            sb.append("## 🦏 Day 5: Endangered Species Sanctuary & Scenic Savanna Farewell\n")
            sb.append("- **Morning (06:30 - 10:30 EAT)**: Visit to the dedicated rhino protection conservancy and anti-poaching canine unit.\n")
            sb.append("- **Afternoon (12:00 - 14:00 EAT)**: Farewell panoramic lunch, souvenir artisan shopping, and departure transfer towards onward flights.\n\n")
        }

        if (durationDays >= 7) {
            sb.append("## 🦅 Day 6: Great Rift Valley Lakes & Raptor Birding Safari\n")
            sb.append("- **Morning (07:00 - 11:30 EAT)**: Exploration of soda lakes and acacia woodlands spotting thousands of pink flamingos, fish eagles, and pelicans.\n")
            sb.append("- **Afternoon (14:30 - 18:00 EAT)**: Photography hide session capturing kingfishers, giraffes, and waterbuck in golden light.\n")
            sb.append("- **Evening (19:30 - 22:00 EAT)**: Fireplace campfire wine tasting discussing African wildlife folklore.\n\n")

            sb.append("## 👑 Day 7: Grand Savanna Finale & High-Tea Overlook\n")
            sb.append("- **Morning (06:30 - 10:00 EAT)**: Final sunrise safari sweep for rare predator sightings.\n")
            sb.append("- **Afternoon (11:30 - 14:00 EAT)**: High tea on the lodge terrace, commemorative certificate presentation, and luxury departure.\n\n")
        }

        if (durationDays >= 10) {
            sb.append("## 🌊 Days 8-10: Bush-to-Beach Swahili Coast / Island Escapade\n")
            sb.append("- **Day 8**: Scenic flight to the coastal marine reserve / Zanzibar spice island. Check-in at oceanfront eco-resort and dhow sunset sailing.\n")
            sb.append("- **Day 9**: Marine safari snorkeling with dolphins and coral reef exploration, followed by a fresh seafood beach dinner.\n")
            sb.append("- **Day 10**: Historic Stone Town spice tour, artisan souvenir browsing, and international airport departure transfer.\n\n")
        }

        sb.append("---\n\n")
        sb.append("### 💡 Ranger AI Bush Tip\n")
        sb.append("In $lodgeLocation ($country), early mornings (06:15 - 07:30 EAT) offer optimal lighting for photography and peak predator hunt activity. Always keep your camera on a fast shutter speed (1/1000s or higher) during vehicle motion!\n\n")

        sb.append("### 🎒 East Africa Packing Essentials\n")
        sb.append("- 10x42 Binoculars for distance wildlife spotting\n")
        sb.append("- Neutral khaki/olive clothing (avoid dark blue/black in tsetse fly zones)\n")
        sb.append("- High-SPF reef-safe sunscreen, wide-brim safari hat & polarized sunglasses\n")
        sb.append("- Lightweight windproof fleece jacket for crisp dawn game drives\n\n")

        sb.append("### 🗣️ Swahili Phrase of the Journey\n")
        sb.append("- *Jambo* (JAM-boh) = Hello / Warm Greetings\n")
        sb.append("- *Asante Sana* (ah-SAHN-teh SAH-nah) = Thank you very much\n")
        sb.append("- *Twende Safari* (TWEN-deh sah-FAH-ree) = Let's go on a wildlife expedition!\n\n")

        sb.append("### 🌿 Conservation Pledge\n")
        sb.append("Every booking in this itinerary contributes directly to local wildlife conservancies and park ranger anti-poaching patrols across $country.")

        return sb.toString()
    }

    // --- API INTEGRATION METHODS ---

    private fun formatMpesaPhoneNumber(phone: String): String {
        val digits = phone.replace(Regex("[^0-9]"), "")
        return when {
            digits.startsWith("0") && digits.length == 10 -> "254" + digits.substring(1)
            digits.startsWith("254") && digits.length == 12 -> digits
            digits.length == 9 -> "254" + digits
            else -> digits.ifEmpty { "254712345678" }
        }
    }

    /**
     * Initiates an M-Pesa STK Push (Lipa Na M-Pesa Online) via PaymentRepository
     */
    suspend fun initiateMpesaPayment(amount: Int, phoneNumber: String, reference: String): StkPushResponse? = withContext(Dispatchers.IO) {
        val result = paymentRepository.initiateMpesaCheckout(
            amount = amount.toDouble(),
            phoneNumber = phoneNumber,
            accountReference = reference,
            transactionDesc = "Safari Stay Payment",
            paymentType = PaymentType.SAFARI_BOOKING
        )

        when (result) {
            is PaymentResult.Success -> {
                StkPushResponse(
                    merchantRequestId = result.transactionId,
                    checkoutRequestId = result.checkoutRequestId,
                    responseCode = "0",
                    responseDescription = "Success",
                    customerMessage = result.customerMessage
                )
            }
            is PaymentResult.Pending -> {
                StkPushResponse(
                    merchantRequestId = "REQ-" + System.currentTimeMillis(),
                    checkoutRequestId = result.checkoutRequestId,
                    responseCode = "0",
                    responseDescription = "Pending",
                    customerMessage = result.customerMessage
                )
            }
            is PaymentResult.Error -> {
                StkPushResponse(
                    merchantRequestId = "ERR-" + System.currentTimeMillis(),
                    checkoutRequestId = "ws_CO_ERR",
                    responseCode = result.responseCode ?: "1",
                    responseDescription = result.message,
                    customerMessage = result.message
                )
            }
        }
    }

    /**
     * Sends an SMS via Africa's Talking API (Fallback for low-connectivity)
     */
    suspend fun sendEmergencySms(to: String, message: String): SmsResponse? = withContext(Dispatchers.IO) {
        try {
            africaTalkingApi.sendSms(
                apiKey = BuildConfig.AFRICAS_TALKING_API_KEY,
                username = BuildConfig.AFRICAS_TALKING_USERNAME,
                to = to,
                message = message
            )
        } catch (e: Exception) {
            android.util.Log.e("SmsError", "Failed to send SMS", e)
            null
        }
    }

    suspend fun sendWhatsAppAlert(to: String, message: String): SmsResponse? = withContext(Dispatchers.IO) {
        try {
            africaTalkingApi.sendWhatsApp(
                apiKey = BuildConfig.AFRICAS_TALKING_API_KEY,
                username = BuildConfig.AFRICAS_TALKING_USERNAME,
                to = to,
                message = message
            )
        } catch (e: Exception) {
            android.util.Log.e("WhatsAppError", "Failed to send WhatsApp", e)
            null
        }
    }

    /**
     * Flushes the local sync queue to the remote server using the Batch Sync API
     */
    suspend fun flushSyncQueue(): BatchSyncResponse? = withContext(Dispatchers.IO) {
        try {
            val pending = syncDao.getPendingActions().first()
            if (pending.isEmpty()) return@withContext null

            val items = pending.map { action ->
                val payloadMap = moshi.adapter(Map::class.java).fromJson(action.payload) ?: emptyMap<String, Any>()
                SyncBatchItem(
                    localId = action.id.toString(),
                    idempotencyKey = "IDEM_${action.id}_${System.currentTimeMillis()}",
                    actionType = action.actionType,
                    entityId = (payloadMap["id"] as? Double)?.toInt()?.toString() ?: "0",
                    clientCreatedAt = java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", java.util.Locale.getDefault()).apply {
                        timeZone = java.util.TimeZone.getTimeZone("UTC")
                    }.format(java.util.Date()),
                    payload = payloadMap as Map<String, Any>
                )
            }

            val request = BatchSyncRequest(
                deviceId = android.provider.Settings.Secure.getString(context.contentResolver, android.provider.Settings.Secure.ANDROID_ID),
                clientSyncTimestamp = java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", java.util.Locale.getDefault()).apply {
                    timeZone = java.util.TimeZone.getTimeZone("UTC")
                }.format(java.util.Date()),
                items = items
            )

            // Generate HMAC signature (placeholder logic for demo)
            val hmac = "hmac_signature_placeholder"

            val response = syncApi.syncBatch(
                bearerToken = "Bearer ${BuildConfig.SAFARI_STAY_API_TOKEN}",
                hmacSignature = hmac,
                request = request
            )

            // Process results: Update local DB based on server status
            response.results.forEach { result ->
                if (result.status == "SUCCESS" || result.status == "CONFLICT_RESOLVED") {
                    syncDao.deleteAction(result.localId.toInt())
                }
            }

            response
        } catch (e: Exception) {
            android.util.Log.e("SyncError", "Batch sync failed", e)
            null
        }
    }

    suspend fun getPlaceDetails(placeId: String): PlaceResponse? {
        return try {
            placesApi.getPlaceDetails(placeId, googleApiKey, fieldMask = "id,displayName,internationalPhoneNumber,photos,rating,userRatingCount")
        } catch (e: Exception) {
            null
        }
    }

    fun getPlacePhotoUrl(photoName: String, maxWidth: Int = 800): String {
        return "https://places.googleapis.com/v1/$photoName/media?maxWidthPx=$maxWidth&key=$googleApiKey"
    }
    private val bookingAdapter = moshi.adapter(BookingEntity::class.java)

    private val localDatabase = com.example.data.local.AppDatabase.getDatabase(context)
    val localBookingRepository = com.example.data.local.BookingRepository(localDatabase.bookingDao())
    val localBookings: Flow<List<com.example.data.local.BookingEntity>> = localBookingRepository.allBookings

    private val bookingDataStore = BookingDataStore(context)
    val cachedBookings: Flow<List<BookingEntity>> = bookingDataStore.cachedBookingsFlow
    val cachedVouchers: Flow<List<VoucherEntity>> = bookingDataStore.cachedVouchersFlow

    suspend fun persistBookingsToDataStore(bookings: List<BookingEntity>) {
        bookingDataStore.saveBookings(bookings)
    }

    suspend fun persistVouchersToDataStore(vouchers: List<VoucherEntity>) {
        bookingDataStore.saveVouchers(vouchers)
    }

    val allBookings: Flow<List<BookingEntity>> = appDao.getAllBookings()
    val allVouchers: Flow<List<VoucherEntity>> = appDao.getAllVouchers()
    val allFavorites: Flow<List<FavoriteEntity>> = appDao.getAllFavorites()
    val allSightings: Flow<List<SightingEntity>> = sightingDao.getAllSightings()
    val allNotifications: Flow<List<NotificationEntity>> = appDao.getAllNotifications()
    val allFeedback: Flow<List<FeedbackEntity>> = appDao.getAllFeedback()
    val allJournalEntries: Flow<List<JournalEntry>> = journalDao.getAllEntries()
    val allChecklistItems: Flow<List<ChecklistItem>> = appDao.getAllChecklistItems()
    val pendingSyncCount: Flow<Int> = syncDao.getPendingCount()
    val pendingActions: Flow<List<SyncAction>> = syncDao.getPendingActions()

    suspend fun queueSyncAction(actionType: String, booking: BookingEntity) = withContext(Dispatchers.IO) {
        // 1. Insert into local bookings table first with "Pending Sync" status
        val localId = appDao.insertBooking(booking.copy(status = "Pending Sync"))
        
        // 2. Queue for satellite sync
        val payload = moshi.adapter(Map::class.java).toJson(mapOf("id" to localId.toInt(), "type" to booking.type))
        syncDao.insertAction(
            SyncAction(
                actionType = actionType,
                payload = payload
            )
        )

        // 3. Trigger SMS Sync immediately if offline
        SmsSyncManager.sendBookingSms(context, booking.copy(id = localId.toInt()))
    }

    suspend fun processSyncQueue() = withContext(Dispatchers.IO) {
        val actions = syncDao.getPendingActions().first()
        actions.forEach { action ->
            try {
                // Simulate network latency for satellite handshake
                delay(1500)
                
                val payloadMap = moshi.adapter(Map::class.java).fromJson(action.payload)
                val bookingId = (payloadMap?.get("id") as? Double)?.toInt() ?: return@forEach
                
                when (action.actionType) {
                    "BOOK_STAY", "BOOK_SAFARI" -> {
                        // Mark as Confirmed in local DB
                        appDao.updateBookingStatus(bookingId, "Confirmed")
                        syncDao.deleteAction(action.id)
                    }
                }
            } catch (e: Exception) {
                syncDao.updateStatus(action.id, "FAILED")
            }
        }
    }

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
        try {
            val updatedList = appDao.getAllBookings().first()
            bookingDataStore.saveBookings(updatedList)
        } catch (e: Exception) {
            e.printStackTrace()
        }
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

    suspend fun updateBookingStatus(id: Int, status: String) = withContext(Dispatchers.IO) {
        appDao.updateBookingStatus(id, status)
    }

    suspend fun addNotification(title: String, message: String, type: String = "INFO", relatedId: Int = 0) = withContext(Dispatchers.IO) {
        appDao.insertNotification(NotificationEntity(title = title, message = message, type = type, relatedId = relatedId))
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
                status = "Held (Escrow)",
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
                status = "Held (Escrow)"
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
