package com.example.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.StayItem
import com.example.data.WeatherCacheEntity
import com.example.viewmodel.SafariViewModel

@Composable
fun StaysTab(
    stays: List<StayItem>,
    favorites: List<String>,
    viewModel: SafariViewModel,
    weather: WeatherCacheEntity? = null,
    onStayClick: (StayItem) -> Unit,
    onMapMarkerClick: (StayItem) -> Unit,
    onBookClick: (StayItem) -> Unit,
    onFavoriteClick: (StayItem) -> Unit,
    isOwner: Boolean = false,
    onLiveChatClick: ((String) -> Unit)? = null
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        if (weather != null) {
            item {
                WeatherWidget(weather = weather)
            }
        }

        // Feature 0: Memory Recap / Anniversary Card (Retention)
        item {
            MemoryAnniversaryCard()
        }

        // Feature 1: Great Migration & Seasonality Tracker Card
        item {
            GreatMigrationTrackerCard(
                stays = stays, 
                onLodgeClick = onStayClick, 
                isOwner = isOwner,
                onBroadcastSms = { msg, viaWhatsApp -> viewModel.sendRangerAlert(msg, viaWhatsApp = viaWhatsApp) }
            )
        }

        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .height(250.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                CatalogMapboxWebView(
                    stays = stays,
                    onMarkerClick = { id, type ->
                        if (type == "Stay") {
                            val stay = stays.find { it.id == id }
                            if (stay != null) {
                                onMapMarkerClick(stay)
                            }
                        }
                    }
                )
            }
        }

        item {
            Surface(
                color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp)
                    .clickable { onLiveChatClick?.invoke("Serengeti & Mara Partner Lodges") }
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .background(MaterialTheme.colorScheme.primary, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Chat,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onPrimary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "Lodge Host Live Chat",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(Modifier.width(6.dp))
                            Surface(
                                color = Color(0xFF2E7D32),
                                shape = RoundedCornerShape(4.dp)
                            ) {
                                Text(
                                    text = "Firestore Live 🟢",
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White,
                                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                )
                            }
                        }
                        Text(
                            text = "Last-minute availability, dietary requests & airstrip transfer inquiries",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Icon(
                        imageVector = Icons.Default.ChevronRight,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }

        item {
            Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "${stays.size} Results",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        TextButton(
                            onClick = { },
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text("Sort", fontSize = 12.sp)
                                Icon(Icons.Default.ArrowDropDown, contentDescription = null, modifier = Modifier.size(16.dp))
                            }
                        }
                        IconButton(
                            onClick = { },
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(Icons.Default.Share, contentDescription = "Share", modifier = Modifier.size(18.dp))
                        }
                    }
                }
                Divider(modifier = Modifier.padding(top = 4.dp), color = MaterialTheme.colorScheme.outline.copy(alpha = 0.1f))
            }
        }
        
        items(stays) { stay ->
            Box(modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)) {
                StayCard(
                    stay = stay,
                    isFavorite = favorites.contains(stay.id),
                    viewModel = viewModel,
                    onStayClick = { onStayClick(stay) },
                    onBookClick = { onBookClick(stay) },
                    onFavoriteClick = { onFavoriteClick(stay) }
                )
            }
        }
    }
}

@Composable
fun GreatMigrationTrackerCard(
    stays: List<StayItem>, 
    onLodgeClick: (StayItem) -> Unit,
    isOwner: Boolean = false,
    onBroadcastSms: (String, Boolean) -> Unit = { _, _ -> }
) {
    var selectedMonthIndex by remember { mutableStateOf(7) } // August as default (River Crossings peak)
    val months = listOf("Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec")
    val fullMonthNames = listOf(
        "January", "February", "March", "April", "May", "June", 
        "July", "August", "September", "October", "November", "December"
    )

    // Migration phases based on selected month
    val migrationPhase = remember(selectedMonthIndex) {
        when (selectedMonthIndex) {
            in 0..2 -> MigrationPhase(
                title = "Calving & Birthing Season",
                location = "Southern Serengeti / Ndutu Area",
                description = "Over 500,000 wildebeest calves are born during this period. The rich southern volcanic plains provide mineral-rich grasses. High big-cat predator action!",
                herdCoordinates = Offset(0.5f, 0.85f),
                alertLevel = "High Sighting Probabilities"
            )
            in 3..5 -> MigrationPhase(
                title = "Grumeti River & Rutting Season",
                location = "Western Corridor, Serengeti",
                description = "The herds begin their march North-west. Huge columns cross the Grumeti River, facing giant Nile crocodiles. Mating season begins with roaring territorial bulls.",
                herdCoordinates = Offset(0.25f, 0.45f),
                alertLevel = "Active river crossing risk"
            )
            in 6..9 -> MigrationPhase(
                title = "Mara River Crossings (Peak)",
                location = "Northern Serengeti & Maasai Mara",
                description = "The absolute climax of the migration! Herds plunge across the crocodile-infested Mara River between Kenya & Tanzania. Incredible drama and action daily.",
                herdCoordinates = Offset(0.5f, 0.15f),
                alertLevel = "🔥 PRIME SEASON CRITICAL"
            )
            else -> MigrationPhase(
                title = "Plains Heading South",
                location = "Central & Eastern Serengeti",
                description = "With the short rains starting in November, the massive herds make their return journey Southward to the central plains, seeking nutrient-dense fresh grass.",
                herdCoordinates = Offset(0.7f, 0.55f),
                alertLevel = "Steady migration columns"
            )
        }
    }

    Card(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.15f)),
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .testTag("great_migration_tracker_card")
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.primaryContainer,
                    modifier = Modifier.size(36.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.Explore,
                            contentDescription = "Great Migration",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = "Great Migration Tracker",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Real-time herd positions & seasonality",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Month Slider Controls
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                months.forEachIndexed { index, m ->
                    val isSelected = selectedMonthIndex == index
                    Surface(
                        onClick = { selectedMonthIndex = index },
                        color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                        contentColor = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface,
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.testTag("month_slider_tab_$m")
                    ) {
                        Text(
                            text = m,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Map & Status Split Section
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Interactive Custom Map View (Canvas)
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = Color(0xFFF9F6F0),
                    border = BorderStroke(1.dp, Color(0xFFE2DDD5)),
                    modifier = Modifier
                        .weight(1.1f)
                        .fillMaxHeight()
                ) {
                    Box {
                        Canvas(modifier = Modifier.fillMaxSize()) {
                            val w = size.width
                            val h = size.height

                            // Draw park boundary rivers (Mara River & Grumeti River)
                            val riverColor = Color(0xFF81D4FA)
                            // Mara River at top
                            val maraRiverPath = Path().apply {
                                moveTo(0f, h * 0.18f)
                                cubicTo(w * 0.3f, h * 0.12f, w * 0.6f, h * 0.25f, w, h * 0.15f)
                            }
                            drawPath(maraRiverPath, color = riverColor, style = Stroke(width = 3.dp.toPx()))

                            // Grumeti River on west side
                            val grumetiRiverPath = Path().apply {
                                moveTo(0f, h * 0.45f)
                                quadraticTo(w * 0.4f, h * 0.4f, w * 0.5f, h * 0.6f)
                            }
                            drawPath(grumetiRiverPath, color = riverColor, style = Stroke(width = 2.dp.toPx()))

                            // Draw boundary line between Kenya (Maasai Mara) & Tanzania (Serengeti)
                            val boundaryColor = Color(0xFFB0BEC5)
                            drawLine(
                                color = boundaryColor,
                                start = Offset(0f, h * 0.22f),
                                end = Offset(w, h * 0.22f),
                                strokeWidth = 1.dp.toPx()
                            )

                            // Draw herd icon marker (glowing pulse dot)
                            val pulseRadius = 14.dp.toPx()
                            val coreRadius = 6.dp.toPx()
                            val markerCenter = Offset(
                                migrationPhase.herdCoordinates.x * w,
                                migrationPhase.herdCoordinates.y * h
                            )

                            // Glowing background pulse
                            drawCircle(
                                color = Color(0xFFFF9800).copy(alpha = 0.25f),
                                radius = pulseRadius,
                                center = markerCenter
                            )
                            // Core pointer
                            drawCircle(
                                color = Color(0xFFFF5722),
                                radius = coreRadius,
                                center = markerCenter
                            )

                            // Draw Serengeti and Maasai Mara text coordinates
                            // We can draw simple boundaries labels or visual blocks
                        }

                        // Text labels on map
                        Text(
                            text = "MAASAI MARA\n(KENYA)",
                            fontSize = 8.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF78909C),
                            modifier = Modifier.align(Alignment.TopCenter).padding(top = 8.dp),
                            textAlign = TextAlign.Center
                        )
                        Text(
                            text = "SERENGETI NP\n(TANZANIA)",
                            fontSize = 8.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF78909C),
                            modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 8.dp),
                            textAlign = TextAlign.Center
                        )

                        // Map Indicator
                        Surface(
                            color = MaterialTheme.colorScheme.primary,
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier
                                .align(Alignment.TopStart)
                                .padding(6.dp)
                        ) {
                            Text(
                                text = "MAP STATUS",
                                fontSize = 7.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimary,
                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                            )
                        }
                    }
                }

                // Info Section
                Column(
                    modifier = Modifier
                        .weight(1.3f)
                        .fillMaxHeight(),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Surface(
                            color = when (selectedMonthIndex) {
                                in 6..9 -> Color(0xFFFFEBEE)
                                in 3..5 -> Color(0xFFFFF3E0)
                                else -> Color(0xFFE8F5E9)
                            },
                            shape = RoundedCornerShape(6.dp),
                            modifier = Modifier.padding(bottom = 6.dp)
                        ) {
                            Text(
                                text = migrationPhase.alertLevel,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = when (selectedMonthIndex) {
                                    in 6..9 -> Color(0xFFC62828)
                                    in 3..5 -> Color(0xFFE65100)
                                    else -> Color(0xFF2E7D32)
                                },
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                            )
                        }

                        Text(
                            text = fullMonthNames[selectedMonthIndex],
                            fontSize = 18.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.primary
                        )

                        Text(
                            text = migrationPhase.title,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.padding(top = 2.dp)
                        )

                        Text(
                            text = migrationPhase.location,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.secondary,
                            modifier = Modifier.padding(top = 1.dp)
                        )

                        Text(
                            text = migrationPhase.description,
                            fontSize = 10.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 4,
                            lineHeight = 13.sp,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))
            Divider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.08f))
            Spacer(modifier = Modifier.height(12.dp))

            // Smart Lodge Proximity Tagging (Feature 1 - Bullet 2)
            Text(
                text = "Partner Camps with Prime Proximity:",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(6.dp))

            // --- LIVE MIGRATION ALERTS ---
            val liveAlerts = listOf(
                "Large herds spotted crossing Sand River (3 hours ago)",
                "Predator activity high near Look-out Point 4",
                "Recommended route: Gate 2 for better visibility today"
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Live Ranger Feed:",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                
                if (isOwner) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "BROADCAST:",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(end = 8.dp)
                        )
                        Text(
                            text = "SMS",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier
                                .clickable { onBroadcastSms("ALERT: " + liveAlerts.first(), false) }
                                .padding(4.dp)
                        )
                        Text(
                            text = "|",
                            fontSize = 10.sp,
                            color = Color.LightGray,
                            modifier = Modifier.padding(horizontal = 4.dp)
                        )
                        Text(
                            text = "WHATSAPP",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF25D366),
                            modifier = Modifier
                                .clickable { onBroadcastSms("ALERT: " + liveAlerts.first(), true) }
                                .padding(4.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Filter partner stays based on seasonality proximity
            val partnerStaysForMonth = remember(selectedMonthIndex) {
                if (selectedMonthIndex in 6..9) {
                    // Northern Mara season: highlight Mara Serena Safari Lodge (and fake mara River ones)
                    stays.filter { it.location.lowercase().contains("mara") }
                } else {
                    // Central/Serengeti season: highlight Four Seasons/Serengeti
                    stays.filter { it.location.lowercase().contains("serengeti") }
                }
            }

            if (partnerStaysForMonth.isNotEmpty()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    partnerStaysForMonth.take(2).forEach { lodge ->
                        Surface(
                            color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f),
                            shape = RoundedCornerShape(12.dp),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)),
                            modifier = Modifier
                                .weight(1f)
                                .clickable { onLodgeClick(lodge) }
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.CheckCircle,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(12.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "⚡ PRIME PROXIMITY",
                                        fontSize = 8.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                }
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = lodge.title,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    maxLines = 1,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = if (selectedMonthIndex in 6..9) "⏱️ 12 mins drive to crossing" else "⏱️ 20 mins drive to herds",
                                    fontSize = 9.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            } else {
                Text(
                    text = "No partner lodges listed in this direct area, but central charters are active.",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            // Live Alerts Section
            Spacer(modifier = Modifier.height(16.dp))
            Surface(
                color = MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.3f),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Campaign,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.tertiary,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "LIVE RANGER ALERTS",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.tertiary,
                            letterSpacing = 1.sp
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    liveAlerts.forEach { alert ->
                        Row(
                            modifier = Modifier.padding(vertical = 4.dp),
                            verticalAlignment = Alignment.Top
                        ) {
                            Text("• ", color = MaterialTheme.colorScheme.tertiary, fontSize = 12.sp)
                            Text(
                                text = alert,
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onTertiaryContainer,
                                lineHeight = 15.sp
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun MemoryAnniversaryCard() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.4f)),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.secondary.copy(alpha = 0.1f))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.secondary,
                    modifier = Modifier.size(32.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(Icons.Default.History, contentDescription = null, tint = MaterialTheme.colorScheme.onSecondary, modifier = Modifier.size(18.dp))
                    }
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = "1 YEAR SINCE YOUR MARA TRIP",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.secondary,
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = "Relive your best sightings from 2025",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.7f)
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Mock memory thumbnails
                listOf("🦁", "🐘", "🦒").forEach { emoji ->
                    Box(
                        modifier = Modifier
                            .size(60.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.5f))
                            .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.1f), RoundedCornerShape(12.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = emoji, fontSize = 24.sp)
                    }
                }
                
                Box(
                    modifier = Modifier
                        .height(60.dp)
                        .weight(1f)
                        .clip(RoundedCornerShape(12.dp))
                        .background(MaterialTheme.colorScheme.secondary)
                        .clickable { },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "VIEW FULL RECAP",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSecondary
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Text(
                text = "Feeling nostalgic? The herds are back in the North. Check current availability at your favorite lodges.",
                fontSize = 10.sp,
                color = MaterialTheme.colorScheme.onSecondaryContainer,
                lineHeight = 14.sp
            )
        }
    }
}

data class MigrationPhase(
    val title: String,
    val location: String,
    val description: String,
    val herdCoordinates: Offset,
    val alertLevel: String
)
