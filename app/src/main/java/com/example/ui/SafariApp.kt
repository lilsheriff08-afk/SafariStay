package com.example.ui

import androidx.compose.animation.*
import androidx.compose.animation.core.spring
import coil.compose.AsyncImage
import coil.request.ImageRequest
import androidx.compose.foundation.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.R
import com.example.data.*
import com.example.viewmodel.SafariViewModel
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SafariApp(
    viewModel: SafariViewModel,
    modifier: Modifier = Modifier
) {
    val currentTab by viewModel.currentTab.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val stays by viewModel.filteredStays.collectAsStateWithLifecycle()
    val safaris by viewModel.filteredSafaris.collectAsStateWithLifecycle()
    val events by viewModel.filteredEvents.collectAsStateWithLifecycle()
    val bookings by viewModel.bookings.collectAsStateWithLifecycle()
    val vouchers by viewModel.vouchers.collectAsStateWithLifecycle()
    val favorites by viewModel.favorites.collectAsStateWithLifecycle()
    val uiEvent by viewModel.uiEvent.collectAsStateWithLifecycle()

    val minPriceFilter by viewModel.minPriceFilter.collectAsStateWithLifecycle()
    val maxPriceFilter by viewModel.maxPriceFilter.collectAsStateWithLifecycle()
    val selectedCurrency by viewModel.selectedCurrency.collectAsStateWithLifecycle()
    val selectedMonthFilter by viewModel.selectedMonthFilter.collectAsStateWithLifecycle()
    val selectedRegionFilter by viewModel.selectedRegionFilter.collectAsStateWithLifecycle()

    var isFilterExpanded by remember { mutableStateOf(false) }

    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    // Booking Dialog states
    var selectedStayForBooking by remember { mutableStateOf<StayItem?>(null) }
    var selectedSafariForBooking by remember { mutableStateOf<SafariItem?>(null) }

    // Toast or snackbar handling
    LaunchedEffect(uiEvent) {
        uiEvent?.let {
            scope.launch {
                snackbarHostState.showSnackbar(it)
            }
            viewModel.clearUiEvent()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    0.0f to Color.White,
                    0.499f to Color.White,
                    0.5f to Color(0xFFE3D2BF), // Elegant light-brown savanna/sandy color
                    1.0f to Color(0xFFE3D2BF)
                )
            )
    ) {
        Scaffold(
            containerColor = Color.Transparent,
            snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
            topBar = {
                Column(
                    modifier = Modifier
                        .background(Color.Transparent)
                        .statusBarsPadding()
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(
                            imageVector = Icons.Default.Landscape,
                            contentDescription = "SAVANNAH SUITES & SAFARIS",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier
                                .size(36.dp)
                                .padding(end = 8.dp)
                        )
                        Column {
                            Text(
                                text = "SAVANNAH SUITES & SAFARIS",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onBackground,
                                letterSpacing = 0.5.sp
                            )
                            Text(
                                text = "Premium Lodges & Custom Expeditions",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                            )
                        }
                    Spacer(modifier = Modifier.weight(1f))
                    // Quick stats pill
                    Surface(
                        color = MaterialTheme.colorScheme.secondaryContainer,
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.ConfirmationNumber,
                                contentDescription = "Active Vouchers",
                                modifier = Modifier.size(14.dp),
                                tint = MaterialTheme.colorScheme.onSecondaryContainer
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            val activeVoucherCount = vouchers.count { it.status == "Active" }
                            Text(
                                text = "$activeVoucherCount Vouchers",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSecondaryContainer
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Modern Search/Filter bar (only show in stays/safari tabs)
                if (currentTab == "stays" || currentTab == "safaris") {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .animateContentSize()
                            .testTag("search_filter_console"),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
                        ),
                        border = BorderStroke(
                            width = 1.dp,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f)
                        )
                    ) {
                        Column(
                            modifier = Modifier.padding(12.dp)
                        ) {
                            val hasActiveFilters = selectedMonthFilter != "All Months" || minPriceFilter > 0.0 || maxPriceFilter < 2000.0

                            // Row with search bar & filter toggle button
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                OutlinedTextField(
                                    value = searchQuery,
                                    onValueChange = { viewModel.updateSearchQuery(it) },
                                    placeholder = {
                                        Text(
                                            if (currentTab == "stays") "Where to? Lodge, beachfront..."
                                            else "Where to? Serengeti, Mara, safari..."
                                        )
                                    },
                                    leadingIcon = {
                                        Icon(
                                            imageVector = Icons.Default.Search,
                                            contentDescription = "Search",
                                            tint = MaterialTheme.colorScheme.primary
                                        )
                                    },
                                    trailingIcon = {
                                        if (searchQuery.isNotEmpty()) {
                                            IconButton(onClick = { viewModel.updateSearchQuery("") }) {
                                                Icon(
                                                    imageVector = Icons.Default.Clear,
                                                    contentDescription = "Clear search"
                                                )
                                            }
                                        }
                                    },
                                    singleLine = true,
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier
                                        .weight(1f)
                                        .testTag("search_input"),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedContainerColor = MaterialTheme.colorScheme.surface,
                                        unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                                        unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f),
                                    )
                                )

                                Spacer(modifier = Modifier.width(8.dp))

                                // Filter Badge Toggle Button
                                Box(modifier = Modifier.testTag("filter_toggle_box")) {
                                    IconButton(
                                        onClick = { isFilterExpanded = !isFilterExpanded },
                                        modifier = Modifier
                                            .size(52.dp)
                                            .background(
                                                color = if (isFilterExpanded || hasActiveFilters) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface,
                                                shape = RoundedCornerShape(12.dp)
                                            )
                                            .border(
                                                width = 1.dp,
                                                color = if (isFilterExpanded || hasActiveFilters) Color.Transparent else MaterialTheme.colorScheme.outline.copy(alpha = 0.2f),
                                                shape = RoundedCornerShape(12.dp)
                                            )
                                            .testTag("filter_toggle_btn")
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.FilterList,
                                            contentDescription = "Toggle Filters",
                                            tint = if (isFilterExpanded || hasActiveFilters) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface
                                        )
                                    }

                                    if (hasActiveFilters) {
                                        Surface(
                                            color = MaterialTheme.colorScheme.error,
                                            shape = RoundedCornerShape(8.dp),
                                            modifier = Modifier
                                                .align(Alignment.TopEnd)
                                                .offset(x = 2.dp, y = (-2).dp)
                                        ) {
                                            Box(
                                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                            ) {
                                                Text(
                                                    text = "•",
                                                    fontSize = 8.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = Color.White
                                                )
                                            }
                                        }
                                    }
                                }
                            }

                            // Range Slider
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 16.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Price: $${minPriceFilter.toInt()} - $${maxPriceFilter.toInt()}",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                // Currency Toggle Dropdown
                                // Box {
                                //     var expanded by remember { mutableStateOf(false) }
                                //     TextButton(onClick = { expanded = true }) {
                                //         Text(selectedCurrency)
                                //     }
                                //     DropdownMenu(
                                //         expanded = expanded,
                                //         onDismissRequest = { expanded = false }
                                //     ) {
                                //         listOf("USD", "KES", "TZS", "UGX").forEach { currency ->
                                //             DropdownMenuItem(
                                //                 text = { Text(currency) },
                                //                 onClick = {
                                //                     viewModel.updateCurrency(currency)
                                //                     expanded = false
                                //                 }
                                //             )
                                //         }
                                //     }
                                // }
                            }
                            RangeSlider(
                                value = minPriceFilter.toFloat()..maxPriceFilter.toFloat(),
                                onValueChange = { range ->
                                    viewModel.updatePriceFilter(range.start.toDouble(), range.endInclusive.toDouble())
                                },
                                valueRange = 0f..2000f,
                                steps = 20,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp)
                                    .testTag("price_range_slider")
                            )

                            // Horizontal list of active filters as quick chips (only when panel is collapsed)
                            if (!isFilterExpanded && hasActiveFilters) {
                                Spacer(modifier = Modifier.height(8.dp))
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .horizontalScroll(rememberScrollState()),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    if (selectedMonthFilter != "All Months") {
                                        FilterChip(
                                            selected = true,
                                            onClick = { viewModel.updateMonthFilter("All Months") },
                                            label = { Text("📅 $selectedMonthFilter") },
                                            trailingIcon = {
                                                Icon(
                                                    imageVector = Icons.Default.Close,
                                                    contentDescription = "Remove Date Filter",
                                                    modifier = Modifier.size(14.dp)
                                                )
                                            },
                                            colors = FilterChipDefaults.filterChipColors(
                                                selectedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                                                selectedLabelColor = MaterialTheme.colorScheme.onSecondaryContainer
                                            ),
                                            modifier = Modifier.padding(end = 4.dp)
                                        )
                                    }

                                    if (minPriceFilter > 0.0 || maxPriceFilter < 2000.0) {
                                        FilterChip(
                                            selected = true,
                                            onClick = { viewModel.updatePriceFilter(0.0, 2000.0) },
                                            label = {
                                                Text("💰 $${minPriceFilter.toInt()} - $${maxPriceFilter.toInt()}")
                                            },
                                            trailingIcon = {
                                                Icon(
                                                    imageVector = Icons.Default.Close,
                                                    contentDescription = "Remove Price Filter",
                                                    modifier = Modifier.size(14.dp)
                                                )
                                            },
                                            colors = FilterChipDefaults.filterChipColors(
                                                selectedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                                                selectedLabelColor = MaterialTheme.colorScheme.onSecondaryContainer
                                            ),
                                            modifier = Modifier.padding(end = 4.dp)
                                        )
                                    }

                                    TextButton(
                                        onClick = { viewModel.resetFilters() },
                                        contentPadding = PaddingValues(horizontal = 8.dp)
                                    ) {
                                        Text("Clear All", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                                    }
                                }
                            }

                            // Expanded Filters Section
                            AnimatedVisibility(
                                visible = isFilterExpanded,
                                enter = expandVertically() + fadeIn(),
                                exit = shrinkVertically() + fadeOut()
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(top = 12.dp)
                                ) {
                                    HorizontalDivider(
                                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f),
                                        modifier = Modifier.padding(bottom = 12.dp)
                                    )

                                    // Filter by Month / Date Selection
                                    Text(
                                        text = "When are you planning to travel?",
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.padding(bottom = 6.dp)
                                    )

                                    val monthsList = listOf("All Months", "August 2026", "September 2026", "October 2026", "December 2026")
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .horizontalScroll(rememberScrollState())
                                            .padding(bottom = 16.dp),
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        monthsList.forEach { month ->
                                            val isSelected = selectedMonthFilter == month
                                            Surface(
                                                color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface,
                                                contentColor = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface,
                                                shape = RoundedCornerShape(12.dp),
                                                border = BorderStroke(
                                                    width = 1.dp,
                                                    color = if (isSelected) Color.Transparent else MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
                                                ),
                                                modifier = Modifier
                                                    .clickable { viewModel.updateMonthFilter(month) }
                                                    .testTag("month_filter_$month")
                                            ) {
                                                Row(
                                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                                    verticalAlignment = Alignment.CenterVertically
                                                ) {
                                                    Icon(
                                                        imageVector = if (month == "All Months") Icons.Default.CalendarToday else Icons.Default.DateRange,
                                                        contentDescription = null,
                                                        modifier = Modifier.size(14.dp),
                                                        tint = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.primary
                                                    )
                                                    Spacer(modifier = Modifier.width(6.dp))
                                                    Text(
                                                        text = month,
                                                        fontSize = 12.sp,
                                                        fontWeight = FontWeight.SemiBold
                                                    )
                                                }
                                            }
                                        }
                                    }

                                    // Filter by Region (For Events)
                                    AnimatedVisibility(visible = currentTab == "events") {
                                        Column {
                                            Text(
                                                text = "Which region?",
                                                fontSize = 13.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = MaterialTheme.colorScheme.primary,
                                                modifier = Modifier.padding(top = 12.dp, bottom = 6.dp)
                                            )

                                            val regionsList = listOf("All Regions", "Kenya", "Tanzania")
                                            Row(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .horizontalScroll(rememberScrollState())
                                                    .padding(bottom = 16.dp),
                                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                                            ) {
                                                regionsList.forEach { region ->
                                                    val isSelected = selectedRegionFilter == region
                                                    Surface(
                                                        color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface,
                                                        contentColor = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface,
                                                        shape = RoundedCornerShape(12.dp),
                                                        border = BorderStroke(
                                                            width = 1.dp,
                                                            color = if (isSelected) Color.Transparent else MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
                                                        ),
                                                        modifier = Modifier
                                                            .clickable { viewModel.updateRegionFilter(region) }
                                                            .testTag("region_filter_$region")
                                                    ) {
                                                        Row(
                                                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                                            verticalAlignment = Alignment.CenterVertically
                                                        ) {
                                                            Icon(
                                                                imageVector = Icons.Default.Place,
                                                                contentDescription = null,
                                                                modifier = Modifier.size(14.dp),
                                                                tint = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.primary
                                                            )
                                                            Spacer(modifier = Modifier.width(6.dp))
                                                            Text(
                                                                text = region,
                                                                fontSize = 12.sp,
                                                                fontWeight = FontWeight.SemiBold
                                                            )
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }

                                    
                                    Spacer(modifier = Modifier.height(16.dp))

                                    // Reset & Apply Actions Row
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.End,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        TextButton(
                                            onClick = {
                                                viewModel.resetFilters()
                                            },
                                            modifier = Modifier.testTag("reset_filters_btn")
                                        ) {
                                            Text("Reset", fontWeight = FontWeight.Bold)
                                        }

                                        Spacer(modifier = Modifier.width(8.dp))

                                        Button(
                                            onClick = { isFilterExpanded = false },
                                            shape = RoundedCornerShape(10.dp),
                                            modifier = Modifier.testTag("apply_filters_btn")
                                        ) {
                                            Text("Apply", fontWeight = FontWeight.Bold)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        },
        bottomBar = {
            NavigationBar(
                modifier = Modifier.windowInsetsPadding(WindowInsets.navigationBars),
                containerColor = Color(0xFFE3D2BF).copy(alpha = 0.95f),
                tonalElevation = 8.dp
            ) {
                NavigationBarItem(
                    selected = currentTab == "stays",
                    onClick = { viewModel.setTab("stays") },
                    icon = { Icon(imageVector = Icons.Default.Home, contentDescription = "Stays") },
                    label = { Text("LODGES") },
                    modifier = Modifier.testTag("tab_stays")
                )
                NavigationBarItem(
                    selected = currentTab == "safaris",
                    onClick = { viewModel.setTab("safaris") },
                    icon = { Icon(imageVector = Icons.Default.Explore, contentDescription = "Safaris") },
                    label = { Text("SAFARIS") },
                    modifier = Modifier.testTag("tab_safaris")
                )
                NavigationBarItem(
                    selected = currentTab == "events",
                    onClick = { viewModel.setTab("events") },
                    icon = { Icon(imageVector = Icons.Default.Event, contentDescription = "Events") },
                    label = { Text("EVENTS") },
                    modifier = Modifier.testTag("tab_events")
                )
                NavigationBarItem(
                    selected = currentTab == "vouchers",
                    onClick = { viewModel.setTab("vouchers") },
                    icon = { Icon(imageVector = Icons.Default.CardMembership, contentDescription = "Vouchers") },
                    label = { Text("VOUCHERS") },
                    modifier = Modifier.testTag("tab_vouchers")
                )
                NavigationBarItem(
                    selected = currentTab == "bookings",
                    onClick = { viewModel.setTab("bookings") },
                    icon = { Icon(imageVector = Icons.Default.BookOnline, contentDescription = "Bookings") },
                    label = { Text("MY TRIPS") },
                    modifier = Modifier.testTag("tab_bookings")
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(Color.Transparent)
        ) {
            // Main content based on current tab
            AnimatedContent(
                targetState = currentTab,
                transitionSpec = {
                    fadeIn(animationSpec = spring()) togetherWith fadeOut(animationSpec = spring())
                },
                label = "TabTransition"
            ) { tab ->
                when (tab) {
                    "stays" -> StaysTab(
                        stays = stays,
                        favorites = favorites.filter { it.type == "STAY" }.map { it.itemId },
                        onBookClick = { selectedStayForBooking = it },
                        onFavoriteClick = { stay -> viewModel.toggleFavorite(stay.id, "STAY", stay.title) }
                    )
                    "safaris" -> SafarisTab(
                        safaris = safaris,
                        favorites = favorites.filter { it.type == "SAFARI" }.map { it.itemId },
                        onBookClick = { selectedSafariForBooking = it },
                        onFavoriteClick = { safari -> viewModel.toggleFavorite(safari.id, "SAFARI", safari.title) }
                    )
                    "events" -> EventsTab(
                        events = events
                    )
                    "vouchers" -> VouchersTab(
                        vouchers = vouchers,
                        onBuyVoucherSubmit = { title, desc, amount ->
                            viewModel.buyVoucher(title, desc, amount)
                        }
                    )
                    "bookings" -> MyTripsTab(
                        bookings = bookings,
                        onCancelClick = { viewModel.deleteBooking(it) },
                        getItinerary = { emptyList() }
                    )
                }
            }

            // Booking Modals
            selectedStayForBooking?.let { stay ->
                BookingDialog(
                    title = "Book Stay",
                    itemName = stay.title,
                    price = stay.pricePerNight,
                    priceLabel = "/ night",
                    vouchers = vouchers.filter { it.status == "Active" && it.remainingValue > 0 },
                    onDismiss = { selectedStayForBooking = null },
                    onConfirm = { startDate: Long?, endDate: Long?, voucherCode: String? ->
                        val formatter = SimpleDateFormat("MMM dd", Locale.US)
                        val dateRange = if (startDate != null && endDate != null) {
                            "${formatter.format(Date(startDate))} - ${formatter.format(Date(endDate))}, 2026"
                        } else {
                            "Dates not selected"
                        }
                        viewModel.bookStay(stay, dateRange, voucherCode)
                        selectedStayForBooking = null
                    }
                )
            }

            selectedSafariForBooking?.let { safari ->
                BookingDialog(
                    title = "Book Expedition",
                    itemName = safari.title,
                    price = safari.price,
                    priceLabel = " total",
                    vouchers = vouchers.filter { it.status == "Active" && it.remainingValue > 0 },
                    onDismiss = { selectedSafariForBooking = null },
                    onConfirm = { startDate: Long?, endDate: Long?, voucherCode: String? ->
                        val formatter = SimpleDateFormat("MMM dd", Locale.US)
                        val dateRange = if (startDate != null && endDate != null) {
                            "${formatter.format(Date(startDate))} - ${formatter.format(Date(endDate))}, 2026"
                        } else {
                            "Dates not selected"
                        }
                        viewModel.bookSafari(safari, dateRange, voucherCode)
                        selectedSafariForBooking = null
                    }
                )
            }
        }
    }
}

@Composable
fun StarRating(
    rating: Double,
    modifier: Modifier = Modifier,
    maxStars: Int = 5
) {
    Row(modifier = modifier) {
        for (i in 1..maxStars) {
            val icon = when {
                i <= rating -> Icons.Default.Star
                i - 0.5 <= rating -> Icons.Default.StarHalf
                else -> Icons.Default.StarBorder
            }
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = Color(0xFFF39C12),
                modifier = Modifier.size(16.dp)
            )
        }
    }
}

// ---------------- STAYS TAB ----------------
@Composable
fun StaysTab(
    stays: List<StayItem>,
    favorites: List<String>,
    onBookClick: (StayItem) -> Unit,
    onFavoriteClick: (StayItem) -> Unit
) {
    if (stays.isEmpty()) {
        EmptyStateView(
            message = "No luxury lodges found. Try another search!",
            icon = Icons.Default.Search
        )
    } else {
        BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
            val columns = if (maxWidth > 600.dp) GridCells.Fixed(2) else GridCells.Fixed(1)
            LazyVerticalGrid(
                columns = columns,
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                // Hero banner image at the top of the tab
                item(span = { GridItemSpan(maxLineSpan) }) {
                    Card(
                        shape = RoundedCornerShape(24.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(180.dp)
                    ) {
                        Box(modifier = Modifier.fillMaxSize()) {
                            AsyncImage(
                                model = ImageRequest.Builder(LocalContext.current)
                                    .data("https://images.unsplash.com/photo-1516426122078-c23e76319801?auto=format&fit=crop&w=1200&q=80")
                                    .crossfade(true)
                                    .build(),
                                placeholder = painterResource(id = R.drawable.img_safari_hero),
                                error = painterResource(id = R.drawable.img_safari_hero),
                                contentDescription = "Safari Hero Banner",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(
                                        Brush.verticalGradient(
                                            colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.7f))
                                        )
                                    )
                            )
                            Column(
                                modifier = Modifier
                                    .align(Alignment.BottomStart)
                                    .padding(16.dp)
                            ) {
                                Text(
                                    text = "Exclusive Savanna Stays",
                                    color = Color.White,
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "Discover Airbnb experiences in East Africa",
                                    color = Color.White.copy(alpha = 0.8f),
                                    fontSize = 12.sp
                                )
                            }
                        }
                    }
                }

                items(stays) { stay ->
                    StayCard(
                        stay = stay,
                        isFavorite = favorites.contains(stay.id),
                        onBookClick = { onBookClick(stay) },
                        onFavoriteClick = { onFavoriteClick(stay) }
                    )
                }
            }
        }
    }
}

@Composable
fun StayCard(
    stay: StayItem,
    isFavorite: Boolean,
    onBookClick: () -> Unit,
    onFavoriteClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.15f)),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("stay_card_${stay.id}")
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
            ) {
                ImageCarousel(
                    imageUrls = stay.imageUrls,
                    modifier = Modifier.fillMaxSize()
                )

                // Favorite button
                IconButton(
                    onClick = onFavoriteClick,
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(8.dp)
                        .background(Color.White.copy(alpha = 0.5f), shape = RoundedCornerShape(20.dp))
                        .size(36.dp)
                        .testTag("favorite_stay_${stay.id}")
                ) {
                    Icon(
                        imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = "Favorite",
                        tint = if (isFavorite) Color.Red else Color.Black
                    )
                }

                // Rating tag
                Surface(
                    color = Color.Black.copy(alpha = 0.65f),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier
                        .padding(12.dp)
                        .align(Alignment.TopEnd)
                ) {
                    StarRating(
                        rating = stay.rating,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }

                // Country Tag
                Surface(
                    color = MaterialTheme.colorScheme.primary,
                    shape = RoundedCornerShape(6.dp),
                    modifier = Modifier
                        .padding(12.dp)
                        .align(Alignment.BottomStart)
                ) {
                    Text(
                        text = stay.country.uppercase(),
                        color = MaterialTheme.colorScheme.onPrimary,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = stay.title,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(vertical = 2.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Place,
                        contentDescription = "Location",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = stay.location,
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = stay.description,
                    fontSize = 13.sp,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis,
                    lineHeight = 18.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.85f)
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Amenities
                Row(
                    modifier = Modifier.horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    stay.amenities.forEach { amenity ->
                        Surface(
                            color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.4f),
                            shape = RoundedCornerShape(8.dp),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.08f))
                        ) {
                            Text(
                                text = amenity,
                                fontSize = 10.sp,
                                color = MaterialTheme.colorScheme.onSecondaryContainer,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Divider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.08f))

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column {
                        Text(
                            text = "From",
                            fontSize = 10.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                        )
                        Row(verticalAlignment = Alignment.Bottom) {
                            Text(
                                text = "$${stay.pricePerNight.toInt()}",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                text = " / night",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                                modifier = Modifier.padding(bottom = 2.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.weight(1f))
                    Button(
                        onClick = onBookClick,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.testTag("book_stay_${stay.id}")
                    ) {
                        Icon(
                            imageVector = Icons.Default.BookOnline,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Reserve", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}


// ---------------- SAFARIS TAB ----------------
@Composable
fun SafarisTab(
    safaris: List<SafariItem>,
    favorites: List<String>,
    onBookClick: (SafariItem) -> Unit,
    onFavoriteClick: (SafariItem) -> Unit
) {
    if (safaris.isEmpty()) {
        EmptyStateView(
            message = "No matching safaris found. Try another search!",
            icon = Icons.Default.Search
        )
    } else {
        BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
            val columns = if (maxWidth > 600.dp) GridCells.Fixed(2) else GridCells.Fixed(1)
            LazyVerticalGrid(
                columns = columns,
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                item(span = { GridItemSpan(maxLineSpan) }) {
                    Card(
                        shape = RoundedCornerShape(24.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(180.dp)
                    ) {
                        Box(modifier = Modifier.fillMaxSize()) {
                            AsyncImage(
                                model = ImageRequest.Builder(LocalContext.current)
                                    .data("https://images.unsplash.com/photo-1513836279014-a89f7a76ae86?auto=format&fit=crop&w=1200&q=80")
                                    .crossfade(true)
                                    .build(),
                                placeholder = painterResource(id = R.drawable.img_safari_balloon),
                                error = painterResource(id = R.drawable.img_safari_balloon),
                                contentDescription = "Safari Experiences Banner",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(
                                        Brush.verticalGradient(
                                            colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.7f))
                                        )
                                    )
                            )
                            Column(
                                modifier = Modifier
                                    .align(Alignment.BottomStart)
                                    .padding(16.dp)
                            ) {
                                Text(
                                    text = "All-Inclusive Expeditions",
                                    color = Color.White,
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "Authentic wildlife packages & certified local guides",
                                    color = Color.White.copy(alpha = 0.8f),
                                    fontSize = 12.sp
                                )
                            }
                        }
                    }
                }

                items(safaris) { safari ->
                    SafariCard(
                        safari = safari,
                        isFavorite = favorites.contains(safari.id),
                        onBookClick = { onBookClick(safari) },
                        onFavoriteClick = { onFavoriteClick(safari) }
                    )
                }

                // Experiences subsection
                item(span = { GridItemSpan(maxLineSpan) }) {
                    Text(
                        text = "Local Experiences & Day Activities",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(top = 16.dp, bottom = 8.dp),
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }

                items(SafariCatalog.experiences) { exp ->
                    ExperienceCard(experience = exp)
                }
            }
        }
    }
}

@Composable
fun SafariCard(
    safari: SafariItem,
    isFavorite: Boolean,
    onBookClick: () -> Unit,
    onFavoriteClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.15f)),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("safari_card_${safari.id}")
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
            ) {
                ImageCarousel(
                    imageUrls = safari.imageUrls,
                    modifier = Modifier.fillMaxSize()
                )

                // Favorite button
                IconButton(
                    onClick = onFavoriteClick,
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(8.dp)
                        .background(Color.White.copy(alpha = 0.5f), shape = RoundedCornerShape(20.dp))
                        .size(36.dp)
                        .testTag("favorite_safari_${safari.id}")
                ) {
                    Icon(
                        imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = "Favorite",
                        tint = if (isFavorite) Color.Red else Color.Black
                    )
                }

                // Rating tag
                Surface(
                    color = Color.Black.copy(alpha = 0.65f),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier
                        .padding(12.dp)
                        .align(Alignment.TopEnd)
                ) {
                    StarRating(
                        rating = safari.rating,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }

                // Duration tag
                Surface(
                    color = MaterialTheme.colorScheme.secondary,
                    shape = RoundedCornerShape(6.dp),
                    modifier = Modifier
                        .padding(12.dp)
                        .align(Alignment.BottomStart)
                ) {
                    Text(
                        text = "${safari.durationDays} DAYS",
                        color = MaterialTheme.colorScheme.onSecondary,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = safari.title,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "${safari.park}, ${safari.country}",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.SemiBold
                )

                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = safari.description,
                    fontSize = 13.sp,
                    lineHeight = 18.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.85f)
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Inclusions
                Text(
                    text = "INCLUSIONS:",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                    modifier = Modifier.padding(bottom = 4.dp)
                )
                Row(
                    modifier = Modifier.horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    safari.inclusions.forEach { inclusion ->
                        Surface(
                            color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.25f),
                            shape = RoundedCornerShape(8.dp),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.1f))
                        ) {
                            Text(
                                text = inclusion,
                                fontSize = 10.sp,
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
                Divider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.08f))
                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column {
                        Text(
                            text = "All-Inclusive Package",
                            fontSize = 10.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                        )
                        Text(
                            text = "$${safari.price.toInt()}",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                    Spacer(modifier = Modifier.weight(1f))
                    Button(
                        onClick = onBookClick,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.testTag("book_safari_${safari.id}")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Explore,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Book Trip", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun ExperienceCard(
    experience: ExperienceItem
) {
    val imageRes = when (experience.imageResName) {
        "img_safari_balloon" -> R.drawable.img_safari_balloon
        "img_luxury_lodge" -> R.drawable.img_luxury_lodge
        "img_safari_hero" -> R.drawable.img_safari_hero
        else -> R.drawable.img_safari_balloon
    }

    val realImageUrl = when (experience.id) {
        "exp_mara_balloon" -> "https://images.unsplash.com/photo-1513836279014-a89f7a76ae86?auto=format&fit=crop&w=400&q=80"
        "exp_maasai_culture" -> "https://images.unsplash.com/photo-1489440543286-a69330151c0b?auto=format&fit=crop&w=400&q=80"
        "exp_zanzibar_dhow" -> "https://images.unsplash.com/photo-1534008897995-27a23e859048?auto=format&fit=crop&w=400&q=80"
        else -> "https://images.unsplash.com/photo-1513836279014-a89f7a76ae86?auto=format&fit=crop&w=400&q=80"
    }

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(realImageUrl)
                    .crossfade(true)
                    .build(),
                placeholder = painterResource(id = imageRes),
                error = painterResource(id = imageRes),
                contentDescription = experience.title,
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(12.dp)),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = experience.title,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "${experience.location} • ${experience.durationHours} hrs",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = experience.description,
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    lineHeight = 16.sp
                )
                Spacer(modifier = Modifier.height(6.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "$${experience.price.toInt()}",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = "Rating",
                            tint = Color(0xFFF39C12),
                            modifier = Modifier.size(12.dp)
                        )
                        Spacer(modifier = Modifier.width(2.dp))
                        Text(
                            text = experience.rating.toString(),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}


// ---------------- EVENTS TAB ----------------
@Composable
fun EventsTab(
    events: List<EventItem>
) {
    if (events.isEmpty()) {
        EmptyStateView(
            message = "No matching events found. Try adjusting filters!",
            icon = Icons.Default.Event
        )
    } else {
        BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
            val columns = if (maxWidth > 600.dp) GridCells.Fixed(2) else GridCells.Fixed(1)
            LazyVerticalGrid(
                columns = columns,
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                item(span = { GridItemSpan(maxLineSpan) }) {
                    Card(
                        shape = RoundedCornerShape(24.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(180.dp)
                    ) {
                        Box(modifier = Modifier.fillMaxSize()) {
                            AsyncImage(
                                model = ImageRequest.Builder(LocalContext.current)
                                    .data("https://images.unsplash.com/photo-1514525253161-7a46d19cd819?auto=format&fit=crop&w=1200&q=80")
                                    .crossfade(true)
                                    .build(),
                                placeholder = painterResource(id = R.drawable.img_safari_hero),
                                error = painterResource(id = R.drawable.img_safari_hero),
                                contentDescription = "Events Banner",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(
                                        Brush.verticalGradient(
                                            colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.7f))
                                        )
                                    )
                            )
                            Column(
                                modifier = Modifier
                                    .align(Alignment.BottomStart)
                                    .padding(16.dp)
                            ) {
                                Text(
                                    text = "Local Festivals & Events",
                                    color = Color.White,
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "Discover vibrant culture, music, and seasonal migrations",
                                    color = Color.White.copy(alpha = 0.8f),
                                    fontSize = 12.sp
                                )
                            }
                        }
                    }
                }

                items(events) { event ->
                    EventCard(event = event)
                }
            }
        }
    }
}

@Composable
fun EventCard(event: EventItem) {
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { /* Detail view for event not specified */ },
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
            ) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(event.imageUrl)
                        .crossfade(true)
                        .build(),
                    placeholder = painterResource(id = R.drawable.img_safari_hero),
                    error = painterResource(id = R.drawable.img_safari_hero),
                    contentDescription = event.title,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )

                // Date Label
                Surface(
                    color = MaterialTheme.colorScheme.primaryContainer,
                    shape = RoundedCornerShape(topStart = 16.dp, bottomEnd = 16.dp),
                    modifier = Modifier.align(Alignment.TopStart)
                ) {
                    Text(
                        text = event.date,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = event.title,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(6.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Place,
                        contentDescription = "Location",
                        modifier = Modifier.size(14.dp),
                        tint = MaterialTheme.colorScheme.secondary
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = event.location,
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.secondary
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = event.description,
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    lineHeight = 18.sp,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ImageCarousel(
    imageUrls: List<String>,
    modifier: Modifier = Modifier
) {
    val pagerState = rememberPagerState(pageCount = { imageUrls.size })
    Box(modifier = modifier) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize()
        ) { page ->
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(imageUrls[page])
                    .crossfade(true)
                    .build(),
                contentDescription = "Image $page",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        }

        // Indicator
        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(8.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            imageUrls.forEachIndexed { index, _ ->
                Surface(
                    shape = RoundedCornerShape(50),
                    color = if (pagerState.currentPage == index) Color.White else Color.White.copy(alpha = 0.5f),
                    modifier = Modifier.size(8.dp)
                ) {}
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DateRangePickerDialog(
    onDismiss: () -> Unit,
    onConfirm: (startDate: Long?, endDate: Long?) -> Unit
) {
    val state = rememberDateRangePickerState()
    DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = {
                onConfirm(state.selectedStartDateMillis, state.selectedEndDateMillis)
            }) {
                Text("Confirm")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    ) {
        DateRangePicker(state = state)
    }
}

// ---------------- VOUCHERS TAB ----------------
@Composable
fun VouchersTab(
    vouchers: List<VoucherEntity>,
    onBuyVoucherSubmit: (String, String, Double) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var amountString by remember { mutableStateOf("") }

    var isBuySectionVisible by remember { mutableStateOf(false) }

    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        // Hero / Promo Card
        item {
            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        text = "Safari Stay Vouchers",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Gift or load customizable travel vouchers. Use them seamlessly to book Airbnb lodgings or thrilling sunrise safaris in East Africa.",
                        fontSize = 13.sp,
                        lineHeight = 18.sp,
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = { isBuySectionVisible = !isBuySectionVisible },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.testTag("toggle_buy_voucher")
                    ) {
                        Icon(
                            imageVector = if (isBuySectionVisible) Icons.Default.Close else Icons.Default.Add,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (isBuySectionVisible) "Cancel Custom Voucher" else "Generate Custom Voucher",
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        // Custom buy voucher form
        if (isBuySectionVisible) {
            item {
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.15f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Create Customizable Voucher",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(12.dp))

                        OutlinedTextField(
                            value = title,
                            onValueChange = { title = it },
                            label = { Text("Voucher Title (e.g. Serengeti Getaway)") },
                            singleLine = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("voucher_title_input")
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        OutlinedTextField(
                            value = description,
                            onValueChange = { description = it },
                            label = { Text("Short Description (e.g. Valid for hot air balloon)") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        OutlinedTextField(
                            value = amountString,
                            onValueChange = { amountString = it },
                            label = { Text("Preloaded Amount ($)") },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("voucher_amount_input")
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Button(
                            onClick = {
                                val amt = amountString.toDoubleOrNull() ?: 0.0
                                if (title.isNotBlank() && amt > 0) {
                                    onBuyVoucherSubmit(title, description, amt)
                                    // Reset fields
                                    title = ""
                                    description = ""
                                    amountString = ""
                                    isBuySectionVisible = false
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("submit_buy_voucher"),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("Purchase & Activate", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        item {
            Text(
                text = "Your Active Vouchers",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
        }

        if (vouchers.isEmpty()) {
            item {
                EmptyStateView(
                    message = "No vouchers active. Generate one above to receive a unique booking code!",
                    icon = Icons.Default.CardMembership
                )
            }
        } else {
            items(vouchers) { voucher ->
                VoucherCard(voucher = voucher)
            }
        }
    }
}

@Composable
fun VoucherCard(
    voucher: VoucherEntity
) {
    val isActive = voucher.status == "Active"

    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isActive) MaterialTheme.colorScheme.surface else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        ),
        border = BorderStroke(
            1.dp,
            if (isActive) MaterialTheme.colorScheme.primary.copy(alpha = 0.15f) else MaterialTheme.colorScheme.outline.copy(alpha = 0.1f)
        ),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("voucher_card_${voucher.code}")
    ) {
        Column(
            modifier = Modifier
                .background(
                    Brush.horizontalGradient(
                        colors = if (isActive) listOf(
                            MaterialTheme.colorScheme.surface,
                            MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.12f)
                        ) else listOf(
                            MaterialTheme.colorScheme.surfaceVariant,
                            MaterialTheme.colorScheme.surfaceVariant
                        )
                    )
                )
                .padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Surface(
                    color = if (isActive) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline,
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.ConfirmationNumber,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(12.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (isActive) "ACTIVE" else "REDEEMED",
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
                Spacer(modifier = Modifier.weight(1f))
                Text(
                    text = "Exp: ${voucher.expiryDate}",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = voucher.title,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            if (voucher.description.isNotBlank()) {
                Text(
                    text = voucher.description,
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    lineHeight = 16.sp,
                    modifier = Modifier.padding(top = 2.dp)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Dashed coupon divider
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                repeat(18) {
                    Box(
                        modifier = Modifier
                            .width(6.dp)
                            .height(1.dp)
                            .background(MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column {
                    Text(
                        text = "VOUCHER CODE",
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                    )
                    SelectionContainer {
                        Text(
                            text = voucher.code,
                            fontSize = 14.sp,
                            fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            color = if (isActive) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                Spacer(modifier = Modifier.weight(1f))
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "BALANCE",
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                    )
                    Text(
                        text = "$${String.format("%.2f", voucher.remainingValue)}",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isActive) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    if (voucher.originalValue != voucher.remainingValue) {
                        Text(
                            text = "Original: $${voucher.originalValue.toInt()}",
                            fontSize = 10.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                        )
                    }
                }
            }
        }
    }
}


@Composable
fun ItineraryView(itinerary: List<ItineraryItem>) {
    Column(modifier = Modifier.padding(top = 8.dp)) {
        Text("Travel Itinerary", fontWeight = FontWeight.Bold, fontSize = 14.sp)
        Spacer(modifier = Modifier.height(8.dp))
        itinerary.forEach { item ->
            Row(modifier = Modifier.padding(vertical = 4.dp)) {
                Text("${item.day}: ", fontWeight = FontWeight.Bold)
                Column {
                    Text(item.activity, fontWeight = FontWeight.Medium)
                    Text(item.description, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }
    }
}

// ---------------- MY TRIPS TAB ----------------
@Composable
fun MyTripsTab(
    bookings: List<BookingEntity>,
    onCancelClick: (BookingEntity) -> Unit,
    getItinerary: (BookingEntity) -> List<ItineraryItem>
) {
    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        item {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.BookOnline,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "My Upcoming Trips",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }
        }

        if (bookings.isEmpty()) {
            item {
                EmptyStateView(
                    message = "No current bookings found. Start exploring luxury lodges or all-inclusive safaris to build your dream East African journey!",
                    icon = Icons.Default.BookOnline
                )
            }
        } else {
            items(bookings) { booking ->
                BookingCard(
                    booking = booking, 
                    onCancelClick = { onCancelClick(booking) },
                    itinerary = getItinerary(booking)
                )
            }
        }
    }
}

@Composable
fun BookingCard(
    booking: BookingEntity,
    onCancelClick: () -> Unit,
    itinerary: List<ItineraryItem> = emptyList()
) {
    var showItinerary by remember { mutableStateOf(false) }
    val imageRes = when (booking.imageResName) {
        "img_luxury_lodge" -> R.drawable.img_luxury_lodge
        "img_safari_hero" -> R.drawable.img_safari_hero
        "img_safari_balloon" -> R.drawable.img_safari_balloon
        else -> R.drawable.img_luxury_lodge
    }

    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.15f)),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("booking_card_${booking.id}")
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth()
            ) {
            Image(
                painter = painterResource(id = imageRes),
                contentDescription = booking.title,
                modifier = Modifier
                    .size(90.dp)
                    .clip(RoundedCornerShape(12.dp)),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Surface(
                    color = if (booking.type == "STAY") MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.secondaryContainer,
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        text = booking.type,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (booking.type == "STAY") MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSecondaryContainer,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = booking.title,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Text(
                    text = booking.location,
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(4.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.CalendarToday,
                        contentDescription = "Dates",
                        tint = MaterialTheme.colorScheme.outline,
                        modifier = Modifier.size(10.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = booking.dateRange,
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontWeight = FontWeight.Medium
                    )
                }

                if (!booking.voucherCodeUsed.isNullOrEmpty()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.ConfirmationNumber,
                            contentDescription = "Voucher",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(10.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Voucher Discount applied",
                            fontSize = 10.sp,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "$${booking.price.toInt()}",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    
                    Surface(
                        color = MaterialTheme.colorScheme.tertiaryContainer,
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Text(
                            text = booking.status,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onTertiaryContainer,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                    
                    Spacer(modifier = Modifier.width(8.dp))
                    
                    IconButton(
                        onClick = onCancelClick,
                        modifier = Modifier
                            .size(32.dp)
                            .testTag("cancel_booking_${booking.id}")
                    ) {
                        Icon(
                            imageVector = Icons.Default.DeleteOutline,
                            contentDescription = "Cancel booking",
                            tint = MaterialTheme.colorScheme.error,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }
    }
}


// ---------------- BOOKING DIALOG ----------------
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookingDialog(
    title: String,
    itemName: String,
    price: Double,
    priceLabel: String,
    vouchers: List<VoucherEntity>,
    onDismiss: () -> Unit,
    onConfirm: (startDate: Long?, endDate: Long?, voucherCode: String?) -> Unit
) {
    var showDatePicker by remember { mutableStateOf(false) }
    var selectedDates by remember { mutableStateOf<Pair<Long?, Long?>>(null to null) }
    var selectedVoucherCode by remember { mutableStateOf("") }
    var isDropdownExpanded by remember { mutableStateOf(false) }

    if (showDatePicker) {
        DateRangePickerDialog(
            onDismiss = { showDatePicker = false },
            onConfirm = { start, end ->
                selectedDates = start to end
                showDatePicker = false
            }
        )
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = title,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Confirm reservation details for:",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                )
                Text(
                    text = itemName,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                OutlinedButton(
                    onClick = { showDatePicker = true },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(text = if (selectedDates.first == null) "Select Dates" else "Dates Selected")
                }

                // Optional voucher select
                if (vouchers.isNotEmpty()) {
                    Column {
                        Text(
                            text = "Apply Safari Voucher (Optional)",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Box {
                            OutlinedButton(
                                onClick = { isDropdownExpanded = true },
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("voucher_select_dropdown")
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(imageVector = Icons.Default.ConfirmationNumber, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = if (selectedVoucherCode.isEmpty()) "Select active voucher" else selectedVoucherCode,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    Spacer(modifier = Modifier.weight(1f))
                                    Icon(imageVector = Icons.Default.ArrowDropDown, contentDescription = null)
                                }
                            }
                            DropdownMenu(
                                expanded = isDropdownExpanded,
                                onDismissRequest = { isDropdownExpanded = false }
                            ) {
                                DropdownMenuItem(
                                    text = { Text("None (Pay regular price)") },
                                    onClick = {
                                        selectedVoucherCode = ""
                                        isDropdownExpanded = false
                                    }
                                )
                                vouchers.forEach { v ->
                                    DropdownMenuItem(
                                        text = { Text("${v.code} (Bal: $${v.remainingValue.toInt()})") },
                                        onClick = {
                                            selectedVoucherCode = v.code
                                            isDropdownExpanded = false
                                        }
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))
                Divider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.1f))

                // Price display
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Base Price:",
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    Text(
                        text = "$${price.toInt()}$priceLabel",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                if (selectedVoucherCode.isNotEmpty()) {
                    val matchingVoucher = vouchers.firstOrNull { it.code == selectedVoucherCode }
                    if (matchingVoucher != null) {
                        val discount = minOf(price, matchingVoucher.remainingValue)
                        val finalPrice = maxOf(0.0, price - discount)
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "Voucher Discount:",
                                fontSize = 13.sp,
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.weight(1f))
                            Text(
                                text = "-$${discount.toInt()}",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "Total Out of Pocket:",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.weight(1f))
                            Text(
                                text = "$${finalPrice.toInt()}",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onConfirm(selectedDates.first, selectedDates.second, selectedVoucherCode.ifEmpty { null })
                },
                modifier = Modifier.testTag("dialog_confirm_booking")
            ) {
                Text("Confirm Book", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

// ---------------- EMPTY STATE ----------------
@Composable
fun EmptyStateView(
    message: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
            modifier = Modifier.size(64.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = message,
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
            textAlign = TextAlign.Center,
            lineHeight = 20.sp
        )
    }
}
}
}
