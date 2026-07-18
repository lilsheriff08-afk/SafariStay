package com.example.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class SafariViewModel(private val repository: SafariRepository) : ViewModel() {

    init {
        // Prepopulate the database with beautiful default data on startup
        viewModelScope.launch {
            repository.prepopulateIfEmpty()
        }
    }

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

    // Price range filters
    private val _maxPriceFilter = MutableStateFlow(2000.0)
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
}

class SafariViewModelFactory(private val repository: SafariRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SafariViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return SafariViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
