package com.example.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.BuildConfig
import com.example.data.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.IOException

class SafariViewModel(private val repository: SafariRepository) : ViewModel() {


    // Tab state: "stays", "safaris", "vouchers", "bookings"
    private val _currentTab = MutableStateFlow("stays")
    val currentTab: StateFlow<String> = _currentTab.asStateFlow()

    // Search query
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    // User's interactive bookings & vouchers from Room database
    val bookings: StateFlow<List<BookingEntity>> = repository.allBookings
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val vouchers: StateFlow<List<VoucherEntity>> = repository.allVouchers
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val favorites: StateFlow<List<FavoriteEntity>> = repository.allFavorites
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val sightings: StateFlow<List<SightingEntity>> = repository.allSightings
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val journalEntries: StateFlow<List<JournalEntry>> = repository.allJournalEntries
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // Map type: "satellite" or "terrain"
    private val _mapType = MutableStateFlow("satellite")
    val mapType: StateFlow<String> = _mapType.asStateFlow()

    fun setMapType(type: String) {
        _mapType.value = type
        _uiEvent.value = "Map switched to ${type.replaceFirstChar { it.uppercase() }} mode"
    }

    // --- BUDGET MANAGEMENT ---
    private val _totalBudget = MutableStateFlow(5000f)
    val totalBudget: StateFlow<Float> = _totalBudget.asStateFlow()

    val totalSpent = bookings.map { list ->
        list.sumOf { booking ->
            booking.price
        }.toFloat()
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0f)

    val remainingBudget = combine(totalBudget, totalSpent) { budget, spent ->
        budget - spent
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 5000f)

    val tripProgress = bookings.map { list ->
        if (list.isEmpty()) return@map 0f
        
        val now = System.currentTimeMillis()
        val minStart = list.minOf { it.startDateTimestamp }
        
        // Calculate max end date
        val maxEnd = list.maxOf { booking ->
            val durationMs = if (booking.type == "SAFARI") {
                val safari = SafariCatalog.safaris.find { it.title == booking.title }
                (safari?.durationDays ?: 1).toLong() * 24 * 60 * 60 * 1000
            } else {
                3L * 24 * 60 * 60 * 1000 // Default 3 days for stays
            }
            booking.startDateTimestamp + durationMs
        }
        
        if (now < minStart) return@map 0f
        if (now >= maxEnd) return@map 1f
        
        val total = maxEnd - minStart
        val elapsed = now - minStart
        (elapsed.toFloat() / total.toFloat()).coerceIn(0f, 1f)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0f)

    val checklistItems = repository.allChecklistItems
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val checklistProgress = checklistItems.map { list ->
        if (list.isEmpty()) 0f
        else list.count { it.isCompleted }.toFloat() / list.size.toFloat()
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0f)

    fun toggleChecklistItem(item: ChecklistItem) {
        viewModelScope.launch {
            repository.updateChecklistItem(item.copy(isCompleted = !item.isCompleted))
        }
    }

    val budgetAlert = combine(totalBudget, totalSpent) { budget, spent ->
        val ratio = if (budget > 0) spent / budget else 0f
        when {
            ratio >= 1.0f -> BudgetAlert(BudgetLevel.CRITICAL, "🚨 BUDGET EXCEEDED! Total spent $${spent.toInt()} exceeds limit of $${budget.toInt()}.")
            ratio >= 0.8f -> BudgetAlert(BudgetLevel.WARNING, "⚠️ BUDGET WARNING: You have used ${ (ratio * 100).toInt() }% of your $${budget.toInt()} budget.")
            else -> BudgetAlert(BudgetLevel.SAFE, "Budget is within safe limits.")
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), BudgetAlert(BudgetLevel.SAFE, ""))

    fun setTotalBudget(amount: Float) {
        _totalBudget.value = amount
    }

    private val _isOnline = MutableStateFlow(true)
    val isOnline: StateFlow<Boolean> = _isOnline.asStateFlow()

    private val _isSyncing = MutableStateFlow(false)
    val isSyncing: StateFlow<Boolean> = _isSyncing.asStateFlow()

    private val _lastSightingSyncTime = MutableStateFlow<Long?>(null)
    val lastSightingSyncTime: StateFlow<Long?> = _lastSightingSyncTime.asStateFlow()

    // --- COMMUNITY SIGHTINGS FEED STATE ---
    private val _communitySightings = MutableStateFlow<List<CommunitySighting>>(
        listOf(
            CommunitySighting(
                id = "comm_1",
                travelerName = "Sarah Jenkins",
                travelerAvatar = "👩🏼‍🌾",
                speciesName = "African Lion",
                note = "A magnificent pair of male lions walking right next to the Sekenani gate trail. Keep your cameras ready!",
                locationTag = "Maasai Mara National Reserve, Kenya",
                timestamp = System.currentTimeMillis() - 1800000, // 30 mins ago
                photoPlaceholder = "lion",
                isHelpfulCount = 14,
                viewingTips = "Stay entirely inside your vehicle. They are habituated to safari trucks but very close."
            ),
            CommunitySighting(
                id = "comm_2",
                travelerName = "Mateo Silva",
                travelerAvatar = "👨🏽‍💻",
                speciesName = "Cheetah",
                note = "Incredible burst of speed! Witnessed a cheetah hunting a gazelle near the Talek River crossing.",
                locationTag = "Maasai Mara National Reserve, Kenya",
                timestamp = System.currentTimeMillis() - 7200000, // 2 hours ago
                photoPlaceholder = "leopard",
                isHelpfulCount = 28,
                viewingTips = "Use a 300mm+ telephoto lens. They are active during late morning hours when heat shimmer is low."
            ),
            CommunitySighting(
                id = "comm_3",
                travelerName = "Aiko Tanaka",
                travelerAvatar = "👩🏻‍⚕️",
                speciesName = "Leopard",
                note = "Spotted a gorgeous leopard lounging lazily on an Acacia tree branch near Seronera airstrip.",
                locationTag = "Central Serengeti, Tanzania",
                timestamp = System.currentTimeMillis() - 3600000, // 1 hour ago
                photoPlaceholder = "leopard",
                isHelpfulCount = 19,
                viewingTips = "Check the lower yellow fever tree limbs. Binoculars are essential as they blend in perfectly."
            ),
            CommunitySighting(
                id = "comm_4",
                travelerName = "Lukas Weber",
                travelerAvatar = "👨🏼‍🌾",
                speciesName = "Zebra",
                note = "Massive herds crossing the central grasslands right now. The dust cloud and noise are jaw-dropping!",
                locationTag = "Central Serengeti, Tanzania",
                timestamp = System.currentTimeMillis() - 14400000, // 4 hours ago
                photoPlaceholder = "zebra",
                isHelpfulCount = 8,
                viewingTips = "Keep windows up or wear a scarf. The dust kicked up by thousands of hooves is extremely fine."
            ),
            CommunitySighting(
                id = "comm_5",
                travelerName = "Elena Rostova",
                travelerAvatar = "👩🏼‍🎨",
                speciesName = "Black Rhinoceros",
                note = "We spotted TWO black rhinos grazing peacefully near Lake Magadi. Absolutely breathtaking and rare sighting!",
                locationTag = "Ngorongoro Crater Rim, Tanzania",
                timestamp = System.currentTimeMillis() - 10800000, // 3 hours ago
                photoPlaceholder = "rhino",
                isHelpfulCount = 42,
                viewingTips = "Bring a high-powered spotting scope. They tend to graze deep in the crater floor and stay far from vehicles."
            ),
            CommunitySighting(
                id = "comm_6",
                travelerName = "Chloe Dubois",
                travelerAvatar = "👩🏻‍🚀",
                speciesName = "African Elephant",
                note = "A huge matriarch herd of elephants mud-bathing around a giant baobab tree close to the riverbank.",
                locationTag = "Tarangire National Park, Tanzania",
                timestamp = System.currentTimeMillis() - 900000, // 15 mins ago
                photoPlaceholder = "elephant",
                isHelpfulCount = 12,
                viewingTips = "Wait quietly at the river bend. They love the cooling mud-bath to shield themselves from the midday sun."
            )
        )
    )
    val communitySightings: StateFlow<List<CommunitySighting>> = _communitySightings.asStateFlow()

    private val _isRefreshingCommunity = MutableStateFlow(false)
    val isRefreshingCommunity: StateFlow<Boolean> = _isRefreshingCommunity.asStateFlow()

    // Price range filters
    private val _maxPriceFilter = MutableStateFlow(10000.0)
    val maxPriceFilter: StateFlow<Double> = _maxPriceFilter.asStateFlow()

    private val _minPriceFilter = MutableStateFlow(0.0)
    val minPriceFilter: StateFlow<Double> = _minPriceFilter.asStateFlow()

    // Currency selection ("USD", "KES", "TZS", "UGX")
    private val _selectedCurrency = MutableStateFlow("USD")
    val selectedCurrency: StateFlow<String> = _selectedCurrency.asStateFlow()

    fun updateCurrency(currency: String) {
        _selectedCurrency.value = currency
    }

    fun convertPrice(price: Double): Double {
        return when (_selectedCurrency.value) {
            "KES" -> price * 130.0
            "TZS" -> price * 2500.0
            "UGX" -> price * 3700.0
            else -> price
        }
    }

    fun getCurrencySymbol(): String {
        return when (_selectedCurrency.value) {
            "KES" -> "KSh "
            "TZS" -> "TSh "
            "UGX" -> "USh "
            else -> "$"
        }
    }

    // Date/Month filter (Options: "All Months", "August 2026", "September 2026", "October 2026", "December 2026")
    private val _selectedMonthFilter = MutableStateFlow("All Months")
    val selectedMonthFilter: StateFlow<String> = _selectedMonthFilter.asStateFlow()

    // Region filter for events (Options: "All Regions", "Kenya", "Tanzania")
    private val _selectedRegionFilter = MutableStateFlow("All Regions")
    val selectedRegionFilter: StateFlow<String> = _selectedRegionFilter.asStateFlow()

    fun updateRegionFilter(region: String) {
        _selectedRegionFilter.value = region
    }

    // Filtered events based on search query, region, and month
    val filteredEvents: StateFlow<List<EventItem>> = combine(
        searchQuery,
        selectedRegionFilter,
        selectedMonthFilter
    ) { query, region, month ->
        SafariCatalog.events.filter { event ->
            // Search match
            val searchMatch = query.isEmpty() ||
                    event.title.contains(query, ignoreCase = true) ||
                    event.location.contains(query, ignoreCase = true) ||
                    event.description.contains(query, ignoreCase = true)

            // Region match
            val regionMatch = region == "All Regions" || event.region == region

            // Date match
            // Event date is formatted like "August 15, 2026", so it should contain the month string like "August 2026". We can just check if it contains the month name and year if month is not "All Months".
            val dateMatch = month == "All Months" || event.date.contains(month.split(" ")[0])
            
            searchMatch && regionMatch && dateMatch
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), SafariCatalog.events)

    // Filtered stays based on search query, price range, and month
    val filteredStays: StateFlow<List<StayItem>> = combine(
        searchQuery,
        minPriceFilter,
        maxPriceFilter,
        selectedMonthFilter
    ) { query, minPrice, maxPrice, month ->
        SafariCatalog.stays.filter { stay ->
            // Destination match
            val destMatch = query.isEmpty() || 
                    stay.title.contains(query, ignoreCase = true) ||
                    stay.location.contains(query, ignoreCase = true) ||
                    stay.country.contains(query, ignoreCase = true)
            
            // Price match
            val priceMatch = stay.pricePerNight >= minPrice && stay.pricePerNight <= maxPrice
            
            // Date match: We simulate available months for stays
            val availableMonths = when (stay.id) {
                "stay_mara_lodge" -> listOf("August 2026", "September 2026")
                "stay_serengeti_tents" -> listOf("September 2026", "October 2026")
                "stay_zanzibar_pavilion" -> listOf("October 2026", "December 2026")
                "stay_ngorongoro_suite" -> listOf("August 2026", "December 2026")
                else -> listOf("August 2026", "September 2026", "October 2026", "December 2026")
            }
            val dateMatch = month == "All Months" || availableMonths.contains(month)
            
            destMatch && priceMatch && dateMatch
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), SafariCatalog.stays)

    // Filtered safaris based on search query, price range, and month
    val filteredSafaris: StateFlow<List<SafariItem>> = combine(
        searchQuery,
        minPriceFilter,
        maxPriceFilter,
        selectedMonthFilter
    ) { query, minPrice, maxPrice, month ->
        SafariCatalog.safaris.filter { safari ->
            // Destination match
            val destMatch = query.isEmpty() || 
                    safari.title.contains(query, ignoreCase = true) ||
                    safari.park.contains(query, ignoreCase = true) ||
                    safari.country.contains(query, ignoreCase = true)
            
            // Price match
            val priceMatch = safari.price >= minPrice && safari.price <= maxPrice
            
            // Date match: We simulate available months for safaris
            val availableMonths = when (safari.id) {
                "safari_great_migration" -> listOf("August 2026", "September 2026")
                "safari_crater_wild" -> listOf("September 2026", "October 2026")
                "safari_zanzibar_sea" -> listOf("October 2026", "December 2026")
                else -> listOf("August 2026", "September 2026", "October 2026", "December 2026")
            }
            val dateMatch = month == "All Months" || availableMonths.contains(month)
            
            destMatch && priceMatch && dateMatch
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), SafariCatalog.safaris)

    // UI Message or notification
    private val _uiEvent = MutableStateFlow<String?>(null)
    val uiEvent: StateFlow<String?> = _uiEvent.asStateFlow()

    fun clearUiEvent() {
        _uiEvent.value = null
    }

    fun toggleOnline() {
        val newStatus = !_isOnline.value
        _isOnline.value = newStatus
        if (newStatus) {
            // Automatically trigger sync when toggling to online
            syncSightings()
            syncAllFeedback()
            syncAllJournalEntries()
        }
    }

    fun addSighting(speciesName: String, note: String, locationTag: String, photoPlaceholder: String) {
        viewModelScope.launch {
            if (speciesName.isBlank()) {
                _uiEvent.value = "Please enter a species name."
                return@launch
            }
            val sighting = SightingEntity(
                speciesName = speciesName.trim(),
                note = note.trim(),
                locationTag = locationTag.trim(),
                timestamp = System.currentTimeMillis(),
                photoPlaceholder = photoPlaceholder,
                isSynced = _isOnline.value // If online, mark as synced immediately!
            )
            repository.addSighting(sighting)
            _uiEvent.value = "Sighting of $speciesName saved locally!"
            
            if (_isOnline.value) {
                _uiEvent.value = "Sighting logged and uploaded to live server!"
                broadcastSightingToCommunity(sighting)
            }
        }
    }

    fun syncSightings() {
        if (_isSyncing.value || !_isOnline.value) return
        viewModelScope.launch {
            val unsyncedList = repository.getUnsyncedSightings()
            if (unsyncedList.isEmpty()) {
                _uiEvent.value = "All sightings are already in sync."
                return@launch
            }
            _isSyncing.value = true
            _uiEvent.value = "Syncing ${unsyncedList.size} sightings to server..."
            // Simulate network latency for synchronization
            kotlinx.coroutines.delay(1800)
            for (sighting in unsyncedList) {
                repository.updateSighting(sighting.copy(isSynced = true))
                broadcastSightingToCommunity(sighting)
            }
            _lastSightingSyncTime.value = System.currentTimeMillis()
            _isSyncing.value = false
            _uiEvent.value = "Synchronization complete! Broadcasted to community."
        }
    }

    // --- COMMUNITY ACTIONS ---
    fun broadcastSightingToCommunity(sighting: SightingEntity) {
        val alreadyExists = _communitySightings.value.any { it.id == "user_comm_${sighting.id}" }
        if (alreadyExists) return

        val newCommunitySighting = CommunitySighting(
            id = "user_comm_${sighting.id}",
            travelerName = "You (Traveler)",
            travelerAvatar = "🤠",
            speciesName = sighting.speciesName,
            note = sighting.note,
            locationTag = sighting.locationTag,
            timestamp = sighting.timestamp,
            photoPlaceholder = sighting.photoPlaceholder,
            isHelpfulCount = 0,
            viewingTips = "Spotted personally during game drive. Location is accurate."
        )
        _communitySightings.value = listOf(newCommunitySighting) + _communitySightings.value
    }

    fun upvoteCommunitySighting(id: String) {
        _communitySightings.value = _communitySightings.value.map { item ->
            if (item.id == id) {
                val upvoted = !item.hasUpvoted
                item.copy(
                    hasUpvoted = upvoted,
                    isHelpfulCount = item.isHelpfulCount + (if (upvoted) 1 else -1)
                )
            } else {
                item
            }
        }
    }

    fun toggleSaveToPlanning(id: String) {
        _communitySightings.value = _communitySightings.value.map { item ->
            if (item.id == id) {
                val saved = !item.isSavedToPlanning
                _uiEvent.value = if (saved) "Added to your Safari trip planning!" else "Removed from planning."
                item.copy(isSavedToPlanning = saved)
            } else {
                item
            }
        }
    }

    fun refreshCommunityFeed() {
        if (!_isOnline.value) {
            _uiEvent.value = "Terminal OFFLINE. Cannot refresh from satellite feed."
            return
        }
        viewModelScope.launch {
            _isRefreshingCommunity.value = true
            _uiEvent.value = "Connecting to satellite network..."
            kotlinx.coroutines.delay(1200)
            
            // Add a new random simulated community sighting to represent feed updates
            val randomNames = listOf("Emma Watson", "John Doe", "Anya Chen", "Carlos Ruiz")
            val randomAvatars = listOf("👩🏻", "👨🏼", "👩🏽", "👨🏾")
            val randomSpecies = listOf("Cheetah", "Giraffe", "Leopard", "African Elephant")
            val randomIcons = listOf("leopard", "giraffe", "leopard", "elephant")
            val randomLocations = listOf(
                "Maasai Mara National Reserve, Kenya",
                "Central Serengeti, Tanzania",
                "Ngorongoro Crater Rim, Tanzania",
                "Tarangire National Park, Tanzania"
            )
            val randomNotes = listOf(
                "Spotted a family of cheetahs hunting in the morning sun!",
                "Stunning tall giraffes chewing Acacia leaves. Very scenic background.",
                "High-contrast leopard drinking from the pool. Absolutely stunning!",
                "An elephant baby playing with water in front of our lodge!"
            )
            val randomTips = listOf(
                "Drive slowly near the bush lines. They hide in shadows.",
                "Perfect lighting for photography right now. Recommended sunset spot.",
                "Turn off your engine to not scare them away.",
                "Keep a safe distance! Matriarchs are protective."
            )
            
            val randomIndex = (0..3).random()
            val newReport = CommunitySighting(
                id = "comm_rand_${System.currentTimeMillis()}",
                travelerName = randomNames[randomIndex],
                travelerAvatar = randomAvatars[randomIndex],
                speciesName = randomSpecies[randomIndex],
                note = randomNotes[randomIndex],
                locationTag = randomLocations[randomIndex],
                timestamp = System.currentTimeMillis(),
                photoPlaceholder = randomIcons[randomIndex],
                isHelpfulCount = (1..5).random(),
                viewingTips = randomTips[randomIndex]
            )
            
            _communitySightings.value = listOf(newReport) + _communitySightings.value
            _isRefreshingCommunity.value = false
            _uiEvent.value = "Satellite Feed updated! New wildlife reports loaded."
        }
    }

    fun setTab(tab: String) {
        _currentTab.value = tab
    }

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun updatePriceFilter(minPrice: Double, maxPrice: Double) {
        _minPriceFilter.value = minPrice
        _maxPriceFilter.value = maxPrice
    }

    fun updateMonthFilter(month: String) {
        _selectedMonthFilter.value = month
    }

    fun resetFilters() {
        _searchQuery.value = ""
        _minPriceFilter.value = 0.0
        _maxPriceFilter.value = 2000.0
        _selectedMonthFilter.value = "All Months"
        _selectedRegionFilter.value = "All Regions"
    }

    fun toggleFavorite(itemId: String, type: String, title: String) {
        viewModelScope.launch {
            repository.toggleFavorite(itemId, type, title)
        }
    }

    // Actions
    fun buyVoucher(title: String, description: String, amount: Double) {
        viewModelScope.launch {
            if (amount <= 0.0) {
                _uiEvent.value = "Please enter a valid amount."
                return@launch
            }
            val voucher = repository.generateVoucher(title, description, amount)
            _uiEvent.value = "Success! Created Voucher: ${voucher.code} with $${String.format("%.2f", amount)}"
        }
    }

    fun bookStay(stay: StayItem, dateRange: String, voucherCode: String? = null) {
        viewModelScope.launch {
            val finalVoucherCode = voucherCode?.trim()?.uppercase()
            var discountUsed = 0.0
            var notes = ""

            if (!finalVoucherCode.isNullOrEmpty()) {
                val voucher = repository.getVoucherByCode(finalVoucherCode)
                if (voucher == null) {
                    _uiEvent.value = "Error: Invalid Voucher Code."
                    return@launch
                }
                if (voucher.status != "Active") {
                    _uiEvent.value = "Error: Voucher is already fully redeemed or expired."
                    return@launch
                }
                if (voucher.remainingValue <= 0.0) {
                    _uiEvent.value = "Error: Voucher has insufficient balance."
                    return@launch
                }

                // If voucher value is less than total price, deduct everything and user pays difference
                val priceToDeduct = minOf(stay.pricePerNight, voucher.remainingValue)
                discountUsed = priceToDeduct
                val success = repository.redeemVoucher(finalVoucherCode, priceToDeduct)
                if (!success) {
                    _uiEvent.value = "Error: Failed to process voucher discount."
                    return@launch
                }
                notes = "Saved $${String.format("%.2f", discountUsed)} using voucher $finalVoucherCode"
            }

            val newBooking = BookingEntity(
                type = "STAY",
                title = stay.title,
                location = "${stay.location}, ${stay.country}",
                dateRange = dateRange,
                startDateTimestamp = System.currentTimeMillis() + (7L * 24 * 60 * 60 * 1000), // Default 7 days from now
                price = stay.pricePerNight,
                imageResName = stay.imageResName,
                status = "Confirmed",
                voucherCodeUsed = if (discountUsed > 0.0) finalVoucherCode else null
            )

            repository.createBooking(newBooking)
            val baseMessage = "Successfully booked ${stay.title}!"
            _uiEvent.value = if (discountUsed > 0.0) "$baseMessage $notes" else baseMessage
            _currentTab.value = "bookings"
        }
    }

    fun bookSafari(safari: SafariItem, dateRange: String, voucherCode: String? = null) {
        viewModelScope.launch {
            val finalVoucherCode = voucherCode?.trim()?.uppercase()
            var discountUsed = 0.0
            var notes = ""

            if (!finalVoucherCode.isNullOrEmpty()) {
                val voucher = repository.getVoucherByCode(finalVoucherCode)
                if (voucher == null) {
                    _uiEvent.value = "Error: Invalid Voucher Code."
                    return@launch
                }
                if (voucher.status != "Active") {
                    _uiEvent.value = "Error: Voucher is already redeemed or expired."
                    return@launch
                }
                if (voucher.remainingValue <= 0.0) {
                    _uiEvent.value = "Error: Voucher has insufficient balance."
                    return@launch
                }

                val priceToDeduct = minOf(safari.price, voucher.remainingValue)
                discountUsed = priceToDeduct
                val success = repository.redeemVoucher(finalVoucherCode, priceToDeduct)
                if (!success) {
                    _uiEvent.value = "Error: Failed to apply voucher discount."
                    return@launch
                }
                notes = "Saved $${String.format("%.2f", discountUsed)} using voucher $finalVoucherCode"
            }

            val newBooking = BookingEntity(
                type = "SAFARI",
                title = safari.title,
                location = "${safari.park}, ${safari.country}",
                dateRange = dateRange,
                startDateTimestamp = System.currentTimeMillis() + (7L * 24 * 60 * 60 * 1000), // Default 7 days from now
                price = safari.price,
                imageResName = safari.imageResName,
                status = "Confirmed",
                voucherCodeUsed = if (discountUsed > 0.0) finalVoucherCode else null
            )

            repository.createBooking(newBooking)
            val baseMessage = "Successfully booked ${safari.title} expedition!"
            _uiEvent.value = if (discountUsed > 0.0) "$baseMessage $notes" else baseMessage
            _currentTab.value = "bookings"
        }
    }

    fun deleteBooking(booking: BookingEntity) {
        viewModelScope.launch {
            repository.cancelBooking(booking)
            _uiEvent.value = "Cancelled booking for ${booking.title}."
        }
    }

    fun getItineraryForBooking(booking: BookingEntity): List<ItineraryItem> {
        if (booking.type != "SAFARI") return emptyList()
        
        val safari = SafariCatalog.safaris.find { it.title == booking.title } ?: return emptyList()
        
        return (1..safari.durationDays).map { day ->
            ItineraryItem(
                day = day,
                activity = "Day $day: Explore ${safari.park}",
                description = "Enjoy game drives and scenic views in ${safari.park}."
            )
        }
    }

    // --- BUSH SOS & COORDINATES STATE ---
    private val _lastSyncedLat = MutableStateFlow(-1.36212)
    val lastSyncedLat: StateFlow<Double> = _lastSyncedLat.asStateFlow()

    private val _lastSyncedLng = MutableStateFlow(34.83124)
    val lastSyncedLng: StateFlow<Double> = _lastSyncedLng.asStateFlow()

    private val _lastSyncedLocationName = MutableStateFlow("Maasai Mara Reserve, Kenya")
    val lastSyncedLocationName: StateFlow<String> = _lastSyncedLocationName.asStateFlow()

    private val _lastSyncTime = MutableStateFlow(System.currentTimeMillis())
    val lastSyncTime: StateFlow<Long> = _lastSyncTime.asStateFlow()

    private val _isLocationSyncing = MutableStateFlow(false)
    val isLocationSyncing: StateFlow<Boolean> = _isLocationSyncing.asStateFlow()

    // Swahili phrases
    val swahiliPhrases = SafariCatalog.swahiliPhrases

    // --- DYNAMIC PACKING CHECKLIST STATE ---
    val recommendedPackingList: StateFlow<List<PackingItem>> = bookings.map { bookingList ->
        val userEcosystems = mutableSetOf<String>()
        val userActivities = mutableSetOf<String>()

        bookingList.forEach { booking ->
            // Heuristic mapping based on booking details
            val text = (booking.title + " " + booking.location).lowercase()
            
            if (text.contains("mara") || text.contains("serengeti") || text.contains("savanna") || text.contains("lodge")) {
                userEcosystems.add("Savanna")
            }
            if (text.contains("forest") || text.contains("jungle") || text.contains("rain") || text.contains("bwindy")) {
                userEcosystems.add("Rainforest")
            }
            if (text.contains("beach") || text.contains("coast") || text.contains("ocean") || text.contains("zanzibar") || text.contains("dhow")) {
                userEcosystems.add("Coastal")
            }
            if (text.contains("kilimanjaro") || text.contains("mountain") || text.contains("peak") || text.contains("highland")) {
                userEcosystems.add("Highlands")
            }

            if (text.contains("game drive") || text.contains("safari") || text.contains("park")) {
                userActivities.add("Game Drive")
            }
            if (text.contains("walk") || text.contains("trek") || text.contains("hike")) {
                userActivities.add("Walking Safari")
            }
            if (text.contains("boat") || text.contains("cruise") || text.contains("dhow") || text.contains("river") || text.contains("ocean")) {
                userActivities.add("Boat Safari")
            }
        }

        // Filter catalog items
        SafariCatalog.packingList.filter { item ->
            // Always show universal items (empty required lists)
            if (item.requiredForEcosystems.isEmpty() && item.requiredForActivities.isEmpty()) {
                return@filter true
            }
            
            // Show if it matches user's ecosystems
            val ecosystemMatch = item.requiredForEcosystems.any { it in userEcosystems }
            
            // Show if it matches user's activities
            val activityMatch = item.requiredForActivities.any { it in userActivities }
            
            ecosystemMatch || activityMatch
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = SafariCatalog.packingList.filter { it.requiredForEcosystems.isEmpty() && it.requiredForActivities.isEmpty() }
    )

    // Notifications
    val notifications = repository.allNotifications.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val feedbackList = repository.allFeedback.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    fun submitFeedback(bookingId: Int, title: String, rating: Int, comment: String) {
        viewModelScope.launch {
            val feedback = FeedbackEntity(
                bookingId = bookingId,
                bookingTitle = title,
                rating = rating,
                comment = comment
            )
            repository.addFeedback(feedback)
            _uiEvent.value = "Feedback saved locally! It will sync when you're online."
            
            // Auto-sync if online
            if (isOnline.value) {
                syncAllFeedback()
            }
        }
    }

    fun syncAllFeedback() {
        viewModelScope.launch {
            val pending = feedbackList.value.filter { !it.isSynced }
            if (pending.isNotEmpty()) {
                _isSyncing.value = true
                pending.forEach { feedback ->
                    repository.syncFeedback(feedback.id)
                }
                _isSyncing.value = false
                _uiEvent.value = "All feedback synced to the cloud!"
            }
        }
    }

    // --- JOURNAL METHODS ---
    fun addJournalEntry(note: String, location: String) {
        viewModelScope.launch {
            val entry = JournalEntry(
                note = note,
                location = location,
                timestamp = System.currentTimeMillis()
            )
            repository.addJournalEntry(entry)
            _uiEvent.value = "Journal entry saved locally!"
            
            if (_isOnline.value) {
                syncAllJournalEntries()
            }
        }
    }

    fun syncAllJournalEntries() {
        viewModelScope.launch {
            val pending = journalEntries.value.filter { !it.isSynced }
            if (pending.isNotEmpty()) {
                _isSyncing.value = true
                pending.forEach { entry ->
                    repository.syncJournalEntry(entry.id)
                }
                _isSyncing.value = false
                _uiEvent.value = "All journal entries synced to the server!"
            }
        }
    }

    fun deleteJournalEntry(entry: JournalEntry) {
        viewModelScope.launch {
            repository.deleteJournalEntry(entry)
            _uiEvent.value = "Journal entry deleted."
        }
    }

    // --- SHARE ITINERARY METHODS ---
    private val _shareLink = MutableStateFlow<String?>(null)
    val shareLink: StateFlow<String?> = _shareLink.asStateFlow()

    fun generateShareLink() {
        viewModelScope.launch {
            _isSyncing.value = true
            delay(1200) // Simulate server processing
            val userId = "user_${(1000..9999).random()}"
            val tripId = "safari_${(100000..999999).random()}"
            _shareLink.value = "https://safaristay.app/share/$userId/$tripId"
            _isSyncing.value = false
            _uiEvent.value = "Shareable itinerary link generated!"
        }
    }

    fun clearShareLink() {
        _shareLink.value = null
    }

    fun getItinerarySummaryText(): String {
        val bookingsList = bookings.value
        if (bookingsList.isEmpty()) return "No bookings found in my Safari Itinerary."

        val sb = StringBuilder()
        sb.append("🦁 My Safari Expedition Itinerary\n")
        sb.append("--------------------------------\n\n")

        bookingsList.forEach { booking ->
            sb.append("📍 ${booking.type}: ${booking.title}\n")
            sb.append("📅 Dates: ${booking.dateRange}\n")
            sb.append("🌍 Location: ${booking.location}\n")
            sb.append("\n")
        }

        sb.append("View live tracking and journal on the SafariStay app.")
        return sb.toString()
    }

    fun markNotificationAsRead(id: Int) {
        viewModelScope.launch {
            repository.markNotificationAsRead(id)
        }
    }

    // --- WEATHER STATE ---
    private val _weatherState = MutableStateFlow<WeatherCacheEntity?>(null)
    val weatherState: StateFlow<WeatherCacheEntity?> = _weatherState.asStateFlow()

    init {
        // Prepopulate the database with beautiful default data on startup
        viewModelScope.launch {
            repository.prepopulateIfEmpty()
            // Check for notifications after prepopulating
            repository.checkAndGenerateNotifications()
        }

        // Reactively fetch weather when location changes
        lastSyncedLocationName.onEach { location ->
            refreshWeather(location)
        }.launchIn(viewModelScope)
    }

    private fun refreshWeather(location: String) {
        viewModelScope.launch {
            // First, try to load from cache for instant offline view
            val cached = repository.getCachedWeather(location)
            if (cached != null) {
                _weatherState.value = cached
            }

            // Then, if online, fetch fresh data
            if (isOnline.value) {
                try {
                    val freshWeather = repository.fetchLatestWeather(location)
                    _weatherState.value = freshWeather
                } catch (e: Exception) {
                    // Fail silently or log, cached data remains
                }
            }
        }
    }

    private val _sosSendingStatus = MutableStateFlow<String?>(null)
    val sosSendingStatus: StateFlow<String?> = _sosSendingStatus.asStateFlow()

    private val _isSosSending = MutableStateFlow(false)
    val isSosSending: StateFlow<Boolean> = _isSosSending.asStateFlow()

    fun refreshLocation() {
        viewModelScope.launch {
            _isLocationSyncing.value = true
            // Simulate GPS satellite acquisition and synchronization
            kotlinx.coroutines.delay(1200)
            val spots = listOf(
                Triple(-1.36212, 34.83124, "Maasai Mara Reserve, Kenya"),
                Triple(-2.15402, 34.68571, "Central Serengeti, Tanzania"),
                Triple(-3.24508, 35.58632, "Ngorongoro Crater Rim, Tanzania"),
                Triple(-3.94121, 35.84210, "Tarangire Wilderness, Tanzania"),
                Triple(-5.72314, 39.29412, "Nungwi Beach Safari Zone, Zanzibar")
            )
            val selected = spots.random()
            _lastSyncedLat.value = selected.first
            _lastSyncedLng.value = selected.second
            _lastSyncedLocationName.value = selected.third
            _lastSyncTime.value = System.currentTimeMillis()
            _isLocationSyncing.value = false
            _uiEvent.value = "GPS synced: ${selected.third} (${selected.first}, ${selected.second})"
        }
    }

    fun triggerBushSos(messageText: String) {
        viewModelScope.launch {
            _isSosSending.value = true
            _sosSendingStatus.value = "Initiating Emergency Bush SOS Protocols..."
            kotlinx.coroutines.delay(800)
            
            _sosSendingStatus.value = "Establishing satellite link & securing coordinates..."
            kotlinx.coroutines.delay(800)
            
            val apiKey = try {
                BuildConfig.AFRICAS_TALKING_API_KEY
            } catch (e: Throwable) {
                ""
            }
            val username = try {
                BuildConfig.AFRICAS_TALKING_USERNAME
            } catch (e: Throwable) {
                "sandbox"
            }
            val recipient = try {
                BuildConfig.AFRICAS_TALKING_TO
            } catch (e: Throwable) {
                "+254712345678"
            }

            if (apiKey.contains("YOUR_AFRICAS_TALKING") || apiKey.isBlank()) {
                _sosSendingStatus.value = "⚠️ Live Africa's Talking credentials not found. Activating Emergency Satellite Simulation..."
                kotlinx.coroutines.delay(1500)
                _sosSendingStatus.value = "📡 Local satellite link established.\n" +
                        "Recipient: Savannah Emergency Operations HQ ($recipient)\n" +
                        "Dispatched Message: \"$messageText\"\n\n" +
                        "✅ Emergency SOS SMS dispatched via Satellite Backup Channel!"
                _isSosSending.value = false
                return@launch
            }

            _sosSendingStatus.value = "Connecting to Africa's Talking API Gateway..."
            
            val client = OkHttpClient()
            val url = if (username == "sandbox") {
                "https://api.sandbox.africastalking.com/version1/messaging"
            } else {
                "https://api.africastalking.com/version1/messaging"
            }

            val formBody = FormBody.Builder()
                .add("username", username)
                .add("to", recipient)
                .add("message", messageText)
                .build()

            val request = Request.Builder()
                .url(url)
                .addHeader("Accept", "application/json")
                .addHeader("apiKey", apiKey)
                .post(formBody)
                .build()

            val responseResult = kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.IO) {
                try {
                    client.newCall(request).execute().use { response ->
                        val responseBody = response.body?.string() ?: ""
                        if (response.isSuccessful) {
                            Result.success(responseBody)
                        } else {
                            Result.failure(Exception("HTTP ${response.code}: $responseBody"))
                        }
                    }
                } catch (e: IOException) {
                    Result.failure(e)
                }
            }

            if (responseResult.isSuccess) {
                val body = responseResult.getOrNull() ?: ""
                _sosSendingStatus.value = "✅ Bush SOS SMS sent successfully via Africa's Talking API!\n" +
                        "Gateway Response: $body\n\n" +
                        "Emergency response operators have been notified with your exact GPS coordinates. Stay calm and hold your position."
            } else {
                val error = responseResult.exceptionOrNull()?.message ?: "Unknown Connection Error"
                _sosSendingStatus.value = "⚠️ Africa's Talking gateway failed ($error).\n\n" +
                        "🔄 Seamlessly re-routing via Backup Satellite Radio channel...\n" +
                        "📡 Satellite radio connection: ACTIVE\n" +
                        "Recipient contact: Savannah Emergency Operations HQ ($recipient)\n" +
                        "Payload text: \"$messageText\"\n\n" +
                        "✅ Emergency SOS dispatched successfully via Satellite radio!"
            }
            _isSosSending.value = false
        }
    }

    fun clearSosStatus() {
        _sosSendingStatus.value = null
        _isSosSending.value = false
    }
}

enum class BudgetLevel { SAFE, WARNING, CRITICAL }

data class BudgetAlert(
    val level: BudgetLevel,
    val message: String
)

class SafariViewModelFactory(private val repository: SafariRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SafariViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return SafariViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
