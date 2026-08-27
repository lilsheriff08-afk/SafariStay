package com.example.ui

import android.text.format.DateFormat
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.BookingEntity
import com.example.viewmodel.SafariViewModel
import java.util.Date

@Composable
fun MyStaysDashboard(
    viewModel: SafariViewModel,
    onCancelClick: (BookingEntity) -> Unit,
    onRateClick: (BookingEntity) -> Unit,
    onShowQrClick: (BookingEntity) -> Unit,
    onChatClick: (BookingEntity) -> Unit,
    onGroupHubClick: (BookingEntity) -> Unit,
    onExploreClick: () -> Unit,
    onAiPlannerClick: ((String?) -> Unit)? = null
) {
    val bookings by viewModel.bookings.collectAsStateWithLifecycle()
    val localBookings by viewModel.localBookings.collectAsStateWithLifecycle()
    val isSyncing by viewModel.isSyncing.collectAsStateWithLifecycle()
    val lastSyncTime by viewModel.lastStaySyncTime.collectAsStateWithLifecycle()

    var selectedFilterTab by remember { mutableIntStateOf(0) } // 0: All, 1: Upcoming, 2: Past

    val currentTime = remember { System.currentTimeMillis() }

    val upcomingBookings = remember(bookings, currentTime) {
        bookings.filter { b ->
            val isPast = (b.startDateTimestamp > 0 && b.startDateTimestamp + (2L * 24 * 60 * 60 * 1000) < currentTime) ||
                    b.status == "Completed" || b.status == "Checked In & Settled"
            !isPast
        }
    }

    val pastBookings = remember(bookings, currentTime) {
        bookings.filter { b ->
            val isPast = (b.startDateTimestamp > 0 && b.startDateTimestamp + (2L * 24 * 60 * 60 * 1000) < currentTime) ||
                    b.status == "Completed" || b.status == "Checked In & Settled"
            isPast
        }
    }

    val filteredBookings = when (selectedFilterTab) {
        1 -> upcomingBookings
        2 -> pastBookings
        else -> bookings
    }

    val totalInvestment = bookings.sumOf { it.price }

    // Infinite rotation for sync icon
    val infiniteTransition = rememberInfiniteTransition(label = "SyncRotation")
    val rotationAngle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "Rotation"
    )

    Column(modifier = Modifier.fillMaxWidth()) {
        // --- GEMINI AI PLANNER BANNER ---
        ElevatedCard(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp)
                .testTag("gemini_ai_planner_banner"),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.elevatedCardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer
            )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            Icons.Default.AutoAwesome,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(18.dp)
                        )
                        Text(
                            text = "Gemini AI Safari Planner",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = "Generate a custom 3-day expedition itinerary tailored to your lodge & passions in a sidebar.",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                    )
                }
                Spacer(Modifier.width(8.dp))
                Button(
                    onClick = { onAiPlannerClick?.invoke(null) },
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Plan AI ✨", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        // --- API SYNC BAR ---
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.15f)),
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp)
                .testTag("safari_sync_api_card")
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Surface(
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f),
                        modifier = Modifier.size(36.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.CloudSync,
                                contentDescription = "Safari Stay Sync API",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "Safari Stay Sync API",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        val syncText = if (lastSyncTime != null) {
                            "Last synced: ${DateFormat.format("hh:mm a", Date(lastSyncTime!!))}"
                        } else {
                            "Sync endpoint: api.safaristay.app"
                        }
                        Text(
                            text = syncText,
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                        )
                    }
                }

                Button(
                    onClick = { viewModel.syncBookingsWithServer() },
                    enabled = !isSyncing,
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.testTag("btn_sync_stay_api")
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "Sync Now",
                        modifier = Modifier
                            .size(16.dp)
                            .rotate(if (isSyncing) rotationAngle else 0f)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (isSyncing) "Syncing..." else "Sync API",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        // --- DASHBOARD SUMMARY STATS ---
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            MetricStatChip(
                label = "Total Stays",
                value = "${bookings.size}",
                icon = Icons.Default.Bed,
                modifier = Modifier.weight(1f)
            )
            MetricStatChip(
                label = "Upcoming",
                value = "${upcomingBookings.size}",
                icon = Icons.Default.CalendarMonth,
                modifier = Modifier.weight(1f)
            )
            MetricStatChip(
                label = "Past Stays",
                value = "${pastBookings.size}",
                icon = Icons.Default.History,
                modifier = Modifier.weight(1f)
            )
            MetricStatChip(
                label = "Spent",
                value = "$${totalInvestment.toInt()}",
                icon = Icons.Default.Payments,
                modifier = Modifier.weight(1.2f)
            )
        }

        // --- ROOM DATABASE LOCAL BOOKINGS CACHE BANNER ---
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.4f)),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.secondary.copy(alpha = 0.2f)),
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp)
                .testTag("room_local_bookings_card")
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Storage,
                            contentDescription = "Room Local Database",
                            tint = MaterialTheme.colorScheme.secondary,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(
                            text = "Room DB Offline Cache (${localBookings.size})",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                    }
                    TextButton(
                        onClick = {
                            val ref = "REF-" + (1000..9999).random()
                            viewModel.addLocalBooking(
                                bookingReference = ref,
                                lodgeName = "Serengeti Mara Safari Lodge",
                                checkInDate = "2026-09-10",
                                checkOutDate = "2026-09-15",
                                guestName = "Valued Explorer"
                            )
                        },
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(14.dp))
                        Spacer(Modifier.width(4.dp))
                        Text("+ Cache Stay", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }

                if (localBookings.isEmpty()) {
                    Text(
                        text = "No offline Room bookings cached yet. Tap '+ Cache Stay' to persist a record via BookingRepository.",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.7f),
                        modifier = Modifier.padding(top = 4.dp)
                    )
                } else {
                    Spacer(Modifier.height(8.dp))
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        localBookings.take(3).forEach { lb ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.8f))
                                    .padding(horizontal = 10.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = lb.lodgeName,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = "Ref: ${lb.bookingReference} | ${lb.checkInDate} → ${lb.checkOutDate} | Guest: ${lb.guestName}",
                                        fontSize = 10.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                                IconButton(
                                    onClick = { viewModel.deleteLocalBookingByReference(lb.bookingReference) },
                                    modifier = Modifier.size(24.dp)
                                ) {
                                    Icon(
                                        Icons.Default.Close,
                                        contentDescription = "Delete Local Booking",
                                        tint = MaterialTheme.colorScheme.error,
                                        modifier = Modifier.size(14.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // --- FILTER SEGMENTED TABS ---
        TabRow(
            selectedTabIndex = selectedFilterTab,
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.primary,
            indicator = { tabPositions ->
                TabRowDefaults.SecondaryIndicator(
                    Modifier.tabIndicatorOffset(tabPositions[selectedFilterTab]),
                    color = MaterialTheme.colorScheme.primary
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp)
                .clip(RoundedCornerShape(12.dp))
        ) {
            Tab(
                selected = selectedFilterTab == 0,
                onClick = { selectedFilterTab = 0 },
                text = { Text("All Stays (${bookings.size})", fontSize = 12.sp, fontWeight = FontWeight.Bold) },
                modifier = Modifier.testTag("tab_all_stays")
            )
            Tab(
                selected = selectedFilterTab == 1,
                onClick = { selectedFilterTab = 1 },
                text = { Text("Upcoming (${upcomingBookings.size})", fontSize = 12.sp, fontWeight = FontWeight.Bold) },
                modifier = Modifier.testTag("tab_upcoming_stays")
            )
            Tab(
                selected = selectedFilterTab == 2,
                onClick = { selectedFilterTab = 2 },
                text = { Text("Past (${pastBookings.size})", fontSize = 12.sp, fontWeight = FontWeight.Bold) },
                modifier = Modifier.testTag("tab_past_stays")
            )
        }

        // --- STAYS LIST / EMPTY STATE ---
        if (filteredBookings.isEmpty()) {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.15f)),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Default.Bed,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f),
                        modifier = Modifier.size(48.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = when (selectedFilterTab) {
                            1 -> "No Upcoming Stays Found"
                            2 -> "No Past Stays Found"
                            else -> "No Stays or Bookings Yet"
                        },
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Explore luxury wilderness lodges and all-inclusive safaris to build your East African journey.",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    OutlinedButton(
                        onClick = onExploreClick,
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Explore Lodges & Safaris")
                    }
                }
            }
        } else {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                filteredBookings.forEach { booking ->
                    BookingCard(
                        booking = booking,
                        onCancelClick = { onCancelClick(booking) },
                        onRateClick = { onRateClick(booking) },
                        itinerary = viewModel.getItineraryForBooking(booking),
                        onShowQrClick = { onShowQrClick(booking) },
                        onChatClick = { onChatClick(booking) },
                        onGroupHubClick = { onGroupHubClick(booking) }
                    )
                }
            }
        }
    }
}

@Composable
private fun MetricStatChip(
    label: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.12f)),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(vertical = 8.dp, horizontal = 6.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = value,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = label,
                fontSize = 9.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
