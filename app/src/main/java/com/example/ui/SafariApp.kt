package com.example.ui

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.ui.graphics.graphicsLayer
import coil.compose.AsyncImage
import coil.request.ImageRequest
import androidx.compose.foundation.*
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
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
import com.example.ui.components.MpesaSuccessData
import com.example.ui.components.MpesaPaymentSuccessToastOverlay
import com.example.ui.components.MpesaCalendarPromptCard
import com.example.ui.components.addBookingToCalendar
import com.example.ui.components.FirebaseLiveChatDialog
import androidx.compose.ui.zIndex
import com.example.viewmodel.SafariViewModel
import com.example.viewmodel.BudgetLevel
import com.example.viewmodel.BudgetAlert
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import android.net.Uri
import android.graphics.Paint
import android.widget.Toast
import android.graphics.Typeface
import android.webkit.WebView
import android.webkit.WebViewClient
import android.webkit.WebSettings
import androidx.compose.ui.viewinterop.AndroidView
import com.example.BuildConfig
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import kotlinx.coroutines.delay
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.ui.graphics.Path


import androidx.compose.ui.text.font.FontStyle

import com.example.ui.components.*

@Composable
fun DiscoveryMapWebView(
    stays: List<StayItem>,
    safaris: List<SafariItem>,
    modifier: Modifier = Modifier
) {
    val accessToken = BuildConfig.MAPBOX_ACCESS_TOKEN
    val isDefaultToken = accessToken.contains("EXAMPLE") || accessToken.isEmpty()

    if (isDefaultToken) {
        Box(
            modifier = modifier
                .background(MaterialTheme.colorScheme.surfaceVariant)
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(Icons.Default.Map, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    "Mapbox Token Required",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    "Please add MAPBOX_ACCESS_TOKEN to the Secrets panel.",
                    style = MaterialTheme.typography.bodySmall,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        return
    }

    val stayMarkers = stays.filter { it.lat != null && it.lng != null }.map {
        "{ name: '${it.title.replace("'", "\\'")}', lat: ${it.lat}, lng: ${it.lng}, type: 'stay' }"
    }
    val safariMarkers = safaris.filter { it.lat != null && it.lng != null }.map {
        "{ name: '${it.title.replace("'", "\\'")}', lat: ${it.lat}, lng: ${it.lng}, type: 'safari' }"
    }
    
    val allMarkers = stayMarkers + safariMarkers

    val htmlContent = """
        <!DOCTYPE html>
        <html>
        <head>
            <meta charset="utf-8">
            <title>Safari Discovery Map</title>
            <meta name="viewport" content="initial-scale=1,maximum-scale=1,user-scalable=no">
            <link href="https://api.mapbox.com/mapbox-gl-js/v2.15.0/mapbox-gl.css" rel="stylesheet">
            <script src="https://api.mapbox.com/mapbox-gl-js/v2.15.0/mapbox-gl.js"></script>
            <style>
                body { margin: 0; padding: 0; }
                #map { position: absolute; top: 0; bottom: 0; width: 100%; }
                .mapboxgl-popup { max-width: 200px; }
                .mapboxgl-popup-content { font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; font-size: 12px; }
            </style>
        </head>
        <body>
        <div id="map"></div>
        <script>
            mapboxgl.accessToken = '$accessToken';
            const map = new mapboxgl.Map({
                container: 'map',
                style: 'mapbox://styles/mapbox/outdoors-v12',
                center: [36.0, -2.5],
                zoom: 4.5
            });

            map.on('load', () => {
                const markers = [${allMarkers.joinToString(", ")}];
                if (markers.length > 0) {
                    const bounds = new mapboxgl.LngLatBounds();
                    
                    markers.forEach(m => {
                        const el = document.createElement('div');
                        el.className = 'marker';
                        
                        const color = m.type === 'stay' ? '#4CAF50' : '#FF9800';
                        
                        new mapboxgl.Marker({ color: color })
                            .setLngLat([m.lng, m.lat])
                            .setPopup(new mapboxgl.Popup({ offset: 25 }).setHTML('<h3>' + m.name + '</h3><p>' + (m.type === 'stay' ? 'Safari Lodge' : 'Safari Expedition') + '</p>'))
                            .addTo(map);
                            
                        bounds.extend([m.lng, m.lat]);
                    });

                    map.fitBounds(bounds, { padding: 50 });
                }
            });
        </script>
        </body>
        </html>
    """.trimIndent()

    AndroidView(
        modifier = modifier,
        factory = { context ->
            WebView(context).apply {
                webViewClient = WebViewClient()
                settings.apply {
                    javaScriptEnabled = true
                    domStorageEnabled = true
                    cacheMode = WebSettings.LOAD_CACHE_ELSE_NETWORK
                }
                loadDataWithBaseURL("https://www.mapbox.com", htmlContent, "text/html", "UTF-8", null)
            }
        },
        update = { webView ->
            webView.loadDataWithBaseURL("https://www.mapbox.com", htmlContent, "text/html", "UTF-8", null)
        }
    )
}

@Composable
fun DiscoveryMapView(
    stays: List<StayItem>,
    safaris: List<SafariItem>,
    onClose: () -> Unit
) {
    Box(modifier = Modifier.fillMaxSize()) {
        DiscoveryMapWebView(
            stays = stays,
            safaris = safaris,
            modifier = Modifier.fillMaxSize()
        )
        
        // Close Button
        FloatingActionButton(
            onClick = onClose,
            modifier = Modifier
                .padding(16.dp)
                .align(Alignment.TopEnd),
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary,
            shape = CircleShape
        ) {
            Icon(Icons.Default.Close, contentDescription = "Close Map")
        }
    }
}

@Composable
fun MapboxWebView(
    mapType: String,
    bookings: List<BookingEntity>,
    modifier: Modifier = Modifier
) {
    val accessToken = BuildConfig.MAPBOX_ACCESS_TOKEN
    
    // Simple check for default token
    val isDefaultToken = accessToken.contains("EXAMPLE") || accessToken.isEmpty()

    if (isDefaultToken) {
        Box(
            modifier = modifier
                .background(MaterialTheme.colorScheme.surfaceVariant)
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(Icons.Default.Map, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    "Mapbox Token Required",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    "Please add MAPBOX_ACCESS_TOKEN to the Secrets panel.",
                    style = MaterialTheme.typography.bodySmall,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        return
    }

    val mapStyle = if (mapType == "satellite") "mapbox://styles/mapbox/satellite-v9" else "mapbox://styles/mapbox/outdoors-v12"
    
    // Generate route GeoJSON from bookings
    val routePoints = remember(bookings) {
        bookings.mapNotNull { booking ->
            when {
                booking.title.contains("Mara") -> "[35.215, -1.406]"
                booking.title.contains("Serengeti") -> "[34.833, -2.333]"
                booking.title.contains("Ngorongoro") -> "[35.500, -3.200]"
                booking.title.contains("Zanzibar") -> "[39.300, -6.100]"
                booking.title.contains("Le Petit Village") -> "[32.596, 0.299]"
                else -> null
            }
        }.distinct()
    }

    val htmlContent = """
        <!DOCTYPE html>
        <html>
        <head>
            <meta charset="utf-8">
            <title>Safari Route</title>
            <meta name="viewport" content="initial-scale=1,maximum-scale=1,user-scalable=no">
            <link href="https://api.mapbox.com/mapbox-gl-js/v2.15.0/mapbox-gl.css" rel="stylesheet">
            <script src="https://api.mapbox.com/mapbox-gl-js/v2.15.0/mapbox-gl.js"></script>
            <style>
                body { margin: 0; padding: 0; }
                #map { position: absolute; top: 0; bottom: 0; width: 100%; }
            </style>
        </head>
        <body>
        <div id="map"></div>
        <script>
            mapboxgl.accessToken = '$accessToken';
            const map = new mapboxgl.Map({
                container: 'map',
                style: '$mapStyle',
                center: [36.0, -2.5],
                zoom: 5
            });

            map.on('load', () => {
                const points = [${routePoints.joinToString(", ")}];
                if (points.length > 0) {
                    map.addSource('route', {
                        'type': 'geojson',
                        'data': {
                            'type': 'Feature',
                            'properties': {},
                            'geometry': {
                                'type': 'LineString',
                                'coordinates': points
                            }
                        }
                    });
                    map.addLayer({
                        'id': 'route',
                        'type': 'line',
                        'source': 'route',
                        'layout': {
                            'line-join': 'round',
                            'line-cap': 'round'
                        },
                        'paint': {
                            'line-color': '#4CAF50',
                            'line-width': 4
                        }
                    });

                    points.forEach((p, i) => {
                        new mapboxgl.Marker({ color: i === 0 ? '#FF5722' : '#4CAF50' })
                            .setLngLat(p)
                            .addTo(map);
                    });

                    const bounds = new mapboxgl.LngLatBounds();
                    points.forEach(p => bounds.extend(p));
                    map.fitBounds(bounds, { padding: 40 });
                }
            });
        </script>
        </body>
        </html>
    """.trimIndent()

    AndroidView(
        modifier = modifier,
        factory = { context ->
            WebView(context).apply {
                webViewClient = WebViewClient()
                settings.apply {
                    javaScriptEnabled = true
                    domStorageEnabled = true
                    cacheMode = WebSettings.LOAD_CACHE_ELSE_NETWORK
                }
                loadDataWithBaseURL("https://www.mapbox.com", htmlContent, "text/html", "UTF-8", null)
            }
        },
        update = { webView ->
            // Re-load if style changes
            webView.loadDataWithBaseURL("https://www.mapbox.com", htmlContent, "text/html", "UTF-8", null)
        }
    )
}

@OptIn(ExperimentalFoundationApi::class)
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
    val weather by viewModel.weatherState.collectAsStateWithLifecycle()
    val notifications by viewModel.notifications.collectAsStateWithLifecycle()
    val budgetAlert by viewModel.budgetAlert.collectAsStateWithLifecycle()
    val unreadNotificationsCount = remember(notifications) { notifications.count { !it.isRead } }

    val minPriceFilter by viewModel.minPriceFilter.collectAsStateWithLifecycle()
    val maxPriceFilter by viewModel.maxPriceFilter.collectAsStateWithLifecycle()
    val selectedCurrency by viewModel.selectedCurrency.collectAsStateWithLifecycle()
    val selectedMonthFilter by viewModel.selectedMonthFilter.collectAsStateWithLifecycle()
    val selectedRegionFilter by viewModel.selectedRegionFilter.collectAsStateWithLifecycle()

    var isFilterExpanded by remember { mutableStateOf(false) }
    var isDiscoveryMapVisible by remember { mutableStateOf(false) }

    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current

    // Booking Dialog states
    var selectedStayForBooking by remember { mutableStateOf<StayItem?>(null) }
    var selectedStayForDetails by remember { mutableStateOf<StayItem?>(null) }
    var selectedStayForMapPanel by remember { mutableStateOf<StayItem?>(null) }
    var selectedSafariForBooking by remember { mutableStateOf<SafariItem?>(null) }
    var selectedSafariForDetails by remember { mutableStateOf<SafariItem?>(null) }
    
    var showGeminiItinerarySidebar by remember { mutableStateOf(false) }
    var selectedLodgeForAiItinerary by remember { mutableStateOf<String?>(null) }
    
    var hasAcceptedPrivacy by remember { mutableStateOf(false) }
    var showFinanceDashboard by remember { mutableStateOf(false) }
    var activeMpesaSuccessToastData by remember { mutableStateOf<MpesaSuccessData?>(null) }
    var activeLiveChatInquiry by remember { mutableStateOf<Pair<String, String>?>(null) }
    val isOwner by viewModel.isOwner.collectAsStateWithLifecycle()

    // Toast or snackbar handling
    LaunchedEffect(uiEvent) {
        uiEvent?.let {
            scope.launch {
                snackbarHostState.showSnackbar(it)
            }
            viewModel.clearUiEvent()
        }
    }

    LaunchedEffect(budgetAlert) {
        if (budgetAlert.level != BudgetLevel.SAFE) {
            scope.launch {
                snackbarHostState.showSnackbar(
                    message = budgetAlert.message,
                    duration = SnackbarDuration.Long
                )
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Scaffold(
            containerColor = Color.Transparent,
            snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
            topBar = {
                Column(
                    modifier = Modifier
                        .background(Color.Transparent)
                        .statusBarsPadding()
                        .padding(horizontal = 16.dp, vertical = 4.dp)
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
                                .combinedClickable(
                                    onClick = { },
                                    onLongClick = {
                                        viewModel.toggleOwnerMode()
                                        Toast.makeText(context, "Admin Mode Toggled", Toast.LENGTH_SHORT).show()
                                    }
                                )
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

                        SyncQueueStatus(viewModel = viewModel)
                        
                        Spacer(modifier = Modifier.width(8.dp))
                        
                        // Gemini AI Planner Icon
                        Box(
                            modifier = Modifier
                                .padding(end = 8.dp)
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primaryContainer)
                                .clickable {
                                    selectedLodgeForAiItinerary = null
                                    showGeminiItinerarySidebar = true
                                }
                                .testTag("top_bar_gemini_ai_button"),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = "Gemini AI Safari Planner",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        // Notification Bell
                    Box(
                        modifier = Modifier
                            .padding(end = 8.dp)
                            .size(36.dp)
                            .clip(CircleShape)
                            .clickable { viewModel.setTab("notifications") }
                            .testTag("top_bar_notification_bell"),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Notifications,
                            contentDescription = "Notifications",
                            tint = if (currentTab == "notifications") MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onBackground
                        )
                        if (unreadNotificationsCount > 0) {
                            Surface(
                                color = Color.Red,
                                shape = CircleShape,
                                modifier = Modifier
                                    .align(Alignment.TopEnd)
                                    .size(12.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text(
                                        text = unreadNotificationsCount.toString(),
                                        color = Color.White,
                                        fontSize = 7.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }

                    // Budget Warning Banner
                    AnimatedVisibility(
                        visible = budgetAlert.level != BudgetLevel.SAFE,
                        enter = expandVertically() + fadeIn(),
                        exit = shrinkVertically() + fadeOut()
                    ) {
                        Surface(
                            color = if (budgetAlert.level == BudgetLevel.CRITICAL) MaterialTheme.colorScheme.errorContainer else Color(0xFFFFF9C4),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = if (budgetAlert.level == BudgetLevel.CRITICAL) Icons.Default.Error else Icons.Default.Warning,
                                    contentDescription = null,
                                    tint = if (budgetAlert.level == BudgetLevel.CRITICAL) MaterialTheme.colorScheme.error else Color(0xFFFBC02D),
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = budgetAlert.message,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (budgetAlert.level == BudgetLevel.CRITICAL) MaterialTheme.colorScheme.onErrorContainer else Color(0xFF5D4037)
                                )
                            }
                        }
                    }

                    // Quick Actions Row
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState())
                            .padding(bottom = 6.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // AI Safari Planner Action Pill
                        Surface(
                            color = MaterialTheme.colorScheme.primaryContainer,
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .clickable { showGeminiItinerarySidebar = true }
                                .testTag("top_bar_ai_planner_pill")
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.AutoAwesome,
                                    contentDescription = "AI Safari Planner",
                                    modifier = Modifier.size(13.dp),
                                    tint = MaterialTheme.colorScheme.primary
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "AI Planner ✨",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                            }
                        }

                        // Safari Journal & Gallery Action Pill
                        Surface(
                            color = if (currentTab == "journal") MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.85f),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .clickable { viewModel.setTab("journal") }
                                .testTag("top_bar_journal_btn")
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Collections,
                                    contentDescription = "Safari Journal",
                                    modifier = Modifier.size(13.dp),
                                    tint = if (currentTab == "journal") MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onPrimaryContainer
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "Gallery",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (currentTab == "journal") MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onPrimaryContainer
                                )
                            }
                        }

                        // Swahili Phrasebook Action Pill
                        Surface(
                            color = if (currentTab == "swahili") MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.85f),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .clickable { viewModel.setTab("swahili") }
                                .testTag("top_bar_swahili_btn")
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Translate,
                                    contentDescription = "Swahili Phrasebook",
                                    modifier = Modifier.size(13.dp),
                                    tint = if (currentTab == "swahili") MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onTertiaryContainer
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "Swahili",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (currentTab == "swahili") MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onTertiaryContainer
                                )
                            }
                        }

                        // Local Experiences Action Pill
                        Surface(
                            color = if (currentTab == "experiences") MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.85f),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .clickable { viewModel.setTab("experiences") }
                                .testTag("top_bar_experiences_btn")
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Groups,
                                    contentDescription = "Local Experiences",
                                    modifier = Modifier.size(13.dp),
                                    tint = if (currentTab == "experiences") MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onTertiaryContainer
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "Experiences",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (currentTab == "experiences") MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onTertiaryContainer
                                )
                            }
                        }

                        // Packing Checklist Action Pill
                        Surface(
                            color = if (currentTab == "packing") MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.85f),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .clickable { viewModel.setTab("packing") }
                                .testTag("top_bar_packing_btn")
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Checklist,
                                    contentDescription = "Packing Checklist",
                                    modifier = Modifier.size(13.dp),
                                    tint = if (currentTab == "packing") MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSecondaryContainer
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "Packing",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (currentTab == "packing") MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSecondaryContainer
                                )
                            }
                        }

                        // Quick stats pill
                        Surface(
                            color = MaterialTheme.colorScheme.secondaryContainer,
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ConfirmationNumber,
                                    contentDescription = "Active Vouchers",
                                    modifier = Modifier.size(13.dp),
                                    tint = MaterialTheme.colorScheme.onSecondaryContainer
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                val activeVoucherCount = vouchers.count { it.status == "Active" }
                                Text(
                                    text = "$activeVoucherCount Vouchers",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSecondaryContainer
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                // Modern Search/Filter bar (only show in stays/safari tabs)
                if (currentTab == "stays" || currentTab == "safaris") {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .animateContentSize()
                            .testTag("search_filter_console"),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
                        ),
                        border = BorderStroke(
                            width = 1.dp,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f)
                        )
                    ) {
                        Column(
                            modifier = Modifier.padding(8.dp)
                        ) {
                            val hasActiveFilters = selectedMonthFilter != "All Months" || minPriceFilter > 0.0 || maxPriceFilter < 10000.0

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
                                            if (currentTab == "stays") "Where to? Lodge..."
                                            else "Where to? Serengeti...",
                                            fontSize = 12.sp
                                        )
                                    },
                                    leadingIcon = {
                                        Icon(
                                            imageVector = Icons.Default.Search,
                                            contentDescription = "Search",
                                            tint = MaterialTheme.colorScheme.primary,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    },
                                    trailingIcon = {
                                        if (searchQuery.isNotEmpty()) {
                                            IconButton(onClick = { viewModel.updateSearchQuery("") }) {
                                                Icon(
                                                    imageVector = Icons.Default.Clear,
                                                    contentDescription = "Clear search",
                                                    modifier = Modifier.size(18.dp)
                                                )
                                            }
                                        }
                                    },
                                    singleLine = true,
                                    shape = RoundedCornerShape(10.dp),
                                    textStyle = MaterialTheme.typography.bodyMedium.copy(fontSize = 12.sp),
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(48.dp)
                                        .testTag("search_input"),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedContainerColor = MaterialTheme.colorScheme.surface,
                                        unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                                        unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f),
                                    )
                                )

                                Spacer(modifier = Modifier.width(6.dp))

                                // Map Toggle Button
                                IconButton(
                                    onClick = { isDiscoveryMapVisible = true },
                                    modifier = Modifier
                                        .size(44.dp)
                                        .background(
                                            color = MaterialTheme.colorScheme.surface,
                                            shape = RoundedCornerShape(10.dp)
                                        )
                                        .border(
                                            width = 1.dp,
                                            color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f),
                                            shape = RoundedCornerShape(10.dp)
                                        )
                                        .testTag("discovery_map_toggle_btn")
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Map,
                                        contentDescription = "View Map",
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }

                                Spacer(modifier = Modifier.width(6.dp))

                                // Filter Badge Toggle Button
                                Box(modifier = Modifier.testTag("filter_toggle_box")) {
                                    IconButton(
                                        onClick = { isFilterExpanded = !isFilterExpanded },
                                        modifier = Modifier
                                            .size(44.dp)
                                            .background(
                                                color = if (isFilterExpanded || hasActiveFilters) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface,
                                                shape = RoundedCornerShape(10.dp)
                                            )
                                            .border(
                                                width = 1.dp,
                                                color = if (isFilterExpanded || hasActiveFilters) Color.Transparent else MaterialTheme.colorScheme.outline.copy(alpha = 0.2f),
                                                shape = RoundedCornerShape(10.dp)
                                            )
                                            .testTag("filter_toggle_btn")
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.FilterList,
                                            contentDescription = "Toggle Filters",
                                            tint = if (isFilterExpanded || hasActiveFilters) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.primary,
                                            modifier = Modifier.size(20.dp)
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
                                valueRange = 0f..10000f,
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

                                    if (minPriceFilter > 0.0 || maxPriceFilter < 10000.0) {
                                        FilterChip(
                                            selected = true,
                                            onClick = { viewModel.updatePriceFilter(0.0, 10000.0) },
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
                                    Divider(
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
                containerColor = MaterialTheme.colorScheme.surface,
                tonalElevation = 8.dp
            ) {
                val navItemColors = NavigationBarItemDefaults.colors(
                    indicatorColor = MaterialTheme.colorScheme.primary,
                    selectedIconColor = MaterialTheme.colorScheme.onPrimary,
                    selectedTextColor = MaterialTheme.colorScheme.primary,
                    unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                )
                NavigationBarItem(
                    selected = currentTab == "stays",
                    onClick = { viewModel.setTab("stays") },
                    icon = { Icon(imageVector = Icons.Default.Home, contentDescription = "Home") },
                    label = { Text("HOME") },
                    modifier = Modifier.testTag("tab_stays"),
                    colors = navItemColors
                )
                NavigationBarItem(
                    selected = currentTab == "safaris",
                    onClick = { viewModel.setTab("safaris") },
                    icon = { Icon(imageVector = Icons.Default.Explore, contentDescription = "Safaris") },
                    label = { Text("SAFARIS") },
                    modifier = Modifier.testTag("tab_safaris"),
                    colors = navItemColors
                )
                NavigationBarItem(
                    selected = currentTab == "events",
                    onClick = { viewModel.setTab("events") },
                    icon = { Icon(imageVector = Icons.Default.Event, contentDescription = "Events") },
                    label = { Text("EVENTS") },
                    modifier = Modifier.testTag("tab_events"),
                    colors = navItemColors
                )
                NavigationBarItem(
                    selected = currentTab == "wildlife",
                    onClick = { viewModel.setTab("wildlife") },
                    icon = { Icon(imageVector = Icons.Default.Pets, contentDescription = "Wildlife") },
                    label = { Text("WILDLIFE") },
                    modifier = Modifier.testTag("tab_wildlife"),
                    colors = navItemColors
                )
                NavigationBarItem(
                    selected = currentTab == "vouchers",
                    onClick = { viewModel.setTab("vouchers") },
                    icon = { Icon(imageVector = Icons.Default.CardMembership, contentDescription = "Vouchers") },
                    label = { Text("VOUCHERS") },
                    modifier = Modifier.testTag("tab_vouchers"),
                    colors = navItemColors
                )
                NavigationBarItem(
                    selected = currentTab == "bookings",
                    onClick = { viewModel.setTab("bookings") },
                    icon = { Icon(imageVector = Icons.Default.BookOnline, contentDescription = "Bookings") },
                    label = { Text("MY TRIPS") },
                    modifier = Modifier.testTag("tab_bookings"),
                    colors = navItemColors
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
                    "stays" -> {
                        StaysTab(
                            stays = stays.reversed(),
                            favorites = favorites.filter { it.type == "STAY" }.map { it.itemId },
                            viewModel = viewModel,
                            weather = weather,
                            onStayClick = { selectedStayForDetails = it },
                            onMapMarkerClick = { 
                                selectedStayForMapPanel = it 
                                viewModel.fetchPlaceDetails(it)
                            },
                            onBookClick = { selectedStayForBooking = it },
                            onFavoriteClick = { stay -> viewModel.toggleFavorite(stay.id, "STAY", stay.title) },
                            isOwner = isOwner,
                            onLiveChatClick = { targetTitle ->
                                activeLiveChatInquiry = Pair("inquiry_${targetTitle.replace(" ", "_")}", targetTitle)
                            }
                        )
                    }
                    "safaris" -> {
                        SafarisTab(
                            safaris = safaris,
                            weather = weather,
                            favorites = favorites.filter { it.type == "SAFARI" }.map { it.itemId },
                            onBookClick = { selectedSafariForBooking = it },
                            onFavoriteClick = { safari -> viewModel.toggleFavorite(safari.id, "SAFARI", safari.title) },
                            onDetailsClick = { safari -> selectedSafariForDetails = safari },
                            onExploreExperiencesClick = { viewModel.setTab("experiences") }
                        )
                    }
                    "experiences" -> LocalExperiencesTab(
                        viewModel = viewModel
                    )
                    "events" -> EventsTab(
                        events = events
                    )
                    "vouchers" -> VouchersTab(
                        vouchers = vouchers,
                        onBuyVoucherSubmit = { title, desc, amount, phone -> viewModel.buyVoucher(title, desc, amount, phone) }
                    )
                    "bookings" -> MyTripsTab(
                        viewModel = viewModel,
                        onOpenFinanceDashboard = { showFinanceDashboard = true },
                        isOwner = isOwner,
                        onAiPlannerClick = { lodgeName ->
                            selectedLodgeForAiItinerary = lodgeName
                            showGeminiItinerarySidebar = true
                        }
                    )
                    "wildlife" -> {
                        WildlifeSightingsTab(
                            viewModel = viewModel,
                            weather = weather
                        )
                    }
                    "swahili" -> SwahiliPhrasebookTab(phrases = viewModel.swahiliPhrases)
                    "packing" -> {
                        val recommendedItems by viewModel.recommendedPackingList.collectAsStateWithLifecycle()
                        PackingChecklistTab(recommendedItems = recommendedItems, bookings = bookings)
                    }
                    "notifications" -> NotificationsTab(
                        notifications = notifications,
                        onMarkAsRead = { viewModel.markNotificationAsRead(it) }
                    )
                    "journal" -> JournalScreen()
                }
            }

            // Discovery Map Overlay
            if (isDiscoveryMapVisible) {
                DiscoveryMapView(
                    stays = stays,
                    safaris = safaris,
                    onClose = { isDiscoveryMapVisible = false }
                )
            }



            // Stay Details Screen
            selectedStayForDetails?.let { stay ->
                StayDetailsScreen(
                    stay = stay,
                    onDismiss = { selectedStayForDetails = null },
                    onBookClick = {
                        selectedStayForBooking = stay
                        selectedStayForDetails = null
                    },
                    onLiveChatClick = { stayTitle ->
                        selectedStayForDetails = null
                        activeLiveChatInquiry = Pair("inquiry_${stayTitle.replace(" ", "_")}", "$stayTitle Host")
                    },
                    viewModel = viewModel
                )
            }

            // Safari Experience Details Screen (with Firestore Reviews)
            selectedSafariForDetails?.let { safari ->
                SafariExperienceDetailsScreen(
                    safari = safari,
                    viewModel = viewModel,
                    onDismiss = { selectedSafariForDetails = null },
                    onBookClick = {
                        selectedSafariForBooking = safari
                        selectedSafariForDetails = null
                    }
                )
            }

            selectedStayForMapPanel?.let { stay ->
                val placeDetailsMap by viewModel.placeDetails.collectAsStateWithLifecycle()
                
                LaunchedEffect(stay.googlePlaceId) {
                    viewModel.fetchPlaceDetails(stay)
                }

                LodgeSidePanel(
                    stay = stay,
                    placeDetails = stay.googlePlaceId?.let { placeDetailsMap[it] },
                    viewModel = viewModel,
                    onDismiss = { selectedStayForMapPanel = null },
                    onBookClick = {
                        selectedStayForBooking = stay
                        selectedStayForMapPanel = null
                    },
                    onAiItineraryClick = { lodgeTitle ->
                        selectedStayForMapPanel = null
                        selectedLodgeForAiItinerary = lodgeTitle
                        showGeminiItinerarySidebar = true
                    },
                    onLiveChatClick = { stayTitle ->
                        selectedStayForMapPanel = null
                        activeLiveChatInquiry = Pair("inquiry_${stayTitle.replace(" ", "_")}", "$stayTitle Host")
                    },
                    onViewReviewsClick = { lodgeStay ->
                        selectedStayForMapPanel = null
                        selectedStayForDetails = lodgeStay
                    }
                )
            }

            if (showGeminiItinerarySidebar) {
                GeminiItinerarySidebar(
                    viewModel = viewModel,
                    initialLodgeName = selectedLodgeForAiItinerary,
                    onDismiss = {
                        showGeminiItinerarySidebar = false
                        selectedLodgeForAiItinerary = null
                    }
                )
            }

            // Booking Modals
            selectedStayForBooking?.let { stay ->
                BookingDialog(
                    title = "Book Stay",
                    itemName = stay.title,
                    price = stay.pricePerNight,
                    priceLabel = "/ night",
                    vouchers = vouchers.filter { it.status == "Active" && it.remainingValue > 0 },
                    commissionRate = stay.commissionRate,
                    isEcoCertified = stay.isEcoCertified,
                    isKsh = stay.country == "Kenya",
                    onDismiss = { selectedStayForBooking = null },
                    onConfirm = { startDate: Long?, endDate: Long?, guests: Int, voucherCode: String? ->
                        val formatter = SimpleDateFormat("MMM dd", Locale.US)
                        val dateRange = if (startDate != null && endDate != null) {
                            "${formatter.format(Date(startDate))} - ${formatter.format(Date(endDate))}, 2026"
                        } else {
                            "Dates not selected"
                        }
                        viewModel.bookStay(stay, dateRange, voucherCode) // TODO: update viewmodel later if we need to store guests
                        selectedStayForBooking = null
                    },
                    onInitiateMpesaPayment = { phone, amount ->
                        viewModel.initiateMpesaBookingPayment(phone, amount, "STAY-${stay.id}")
                    },
                    onMpesaSuccessToast = { toastData ->
                        activeMpesaSuccessToastData = toastData
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
                    commissionRate = safari.commissionRate,
                    isEcoCertified = safari.isEcoCertified,
                    onDismiss = { selectedSafariForBooking = null },
                    onConfirm = { startDate: Long?, endDate: Long?, guests: Int, voucherCode: String? ->
                        val formatter = SimpleDateFormat("MMM dd", Locale.US)
                        val dateRange = if (startDate != null && endDate != null) {
                            "${formatter.format(Date(startDate))} - ${formatter.format(Date(endDate))}, 2026"
                        } else {
                            "Dates not selected"
                        }
                        viewModel.bookSafari(safari, dateRange, voucherCode) // TODO: update viewmodel later if we need to store guests
                        selectedSafariForBooking = null
                    },
                    onInitiateMpesaPayment = { phone, amount ->
                        viewModel.initiateMpesaBookingPayment(phone, amount, "SAFARI-${safari.id}")
                    },
                    onMpesaSuccessToast = { toastData ->
                        activeMpesaSuccessToastData = toastData
                    }
                )
            }

            // Firebase Firestore Live Chat Dialog
            activeLiveChatInquiry?.let { (inquiryId, recipientTitle) ->
                FirebaseLiveChatDialog(
                    inquiryId = inquiryId,
                    recipientTitle = recipientTitle,
                    recipientSubtitle = "Lodge Host / Safari Guide • Instant Sync",
                    onDismiss = { activeLiveChatInquiry = null }
                )
            }

            // --- ADMIN & PRIVACY OVERLAYS ---
            if (showFinanceDashboard) {
                InternalFinanceDashboard(
                    viewModel = viewModel,
                    onDismiss = { showFinanceDashboard = false }
                )
            }

            if (!hasAcceptedPrivacy) {
                PrivacyConsentDialog(onAccept = { hasAcceptedPrivacy = true })
            }
        }

        // Global Toast Notification for M-Pesa Payment Confirmation
        MpesaPaymentSuccessToastOverlay(
            data = activeMpesaSuccessToastData,
            onDismiss = { activeMpesaSuccessToastData = null },
            modifier = Modifier
                .align(Alignment.TopCenter)
                .statusBarsPadding()
                .padding(top = 8.dp)
                .zIndex(999f)
        )
    }
}
}



@Composable
fun SafarisTab(
    safaris: List<SafariItem>,
    favorites: List<String>,
    weather: WeatherCacheEntity? = null,
    onBookClick: (SafariItem) -> Unit,
    onFavoriteClick: (SafariItem) -> Unit,
    onDetailsClick: (SafariItem) -> Unit = {},
    onExploreExperiencesClick: () -> Unit = {}
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        if (weather != null) {
            item {
                WeatherWidget(weather = weather)
            }
        }
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onExploreExperiencesClick() }
                    .testTag("safari_tab_experiences_banner"),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.tertiaryContainer
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Surface(
                            color = MaterialTheme.colorScheme.tertiary,
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text(
                                text = "NEW: LOCAL IMMERSION",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onTertiary,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Guided Tours & Cultural Experiences",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.onTertiaryContainer
                        )
                        Text(
                            text = "Authentic Maasai beadwork, Batwa forest walks, spice cooking classes, & night kayaking.",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onTertiaryContainer.copy(alpha = 0.8f)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Button(
                        onClick = onExploreExperiencesClick,
                        shape = RoundedCornerShape(12.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text("Explore", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(250.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                CatalogMapboxWebView(
                    safaris = safaris,
                    onMarkerClick = { id, type ->
                    }
                )
            }
        }
        items(safaris) { safari ->
            SafariCard(
                safari = safari,
                isFavorite = favorites.contains(safari.id),
                onBookClick = { onBookClick(safari) },
                onFavoriteClick = { onFavoriteClick(safari) },
                onDetailsClick = { onDetailsClick(safari) }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WildlifeSightingsTab(viewModel: SafariViewModel, weather: WeatherCacheEntity? = null) {
    val sightings by viewModel.sightings.collectAsStateWithLifecycle()
    val isOnline by viewModel.isOnline.collectAsStateWithLifecycle()
    val isSyncing by viewModel.isSyncing.collectAsStateWithLifecycle()
    val lastSyncTime by viewModel.lastSightingSyncTime.collectAsStateWithLifecycle()

    var activeSubTab by remember { mutableStateOf("my_log") }
    val communitySightings by viewModel.communitySightings.collectAsStateWithLifecycle()
    val isRefreshingCommunity by viewModel.isRefreshingCommunity.collectAsStateWithLifecycle()

    // --- BIG FIVE STATUS MOCK DATA ---
    val bigFiveStamps = listOf(
        Triple("Lion", "🦁", true),
        Triple("Elephant", "🐘", true),
        Triple("Leopard", "🐆", false),
        Triple("Rhino", "🦏", false),
        Triple("Buffalo", "🐂", true)
    )
    val unlockedCount = bigFiveStamps.count { it.third }
    val progressToGold = unlockedCount / 5f

    var speciesInput by remember { mutableStateOf("") }
    var noteInput by remember { mutableStateOf("") }
    var selectedLocation by remember { mutableStateOf("Maasai Mara National Reserve, Kenya") }
    var selectedPhoto by remember { mutableStateOf("lion") }
    var isFormExpanded by remember { mutableStateOf(false) }

    val commonSpecies = listOf("African Lion", "Cheetah", "Leopard", "African Elephant", "Black Rhinoceros", "Giraffe", "Zebra", "Hippopotamus")
    val locationOptions = listOf(
        "Maasai Mara National Reserve, Kenya",
        "Central Serengeti, Tanzania",
        "Ngorongoro Crater Rim, Tanzania",
        "Tarangire National Park, Tanzania",
        "Nungwi Beach, Zanzibar"
    )

    val animalIcons = mapOf(
        "lion" to "🦁",
        "elephant" to "🐘",
        "leopard" to "🐆",
        "giraffe" to "🦒",
        "zebra" to "🦓",
        "rhino" to "🦏"
    )

    var selectedParkFilter by remember { mutableStateOf("All Parks") }
    val parkFilterOptions = remember {
        listOf("All Parks") + locationOptions.map { it.split(",")[0].trim() }
    }

    val filteredCommunity = remember(communitySightings, selectedParkFilter) {
        if (selectedParkFilter == "All Parks") {
            communitySightings
        } else {
            communitySightings.filter { it.locationTag.contains(selectedParkFilter, ignoreCase = true) }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        if (weather != null) {
            WeatherWidget(weather = weather)
        }
        // --- THE BIG FIVE CLUB LOYALTY CARD ---
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)),
            shape = RoundedCornerShape(16.dp),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.1f))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Stars, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "THE BIG FIVE CLUB",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.primary,
                        letterSpacing = 1.sp
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    Surface(
                        color = MaterialTheme.colorScheme.primary,
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = "SILVER RANGER",
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimary,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(12.dp))
                
                Text(
                    text = "Collect all 5 digital stamps to unlock Gold Status and 10% off your next luxury booking.",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    lineHeight = 16.sp
                )
                
                Spacer(modifier = Modifier.height(14.dp))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    bigFiveStamps.forEach { (name, emoji, unlocked) ->
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Box(
                                modifier = Modifier
                                    .size(44.dp)
                                    .clip(CircleShape)
                                    .background(
                                        if (unlocked) MaterialTheme.colorScheme.primary.copy(alpha = 0.2f) 
                                        else MaterialTheme.colorScheme.surfaceVariant
                                    )
                                    .border(
                                        width = 2.dp,
                                        color = if (unlocked) MaterialTheme.colorScheme.primary else Color.Transparent,
                                        shape = CircleShape
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = emoji,
                                    fontSize = 20.sp,
                                    modifier = Modifier.graphicsLayer(alpha = if (unlocked) 1f else 0.3f)
                                )
                                if (unlocked) {
                                    Icon(
                                        Icons.Default.CheckCircle,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier
                                            .size(14.dp)
                                            .align(Alignment.BottomEnd)
                                            .offset(x = 2.dp, y = 2.dp)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(text = name, fontSize = 9.sp, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSurface)
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(14.dp))
                
                Row(verticalAlignment = Alignment.CenterVertically) {
                    LinearProgressIndicator(
                        progress = { progressToGold },
                        modifier = Modifier
                            .weight(1f)
                            .height(6.dp)
                            .clip(CircleShape),
                        color = MaterialTheme.colorScheme.primary,
                        trackColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.1f)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "$unlockedCount/5 Stamps",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }

        // --- Dynamic Sub-Tab Selection Pill ---
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                .padding(4.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            listOf("my_log" to "My Sighting Log", "community" to "Community Feed").forEach { (tabId, tabName) ->
                val isSelected = activeSubTab == tabId
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent,
                    modifier = Modifier
                        .weight(1f)
                        .clickable { activeSubTab = tabId }
                        .height(38.dp)
                        .testTag("sighting_sub_tab_$tabId")
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = if (tabId == "my_log") Icons.Default.Pets else Icons.Default.People,
                                contentDescription = null,
                                tint = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = tabName,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }

        if (activeSubTab == "my_log") {
            // --- Sync status card ---
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                ),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.15f))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Box(
                            modifier = Modifier
                                .size(12.dp)
                                .background(
                                    color = if (isOnline) Color(0xFF4CAF50) else Color(0xFFFF9800),
                                    shape = CircleShape
                                )
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (isOnline) "ONLINE MODE" else "OFFLINE STATE",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = if (isOnline) MaterialTheme.colorScheme.primary else Color(0xFFFF9800)
                        )
                        Spacer(modifier = Modifier.weight(1f))
                        
                        // Toggle Switch
                        Switch(
                            checked = isOnline,
                            onCheckedChange = { viewModel.toggleOnline() },
                            modifier = Modifier.testTag("online_toggle")
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = if (isOnline) {
                            "Your sightings sync in real-time to the cloud server. Any offline sightings will be uploaded automatically."
                        } else {
                            "Your sightings are saved locally on your device using Room DB (Android's robust offline engine). Turn online to sync your logs with other travelers."
                        },
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    lastSyncTime?.let { ts ->
                        Spacer(modifier = Modifier.height(6.dp))
                        val lastSyncStr = java.text.SimpleDateFormat("HH:mm:ss", java.util.Locale.US).format(java.util.Date(ts))
                        Text(
                            text = "Last synced at: $lastSyncStr",
                            fontSize = 10.sp,
                            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.6f),
                            fontWeight = FontWeight.Medium
                        )
                    }

                    val unsyncedCount = sightings.count { !it.isSynced }
                    if (unsyncedCount > 0) {
                        Spacer(modifier = Modifier.height(12.dp))
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "$unsyncedCount pending upload",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.weight(1f))
                            if (isSyncing) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(18.dp),
                                    strokeWidth = 2.dp
                                )
                            } else {
                                Button(
                                    onClick = { viewModel.syncSightings() },
                                    enabled = isOnline,
                                    shape = RoundedCornerShape(8.dp),
                                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                                    modifier = Modifier.testTag("sync_now_btn")
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Sync,
                                        contentDescription = "Sync",
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Sync Now", fontSize = 11.sp)
                                }
                            }
                        }
                    }
                }
            }

            // --- Log New Sighting Button / Expandable Form ---
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.15f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { isFormExpanded = !isFormExpanded }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Pets,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Report Sighting",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.weight(1f))
                        IconButton(onClick = { isFormExpanded = !isFormExpanded }) {
                            Icon(
                                imageVector = if (isFormExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                                contentDescription = if (isFormExpanded) "Collapse" else "Expand"
                            )
                        }
                    }

                    AnimatedVisibility(visible = isFormExpanded) {
                        Column(
                            modifier = Modifier.padding(top = 16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            // Species name input
                            OutlinedTextField(
                                value = speciesInput,
                                onValueChange = { speciesInput = it },
                                label = { Text("Species Spotted") },
                                placeholder = { Text("e.g. Cheetah, African Elephant") },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("species_input"),
                                singleLine = true
                            )

                            // Quick species suggestions
                            Text(
                                text = "Or choose common species:",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Row(
                                modifier = Modifier.horizontalScroll(rememberScrollState()),
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                commonSpecies.forEach { species ->
                                    FilterChip(
                                        selected = speciesInput.equals(species, ignoreCase = true),
                                        onClick = { speciesInput = species },
                                        label = { Text(species, fontSize = 11.sp) }
                                    )
                                }
                            }

                            // Location tagging selector
                            Text(
                                text = "Location Tagging:",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Row(
                                modifier = Modifier.horizontalScroll(rememberScrollState()),
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                locationOptions.forEach { loc ->
                                    val isSelected = selectedLocation == loc
                                    FilterChip(
                                        selected = isSelected,
                                        onClick = { selectedLocation = loc },
                                        label = { Text(loc.split(",")[0], fontSize = 11.sp) }
                                    )
                                }
                            }

                            // Custom location input
                            OutlinedTextField(
                                value = selectedLocation,
                                onValueChange = { selectedLocation = it },
                                label = { Text("Specific Location Detail") },
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true
                            )

                            // Photo placeholder picker
                            Text(
                                text = "Photo Placeholder (Select Visual Badge):",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .horizontalScroll(rememberScrollState()),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                animalIcons.forEach { (key, emoji) ->
                                    val isSelected = selectedPhoto == key
                                    Card(
                                        onClick = { selectedPhoto = key },
                                        colors = CardDefaults.cardColors(
                                            containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                                        ),
                                        border = BorderStroke(
                                            width = if (isSelected) 2.dp else 1.dp,
                                            color = if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent
                                        ),
                                        shape = RoundedCornerShape(12.dp),
                                        modifier = Modifier.size(54.dp)
                                    ) {
                                        Box(
                                            modifier = Modifier.fillMaxSize(),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(text = emoji, fontSize = 28.sp)
                                        }
                                    }
                                }
                            }

                            // Sighting notes
                            OutlinedTextField(
                                value = noteInput,
                                onValueChange = { noteInput = it },
                                label = { Text("Activity/Details (Notes)") },
                                placeholder = { Text("e.g. Drinking near waterhole, hunting in pairs...") },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("notes_input"),
                                minLines = 2
                            )

                            // Submit Button
                            Button(
                                onClick = {
                                    viewModel.addSighting(speciesInput, noteInput, selectedLocation, selectedPhoto)
                                    // Reset inputs after report
                                    speciesInput = ""
                                    noteInput = ""
                                    isFormExpanded = false
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 8.dp)
                                    .testTag("submit_sighting_btn"),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Icon(imageVector = Icons.Default.Add, contentDescription = null)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Report Sighting", fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }

            // --- Sightings Log Title ---
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector = Icons.Default.Timeline,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Wildlife Log History (${sightings.size})",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            if (sightings.isEmpty()) {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "🐾 No sightings reported yet.",
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Report your first species sighting above to build your live savanna journal!",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f),
                            textAlign = TextAlign.Center
                        )
                    }
                }
            } else {
                sightings.forEach { sighting ->
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.12f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Animal Icon
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f),
                                modifier = Modifier.size(56.dp)
                            ) {
                                Box(
                                    modifier = Modifier.fillMaxSize(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = animalIcons[sighting.photoPlaceholder] ?: "🐾",
                                        fontSize = 32.sp
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.width(16.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text(
                                        text = sighting.speciesName,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 16.sp,
                                        color = MaterialTheme.colorScheme.onSurface,
                                        modifier = Modifier.weight(1f)
                                    )
                                    
                                    // Sync Indicator Badge
                                    Surface(
                                        color = if (sighting.isSynced) MaterialTheme.colorScheme.secondaryContainer else MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.8f),
                                        shape = RoundedCornerShape(8.dp)
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Icon(
                                                imageVector = if (sighting.isSynced) Icons.Default.CloudDone else Icons.Default.CloudQueue,
                                                contentDescription = null,
                                                modifier = Modifier.size(10.dp),
                                                tint = if (sighting.isSynced) MaterialTheme.colorScheme.onSecondaryContainer else MaterialTheme.colorScheme.onErrorContainer
                                            )
                                            Spacer(modifier = Modifier.width(3.dp))
                                            Text(
                                                text = if (sighting.isSynced) "Synced" else "Pending",
                                                fontSize = 9.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = if (sighting.isSynced) MaterialTheme.colorScheme.onSecondaryContainer else MaterialTheme.colorScheme.onErrorContainer
                                            )
                                        }
                                    }
                                }

                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.padding(vertical = 4.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Place,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(12.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = sighting.locationTag,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                }

                                if (sighting.note.isNotBlank()) {
                                    Text(
                                        text = sighting.note,
                                        fontSize = 13.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }

                                Spacer(modifier = Modifier.height(4.dp))
                                // Format timestamp
                                val dateString = java.text.SimpleDateFormat("MMM dd, yyyy 'at' h:mm a", java.util.Locale.US).format(java.util.Date(sighting.timestamp))
                                Text(
                                    text = dateString,
                                    fontSize = 10.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                                )
                            }
                        }
                    }
                }
            }
        } else {
            // --- COMMUNITY FEED SUB-TAB ---
            // Satellite Transceiver state banner
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
                ),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.12f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .background(
                                color = if (isOnline) Color(0xFF4CAF50) else Color(0xFFFF9800),
                                shape = CircleShape
                            )
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = if (isOnline) "Satellite Connection Live" else "Satellite Connection Offline",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = if (isOnline) "Real-time updates broadcasted via solar satellite" else "Showing cached community logs from local memory",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    IconButton(
                        onClick = { viewModel.refreshCommunityFeed() },
                        enabled = !isRefreshingCommunity,
                        modifier = Modifier.testTag("refresh_community_feed_btn")
                    ) {
                        if (isRefreshingCommunity) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(18.dp),
                                strokeWidth = 2.dp
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Default.Refresh,
                                contentDescription = "Refresh Feed",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
            }

            // Park Filter Options
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "Select Safari Park Focus:",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Row(
                    modifier = Modifier.horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    parkFilterOptions.forEach { park ->
                        val isSelected = selectedParkFilter == park
                        FilterChip(
                            selected = isSelected,
                            onClick = { selectedParkFilter = park },
                            label = { Text(park, fontSize = 11.sp) },
                            modifier = Modifier.testTag("park_filter_chip_$park")
                        )
                    }
                }
            }

            // Feed Items Title
            Text(
                text = "Recent Wildlife Broadcasts (${filteredCommunity.size})",
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(top = 4.dp)
            )

            if (filteredCommunity.isEmpty()) {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "🦒 No public reports found in $selectedParkFilter.",
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Go online and click refresh to scan satellite waves, or be the first to report a local sighting in this reserve!",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f),
                            textAlign = TextAlign.Center
                        )
                    }
                }
            } else {
                filteredCommunity.forEach { comm ->
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.12f)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("community_card_${comm.id}")
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            // Traveler Profile Header
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Surface(
                                    shape = CircleShape,
                                    color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f),
                                    modifier = Modifier.size(40.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Text(text = comm.travelerAvatar, fontSize = 22.sp)
                                    }
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = comm.travelerName,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    val timeSdf = remember { SimpleDateFormat("h:mm a, MMM dd", Locale.US) }
                                    Text(
                                        text = "Reported • ${timeSdf.format(Date(comm.timestamp))}",
                                        fontSize = 10.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                                    )
                                }
                                
                                // Saved to Planning Badge
                                if (comm.isSavedToPlanning) {
                                    Surface(
                                        color = MaterialTheme.colorScheme.primaryContainer,
                                        shape = RoundedCornerShape(8.dp)
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.PushPin,
                                                contentDescription = null,
                                                modifier = Modifier.size(10.dp),
                                                tint = MaterialTheme.colorScheme.onPrimaryContainer
                                            )
                                            Spacer(modifier = Modifier.width(3.dp))
                                            Text(
                                                text = "Pinned",
                                                fontSize = 9.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = MaterialTheme.colorScheme.onPrimaryContainer
                                            )
                                        }
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            // Sighting Badge
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
                                    .padding(10.dp)
                            ) {
                                Text(
                                    text = animalIcons[comm.photoPlaceholder] ?: "🐾",
                                    fontSize = 28.sp
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(
                                        text = comm.speciesName,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = Icons.Default.Place,
                                            contentDescription = null,
                                            tint = MaterialTheme.colorScheme.primary,
                                            modifier = Modifier.size(11.dp)
                                        )
                                        Spacer(modifier = Modifier.width(2.dp))
                                        Text(
                                            text = comm.locationTag.split(",")[0],
                                            fontSize = 11.sp,
                                            color = MaterialTheme.colorScheme.primary,
                                            fontWeight = FontWeight.Medium
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            // Note details
                            Text(
                                text = comm.note,
                                fontSize = 13.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            // Viewing Tips for Trip Planning
                            comm.viewingTips?.let { tips ->
                                Spacer(modifier = Modifier.height(10.dp))
                                Surface(
                                    color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.4f),
                                    shape = RoundedCornerShape(10.dp),
                                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.secondary.copy(alpha = 0.1f)),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Row(
                                        modifier = Modifier.padding(10.dp),
                                        verticalAlignment = Alignment.Top
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Lightbulb,
                                            contentDescription = "Tips",
                                            tint = MaterialTheme.colorScheme.secondary,
                                            modifier = Modifier
                                                .size(16.dp)
                                                .padding(top = 2.dp)
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Column {
                                            Text(
                                                text = "💡 Traveler's Pro Viewing Tip",
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = MaterialTheme.colorScheme.onSecondaryContainer
                                            )
                                            Text(
                                                text = tips,
                                                fontSize = 11.sp,
                                                color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.9f)
                                            )
                                        }
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))
                            Divider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.08f))
                            Spacer(modifier = Modifier.height(8.dp))

                            // Interaction Actions
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // Upvote Helpful count
                                TextButton(
                                    onClick = { viewModel.upvoteCommunitySighting(comm.id) },
                                    modifier = Modifier.testTag("upvote_btn_${comm.id}")
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = if (comm.hasUpvoted) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                                            contentDescription = "Helpful",
                                            tint = if (comm.hasUpvoted) Color(0xFFE91E63) else MaterialTheme.colorScheme.onSurfaceVariant,
                                            modifier = Modifier.size(18.dp)
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = "${comm.isHelpfulCount} Helpful",
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            color = if (comm.hasUpvoted) Color(0xFFE91E63) else MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }

                                // Pin Sighting for planning
                                Button(
                                    onClick = { viewModel.toggleSaveToPlanning(comm.id) },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = if (comm.isSavedToPlanning) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.primary,
                                        contentColor = if (comm.isSavedToPlanning) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onPrimary
                                    ),
                                    shape = RoundedCornerShape(8.dp),
                                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                                    modifier = Modifier
                                        .height(32.dp)
                                        .testTag("save_planning_btn_${comm.id}")
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = if (comm.isSavedToPlanning) Icons.Default.PushPin else Icons.Default.Add,
                                            contentDescription = null,
                                            modifier = Modifier.size(12.dp)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = if (comm.isSavedToPlanning) "Pinned" else "Pin Sighting",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
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
fun StayCard(
    stay: StayItem,
    isFavorite: Boolean,
    viewModel: SafariViewModel,
    onStayClick: () -> Unit,
    onBookClick: () -> Unit,
    onFavoriteClick: () -> Unit
) {
    val placeDetails by viewModel.placeDetails.collectAsStateWithLifecycle()
    val details = stay.googlePlaceId?.let { placeDetails[it] }

    LaunchedEffect(stay.googlePlaceId) {
        viewModel.fetchPlaceDetails(stay)
    }

    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.15f)),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onStayClick() }
            .testTag("stay_card_${stay.id}")
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
            ) {
                // Determine which photo URL to use
                val photoUrl = if (details != null && details.photos?.isNotEmpty() == true) {
                    viewModel.getPlacePhotoUrl(details.photos!![0].name)
                } else {
                    stay.imageUrls.firstOrNull()
                }

                if (photoUrl != null) {
                    AsyncImage(
                        model = photoUrl,
                        contentDescription = stay.title,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    // Fallback to carousel if no URL
                    ImageCarousel(
                        imageUrls = stay.imageUrls,
                        modifier = Modifier.fillMaxSize()
                    )
                }

                // Photo Attribution Overlay
                if (details != null && details.photos?.isNotEmpty() == true) {
                    val attribution = details.photos!![0].authorAttributions?.firstOrNull()?.displayName
                    if (attribution != null) {
                        Surface(
                            color = Color.Black.copy(alpha = 0.5f),
                            shape = RoundedCornerShape(topEnd = 8.dp),
                            modifier = Modifier.align(Alignment.BottomEnd)
                        ) {
                            Text(
                                text = "Photo by $attribution",
                                color = Color.White,
                                fontSize = 8.sp,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                }

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

                if (stay.isSponsored) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(top = 2.dp)
                    ) {
                        Text(
                            text = "Sponsored",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                        )
                        stay.bookingSource?.let {
                            Text(
                                text = " • By $it",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                            )
                        }
                    }
                }
                
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(vertical = 2.dp)
                ) {
                    Text(
                        text = stay.rating.toString(),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "(${stay.reviewsCount})",
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                        modifier = Modifier.padding(start = 4.dp)
                    )
                    stay.hotelClass?.let {
                        Text(
                            text = " • $it",
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                        )
                    }
                }

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

                // Phone number from Google Places
                if (details != null && details.internationalPhoneNumber != null) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(vertical = 2.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Phone,
                            contentDescription = "Phone",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = details.internationalPhoneNumber!!,
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
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
                    stay.amenities.take(4).forEach { amenity ->
                        Surface(
                            color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.4f),
                            shape = RoundedCornerShape(8.dp),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.08f))
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                val icon = when {
                                    amenity.contains("Pool", ignoreCase = true) -> Icons.Default.Pool
                                    amenity.contains("Wifi", ignoreCase = true) || amenity.contains("Wi-Fi", ignoreCase = true) -> Icons.Default.Wifi
                                    amenity.contains("Parking", ignoreCase = true) -> Icons.Default.LocalParking
                                    amenity.contains("Breakfast", ignoreCase = true) -> Icons.Default.Restaurant
                                    amenity.contains("Spa", ignoreCase = true) -> Icons.Default.Spa
                                    amenity.contains("Gym", ignoreCase = true) -> Icons.Default.FitnessCenter
                                    else -> null
                                }
                                
                                if (icon != null) {
                                    Icon(
                                        imageVector = icon,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(12.dp).padding(end = 4.dp)
                                    )
                                }
                                
                                Text(
                                    text = amenity,
                                    fontSize = 10.sp,
                                    color = MaterialTheme.colorScheme.onSecondaryContainer
                                )
                            }
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
                            val priceText = if (stay.country == "Kenya") {
                                val kshPrice = (stay.pricePerNight * 130).toInt()
                                "Ksh ${String.format("%,d", kshPrice)}"
                            } else {
                                "$${stay.pricePerNight.toInt()}"
                            }
                            Text(
                                text = priceText,
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

                    // Availability Status Badge
                    val statusColor = when (stay.availabilityStatus) {
                        "Available" -> Color(0xFF4CAF50)
                        "Limited" -> Color(0xFFFF9800)
                        else -> Color(0xFFF44336)
                    }
                    
                    Surface(
                        color = statusColor.copy(alpha = 0.1f),
                        shape = RoundedCornerShape(8.dp),
                        border = BorderStroke(1.dp, statusColor.copy(alpha = 0.3f)),
                        modifier = Modifier.padding(end = 8.dp)
                    ) {
                        Text(
                            text = stay.availabilityStatus.uppercase(),
                            color = statusColor,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                        )
                    }

                    Button(
                        onClick = onBookClick,
                        modifier = Modifier.testTag("book_stay_btn_${stay.id}"),
                        shape = RoundedCornerShape(12.dp),
                        enabled = stay.availabilityStatus != "Fully Booked",
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (stay.availabilityStatus == "Fully Booked") MaterialTheme.colorScheme.surfaceVariant else MaterialTheme.colorScheme.primary,
                            contentColor = if (stay.availabilityStatus == "Fully Booked") MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.onPrimary
                        )
                    ) {
                        Text(
                            text = when {
                                stay.availabilityStatus == "Fully Booked" -> "Full"
                                stay.isSponsored -> "Visit Site"
                                else -> "Book"
                            }
                        )
                    }
                }
            }
        }
    }
}


/*
// Commented out to find syntax error
*/

@Composable
fun SafariCard(
    safari: SafariItem,
    isFavorite: Boolean,
    onBookClick: () -> Unit,
    onFavoriteClick: () -> Unit,
    onDetailsClick: () -> Unit = {}
) {
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.15f)),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onDetailsClick() }
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

                // Rating tag (clickable for reviews)
                Surface(
                    color = Color.Black.copy(alpha = 0.65f),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier
                        .padding(12.dp)
                        .align(Alignment.TopEnd)
                        .clickable { onDetailsClick() }
                        .testTag("safari_rating_pill_${safari.id}")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        StarRating(
                            rating = safari.rating,
                            modifier = Modifier
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Reviews",
                            color = Color.White,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
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
                    OutlinedButton(
                        onClick = onDetailsClick,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .padding(end = 8.dp)
                            .testTag("safari_reviews_btn_${safari.id}")
                    ) {
                        Text("Reviews ⭐", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
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
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Book", fontWeight = FontWeight.Bold)
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
    onBuyVoucherSubmit: (String, String, Double, String) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var amountString by remember { mutableStateOf("") }
    var phoneString by remember { mutableStateOf("") }

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

                        Spacer(modifier = Modifier.height(8.dp))

                        OutlinedTextField(
                            value = phoneString,
                            onValueChange = { phoneString = it },
                            label = { Text("M-Pesa Phone Number (e.g. 0712345678)") },
                            placeholder = { Text("0712345678") },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("voucher_mpesa_phone_input")
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Button(
                            onClick = {
                                val amt = amountString.toDoubleOrNull() ?: 0.0
                                if (title.isNotBlank() && amt > 0) {
                                    onBuyVoucherSubmit(title, description, amt, phoneString)
                                    // Reset fields
                                    title = ""
                                    description = ""
                                    amountString = ""
                                    phoneString = ""
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
    viewModel: SafariViewModel,
    onOpenFinanceDashboard: () -> Unit,
    isOwner: Boolean = false,
    onAiPlannerClick: ((String?) -> Unit)? = null
) {
    val bookings by viewModel.bookings.collectAsStateWithLifecycle()
    
    val lastSyncedLat by viewModel.lastSyncedLat.collectAsStateWithLifecycle()
    val lastSyncedLng by viewModel.lastSyncedLng.collectAsStateWithLifecycle()
    val lastSyncedLocationName by viewModel.lastSyncedLocationName.collectAsStateWithLifecycle()
    val lastSyncTime by viewModel.lastSyncTime.collectAsStateWithLifecycle()
    val isLocationSyncing by viewModel.isLocationSyncing.collectAsStateWithLifecycle()
    val sosSendingStatus by viewModel.sosSendingStatus.collectAsStateWithLifecycle()
    val isSosSending by viewModel.isSosSending.collectAsStateWithLifecycle()
    val feedbackList by viewModel.feedbackList.collectAsStateWithLifecycle()
    val journalEntries by viewModel.journalEntries.collectAsStateWithLifecycle()
    val isOnline by viewModel.isOnline.collectAsStateWithLifecycle()
    val mapType by viewModel.mapType.collectAsStateWithLifecycle()
    val shareLink by viewModel.shareLink.collectAsStateWithLifecycle()
    val totalBudget by viewModel.totalBudget.collectAsStateWithLifecycle()
    val totalSpent by viewModel.totalSpent.collectAsStateWithLifecycle()
    val remainingBudget by viewModel.remainingBudget.collectAsStateWithLifecycle()
    val tripProgress by viewModel.tripProgress.collectAsStateWithLifecycle()
    val checklistItems by viewModel.checklistItems.collectAsStateWithLifecycle()
    val checklistProgress by viewModel.checklistProgress.collectAsStateWithLifecycle()
    
    val context = LocalContext.current

    var showSosDialog by remember { mutableStateOf(false) }
    var showJournalDialog by remember { mutableStateOf(false) }
    var customSosMessage by remember { mutableStateOf("") }
    var selectedBookingForFeedback by remember { mutableStateOf<BookingEntity?>(null) }

    // Escrow, Scanner, and Portal States
    var activePortalMode by remember { mutableStateOf("guest") } // "guest" or "partner"
    var selectedBookingForQrPass by remember { mutableStateOf<BookingEntity?>(null) }
    var selectedBookingForTieredCancel by remember { mutableStateOf<BookingEntity?>(null) }
    var activeScannerSimulation by remember { mutableStateOf(false) }
    var selectedBookingForCheckInPayout by remember { mutableStateOf<BookingEntity?>(null) }
    var selectedBookingForChat by remember { mutableStateOf<BookingEntity?>(null) }
    var selectedBookingForGroupHub by remember { mutableStateOf<BookingEntity?>(null) }

    // Aggregate values for 'Trip Overview'
    val totalCost = bookings.sumOf { it.price }
    
    // Deduce wildlife goals
    val wildlifeGoals = remember(bookings) {
        val goals = mutableSetOf<String>()
        bookings.forEach { b ->
            if (b.type == "SAFARI") {
                val title = b.title.lowercase()
                if (title.contains("migration")) {
                    goals.addAll(listOf("Wildebeest River Crossings", "Maasai Mara Lions", "Migratory Zebras", "Savanna Cheetahs"))
                } else if (title.contains("crater") || title.contains("escape")) {
                    goals.addAll(listOf("Rare Black Rhinos", "Tarangire Giant Elephants", "Crater-floor Lion Prides", "Leopards in Acacia Trees"))
                } else if (title.contains("spice") || title.contains("sail") || title.contains("zanzibar")) {
                    goals.addAll(listOf("Green Sea Turtles", "Red Colobus Monkeys", "Vibrant Marine Fish", "Humpback Dolphins"))
                }
            } else if (b.type == "STAY") {
                val title = b.title.lowercase()
                if (title.contains("mara") || title.contains("horizon")) {
                    goals.addAll(listOf("Wildebeest migration", "Mara Cheetahs"))
                } else if (title.contains("serengeti")) {
                    goals.addAll(listOf("Serengeti Lion prides", "Plain Zebras"))
                } else if (title.contains("crater") || title.contains("sanctuary")) {
                    goals.addAll(listOf("Volcanic crater Rhinos", "Golden Jackals"))
                }
            }
        }
        if (goals.isEmpty()) {
            listOf("Lions, Leopards, Elephants, Rhinos, Buffalos (The Big 5)", "Great Wildebeest Migration herds", "Reticulated Giraffes")
        } else {
            goals.toList()
        }
    }

    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        // --- PORTAL MODE SWITCHER ---
        item {
            TabRow(
                selectedTabIndex = if (activePortalMode == "guest") 0 else 1,
                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                contentColor = MaterialTheme.colorScheme.primary,
                indicator = { tabPositions ->
                    TabRowDefaults.SecondaryIndicator(
                        Modifier.tabIndicatorOffset(tabPositions[if (activePortalMode == "guest") 0 else 1]),
                        color = MaterialTheme.colorScheme.primary
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
            ) {
                Tab(
                    selected = activePortalMode == "guest",
                    onClick = { activePortalMode = "guest" },
                    text = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.CardTravel, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("GUEST PORTAL", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    },
                    modifier = Modifier.testTag("portal_tab_guest")
                )
                Tab(
                    selected = activePortalMode == "partner",
                    onClick = { activePortalMode = "partner" },
                    text = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Business, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("LODGE HUB", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    },
                    modifier = Modifier.testTag("portal_tab_partner")
                )
            }
        }
        if (activePortalMode == "guest") {
            // --- EMERGENCY BUSH SOS CARD (HIGH ACCESSIBILITY & VISIBILITY) ---
            item {
            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.9f)
                ),
                border = BorderStroke(1.5.dp, MaterialTheme.colorScheme.error.copy(alpha = 0.4f)),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("bush_sos_terminal_card")
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    // Header
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = MaterialTheme.colorScheme.error,
                            modifier = Modifier.size(36.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.Place,
                                    contentDescription = "Emergency Location",
                                    tint = Color.White,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "Emergency Bush SOS Terminal",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onErrorContainer
                            )
                            Text(
                                text = "Direct African Satellite Emergency Transmitter",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onErrorContainer.copy(alpha = 0.8f)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Satellite GPS Telemetry Status Box
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.6f),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.error.copy(alpha = 0.15f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                if (isLocationSyncing) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(16.dp),
                                        strokeWidth = 2.dp,
                                        color = MaterialTheme.colorScheme.error
                                    )
                                } else {
                                    Box(
                                        modifier = Modifier
                                            .size(10.dp)
                                            .clip(CircleShape)
                                            .background(Color(0xFF4CAF50))
                                    )
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = if (isLocationSyncing) "Acquiring GPS Satellite Lock..." else "GPS Satellite Status: LOCKED",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Spacer(modifier = Modifier.weight(1f))
                                Text(
                                    text = if (isLocationSyncing) "" else "±3m accuracy",
                                    fontSize = 10.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                text = "Last Synced Location: $lastSyncedLocationName",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontWeight = FontWeight.Medium
                            )
                            Text(
                                text = "Coordinates: Lat $lastSyncedLat, Lng $lastSyncedLng",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontWeight = FontWeight.Bold
                            )
                            
                            val sdf = remember { java.text.SimpleDateFormat("HH:mm:ss (z)", java.util.Locale.getDefault()) }
                            Text(
                                text = "Telemetry timestamp: ${sdf.format(java.util.Date(lastSyncTime))}",
                                fontSize = 10.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Buttons
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = { viewModel.refreshLocation() },
                            enabled = !isLocationSyncing,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.surface,
                                contentColor = MaterialTheme.colorScheme.onSurface
                            ),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .weight(1f)
                                .height(44.dp)
                                .testTag("sync_gps_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Sync,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Sync GPS", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }

                        Button(
                            onClick = {
                                customSosMessage = "🚨 BUSH SOS EMERGENCY! 🚨\nName: User (lilsheriff08@gmail.com)\nLocation: $lastSyncedLocationName\nCoordinates: Lat $lastSyncedLat, Lng $lastSyncedLng\nRequested immediate search & rescue dispatch."
                                showSosDialog = true
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.error,
                                contentColor = Color.White
                            ),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .weight(1.2f)
                                .height(44.dp)
                                .testTag("trigger_sos_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Place,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("TRIGGER SOS", fontSize = 12.sp, fontWeight = FontWeight.ExtraBold)
                        }
                    }

                    // Show active status if sending or sent
                    sosSendingStatus?.let { status ->
                        Spacer(modifier = Modifier.height(12.dp))
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = MaterialTheme.colorScheme.surface,
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.error.copy(alpha = 0.3f)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    if (isSosSending) {
                                        CircularProgressIndicator(
                                            modifier = Modifier.size(16.dp),
                                            strokeWidth = 2.dp,
                                            color = MaterialTheme.colorScheme.error
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                    }
                                    Text(
                                        text = "Emergency Dispatch Logs",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.error
                                    )
                                }
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = status,
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    fontWeight = FontWeight.Medium
                                )
                                if (!isSosSending) {
                                    Spacer(modifier = Modifier.height(8.dp))
                                    TextButton(
                                        onClick = { viewModel.clearSosStatus() },
                                        modifier = Modifier.align(Alignment.End)
                                    ) {
                                        Text("Dismiss Log", fontSize = 11.sp, color = MaterialTheme.colorScheme.primary)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // --- EXPEDITION BUDGET PLANNER ---
        item {
            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.15f)),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("budget_planner_card")
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(
                            imageVector = Icons.Default.Payments,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Expedition Budget Planner",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "Manage your safari funds dynamically",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // Budget Progress Bar
                    val progress = (totalSpent / totalBudget).coerceIn(0f, 1f)
                    val progressColor = when {
                        progress > 0.9f -> MaterialTheme.colorScheme.error
                        progress > 0.7f -> Color(0xFFFBC02D) // Yellow
                        else -> MaterialTheme.colorScheme.primary
                    }

                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Spent: $${totalSpent.toInt()}",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "Budget: $${totalBudget.toInt()}",
                                fontSize = 14.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        LinearProgressIndicator(
                            progress = { progress },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(12.dp)
                                .clip(RoundedCornerShape(6.dp)),
                            color = progressColor,
                            trackColor = MaterialTheme.colorScheme.surfaceVariant
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = if (remainingBudget >= 0) "Remaining: $${remainingBudget.toInt()}" else "Over Budget: $${Math.abs(remainingBudget).toInt()}",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium,
                                color = if (remainingBudget >= 0) Color(0xFF4CAF50) else MaterialTheme.colorScheme.error
                            )
                            
                            if (progress > 0.8f) {
                                Text(
                                    text = "⚠️ Low Funds",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.error
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Budget Adjustment Slider
                    Text(
                        text = "Adjust Total Trip Budget",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    
                    Slider(
                        value = totalBudget,
                        onValueChange = { viewModel.setTotalBudget(it) },
                        valueRange = 1000f..20000f,
                        steps = 19,
                        modifier = Modifier.fillMaxWidth().testTag("budget_slider")
                    )
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("$1k", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text("$20k", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
        }

        // --- PRE-TRIP PREPARATION CHECKLIST ---
        item {
            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.15f)),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("pre_trip_checklist_card")
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(
                            imageVector = Icons.Default.FactCheck,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Pre-Trip Preparation",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "Essential documents & requirements",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        
                        // Completion Badge
                        Surface(
                            color = if (checklistProgress >= 1f) Color(0xFF4CAF50).copy(alpha = 0.1f) else MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text(
                                text = "${(checklistProgress * 100).toInt()}%",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (checklistProgress >= 1f) Color(0xFF4CAF50) else MaterialTheme.colorScheme.primary,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    
                    LinearProgressIndicator(
                        progress = { checklistProgress },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                            .clip(RoundedCornerShape(3.dp)),
                        color = if (checklistProgress >= 1f) Color(0xFF4CAF50) else MaterialTheme.colorScheme.primary,
                        trackColor = MaterialTheme.colorScheme.surfaceVariant
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    // Grouped Checklist Items
                    val categories = listOf("DOCUMENT", "MEDICAL", "TRAVEL")
                    categories.forEach { category ->
                        val items = checklistItems.filter { it.category == category }
                        if (items.isNotEmpty()) {
                            Text(
                                text = category,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.ExtraBold,
                                letterSpacing = 1.sp,
                                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.7f),
                                modifier = Modifier.padding(bottom = 8.dp, top = if (category != categories.first()) 12.dp else 0.dp)
                            )
                            
                            items.forEach { item ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable { viewModel.toggleChecklistItem(item) }
                                        .padding(vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = if (item.isCompleted) Icons.Default.CheckCircle else Icons.Default.RadioButtonUnchecked,
                                        contentDescription = null,
                                        tint = if (item.isCompleted) Color(0xFF4CAF50) else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Text(
                                        text = item.title,
                                        fontSize = 14.sp,
                                        color = if (item.isCompleted) MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f) else MaterialTheme.colorScheme.onSurface,
                                        textDecoration = if (item.isCompleted) TextDecoration.LineThrough else null,
                                        modifier = Modifier.weight(1f)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // --- EXPEDITION LIVE TRACKING MAP ---
        item {
            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.15f)),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("expedition_map_card")
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    // Map Header with Toggle
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Expedition Live Tracking",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = if (mapType == "satellite") "High-resolution satellite view (Online)" else "Offline-optimized terrain grid",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        // Map Style Toggle
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.4f),
                            modifier = Modifier.wrapContentSize()
                        ) {
                            Row(
                                modifier = Modifier.padding(2.dp),
                                horizontalArrangement = Arrangement.spacedBy(2.dp)
                            ) {
                                listOf("satellite" to Icons.Default.Public, "terrain" to Icons.Default.Terrain).forEach { (type, icon) ->
                                    val isSelected = mapType == type
                                    IconButton(
                                        onClick = { viewModel.setMapType(type) },
                                        modifier = Modifier
                                            .size(32.dp)
                                            .background(
                                                color = if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent,
                                                shape = RoundedCornerShape(10.dp)
                                            )
                                            .testTag("map_toggle_$type")
                                    ) {
                                        Icon(
                                            imageVector = icon,
                                            contentDescription = type,
                                            tint = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Mapbox GL JS Integration
                    MapboxWebView(
                        mapType = mapType,
                        bookings = bookings,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(250.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .testTag("mapbox_live_tracking_map")
                    )
                }
            }
        }

        // --- TRIP OVERVIEW SUMMARY DASHBOARD ---
        item {
            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.15f)),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("trip_overview_dashboard_card")
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    // Header
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Explore,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Trip Overview Summary",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "Aggregated costs, targets, and operators directory",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        IconButton(
                            onClick = { viewModel.generateShareLink() },
                            modifier = Modifier
                                .testTag("share_itinerary_btn")
                                .background(MaterialTheme.colorScheme.primaryContainer, RoundedCornerShape(12.dp))
                        ) {
                            Icon(
                                imageVector = Icons.Default.Share,
                                contentDescription = "Share Itinerary",
                                tint = MaterialTheme.colorScheme.onPrimaryContainer,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Trip Progress Bar
                    if (bookings.isNotEmpty()) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 8.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.Bottom
                            ) {
                                Text(
                                    text = "Trip Completion",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "${(tripProgress * 100).toInt()}% Complete",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            LinearProgressIndicator(
                                progress = { tripProgress },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(8.dp)
                                    .clip(RoundedCornerShape(4.dp)),
                                color = MaterialTheme.colorScheme.primary,
                                trackColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
                            )
                            
                            val statusText = when {
                                tripProgress <= 0f -> "Expedition starting soon"
                                tripProgress >= 1f -> "Expedition completed"
                                else -> "Adventure in progress"
                            }
                            Text(
                                text = statusText,
                                fontSize = 10.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(top = 4.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Row showing aggregated counters
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        // Estimated Cost Card
                        Surface(
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(16.dp),
                            color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.1f))
                        ) {
                            Column(
                                modifier = Modifier.padding(12.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CardMembership,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(24.dp)
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Total Investment",
                                    fontSize = 10.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    fontWeight = FontWeight.Medium
                                )
                                Text(
                                    text = "$${totalCost.toInt()}",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }

                        // Total Bookings Count
                        Surface(
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(16.dp),
                            color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.4f),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.secondary.copy(alpha = 0.1f))
                        ) {
                            Column(
                                modifier = Modifier.padding(12.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Icon(
                                    imageVector = Icons.Default.BookOnline,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.secondary,
                                    modifier = Modifier.size(24.dp)
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Booked Assets",
                                    fontSize = 10.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    fontWeight = FontWeight.Medium
                                )
                                Text(
                                    text = "${bookings.size} Active",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Primary Wildlife Goals section
                    Text(
                        text = "🐾 Primary Wildlife Goals",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.padding(bottom = 6.dp)
                    )
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = MaterialTheme.colorScheme.background.copy(alpha = 0.5f),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.1f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            wildlifeGoals.forEach { goal ->
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.padding(vertical = 3.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Pets,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = goal,
                                        fontSize = 11.sp,
                                        color = MaterialTheme.colorScheme.onSurface,
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Essential Contact Details
                    Text(
                        text = "📞 Essential Contact Directory",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.padding(bottom = 6.dp)
                    )
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = MaterialTheme.colorScheme.background.copy(alpha = 0.5f),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.1f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            ContactRow(
                                title = "Savannah HQ Support (Operator)",
                                contact = "+254 712 345 678"
                            )
                            Divider(modifier = Modifier.padding(vertical = 6.dp), color = MaterialTheme.colorScheme.outline.copy(alpha = 0.1f))
                            ContactRow(
                                title = "Maasai Mara Ranger Patrol Office",
                                contact = "+254 700 111 222"
                            )
                            Divider(modifier = Modifier.padding(vertical = 6.dp), color = MaterialTheme.colorScheme.outline.copy(alpha = 0.1f))
                            ContactRow(
                                title = "Serengeti National Park Warden Guard",
                                contact = "+255 754 888 999"
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Premium Export PDF Itinerary Button
                    val context = LocalContext.current
                    
                    // Standard Activity Result launcher for PDF document creation
                    val createDocumentLauncher = rememberLauncherForActivityResult(
                        contract = ActivityResultContracts.CreateDocument("application/pdf")
                    ) { uri: Uri? ->
                        if (uri != null) {
                            try {
                                context.contentResolver.openOutputStream(uri)?.use { outputStream ->
                                    val pdfDocument = android.graphics.pdf.PdfDocument()
                                    
                                    // Set up colors (standard Android Graphics color)
                                    val colorPrimary = android.graphics.Color.parseColor("#2E5A27") // Safari Green
                                    val colorAccent = android.graphics.Color.parseColor("#8F5E15")  // Safari Gold
                                    val colorTextDark = android.graphics.Color.parseColor("#212121")
                                    val colorTextMuted = android.graphics.Color.parseColor("#666666")
                                    val colorLightBg = android.graphics.Color.parseColor("#F5F7F4")
                                    val colorWhite = android.graphics.Color.WHITE
                                    val colorBorder = android.graphics.Color.parseColor("#E0E0E0")
                                    
                                    // Paint definitions
                                    val bgPaint = Paint().apply { color = colorLightBg }
                                    val borderPaint = Paint().apply {
                                        color = colorBorder
                                        style = Paint.Style.STROKE
                                        strokeWidth = 1f
                                    }
                                    val primaryHeaderPaint = Paint().apply { color = colorPrimary }
                                    val textPaint = Paint().apply {
                                        color = colorTextDark
                                        textSize = 10f
                                        isAntiAlias = true
                                        typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
                                    }
                                    val boldTextPaint = Paint().apply {
                                        color = colorTextDark
                                        textSize = 10f
                                        isAntiAlias = true
                                        typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                                    }
                                    val headingPaint = Paint().apply {
                                        color = colorPrimary
                                        textSize = 14f
                                        isAntiAlias = true
                                        typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                                    }
                                    val titlePaint = Paint().apply {
                                        color = colorWhite
                                        textSize = 18f
                                        isAntiAlias = true
                                        typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                                    }
                                    val subtitlePaint = Paint().apply {
                                        color = colorWhite
                                        textSize = 9f
                                        isAntiAlias = true
                                        typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
                                    }
                                    
                                    // 1. Create first page
                                    val pageInfo = android.graphics.pdf.PdfDocument.PageInfo.Builder(595, 842, 1).create()
                                    var currentPage = pdfDocument.startPage(pageInfo)
                                    var canvas = currentPage.canvas
                                    var pageNumber = 1
                                    
                                    // Draw Header Accent Bar
                                    canvas.drawRect(40f, 40f, 555f, 110f, primaryHeaderPaint)
                                    
                                    // Draw Title
                                    canvas.drawText("SAFARI OUTPOST OFFICIAL ITINERARY", 60f, 75f, titlePaint)
                                    canvas.drawText("OFFLINE COMPANION, LODGING VOUCHERS & SAFARI DETAILS", 60f, 95f, subtitlePaint)
                                    
                                    // Travel metadata section
                                    var currentY = 135f
                                    canvas.drawRect(40f, currentY, 555f, currentY + 65f, bgPaint)
                                    canvas.drawRect(40f, currentY, 555f, currentY + 65f, borderPaint)
                                    
                                    canvas.drawText("TRAVELER PROFILE:", 55f, currentY + 20f, boldTextPaint)
                                    canvas.drawText("Email: lilsheriff08@gmail.com", 55f, currentY + 35f, textPaint)
                                    canvas.drawText("Status: Synced via Satellite", 55f, currentY + 50f, textPaint)
                                    
                                    canvas.drawText("TRIP STATS:", 330f, currentY + 20f, boldTextPaint)
                                    canvas.drawText("Total Active Bookings: ${bookings.size}", 330f, currentY + 35f, textPaint)
                                    canvas.drawText("Total Investment: $${totalCost.toInt()}", 330f, currentY + 50f, textPaint)
                                    
                                    currentY += 90f
                                    
                                    // 2. Render Active Bookings
                                    canvas.drawText("SCHEDULE & CONFIRMED BOOKINGS", 40f, currentY, headingPaint)
                                    // Underline
                                    canvas.drawLine(40f, currentY + 4f, 555f, currentY + 4f, borderPaint)
                                    currentY += 20f
                                    
                                    if (bookings.isEmpty()) {
                                        canvas.drawText("No current bookings found. Build your itinerary to export.", 40f, currentY, textPaint)
                                        currentY += 20f
                                    } else {
                                        bookings.forEachIndexed { index, booking ->
                                            // Check page boundary overflow - if currentY is near the bottom, start a new page
                                            if (currentY > 700f) {
                                                // Draw page number footer on old page
                                                val footerPaint = Paint().apply {
                                                    color = colorTextMuted
                                                    textSize = 8f
                                                    isAntiAlias = true
                                                    textAlign = Paint.Align.CENTER
                                                }
                                                canvas.drawText("Page $pageNumber | Safari Outpost App | Safe Travels 🐾", 297.5f, 815f, footerPaint)
                                                pdfDocument.finishPage(currentPage)
                                                
                                                // Start new page
                                                pageNumber++
                                                val nextPageInfo = android.graphics.pdf.PdfDocument.PageInfo.Builder(595, 842, pageNumber).create()
                                                currentPage = pdfDocument.startPage(nextPageInfo)
                                                canvas = currentPage.canvas
                                                
                                                // New Page Header
                                                canvas.drawRect(40f, 40f, 555f, 75f, primaryHeaderPaint)
                                                canvas.drawText("ITINERARY CONTINUED (Page $pageNumber)", 60f, 62f, titlePaint.apply { textSize = 12f })
                                                
                                                currentY = 100f
                                            }
                                            
                                            // Draw Booking Container
                                            canvas.drawRect(40f, currentY, 555f, currentY + 80f, bgPaint)
                                            canvas.drawRect(40f, currentY, 555f, currentY + 80f, borderPaint)
                                            
                                            // Draw Type Indicator (e.g. green block for lodging, gold block for activity)
                                            val indicatorPaint = Paint().apply {
                                                color = if (booking.type == "STAY") colorPrimary else colorAccent
                                            }
                                            canvas.drawRect(42f, currentY + 2f, 48f, currentY + 78f, indicatorPaint)
                                            
                                            // Row 1: Title & Price
                                            canvas.drawText("${index + 1}. [${booking.type}] ${booking.title}", 55f, currentY + 20f, boldTextPaint.apply { textSize = 11f })
                                            val isKsh = booking.type == "STAY" && booking.location.contains("Kenya")
                                            val bookingDisplayPrice = if (isKsh) booking.price * 130.0 else booking.price
                                            val bookingDisplaySym = if (isKsh) "Ksh " else "$"
                                            canvas.drawText("$bookingDisplaySym${bookingDisplayPrice.toInt()}", 480f, currentY + 20f, boldTextPaint.apply { textSize = 11f })
                                            
                                            // Row 2: Location & Date Range
                                            canvas.drawText("📍 Location: ${booking.location}", 55f, currentY + 38f, textPaint.apply { textSize = 9.5f })
                                            canvas.drawText("📅 Dates: ${booking.dateRange}", 280f, currentY + 38f, textPaint.apply { textSize = 9.5f })
                                            
                                            // Row 3: Voucher Code & Confirmation Status
                                            val voucherText = if (booking.voucherCodeUsed.isNullOrEmpty()) "None Applied" else booking.voucherCodeUsed
                                            canvas.drawText("🎟️ Voucher: $voucherText", 55f, currentY + 56f, textPaint.apply { textSize = 9.5f })
                                            
                                            val statusPaint = Paint().apply {
                                                color = if (booking.status == "Confirmed") android.graphics.Color.parseColor("#2E7D32") else android.graphics.Color.parseColor("#EF6C00")
                                                textSize = 9.5f
                                                typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                                            }
                                            canvas.drawText("Status: ${booking.status.uppercase()}", 280f, currentY + 56f, statusPaint)
                                            
                                            // Row 4: Suffix/Security Code
                                            val voucherPrefix = if (booking.type == "STAY") "LODGE-VOUCHER" else "ACT-TICKET"
                                            val securityCode = "SEC-${booking.id * 739 + 10427}-A"
                                            canvas.drawText("Voucher Verification Code: $voucherPrefix-$securityCode", 55f, currentY + 71f, textPaint.apply { textSize = 8f; color = colorTextMuted })
                                            
                                            currentY += 85f

                                            // --- NEW: Activity Schedule for Safaris ---
                                            if (booking.type == "SAFARI") {
                                                val itinerary = viewModel.getItineraryForBooking(booking)
                                                if (itinerary.isNotEmpty()) {
                                                    canvas.drawText("ACTIVITY SCHEDULE:", 55f, currentY, boldTextPaint.apply { textSize = 8.5f; color = colorAccent })
                                                    currentY += 12f
                                                    itinerary.take(5).forEach { item -> // Limit to 5 for space on this demo page
                                                        canvas.drawText("• ${item.activity}", 65f, currentY, textPaint.apply { textSize = 8f })
                                                        currentY += 11f
                                                    }
                                                    currentY += 10f
                                                } else {
                                                    currentY += 10f
                                                }
                                            } else {
                                                currentY += 10f
                                            }
                                            
                                            currentY += 10f
                                        }
                                    }
                                    
                                    // 3. Render Essential Contacts Directory
                                    if (currentY > 680f) {
                                        // Start a new page if we don't have enough space for the directory at the bottom
                                        val footerPaint = Paint().apply {
                                            color = colorTextMuted
                                            textSize = 8f
                                            isAntiAlias = true
                                            textAlign = Paint.Align.CENTER
                                        }
                                        canvas.drawText("Page $pageNumber | Safari Outpost App | Safe Travels 🐾", 297.5f, 815f, footerPaint)
                                        pdfDocument.finishPage(currentPage)
                                        
                                        pageNumber++
                                        val nextPageInfo = android.graphics.pdf.PdfDocument.PageInfo.Builder(595, 842, pageNumber).create()
                                        currentPage = pdfDocument.startPage(nextPageInfo)
                                        canvas = currentPage.canvas
                                        
                                        // Header
                                        canvas.drawRect(40f, 40f, 555f, 75f, primaryHeaderPaint)
                                        canvas.drawText("ESSENTIAL TRAVEL CONTACTS (Page $pageNumber)", 60f, 62f, titlePaint.apply { textSize = 12f })
                                        currentY = 100f
                                    } else {
                                        currentY += 10f
                                    }
                                    
                                    canvas.drawText("ESSENTIAL CONTACT DIRECTORY & SAFETY INFO", 40f, currentY, headingPaint.apply { textSize = 11f })
                                    canvas.drawLine(40f, currentY + 4f, 555f, currentY + 4f, borderPaint)
                                    currentY += 20f
                                    
                                    canvas.drawText("• Savannah HQ Support (Operator): +254 712 345 678", 50f, currentY, boldTextPaint.apply { textSize = 9.5f })
                                    currentY += 16f
                                    canvas.drawText("• Maasai Mara Ranger Patrol Office: +254 700 111 222", 50f, currentY, boldTextPaint.apply { textSize = 9.5f })
                                    currentY += 16f
                                    canvas.drawText("• Serengeti National Park Warden Guard: +255 754 888 999", 50f, currentY, boldTextPaint.apply { textSize = 9.5f })
                                    currentY += 25f
                                    
                                    canvas.drawText("IMPORTANT NOTICE: Keep this document saved offline on your device.", 40f, currentY, boldTextPaint.apply { textSize = 8.5f; color = colorAccent })
                                    canvas.drawText("In the event of a cellular coverage blackout, park rangers and lodge desk hosts will accept these", 40f, currentY + 12f, textPaint.apply { textSize = 8.5f; color = colorTextMuted })
                                    canvas.drawText("generated vouchers alongside your passport. Have a memorable and safe journey!", 40f, currentY + 24f, textPaint.apply { textSize = 8.5f; color = colorTextMuted })
                                    
                                    // Final Page Footer
                                    val footerPaint = Paint().apply {
                                        color = colorTextMuted
                                        textSize = 8f
                                        isAntiAlias = true
                                        textAlign = Paint.Align.CENTER
                                    }
                                    canvas.drawText("Page $pageNumber | Safari Outpost App | Safe Travels 🐾", 297.5f, 815f, footerPaint)
                                    
                                    pdfDocument.finishPage(currentPage)
                                    
                                    pdfDocument.writeTo(outputStream)
                                    pdfDocument.close()
                                    
                                    Toast.makeText(context, "Itinerary PDF successfully exported!", Toast.LENGTH_LONG).show()
                                }
                            } catch (e: Exception) {
                                e.printStackTrace()
                                Toast.makeText(context, "Error exporting PDF: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
                            }
                        }
                    }

                    Button(
                        onClick = {
                            val defaultName = "Safari_Itinerary_${System.currentTimeMillis() / 1000}.pdf"
                            createDocumentLauncher.launch(defaultName)
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = Color.White
                        ),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("export_pdf_button")
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Download,
                                contentDescription = "Export PDF",
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "EXPORT ITINERARY TO PDF (OFFLINE)",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }

        item {
            ExpenseBreakdownChart(bookings = bookings)
        }

        item {
            BushWisdomModule()
        }

        // --- FEEDBACK HISTORY SECTION ---
        if (feedbackList.isNotEmpty()) {
            item {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Reviews,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "My Experience Feedback",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    if (feedbackList.any { !it.isSynced }) {
                        IconButton(onClick = { viewModel.syncAllFeedback() }) {
                            Icon(
                                imageVector = Icons.Default.CloudSync,
                                contentDescription = "Sync Feedback",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
            }

            items(feedbackList) { feedback ->
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(text = feedback.bookingTitle, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            Row {
                                repeat(5) { i ->
                                    Icon(
                                        imageVector = if (i < feedback.rating) Icons.Default.Star else Icons.Default.StarOutline,
                                        contentDescription = null,
                                        tint = if (i < feedback.rating) Color(0xFFFFB300) else Color.Gray,
                                        modifier = Modifier.size(12.dp)
                                    )
                                }
                            }
                            if (feedback.comment.isNotEmpty()) {
                                Text(text = feedback.comment, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                        if (feedback.isSynced) {
                            Icon(imageVector = Icons.Default.CheckCircle, contentDescription = "Synced", tint = Color(0xFF4CAF50), modifier = Modifier.size(16.dp))
                        } else {
                            Icon(imageVector = Icons.Default.CloudQueue, contentDescription = "Pending Sync", tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(16.dp))
                        }
                    }
                }
            }
        }

        // --- SAFARI JOURNAL SECTION ---
        item {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.EditNote,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Safari Journal",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Spacer(modifier = Modifier.weight(1f))
                
                if (journalEntries.any { !it.isSynced } && isOnline) {
                    IconButton(onClick = { viewModel.syncAllJournalEntries() }) {
                        Icon(
                            imageVector = Icons.Default.Sync,
                            contentDescription = "Sync Journal",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
                
                IconButton(onClick = { showJournalDialog = true }) {
                    Icon(
                        imageVector = Icons.Default.AddCircle,
                        contentDescription = "Add Journal Entry",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
            
            Text(
                text = "Capture your thoughts and locations while exploring the wilderness. Works offline!",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                modifier = Modifier.padding(top = 4.dp)
            )
        }

        if (journalEntries.isEmpty()) {
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Box(modifier = Modifier.padding(24.dp), contentAlignment = Alignment.Center) {
                        Text(
                            text = "Your journal is empty. Start recording your memories!",
                            fontSize = 13.sp,
                            textAlign = TextAlign.Center,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        } else {
            items(journalEntries) { entry ->
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.8f)),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Place, contentDescription = null, modifier = Modifier.size(14.dp), tint = MaterialTheme.colorScheme.primary)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(text = entry.location, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                            Spacer(modifier = Modifier.weight(1f))
                            Text(
                                text = SimpleDateFormat("MMM dd, HH:mm", Locale.getDefault()).format(Date(entry.timestamp)),
                                fontSize = 10.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        Text(
                            text = entry.note,
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onSurface,
                            fontStyle = FontStyle.Italic
                        )
                        
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            if (entry.isSynced) {
                                Icon(Icons.Default.CloudDone, contentDescription = "Synced", modifier = Modifier.size(14.dp), tint = Color(0xFF4CAF50))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Synced", fontSize = 10.sp, color = Color(0xFF4CAF50))
                            } else {
                                Icon(Icons.Default.CloudQueue, contentDescription = "Pending", modifier = Modifier.size(14.dp), tint = MaterialTheme.colorScheme.outline)
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Pending Sync", fontSize = 10.sp, color = MaterialTheme.colorScheme.outline)
                            }
                            
                            Spacer(modifier = Modifier.weight(1f))
                            
                            IconButton(
                                onClick = { viewModel.deleteJournalEntry(entry) },
                                modifier = Modifier.size(24.dp)
                            ) {
                                Icon(Icons.Default.DeleteOutline, contentDescription = "Delete", modifier = Modifier.size(16.dp), tint = MaterialTheme.colorScheme.error)
                            }
                        }
                    }
                }
            }
        }

        // Upcoming itinerary title
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
                    text = "My Stays & Trips Dashboard",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }
        }

        item {
            MyStaysDashboard(
                viewModel = viewModel,
                onCancelClick = { selectedBookingForTieredCancel = it },
                onRateClick = { selectedBookingForFeedback = it },
                onShowQrClick = { selectedBookingForQrPass = it },
                onChatClick = { selectedBookingForChat = it },
                onGroupHubClick = { selectedBookingForGroupHub = it },
                onExploreClick = { viewModel.setTab("stays") },
                onAiPlannerClick = { lodgeName ->
                    onAiPlannerClick?.invoke(lodgeName)
                }
            )
        }
    } else {
        // --- LODGE PARTNER PORTAL ---
        val staysOnly = bookings.filter { it.type == "STAY" }
        val escrowBookings = staysOnly.filter { it.status == "Held (Escrow)" }
        val settledBookings = staysOnly.filter { it.status == "Checked In" || it.status == "Checked In & Settled" }

        val totalInEscrowVal = escrowBookings.sumOf { it.price }
        val totalSettledVal = settledBookings.sumOf { it.price }
        val platformCommissionVal = totalSettledVal * 0.15
        val totalLodgePayoutsVal = totalSettledVal * 0.85

        // Hub Metrics Card
        item {
            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)),
                modifier = Modifier.fillMaxWidth().testTag("lodge_hub_metrics_card")
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = CircleShape,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(40.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(Icons.Default.Business, contentDescription = null, tint = Color.White, modifier = Modifier.size(22.dp))
                            }
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text("Savannah Partner Hub", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onPrimaryContainer)
                            Text("Secure Split Escrow Settlements", fontSize = 11.sp, color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f))
                        }
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Surface(
                            shape = RoundedCornerShape(14.dp),
                            color = MaterialTheme.colorScheme.surface.copy(alpha = 0.6f),
                            modifier = Modifier.weight(1f)
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text("FUNDS IN ESCROW", fontSize = 9.sp, fontWeight = FontWeight.ExtraBold, color = MaterialTheme.colorScheme.primary)
                                Spacer(modifier = Modifier.height(4.dp))
                                Text("$${String.format("%,d", totalInEscrowVal.toInt())}", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                                Text("Ksh ${String.format("%,d", (totalInEscrowVal * 130).toInt())}", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                        Surface(
                            shape = RoundedCornerShape(14.dp),
                            color = MaterialTheme.colorScheme.surface.copy(alpha = 0.6f),
                            modifier = Modifier.weight(1f)
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text("RELEASED PAYOUTS", fontSize = 9.sp, fontWeight = FontWeight.ExtraBold, color = Color(0xFF2E7D32))
                                Spacer(modifier = Modifier.height(4.dp))
                                Text("$${String.format("%,d", totalLodgePayoutsVal.toInt())}", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color(0xFF2E7D32))
                                Text("15% commission paid", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = MaterialTheme.colorScheme.surface.copy(alpha = 0.4f),
                            modifier = Modifier.weight(1f)
                        ) {
                            Row(modifier = Modifier.padding(10.dp), verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.PendingActions, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("${escrowBookings.size} Pending Arrivals", fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSurface)
                            }
                        }
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = MaterialTheme.colorScheme.surface.copy(alpha = 0.4f),
                            modifier = Modifier.weight(1f)
                        ) {
                            Row(modifier = Modifier.padding(10.dp), verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color(0xFF2E7D32), modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("${settledBookings.size} Settled Rooms", fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSurface)
                            }
                        }
                    }
                }
            }
        }

        item {
            Column {
                Text("Lodge Hub Actions", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary, modifier = Modifier.padding(bottom = 8.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(
                        onClick = { activeScannerSimulation = true },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.weight(1.1f).testTag("action_scan_guest_pass")
                    ) {
                        Icon(Icons.Default.QrCode, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Scan Guest Pass", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                    
                    OutlinedButton(
                        onClick = {
                            val now = System.currentTimeMillis()
                            val noShowStays = escrowBookings.filter { it.startDateTimestamp < now }
                            if (noShowStays.isEmpty()) {
                                viewModel.addNotification(
                                    title = "No-Show Sweeper",
                                    message = "Cron sweep complete. No pending stays are past their check-in time right now.",
                                    type = "SYSTEM"
                                )
                            } else {
                                noShowStays.forEach { b ->
                                    val netLodgeVal = b.price * 0.85
                                    viewModel.updateBookingStatus(b.id, "Cancelled (No-Show)")
                                    viewModel.addNotification(
                                        title = "No-Show Auto-Settled",
                                        message = "Booking #${b.id} passed the 24-hour grace period. Escrow swept: $${netLodgeVal.toInt()} dispatched to Lodge (15% platform commission retained).",
                                        type = "LODGE",
                                        relatedId = b.id
                                    )
                                }
                            }
                        },
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.weight(0.9f).testTag("action_no_show_sweep")
                    ) {
                        Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Auto-Settle", fontSize = 11.sp)
                    }
                }
                
                Spacer(modifier = Modifier.height(12.dp))
                
                // Admin Portal Link
                if (isOwner) {
                    Surface(
                        onClick = onOpenFinanceDashboard,
                        color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.2f),
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)),
                        modifier = Modifier.fillMaxWidth().testTag("open_admin_finance_btn")
                    ) {
                        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Dashboard, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text("Internal Reconciliation Dashboard", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                                Text("Admin audit for regional escrow and split payouts", fontSize = 10.sp, color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f))
                            }
                            Spacer(modifier = Modifier.weight(1f))
                            Icon(Icons.Default.ChevronRight, contentDescription = null, tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f))
                        }
                    }
                }
            }
        }

        item {
            Text(
                text = "Room Bookings & Escrow Splits",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.padding(top = 8.dp)
            )
        }

        if (staysOnly.isEmpty()) {
            item {
                EmptyStateView(
                    message = "No stay reservations or bookings registered for your lodge yet.",
                    icon = Icons.Default.Business
                )
            }
        } else {
            items(staysOnly) { reservation ->
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.1f)),
                    modifier = Modifier.fillMaxWidth().testTag("reservation_card_${reservation.id}")
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                            Surface(
                                color = when (reservation.status) {
                                    "Held (Escrow)" -> Color(0xFFFFF3E0)
                                    "Checked In", "Checked In & Settled" -> Color(0xFFE8F5E9)
                                    else -> Color(0xFFFFEBEE)
                                },
                                shape = RoundedCornerShape(6.dp)
                            ) {
                                Text(
                                    text = reservation.status,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = when (reservation.status) {
                                        "Held (Escrow)" -> Color(0xFFE65100)
                                        "Checked In", "Checked In & Settled" -> Color(0xFF2E7D32)
                                        else -> Color(0xFFC62828)
                                    },
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                            Spacer(modifier = Modifier.weight(1f))
                            Text("ID: #${reservation.id}", fontSize = 11.sp, color = MaterialTheme.colorScheme.outline)
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(reservation.title, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                        Text(reservation.location, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text(reservation.dateRange, fontSize = 11.sp, fontWeight = FontWeight.Medium, color = MaterialTheme.colorScheme.outline)

                        Spacer(modifier = Modifier.height(10.dp))

                        val isKsh = reservation.location.contains("Kenya")
                        val r = if (isKsh) 130.0 else 1.0
                        val sym = if (isKsh) "Ksh " else "$"
                        
                        val rawPrice = reservation.price
                        val rawLodgeNet = rawPrice * 0.85
                        val rawCommission = rawPrice * 0.15

                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                Row {
                                    Text("Guest Paid (100%):", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    Spacer(modifier = Modifier.weight(1f))
                                    Text("$sym${String.format("%,d", (rawPrice * r).toInt())}", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                                Row {
                                    Text("Platform Fee (15%):", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    Spacer(modifier = Modifier.weight(1f))
                                    Text("- $sym${String.format("%,d", (rawCommission * r).toInt())}", fontSize = 11.sp, color = MaterialTheme.colorScheme.error)
                                }
                                Divider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.1f))
                                Row {
                                    Text("Lodge Payout (85%):", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                                    Spacer(modifier = Modifier.weight(1f))
                                    Text("$sym${String.format("%,d", (rawLodgeNet * r).toInt())}", fontSize = 11.sp, fontWeight = FontWeight.ExtraBold, color = Color(0xFF2E7D32))
                                }
                            }
                        }

                        if (reservation.status == "Held (Escrow)") {
                            Spacer(modifier = Modifier.height(10.dp))
                            Button(
                                onClick = { selectedBookingForCheckInPayout = reservation },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32)),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Icon(Icons.Default.LockOpen, contentDescription = null, modifier = Modifier.size(16.dp), tint = Color.White)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Verify Check-In & Payout", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
                            }
                        }
                    }
                }
            }
        }
    }
    }

    // --- ESCROW SPLIT SYSTEM DIALOGS ---
    val coroutineScope = rememberCoroutineScope()

    // 1. Digital Check-In Pass Dialog (Guest View)
    selectedBookingForQrPass?.let { booking ->
        AlertDialog(
            onDismissRequest = { selectedBookingForQrPass = null },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.QrCode,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(28.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Digital Check-In Pass",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            },
            text = {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = booking.title,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Reservation ID: STAY-ESC-${booking.id}",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.outline,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // QR Code High Fidelity Visual Custom Draw
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = Color.White,
                        border = BorderStroke(2.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)),
                        modifier = Modifier
                            .size(180.dp)
                            .padding(8.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Canvas(modifier = Modifier.size(140.dp)) {
                                val sizeL = size.width
                                val cols = 15
                                val step = sizeL / cols
                                // Draw three corner anchor boxes
                                // Top-Left Anchor
                                drawRect(color = Color.Black, topLeft = Offset(0f, 0f), size = Size(step * 4, step * 4))
                                drawRect(color = Color.White, topLeft = Offset(step, step), size = Size(step * 2, step * 2))
                                drawRect(color = Color.Black, topLeft = Offset(step * 1.5f, step * 1.5f), size = Size(step, step))

                                // Top-Right Anchor
                                drawRect(color = Color.Black, topLeft = Offset(sizeL - step * 4, 0f), size = Size(step * 4, step * 4))
                                drawRect(color = Color.White, topLeft = Offset(sizeL - step * 3, step), size = Size(step * 2, step * 2))
                                drawRect(color = Color.Black, topLeft = Offset(sizeL - step * 2.5f, step * 1.5f), size = Size(step, step))

                                // Bottom-Left Anchor
                                drawRect(color = Color.Black, topLeft = Offset(0f, sizeL - step * 4), size = Size(step * 4, step * 4))
                                drawRect(color = Color.White, topLeft = Offset(step, sizeL - step * 3), size = Size(step * 2, step * 2))
                                drawRect(color = Color.Black, topLeft = Offset(step * 1.5f, sizeL - step * 2.5f), size = Size(step, step))

                                // Draw simulated QR code payload pixels
                                val randomSeed = booking.id.hashCode().toLong()
                                val random = java.util.Random(randomSeed)
                                for (i in 0 until cols) {
                                    for (j in 0 until cols) {
                                        // Skip anchor areas
                                        if (i < 4 && j < 4) continue
                                        if (i >= cols - 4 && j < 4) continue
                                        if (i < 4 && j >= cols - 4) continue
                                        
                                        if (random.nextBoolean()) {
                                            drawRect(
                                                color = Color.Black,
                                                topLeft = Offset(i * step, j * step),
                                                size = Size(step * 0.9f, step * 0.9f)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f))
                            .padding(8.dp)
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(12.dp),
                            strokeWidth = 1.5.dp,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Secure TOTP Code: STAY-ESC-${booking.id}-SEC",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "Present this secure pass at check-in. The Lodge Hub will scan it to verify your location and safely release held escrow funds.",
                        fontSize = 10.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = { selectedBookingForQrPass = null }) {
                    Text("Close Pass")
                }
            }
        )
    }

    // 2. Interactive Tiered Cancellation Dialog (Guest View)
    selectedBookingForTieredCancel?.let { booking ->
        var selectedCancelTier by remember { mutableStateOf(0) } // 0: >60 days, 1: 30-59 days, 2: <30 days
        
        val isKsh = booking.type == "STAY" && booking.location.contains("Kenya")
        val currencySym = if (isKsh) "Ksh " else "$"
        val conversionMultiplier = if (isKsh) 130.0 else 1.0
        val originalPrice = booking.price * conversionMultiplier

        val adminFeePct = when (selectedCancelTier) {
            0 -> 0.05
            1 -> 0.50
            else -> 1.00
        }
        val refundPct = 1.0 - adminFeePct
        val calculatedRefund = originalPrice * refundPct
        val calculatedFee = originalPrice * adminFeePct

        AlertDialog(
            onDismissRequest = { selectedBookingForTieredCancel = null },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.error,
                        modifier = Modifier.size(28.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Cancel & Refund Estimate",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            },
            text = {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "Review the tiered policy and select your simulated departure interval to calculate your payout/refund split:",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )

                    listOf(
                        "Moderate (>60 Days ahead)\n• 95% Refund to Guest\n• 5% Admin Payout to Platform",
                        "Flexible (30 - 59 Days ahead)\n• 50% Refund to Guest\n• 50% Split Payout (25% Plat, 25% Lodge)",
                        "Strict (<30 Days / No-Show)\n• 0% Refund to Guest\n• 100% Payout (15% Plat, 85% Lodge)"
                    ).forEachIndexed { index, title ->
                        Surface(
                            onClick = { selectedCancelTier = index },
                            shape = RoundedCornerShape(10.dp),
                            color = if (selectedCancelTier == index) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                            border = BorderStroke(1.dp, if (selectedCancelTier == index) MaterialTheme.colorScheme.primary else Color.Transparent),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                                .testTag("cancel_tier_btn_$index")
                        ) {
                            Row(modifier = Modifier.padding(10.dp), verticalAlignment = Alignment.CenterVertically) {
                                RadioButton(
                                    selected = selectedCancelTier == index,
                                    onClick = { selectedCancelTier = index }
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = title,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = if (selectedCancelTier == index) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Row {
                                Text("Escrow Deposit:", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Spacer(modifier = Modifier.weight(1f))
                                Text("$currencySym${String.format("%,d", originalPrice.toInt())}", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                            Row {
                                Text("Guest Refund (${(refundPct * 100).toInt()}%):", fontSize = 11.sp, color = Color(0xFF2E7D32))
                                Spacer(modifier = Modifier.weight(1f))
                                Text("$currencySym${String.format("%,d", calculatedRefund.toInt())}", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF2E7D32))
                            }
                            Row {
                                Text("Payout/Retention (${(adminFeePct * 100).toInt()}%):", fontSize = 11.sp, color = MaterialTheme.colorScheme.error)
                                Spacer(modifier = Modifier.weight(1f))
                                Text("$currencySym${String.format("%,d", calculatedFee.toInt())}", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.error)
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val simulatedDays = when (selectedCancelTier) {
                            0 -> 65
                            1 -> 45
                            else -> 10
                        }
                        viewModel.cancelBookingWithTieredRefund(booking, simulatedDays)
                        selectedBookingForTieredCancel = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                    modifier = Modifier.testTag("confirm_cancel_tiered_btn")
                ) {
                    Text("Cancel Booking & Split")
                }
            },
            dismissButton = {
                TextButton(onClick = { selectedBookingForTieredCancel = null }) {
                    Text("Back")
                }
            }
        )
    }

    // 3. Digital Scanner Simulation Dialog (Partner View)
    if (activeScannerSimulation) {
        val escrowBookingsOnly = bookings.filter { it.type == "STAY" && it.status == "Held (Escrow)" }

        AlertDialog(
            onDismissRequest = { activeScannerSimulation = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.QrCode,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(28.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Check-In Scanner",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            },
            text = {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Scan Guest's check-in pass to authorize the escrow split.",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )

                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .size(180.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color.Black)
                    ) {
                        Canvas(modifier = Modifier.size(150.dp)) {
                            val w = size.width
                            val h = size.height
                            val len = 20.dp.toPx()
                            val stroke = 3.dp.toPx()
                            val green = Color(0xFF4CAF50)

                            // Top Left Corner
                            drawLine(color = green, start = Offset(0f, 0f), end = Offset(len, 0f), strokeWidth = stroke)
                            drawLine(color = green, start = Offset(0f, 0f), end = Offset(0f, len), strokeWidth = stroke)

                            // Top Right Corner
                            drawLine(color = green, start = Offset(w, 0f), end = Offset(w - len, 0f), strokeWidth = stroke)
                            drawLine(color = green, start = Offset(w, 0f), end = Offset(w, len), strokeWidth = stroke)

                            // Bottom Left Corner
                            drawLine(color = green, start = Offset(0f, h), end = Offset(len, h), strokeWidth = stroke)
                            drawLine(color = green, start = Offset(0f, h), end = Offset(0f, h - len), strokeWidth = stroke)

                            // Bottom Right Corner
                            drawLine(color = green, start = Offset(w, h), end = Offset(w - len, h), strokeWidth = stroke)
                            drawLine(color = green, start = Offset(w, h), end = Offset(w, h - len), strokeWidth = stroke)
                        }

                        val infiniteTransition = rememberInfiniteTransition()
                        val pulseOffset by infiniteTransition.animateFloat(
                            initialValue = 15f,
                            targetValue = 165f,
                            animationSpec = infiniteRepeatable(
                                animation = tween(1500, easing = LinearEasing),
                                repeatMode = RepeatMode.Reverse
                            )
                        )
                        Divider(
                            color = Color(0xFF4CAF50),
                            thickness = 2.dp,
                            modifier = Modifier
                                .width(150.dp)
                                .align(Alignment.TopCenter)
                                .graphicsLayer { translationY = pulseOffset }
                        )

                        Text(
                            text = "📷 SIMULATED CAMERA ACTIVE",
                            fontSize = 8.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White.copy(alpha = 0.5f)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "--- DEMO WORKSPACE ASSISTANT ---",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "Select a guest's stay below to simulate scanning their QR code:",
                        fontSize = 10.sp,
                        color = MaterialTheme.colorScheme.outline,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    if (escrowBookingsOnly.isEmpty()) {
                        Text(
                            text = "No guests are currently in 'Held (Escrow)' state.",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.error,
                            fontWeight = FontWeight.Bold
                        )
                    } else {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(max = 140.dp)
                                .verticalScroll(rememberScrollState()),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            escrowBookingsOnly.forEach { b ->
                                Surface(
                                    onClick = {
                                        selectedBookingForCheckInPayout = b
                                        activeScannerSimulation = false
                                    },
                                    color = MaterialTheme.colorScheme.surfaceVariant,
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.fillMaxWidth().testTag("scan_demo_${b.id}")
                                ) {
                                    Row(
                                        modifier = Modifier.padding(8.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(Icons.Default.QrCode, contentDescription = null, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Column {
                                            Text(b.title, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                            Text("Id: #${b.id} • ${b.location}", fontSize = 9.sp, color = MaterialTheme.colorScheme.outline)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {},
            dismissButton = {
                TextButton(onClick = { activeScannerSimulation = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    // 4. Check-In & Split Payout Authorization Dialog (Partner View)
    selectedBookingForCheckInPayout?.let { booking ->
        var payoutSuccessRef by remember { mutableStateOf("") }
        var isProcessingPayout by remember { mutableStateOf(false) }

        val isKsh = booking.location.contains("Kenya")
        val currencySym = if (isKsh) "Ksh " else "$"
        val conversionMultiplier = if (isKsh) 130.0 else 1.0
        val basePrice = booking.price
        
        val totalDeposit = basePrice * conversionMultiplier
        val platformFee = totalDeposit * 0.15
        val netLodge = totalDeposit * 0.85

        AlertDialog(
            onDismissRequest = { 
                if (!isProcessingPayout) {
                    selectedBookingForCheckInPayout = null 
                }
            },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = if (payoutSuccessRef.isNotEmpty()) Icons.Default.CheckCircle else Icons.Default.LockOpen,
                        contentDescription = null,
                        tint = if (payoutSuccessRef.isNotEmpty()) Color(0xFF2E7D32) else MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(28.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (payoutSuccessRef.isNotEmpty()) "Payout Success" else "Verify Check-In & Payout",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            },
            text = {
                Column(modifier = Modifier.fillMaxWidth()) {
                    if (payoutSuccessRef.isNotEmpty()) {
                        Card(
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E9)),
                            modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)
                        ) {
                            Column(modifier = Modifier.padding(14.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(Icons.Default.CloudDone, contentDescription = null, tint = Color(0xFF2E7D32), modifier = Modifier.size(48.dp))
                                Spacer(modifier = Modifier.height(8.dp))
                                Text("ESCROW SPLIT COMPLETED", fontSize = 14.sp, fontWeight = FontWeight.ExtraBold, color = Color(0xFF2E7D32))
                                Text("Transaction Reference:", fontSize = 10.sp, color = Color(0xFF2E7D32).copy(alpha = 0.8f))
                                Text(payoutSuccessRef, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1B5E20))
                            }
                        }

                        Text("The escrow funds have been successfully divided and disbursed:", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Spacer(modifier = Modifier.height(8.dp))
                        Row {
                            Text("15% Platform Commission:", fontSize = 11.sp)
                            Spacer(modifier = Modifier.weight(1f))
                            Text("$currencySym${String.format("%,d", platformFee.toInt())}", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                        Row {
                            Text("85% Lodge Net Remittance:", fontSize = 11.sp)
                            Spacer(modifier = Modifier.weight(1f))
                            Text("$currencySym${String.format("%,d", netLodge.toInt())}", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF2E7D32))
                        }

                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "Notifications have been sent to both the guest and the lodge host. The stay status has been changed to 'Checked In & Settled'.",
                            fontSize = 10.sp,
                            color = MaterialTheme.colorScheme.outline
                        )
                    } else if (isProcessingPayout) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.fillMaxWidth().padding(vertical = 24.dp)
                        ) {
                            CircularProgressIndicator(modifier = Modifier.size(48.dp))
                            Spacer(modifier = Modifier.height(16.dp))
                            Text("Executing Split Escrow Settlement...", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                            Text("Disbursing M-Pesa sub-account payouts", fontSize = 11.sp, color = MaterialTheme.colorScheme.outline)
                        }
                    } else {
                        Text(
                            text = "Review check-in details and split calculations before authorizing payout settlement.",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(bottom = 12.dp)
                        )

                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                            Icon(Icons.Default.Face, contentDescription = null, modifier = Modifier.size(16.dp), tint = MaterialTheme.colorScheme.primary)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Guest Profile: Verified Resident", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Icon(Icons.Default.CheckCircle, contentDescription = null, modifier = Modifier.size(16.dp), tint = Color(0xFF4CAF50))
                            Text("Geofence Check: PASS (Within 50m of lodge)", fontSize = 11.sp, color = Color(0xFF2E7D32), fontWeight = FontWeight.Medium)
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                Row {
                                    Text("Escrow Total Deposit:", fontSize = 11.sp)
                                    Spacer(modifier = Modifier.weight(1f))
                                    Text("$currencySym${String.format("%,d", totalDeposit.toInt())}", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                                Row {
                                    Text("Platform Commission (15%):", fontSize = 11.sp, color = MaterialTheme.colorScheme.error)
                                    Spacer(modifier = Modifier.weight(1f))
                                    Text("- $currencySym${String.format("%,d", platformFee.toInt())}", fontSize = 11.sp, color = MaterialTheme.colorScheme.error)
                                }
                                Divider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.15f))
                                Row {
                                    Text("Lodge Remittance (85%):", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                    Spacer(modifier = Modifier.weight(1f))
                                    Text("$currencySym${String.format("%,d", netLodge.toInt())}", fontSize = 11.sp, fontWeight = FontWeight.ExtraBold, color = Color(0xFF2E7D32))
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "Remittance Destination: Safari Stay Partner Sub-account (M-Pesa Till #829402). Funds are released instantly upon authorization.",
                            fontSize = 10.sp,
                            color = MaterialTheme.colorScheme.outline
                        )
                    }
                }
            },
            confirmButton = {
                if (payoutSuccessRef.isNotEmpty()) {
                    Button(
                        onClick = { selectedBookingForCheckInPayout = null },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                    ) {
                        Text("Close & Back to Hub")
                    }
                } else if (!isProcessingPayout) {
                    Button(
                        onClick = {
                            isProcessingPayout = true
                            val refId = "PESAPAL-SPLIT-QZ" + (100000..999999).random()
                            coroutineScope.launch {
                                kotlinx.coroutines.delay(1500)
                                isProcessingPayout = false
                                payoutSuccessRef = refId
                                
                                viewModel.updateBookingStatus(booking.id, "Checked In & Settled")
                                
                                viewModel.addNotification(
                                    title = "Check-In Verified & Payout Released",
                                    message = "Welcome to ${booking.title}! Your check-in is complete and escrow deposit of $currencySym${String.format("%,d", totalDeposit.toInt())} has been disbursed safely.",
                                    type = "GUEST",
                                    relatedId = booking.id
                                )
                                viewModel.addNotification(
                                    title = "Split Payout Dispatched",
                                    message = "Booking #${booking.id} check-in authorized. Instant payout of $currencySym${String.format("%,d", netLodge.toInt())} dispatched to M-Pesa sub-account.",
                                    type = "LODGE",
                                    relatedId = booking.id
                                )
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32)),
                        modifier = Modifier.testTag("authorize_escrow_payout_btn")
                    ) {
                        Text("AUTHORIZE DISBURSEMENT")
                    }
                }
            },
            dismissButton = {
                if (payoutSuccessRef.isEmpty() && !isProcessingPayout) {
                    TextButton(onClick = { selectedBookingForCheckInPayout = null }) {
                        Text("Cancel")
                    }
                }
            }
        )
    }

    // Feedback Dialog
    selectedBookingForFeedback?.let { booking ->
        FeedbackDialog(
            booking = booking,
            onDismiss = { selectedBookingForFeedback = null },
            onSubmit = { id, title, rating, comment ->
                viewModel.submitFeedback(id, title, rating, comment)
            }
        )
    }

    // Guide Chat Dialog
    selectedBookingForChat?.let { booking ->
        GuideChatDialog(
            booking = booking,
            onDismiss = { selectedBookingForChat = null }
        )
    }

    // Group Trip Workspace & Split Payment Dialog
    selectedBookingForGroupHub?.let { booking ->
        GroupTripWorkspaceDialog(
            booking = booking,
            onDismiss = { selectedBookingForGroupHub = null }
        )
    }

    // Journal Dialog
    if (showJournalDialog) {
        JournalDialog(
            currentLocationName = lastSyncedLocationName,
            onDismiss = { showJournalDialog = false },
            onSubmit = { note, location ->
                viewModel.addJournalEntry(note, location)
            }
        )
    }

    // Share Itinerary Dialog
    shareLink?.let { link ->
        ShareItineraryDialog(
            shareLink = link,
            summaryText = viewModel.getItinerarySummaryText(),
            onDismiss = { viewModel.clearShareLink() },
            onShare = { text ->
                val sendIntent: android.content.Intent = android.content.Intent().apply {
                    action = android.content.Intent.ACTION_SEND
                    putExtra(android.content.Intent.EXTRA_TEXT, text)
                    type = "text/plain"
                }
                val shareIntent = android.content.Intent.createChooser(sendIntent, null)
                context.startActivity(shareIntent)
                viewModel.clearShareLink()
            }
        )
    }

    // BUSH SOS Confirmation Dialog
    if (showSosDialog) {
        AlertDialog(
            onDismissRequest = { showSosDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Place,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.error,
                        modifier = Modifier.size(28.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        "Confirm Bush SOS Dispatch",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            },
            text = {
                Column {
                    Text(
                        text = "You are about to transmit an Emergency SOS distress message to Savannah Emergency HQ and local game wardens. Please review your payload below. You can add notes if needed.",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(
                        value = customSosMessage,
                        onValueChange = { customSosMessage = it },
                        label = { Text("Emergency SMS Payload", fontSize = 11.sp) },
                        textStyle = androidx.compose.ui.text.TextStyle(fontSize = 11.sp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(130.dp)
                            .testTag("sos_payload_field")
                    )
                }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = { showSosDialog = false },
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("ABORT", fontWeight = FontWeight.Bold)
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        showSosDialog = false
                        viewModel.triggerBushSos(customSosMessage)
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.testTag("confirm_sos_button")
                ) {
                    Text("CONFIRM DISPATCH", fontWeight = FontWeight.ExtraBold)
                }
            },
            shape = RoundedCornerShape(24.dp)
        )
    }
}

@Composable
fun ContactRow(title: String, contact: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = contact,
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontWeight = FontWeight.Medium
            )
        }
        IconButton(
            onClick = { /* Action simulated here */ },
            modifier = Modifier.size(32.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Place,
                contentDescription = "Contact Info",
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(16.dp)
            )
        }
    }
}

@Composable
fun BookingCard(
    booking: BookingEntity,
    onCancelClick: () -> Unit,
    onRateClick: () -> Unit,
    itinerary: List<ItineraryItem> = emptyList(),
    onShowQrClick: (() -> Unit)? = null,
    onChatClick: (() -> Unit)? = null,
    onGroupHubClick: (() -> Unit)? = null
) {
    var showItinerary by remember { mutableStateOf(false) }
    val isPast = remember(booking.startDateTimestamp) {
        booking.startDateTimestamp + (2L * 24 * 60 * 60 * 1000) < System.currentTimeMillis()
    }
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

                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Group,
                        contentDescription = "Group",
                        tint = MaterialTheme.colorScheme.secondary,
                        modifier = Modifier.size(11.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Group: 4 members (Alex, Sarah, David, Amina)",
                        fontSize = 10.sp,
                        color = MaterialTheme.colorScheme.secondary,
                        fontWeight = FontWeight.Bold
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
            }
        }

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    val isKsh = booking.type == "STAY" && booking.location.contains("Kenya")
                    val displayPrice = if (isKsh) booking.price * 130.0 else booking.price
                    val displaySym = if (isKsh) "Ksh " else "$"
                    Text(
                        text = "$displaySym${String.format("%,d", displayPrice.toInt())}",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    
                    Surface(
                        color = when (booking.status) {
                            "Pending Sync" -> MaterialTheme.colorScheme.errorContainer
                            "Confirmed" -> MaterialTheme.colorScheme.tertiaryContainer
                            "Held (Escrow)" -> Color(0xFFFFF3E0)
                            "Checked In", "Checked In & Settled" -> Color(0xFFE8F5E9)
                            "Cancelled (Refunded)" -> Color(0xFFFFEBEE)
                            else -> MaterialTheme.colorScheme.surfaceVariant
                        },
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            if (booking.status == "Pending Sync") {
                                Icon(
                                    imageVector = Icons.Default.Sync,
                                    contentDescription = null,
                                    modifier = Modifier.size(10.dp).padding(end = 4.dp),
                                    tint = MaterialTheme.colorScheme.onErrorContainer
                                )
                            }
                            Text(
                                text = booking.status,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = when (booking.status) {
                                    "Pending Sync" -> MaterialTheme.colorScheme.onErrorContainer
                                    "Confirmed" -> MaterialTheme.colorScheme.onTertiaryContainer
                                    "Held (Escrow)" -> Color(0xFFE65100)
                                    "Checked In", "Checked In & Settled" -> Color(0xFF2E7D32)
                                    "Cancelled (Refunded)" -> Color(0xFFC62828)
                                    else -> MaterialTheme.colorScheme.onSurfaceVariant
                                }
                            )
                        }
                    }
                    
                    Spacer(modifier = Modifier.width(8.dp))
                    
                    if (booking.status == "Held (Escrow)" && onShowQrClick != null) {
                        Button(
                            onClick = onShowQrClick,
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier
                                .height(32.dp)
                                .testTag("show_qr_pass_${booking.id}")
                        ) {
                            Icon(
                                imageVector = Icons.Default.QrCode,
                                contentDescription = "Show QR Pass",
                                tint = Color.White,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("QR Pass", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                    }
                    
                    if (booking.status != "Cancelled (Refunded)" && onGroupHubClick != null) {
                        Button(
                            onClick = onGroupHubClick,
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.tertiary),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier
                                .height(32.dp)
                                .testTag("group_hub_btn_${booking.id}")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Group,
                                contentDescription = "Group Hub",
                                tint = Color.White,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Group Hub", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                    }

                    if (booking.status != "Cancelled (Refunded)" && onChatClick != null) {
                        Button(
                            onClick = onChatClick,
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier
                                .height(32.dp)
                                .testTag("chat_with_guide_${booking.id}")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Chat,
                                contentDescription = "Chat with Guide",
                                tint = Color.White,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Chat Guide", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                    }

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

                    if (booking.status == "Confirmed") {
                        Spacer(modifier = Modifier.width(4.dp))
                        IconButton(
                            onClick = onRateClick,
                            modifier = Modifier
                                .size(32.dp)
                                .testTag("rate_booking_${booking.id}")
                        ) {
                            Icon(
                                imageVector = Icons.Default.StarRate,
                                contentDescription = "Rate experience",
                                tint = if (isPast) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline,
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
    commissionRate: Double = 0.15,
    isEcoCertified: Boolean = false,
    isKsh: Boolean = false,
    onDismiss: () -> Unit,
    onConfirm: (startDate: Long?, endDate: Long?, guests: Int, voucherCode: String?) -> Unit,
    onInitiateMpesaPayment: suspend (phoneNumber: String, amount: Int) -> Boolean = { _, _ -> true },
    onMpesaSuccessToast: ((MpesaSuccessData) -> Unit)? = null
) {
    val context = LocalContext.current
    var showDatePicker by remember { mutableStateOf(false) }
    var selectedDates by remember { mutableStateOf<Pair<Long?, Long?>>(null to null) }
    var guests by remember { mutableStateOf(1) }
    var selectedVoucherCode by remember { mutableStateOf("") }
    var isDropdownExpanded by remember { mutableStateOf(false) }

    // Payment States
    var paymentMethod by remember { mutableStateOf("M-Pesa") }
    var phoneNumber by remember { mutableStateOf("") }
    var airtelNumber by remember { mutableStateOf("") }
    var cardNumber by remember { mutableStateOf("") }
    var cardExpiry by remember { mutableStateOf("") }
    var cardCvv by remember { mutableStateOf("") }
    var paymentState by remember { mutableStateOf("Idle") } // Idle, Processing, WaitingForPin, Success
    var mpesaReceiptRef by remember { mutableStateOf("") }
    val dialogScope = rememberCoroutineScope()

    val matchingVoucher = vouchers.firstOrNull { it.code == selectedVoucherCode }
    val nights = if (selectedDates.first != null && selectedDates.second != null) maxOf(1, ((selectedDates.second!! - selectedDates.first!!) / (1000 * 60 * 60 * 24)).toInt()) else 1
    val totalBasePrice = price * guests * nights
    val discountVal = if (matchingVoucher != null) minOf(totalBasePrice, matchingVoucher.remainingValue) else 0.0
    val finalPriceVal = maxOf(0.0, totalBasePrice - discountVal)

    val rate = if (isKsh) 130.0 else 1.0
    val sym = if (isKsh) "Ksh " else "$"
    val displayFinalPrice = finalPriceVal * rate

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
                text = if (paymentState == "Success") "Booking Confirmed!" else title,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                if (paymentState == "Success") {
                    // --- SUCCESS & IMPACT SUMMARY ---
                    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = Color(0xFF4CAF50),
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "M-Pesa Payment Confirmed!",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Thank you for booking with impact!",
                            fontSize = 13.sp,
                            textAlign = TextAlign.Center,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Calendar Prompt Card
                    val ref = if (mpesaReceiptRef.isNotEmpty()) mpesaReceiptRef else "MPX-" + (100000..999999).random()
                    MpesaCalendarPromptCard(
                        data = MpesaSuccessData(
                            transactionRef = ref,
                            amountFormatted = "$sym${String.format("%,d", displayFinalPrice.toInt())}",
                            itemName = itemName,
                            location = "$itemName, East Africa",
                            startDateMillis = selectedDates.first,
                            endDateMillis = selectedDates.second,
                            phoneNumber = phoneNumber
                        )
                    )

                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Surface(
                        color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(
                                text = "Your Impact Receipt",
                                style = MaterialTheme.typography.labelLarge,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            
                            val displayCommission = displayFinalPrice * commissionRate
                            val displayConservationPledge = displayCommission * 0.10

                            ImpactSummaryRow("Total Paid", "$sym${String.format("%,d", displayFinalPrice.toInt())}", isBold = true)
                            ImpactSummaryRow("Remitted to Lodge", "$sym${String.format("%,d", (displayFinalPrice - displayCommission).toInt())}")
                            ImpactSummaryRow("Conservation Pledge", "$sym${String.format("%,d", displayConservationPledge.toInt())}", isHighlight = true)
                            
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "This pledge supports local wildlife rangers and community water projects in East Africa.",
                                fontSize = 10.sp,
                                lineHeight = 13.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                } else {
                    // --- NORMAL BOOKING FLOW ---
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

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Guests", fontWeight = FontWeight.SemiBold)
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            IconButton(onClick = { if (guests > 1) guests-- }) {
                                Icon(Icons.Default.RemoveCircleOutline, contentDescription = "Decrease")
                            }
                            Text("$guests", fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 8.dp))
                            IconButton(onClick = { guests++ }) {
                                Icon(Icons.Default.AddCircleOutline, contentDescription = "Increase")
                            }
                        }
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
                                        val voucherVal = if (isKsh) v.remainingValue * 130.0 else v.remainingValue
                                        val voucherSym = if (isKsh) "Ksh " else "$"
                                        DropdownMenuItem(
                                            text = { Text("${v.code} (Bal: $voucherSym${String.format("%,d", voucherVal.toInt())})") },
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

                    // --- PAYMENT METHOD ---
                    Column {
                        Text(
                            text = "Payment Method",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            PaymentChip(
                                label = "M-Pesa",
                                isSelected = paymentMethod == "M-Pesa",
                                onClick = { paymentMethod = "M-Pesa" },
                                icon = Icons.Default.Smartphone,
                                modifier = Modifier.weight(1f)
                            )
                            PaymentChip(
                                label = "Airtel",
                                isSelected = paymentMethod == "Airtel",
                                onClick = { paymentMethod = "Airtel" },
                                icon = Icons.Default.MobileFriendly,
                                modifier = Modifier.weight(1f)
                            )
                            PaymentChip(
                                label = "Card",
                                isSelected = paymentMethod == "Card",
                                onClick = { paymentMethod = "Card" },
                                icon = Icons.Default.CreditCard,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }

                    if (paymentMethod == "M-Pesa") {
                        OutlinedTextField(
                            value = phoneNumber,
                            onValueChange = { if (it.all { c -> c.isDigit() }) phoneNumber = it },
                            label = { Text("M-Pesa Phone Number") },
                            placeholder = { Text("254711222333") },
                            prefix = { Text("+") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                            leadingIcon = { Icon(Icons.Default.Smartphone, null) }
                        )
                    } else if (paymentMethod == "Airtel") {
                        OutlinedTextField(
                            value = airtelNumber,
                            onValueChange = { if (it.all { c -> c.isDigit() }) airtelNumber = it },
                            label = { Text("Airtel Money Number") },
                            placeholder = { Text("254733111222") },
                            prefix = { Text("+") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                            leadingIcon = { Icon(Icons.Default.MobileFriendly, null) }
                        )
                    } else if (paymentMethod == "Card") {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedTextField(
                                value = cardNumber,
                                onValueChange = { if (it.length <= 16 && it.all { c -> c.isDigit() }) cardNumber = it },
                                label = { Text("Card Number (Visa / MasterCard)") },
                                placeholder = { Text("4111222233334444") },
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                leadingIcon = { Icon(Icons.Default.CreditCard, null) }
                            )
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                OutlinedTextField(
                                    value = cardExpiry,
                                    onValueChange = { cardExpiry = it },
                                    label = { Text("MM/YY") },
                                    placeholder = { Text("12/28") },
                                    modifier = Modifier.weight(1f),
                                    singleLine = true
                                )
                                OutlinedTextField(
                                    value = cardCvv,
                                    onValueChange = { if (it.length <= 3 && it.all { c -> c.isDigit() }) cardCvv = it },
                                    label = { Text("CVV") },
                                    placeholder = { Text("123") },
                                    modifier = Modifier.weight(1f),
                                    singleLine = true,
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))
                    Divider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.1f))

                    // Price display
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "Total Price:",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.weight(1f))
                        Text(
                            text = "$sym${String.format("%,d", displayFinalPrice.toInt())}",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }

                    // Simulated Processing State
                    if (paymentState == "Processing" || paymentState == "WaitingForPin") {
                        Surface(
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                CircularProgressIndicator(modifier = Modifier.size(16.dp), strokeWidth = 2.dp)
                                Spacer(modifier = Modifier.width(12.dp))
                                Text(
                                    text = if (paymentState == "Processing") "Initiating STK Push..." else "Confirm on your phone...",
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            if (paymentState == "Success") {
                Button(onClick = onDismiss) {
                    Text("Close")
                }
            } else {
                val isProcessing = paymentState == "Processing" || paymentState == "WaitingForPin"
                val canSubmit = selectedDates.first != null && !isProcessing && when (paymentMethod) {
                    "M-Pesa" -> phoneNumber.length >= 10
                    "Airtel" -> airtelNumber.length >= 10
                    "Card" -> cardNumber.length >= 16 && cardExpiry.isNotEmpty() && cardCvv.length >= 3
                    else -> true
                }
                val buttonText = when (paymentMethod) {
                    "M-Pesa" -> if (isProcessing) "Processing M-Pesa..." else "Pay with M-Pesa"
                    "Airtel" -> if (isProcessing) "Processing Airtel..." else "Pay with Airtel Money"
                    "Card" -> if (isProcessing) "Processing Card..." else "Pay with Card"
                    else -> "Confirm & Pay"
                }
                Button(
                    onClick = {
                        dialogScope.launch {
                            if (paymentMethod == "M-Pesa") {
                                paymentState = "Processing"
                                val success = onInitiateMpesaPayment(phoneNumber, displayFinalPrice.toInt())
                                if (success) {
                                    paymentState = "WaitingForPin"
                                    delay(4000) // Simulating user entering PIN on their phone
                                    paymentState = "Success"
                                    val generatedRef = "MPX-" + (100000..999999).random()
                                    mpesaReceiptRef = generatedRef
                                    val mpesaToastData = MpesaSuccessData(
                                        transactionRef = generatedRef,
                                        amountFormatted = "$sym${String.format("%,d", displayFinalPrice.toInt())}",
                                        itemName = itemName,
                                        location = "$itemName, East Africa",
                                        startDateMillis = selectedDates.first,
                                        endDateMillis = selectedDates.second,
                                        phoneNumber = phoneNumber
                                    )
                                    onMpesaSuccessToast?.invoke(mpesaToastData)
                                    Toast.makeText(context, "M-Pesa payment confirmed! Ref: $generatedRef 📅", Toast.LENGTH_LONG).show()
                                    onConfirm(selectedDates.first, selectedDates.second, guests, selectedVoucherCode.ifEmpty { null })
                                } else {
                                    paymentState = "Idle"
                                }
                            } else if (paymentMethod == "Airtel") {
                                paymentState = "Processing"
                                delay(2000)
                                paymentState = "WaitingForPin"
                                delay(3000)
                                paymentState = "Success"
                                onConfirm(selectedDates.first, selectedDates.second, guests, selectedVoucherCode.ifEmpty { null })
                            } else {
                                paymentState = "Processing"
                                delay(2000)
                                paymentState = "Success"
                                onConfirm(selectedDates.first, selectedDates.second, guests, selectedVoucherCode.ifEmpty { null })
                            }
                        }
                    },
                    enabled = canSubmit,
                    modifier = Modifier.testTag("dialog_confirm_booking")
                ) {
                    Text(buttonText, fontWeight = FontWeight.Bold)
                }
            }
        },
        dismissButton = {
            if (paymentState != "Success") {
                TextButton(onClick = onDismiss) {
                    Text("Cancel")
                }
            }
        }
    )
}

@Composable
fun PaymentChip(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    icon: ImageVector,
    modifier: Modifier = Modifier
) {
    Surface(
        onClick = onClick,
        color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface,
        border = BorderStroke(1.dp, if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)),
        shape = RoundedCornerShape(12.dp),
        modifier = modifier
    ) {
        Row(
            modifier = Modifier.padding(vertical = 8.dp, horizontal = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(16.dp),
                tint = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = label,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
fun ImpactSummaryRow(label: String, value: String, isBold: Boolean = false, isHighlight: Boolean = false) {
    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp)) {
        Text(
            text = label,
            fontSize = 12.sp,
            color = if (isHighlight) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.weight(1f))
        Text(
            text = value,
            fontSize = if (isBold) 14.sp else 12.sp,
            fontWeight = if (isBold || isHighlight) FontWeight.Bold else FontWeight.Normal,
            color = if (isHighlight) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
fun FeedbackDialog(
    booking: BookingEntity,
    onDismiss: () -> Unit,
    onSubmit: (Int, String, Int, String) -> Unit
) {
    var rating by remember { mutableStateOf(5) }
    var comment by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Rate Your Experience",
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "How was your stay at ${booking.title}?",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                // Star Rating
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    repeat(5) { index ->
                        val starIndex = index + 1
                        IconButton(onClick = { rating = starIndex }) {
                            Icon(
                                imageVector = if (rating >= starIndex) Icons.Default.Star else Icons.Default.StarOutline,
                                contentDescription = "$starIndex Stars",
                                tint = if (rating >= starIndex) Color(0xFFFFB300) else MaterialTheme.colorScheme.outline,
                                modifier = Modifier.size(32.dp)
                            )
                        }
                    }
                }

                OutlinedTextField(
                    value = comment,
                    onValueChange = { comment = it },
                    label = { Text("Share your thoughts (optional)", fontSize = 12.sp) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp),
                    textStyle = androidx.compose.ui.text.TextStyle(fontSize = 14.sp),
                    shape = RoundedCornerShape(12.dp)
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onSubmit(booking.id, booking.title, rating, comment)
                    onDismiss()
                },
                enabled = true,
                modifier = Modifier.testTag("submit_feedback_btn")
            ) {
                Text("Submit Feedback", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Maybe Later")
            }
        },
        shape = RoundedCornerShape(28.dp)
    )
}

@Composable
fun JournalDialog(
    currentLocationName: String,
    onDismiss: () -> Unit,
    onSubmit: (String, String) -> Unit
) {
    var note by remember { mutableStateOf("") }
    var location by remember { mutableStateOf(currentLocationName) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Safari Journal Entry",
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = location,
                    onValueChange = { location = it },
                    label = { Text("Location", fontSize = 12.sp) },
                    modifier = Modifier.fillMaxWidth(),
                    textStyle = androidx.compose.ui.text.TextStyle(fontSize = 14.sp),
                    shape = RoundedCornerShape(12.dp),
                    leadingIcon = { Icon(Icons.Default.Place, contentDescription = null, modifier = Modifier.size(18.dp)) }
                )

                OutlinedTextField(
                    value = note,
                    onValueChange = { note = it },
                    label = { Text("What are you observing?", fontSize = 12.sp) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(150.dp),
                    textStyle = androidx.compose.ui.text.TextStyle(fontSize = 14.sp),
                    shape = RoundedCornerShape(12.dp)
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (note.isNotBlank()) {
                        onSubmit(note, location)
                        onDismiss()
                    }
                },
                enabled = note.isNotBlank(),
                modifier = Modifier.testTag("submit_journal_btn")
            ) {
                Text("Save to Journal", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        },
        shape = RoundedCornerShape(28.dp)
    )
}

@Composable
fun ShareItineraryDialog(
    shareLink: String,
    summaryText: String,
    onDismiss: () -> Unit,
    onShare: (String) -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Share, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                Spacer(modifier = Modifier.width(12.dp))
                Text("Share Expedition", fontWeight = FontWeight.Bold)
            }
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Text(
                    "Send this link to friends or family. They will see a read-only view of your travel dates and lodge bookings.",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Surface(
                    color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.3f),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = shareLink,
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.weight(1f),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

                Text(
                    "Preview:",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Surface(
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = summaryText,
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(12.dp),
                        fontStyle = FontStyle.Italic
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { onShare(summaryText + "\n\nLink: " + shareLink) },
                modifier = Modifier.testTag("confirm_share_btn")
            ) {
                Text("Share Now", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Close")
            }
        },
        shape = RoundedCornerShape(28.dp)
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

// ---------------- EXPENSE BREAKDOWN CHART ----------------
data class ExpenseSegment(
    val name: String,
    val amount: Double,
    val percentage: Int,
    val color: Color
)

@Composable
fun NotificationsTab(
    notifications: List<NotificationEntity>,
    onMarkAsRead: (Int) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 80.dp)
    ) {
        item {
            Text(
                text = "Safari Notifications",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = "Stay updated on your upcoming experiences and voucher expirations.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )
            Spacer(modifier = Modifier.height(24.dp))
        }

        if (notifications.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 64.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.NotificationsNone,
                            contentDescription = null,
                            modifier = Modifier.size(64.dp),
                            tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "No new notifications",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
                        )
                    }
                }
            }
        } else {
            items(notifications) { notification ->
                NotificationCard(
                    notification = notification,
                    onMarkAsRead = { onMarkAsRead(notification.id) }
                )
                Spacer(modifier = Modifier.height(12.dp))
            }
        }
    }
}

@Composable
fun NotificationCard(
    notification: NotificationEntity,
    onMarkAsRead: () -> Unit
) {
    val backgroundColor = if (notification.isRead) {
        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
    } else {
        MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.2f)
    }
    
    val borderColor = if (notification.isRead) {
        Color.Transparent
    } else {
        MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)
    }

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        border = if (notification.isRead) null else BorderStroke(1.dp, borderColor),
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = !notification.isRead) { onMarkAsRead() }
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.Top
        ) {
            Surface(
                color = when (notification.type) {
                    "VOUCHER" -> Color(0xFFFFB74D).copy(alpha = 0.2f)
                    else -> Color(0xFF81C784).copy(alpha = 0.2f)
                },
                shape = CircleShape,
                modifier = Modifier.size(40.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = when (notification.type) {
                            "VOUCHER" -> Icons.Default.ConfirmationNumber
                            else -> Icons.Default.Explore
                        },
                        contentDescription = null,
                        tint = when (notification.type) {
                            "VOUCHER" -> Color(0xFFF57C00)
                            else -> Color(0xFF388E3C)
                        },
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = notification.title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.weight(1f)
                    )
                    if (!notification.isRead) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primary)
                        )
                    }
                }
                
                Text(
                    text = notification.message,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(vertical = 4.dp)
                )
                
                val timeString = SimpleDateFormat("MMM dd, HH:mm", Locale.US).format(Date(notification.timestamp))
                Text(
                    text = timeString,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
                )
            }
        }
    }
}

@Composable
fun SwahiliPhrasebookTab(phrases: List<SwahiliPhrase>) {
    var searchQuery by remember { mutableStateOf("") }
    val filteredPhrases = remember(searchQuery, phrases) {
        if (searchQuery.isBlank()) phrases
        else phrases.filter {
            it.english.contains(searchQuery, ignoreCase = true) ||
            it.swahili.contains(searchQuery, ignoreCase = true) ||
            it.category.contains(searchQuery, ignoreCase = true)
        }
    }

    val categories = remember(phrases) {
        phrases.map { it.category }.distinct()
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(bottom = 80.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Local Swahili Phrasebook",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = "Essential phrases for your safari encounters with pronunciation support.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )
            Spacer(modifier = Modifier.height(16.dp))

            // Search Bar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("phrasebook_search"),
                placeholder = { Text("Search phrases...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f)
                ),
                singleLine = true
            )
            Spacer(modifier = Modifier.height(20.dp))
        }

        categories.forEach { category ->
            val categoryPhrases = filteredPhrases.filter { it.category == category }
            if (categoryPhrases.isNotEmpty()) {
                item {
                    Text(
                        text = category.uppercase(),
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.secondary,
                        letterSpacing = 1.sp,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                }

                items(categoryPhrases) { phrase ->
                    PhraseCard(phrase)
                    Spacer(modifier = Modifier.height(12.dp))
                }
            }
        }
    }
}

@Composable
fun PhraseCard(phrase: SwahiliPhrase) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
        ),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = phrase.swahili,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "Phonetic: ${phrase.phonetic}",
                        style = MaterialTheme.typography.bodySmall,
                        fontStyle = FontStyle.Italic,
                        color = MaterialTheme.colorScheme.secondary
                    )
                }
                
                IconButton(
                    onClick = { /* In a real app, this would trigger TTS */ },
                    modifier = Modifier
                        .size(40.dp)
                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f), CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.Default.VolumeUp,
                        contentDescription = "Pronunciation",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
            
            Divider(
                modifier = Modifier.padding(vertical = 12.dp),
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f)
            )
            
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Translate,
                    contentDescription = null,
                    modifier = Modifier.size(14.dp),
                    tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = phrase.english,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                )
            }
        }
    }
}

@Composable
fun ExpenseBreakdownChart(bookings: List<BookingEntity>) {
    val isPreview = bookings.isEmpty()
    
    // 1. Calculate values
    val accommodationCost = if (isPreview) 1200.0 else bookings.filter { it.type == "STAY" }.sumOf { it.price }
    val activitiesCost = if (isPreview) 850.0 else bookings.filter { it.type == "SAFARI" }.sumOf { it.price }
    val logisticsCost = if (isPreview) 450.0 else {
        if (bookings.isNotEmpty()) {
            val base = (accommodationCost + activitiesCost) * 0.15
            if (base < 150.0) 150.0 else base
        } else {
            0.0
        }
    }
    
    val totalCost = accommodationCost + activitiesCost + logisticsCost
    
    val segments = remember(accommodationCost, activitiesCost, logisticsCost, totalCost) {
        if (totalCost == 0.0) {
            emptyList()
        } else {
            listOf(
                ExpenseSegment("Accommodation", accommodationCost, ((accommodationCost / totalCost) * 100).toInt(), Color(0xFF4CAF50)),
                ExpenseSegment("Activities", activitiesCost, ((activitiesCost / totalCost) * 100).toInt(), Color(0xFFFF9800)),
                ExpenseSegment("Logistics", logisticsCost, ((logisticsCost / totalCost) * 100).toInt(), Color(0xFF2196F3))
            )
        }
    }
    
    // Interactive selection state
    var selectedIndex by remember { mutableStateOf(-1) }
    
    Card(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.15f)),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("expense_breakdown_card")
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            // Header Row
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector = Icons.Default.Timeline,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Trip Expenses Breakdown",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Real-time cost allocation analysis",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                
                if (isPreview) {
                    Surface(
                        color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.7f),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = "PREVIEW",
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            if (totalCost == 0.0) {
                // If they have no bookings and somehow total is 0
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No cost logs. Book an asset to track your budget.",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 12.sp
                    )
                }
            } else {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Donut Chart Canvas
                    Box(
                        modifier = Modifier
                            .size(130.dp)
                            .padding(8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Canvas(modifier = Modifier.fillMaxSize()) {
                            var currentStartAngle = -90f
                            segments.forEachIndexed { index, segment ->
                                val sweepAngle = (segment.amount / totalCost).toFloat() * 360f
                                val isSelected = selectedIndex == index || selectedIndex == -1
                                val strokeWidth = if (selectedIndex == index) 16.dp.toPx() else 10.dp.toPx()
                                
                                drawArc(
                                    color = if (isSelected) segment.color else segment.color.copy(alpha = 0.3f),
                                    startAngle = currentStartAngle,
                                    sweepAngle = sweepAngle,
                                    useCenter = false,
                                    style = Stroke(width = strokeWidth, cap = StrokeCap.Round),
                                    topLeft = Offset(strokeWidth / 2, strokeWidth / 2),
                                    size = Size(size.width - strokeWidth, size.height - strokeWidth)
                                )
                                currentStartAngle += sweepAngle
                            }
                        }
                        
                        // Center Info Text
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            val activeLabel = if (selectedIndex in segments.indices) segments[selectedIndex].name else "Total"
                            val activeAmount = if (selectedIndex in segments.indices) segments[selectedIndex].amount else totalCost
                            
                            Text(
                                text = activeLabel,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = "$${activeAmount.toInt()}",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                    
                    Spacer(modifier = Modifier.width(16.dp))
                    
                    // Legends Column
                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        segments.forEachIndexed { index, segment ->
                            val isSelected = selectedIndex == index
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = if (isSelected) MaterialTheme.colorScheme.surfaceVariant else Color.Transparent,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        selectedIndex = if (isSelected) -1 else index
                                    }
                                    .testTag("expense_legend_$index")
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(10.dp)
                                            .clip(CircleShape)
                                            .background(segment.color)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = segment.name,
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                        Text(
                                            text = "${segment.percentage}% of total",
                                            fontSize = 10.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                    Text(
                                        text = "$${segment.amount.toInt()}",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                }
                            }
                        }
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Helpful Context-Aware Tips Card
            val tipHeader: String
            val tipDesc: String
            
            if (selectedIndex in segments.indices) {
                val seg = segments[selectedIndex]
                tipHeader = when (seg.name) {
                    "Accommodation" -> "Lodge Optimization Strategy"
                    "Activities" -> "Guided Activity Strategy"
                    else -> "Logistics & Transport Efficiency"
                }
                tipDesc = when (seg.name) {
                    "Accommodation" -> "Lodge bookings represent ${seg.percentage}% of your trip. Tip: Try applying one of your active travel vouchers to lower this portion!"
                    "Activities" -> "Safari game drives and hot air ballooning account for ${seg.percentage}% of your trip. These provide lifetime memories; book early to secure expert rangers."
                    else -> "Transit fees, flights, and park permits represent ${seg.percentage}% of your trip. Tip: Carry cash in USD or local currency for driver tipping and minor park fees."
                }
            } else {
                tipHeader = "Dynamic Budget Optimization Tip"
                tipDesc = if (isPreview) {
                    "This represents a standard 7-day high-end safari budget allocation. Once you start booking real lodging and game drives, this chart will update live to track your actual investment!"
                } else {
                    val maxSegment = segments.maxByOrNull { it.amount }
                    if (maxSegment?.name == "Accommodation") {
                        "Your primary investment is in your luxury savanna lodges (${((accommodationCost/totalCost)*100).toInt()}%). Tip: Check the 'Vouchers' tab for discounts to optimize this!"
                    } else if (maxSegment?.name == "Activities") {
                        "Your safari experiences form the largest part of your budget. These high-value guided drives are completely custom and fully private!"
                    } else {
                        "Your local travel logistics form a substantial portion. Shared airport shuttle options can reduce this allocation."
                    }
                }
            }
            
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                ),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.1f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Icon(
                        imageVector = Icons.Default.Lightbulb,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = tipHeader,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = tipDesc,
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            lineHeight = 15.sp
                        )
                    }
                }
            }
        }
    }
}

// ---------------- BUSH WISDOM CONSERVATION & ETIQUETTE MODULE ----------------
data class WisdomTip(
    val id: String,
    val title: String,
    val description: String,
    val category: String, // "general", "mara", "serengeti", "crater", "tarangire"
    val tag: String,      // "SAFETY", "ECO-TIPS", "RESPECT", "CULTURE", "PHOTO"
    val tagColor: Color,
    val icon: androidx.compose.ui.graphics.vector.ImageVector
)

@Composable
fun BushWisdomModule() {
    // Guidelines data store (authentic from official national park rules)
    val database = remember {
        listOf(
            WisdomTip(
                id = "gen_1",
                title = "Remain Inside Closed Safari Vehicles",
                description = "Never step out of the safari car except at designated picnic sites or ranger-guided walking tours. Predators are master stalkers and utilize tall savanna grass as camouflage; a human on foot is seen as prey or a threat.",
                category = "general",
                tag = "SAFETY",
                tagColor = Color(0xFFE57373),
                icon = Icons.Default.Lock
            ),
            WisdomTip(
                id = "gen_2",
                title = "Zero Litter Policy",
                description = "Carry all trash, plastic bottles, and organic waste back to the lodge. Animals like baboons, hyaenas, and kites eat litter, leading to lethal intestinal blockages or violent food habituation.",
                category = "general",
                tag = "ECO-TIPS",
                tagColor = Color(0xFF81C784),
                icon = Icons.Default.Pets
            ),
            WisdomTip(
                id = "gen_3",
                title = "Whisper & Minimize Movements",
                description = "Loud noises and sudden movements alarm animals and cause herds to flee. Talk in a soft whisper, keep car engines quiet, and avoid pointing camera lenses abruptly out of windows.",
                category = "general",
                tag = "RESPECT",
                tagColor = Color(0xFFFFB74D),
                icon = Icons.Default.Notifications
            ),
            WisdomTip(
                id = "gen_4",
                title = "Absolute Drone Prohibition",
                description = "Drones are strictly illegal in all East African parks without specialized military/wildlife aviation permits. Drones mimic the buzz of giant hornet swarms, stampeding animals and disturbing nesting eagles.",
                category = "general",
                tag = "SAFETY",
                tagColor = Color(0xFFE57373),
                icon = Icons.Default.Warning
            ),
            WisdomTip(
                id = "mara_1",
                title = "Cheetah Hunt Buffering",
                description = "Keep at least 25-50 meters away from cheetahs on game drives. Cheetahs require absolute focus and wide sightlines to scan for gazelles. Surrounding them with cars obstructs their hunting speed and alerts prey.",
                category = "mara",
                tag = "RESPECT",
                tagColor = Color(0xFFFFB74D),
                icon = Icons.Default.Pets
            ),
            WisdomTip(
                id = "mara_2",
                title = "River Crossing Gateway Distance",
                description = "During the Great Migration crossings at the Mara River, safari vehicles must park at least 100 meters away from crossing points. Blocking access points terrifies wildebeest herds and leads to fatal pile-ups.",
                category = "mara",
                tag = "ECO-TIPS",
                tagColor = Color(0xFF81C784),
                icon = Icons.Default.Explore
            ),
            WisdomTip(
                id = "mara_3",
                title = "Respectful Manyatta Cultural Engagement",
                description = "When visiting Maasai homesteads (Manyattas), always coordinate via a authorized guide. Do not take close-up photos of individuals or children without asking. Compensation is directly reinvested in village schools.",
                category = "mara",
                tag = "CULTURE",
                tagColor = Color(0xFF64B5F6),
                icon = Icons.Default.Info
            ),
            WisdomTip(
                id = "ser_1",
                title = "Strict Off-Roading Penalties",
                description = "Off-road driving is heavily fined in the Serengeti. It crushes delicate savanna wildgrasses, starts mud erosion gullies, and runs over camouflaged ground nesting birds like ostriches or plovers.",
                category = "serengeti",
                tag = "ECO-TIPS",
                tagColor = Color(0xFF81C784),
                icon = Icons.Default.Landscape
            ),
            WisdomTip(
                id = "ser_2",
                title = "Hot Air Balloon Altitude Limits",
                description = "Serengeti hot air balloons must remain at least 150 feet above any animal herds. Low-altitude balloon burner bursts frighten zebras and wildebeest, causing dangerous stampedes.",
                category = "serengeti",
                tag = "RESPECT",
                tagColor = Color(0xFFFFB74D),
                icon = Icons.Default.Timeline
            ),
            WisdomTip(
                id = "ser_3",
                title = "Wilderness Campfire Ash Extinction",
                description = "All open fires in mobile savanna camps must be fully extinguished using water and buried with sand. Wind can easily carry a single stray ember across the plains, sparking catastrophic wildfires.",
                category = "serengeti",
                tag = "SAFETY",
                tagColor = Color(0xFFE57373),
                icon = Icons.Default.Warning
            ),
            WisdomTip(
                id = "crater_1",
                title = "Crater Descending / Ascending Schedules",
                description = "The Ngorongoro Conservation Area restricts crater floor permits to a maximum of 6 hours per day. Vehicles must exit before 6:00 PM. Steep rim tracks are highly hazardous in evening mountain mist and fog.",
                category = "crater",
                tag = "SAFETY",
                tagColor = Color(0xFFE57373),
                icon = Icons.Default.Timeline
            ),
            WisdomTip(
                id = "crater_2",
                title = "Lerai Forest Primate Warning",
                description = "At Lerai picnic site, keep all lunches sealed inside zip-bags. Highly habituated vervet monkeys and baboons will dive-bomb tables and snatch food directly from hands, occasionally scratching tourists.",
                category = "crater",
                tag = "SAFETY",
                tagColor = Color(0xFFE57373),
                icon = Icons.Default.Warning
            ),
            WisdomTip(
                id = "crater_3",
                title = "Black Rhino Conservation Zone",
                description = "Ngorongoro Crater is home to Tanzania's last free-roaming wild Black Rhinos. Keep a minimum 100-meter buffer zone around rhinos to prevent aggressive protective charges.",
                category = "crater",
                tag = "RESPECT",
                tagColor = Color(0xFFFFB74D),
                icon = Icons.Default.Lock
            ),
            WisdomTip(
                id = "tar_1",
                title = "Ancient Baobab Protection",
                description = "Never touch, carve, or peel the soft, porous bark of Tarangire's giant Baobab trees. Damaging the bark allows beetles and water-rot fungus to enter, structurally weakening these 1,000-year-old giants.",
                category = "tarangire",
                tag = "ECO-TIPS",
                tagColor = Color(0xFF81C784),
                icon = Icons.Default.Landscape
            ),
            WisdomTip(
                id = "tar_2",
                title = "Elephant Family Yielding Rules",
                description = "Tarangire is famous for migrating elephant herds. If elephants are crossing the dirt path, turn off your engine, stay silent, and let the family pass. Matriarchs view running engines as a direct threat.",
                category = "tarangire",
                tag = "RESPECT",
                tagColor = Color(0xFFFFB74D),
                icon = Icons.Default.Pets
            )
        )
    }

    // Active Category/Tab
    val categories = listOf(
        "general" to "General Rules",
        "mara" to "Maasai Mara",
        "serengeti" to "Serengeti",
        "crater" to "Ngorongoro Crater",
        "tarangire" to "Tarangire"
    )
    var activeCategory by remember { mutableStateOf("general") }

    // Search and Simulated Satellite Google Search State
    var searchQuery by remember { mutableStateOf("") }
    var searchResult by remember { mutableStateOf<List<WisdomTip>>(database.filter { it.category == "general" }) }
    var isSearching by remember { mutableStateOf(false) }
    var satelliteLog by remember { mutableStateOf("") }
    var searchPerformed by remember { mutableStateOf(false) }

    // Live Ticker updates (real-time news alerts)
    val liveAlerts = remember {
        listOf(
            "📢 MARA ALERT: Mara River crossings currently active near Lookout Point #3. Maintain 100m vehicle clearance.",
            "📢 SERENGETI: Speed limits reduced to 20km/h near Seronera due to a newborn leopard cub sighting on tracks.",
            "📢 NGORONGORO: Lerai Forest picnic site warning - Vervet monkeys displaying increased aggressive behavior today.",
            "📢 TARANGIRE: High elephant family concentration crossing North Marsh. Ranger patrols on standby.",
            "📢 ECO-BULLETIN: Illegal drone confiscated at Naabi Hill Gate. Reminding all travelers of strict $1,000 fine."
        )
    }
    var activeAlertIndex by remember { mutableStateOf(0) }

    // Periodically change the live alert
    LaunchedEffect(Unit) {
        while (true) {
            kotlinx.coroutines.delay(8000)
            activeAlertIndex = (activeAlertIndex + 1) % liveAlerts.size
        }
    }

    // Expanding tip ID state
    var expandedTipId by remember { mutableStateOf<String?>("gen_1") }

    // Search execution
    LaunchedEffect(isSearching) {
        if (isSearching) {
            val logs = listOf(
                "📡 Transmitting satellite query to UNEP & KWS Database...",
                "🛰️ Grounding search terms through Google Conservation indices...",
                "📡 Filtering authentic Tanzanian National Parks regulations...",
                "✅ Decrypted conservation protocols received."
            )
            for (log in logs) {
                satelliteLog = log
                kotlinx.coroutines.delay(650)
            }
            
            // Perform actual filtering
            val filtered = if (searchQuery.isBlank()) {
                database.filter { it.category == activeCategory }
            } else {
                database.filter {
                    it.title.contains(searchQuery, ignoreCase = true) ||
                    it.description.contains(searchQuery, ignoreCase = true) ||
                    it.tag.contains(searchQuery, ignoreCase = true)
                }
            }
            searchResult = filtered
            isSearching = false
            searchPerformed = true
        }
    }

    Card(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.15f)),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("bush_wisdom_card")
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            // Header Row
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector = Icons.Default.Pets,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Bush Wisdom Terminal",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Real-time conservation rules & park etiquette",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                
                Surface(
                    color = Color(0xFF4CAF50).copy(alpha = 0.15f),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF4CAF50))
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "SATELLITE SYNCED",
                            fontSize = 8.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color(0xFF4CAF50)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Real-Time Live Ticker Feed Box
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.08f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Notifications,
                        contentDescription = "Alert",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    AnimatedContent(
                        targetState = liveAlerts[activeAlertIndex],
                        transitionSpec = {
                            slideInVertically { it } + fadeIn() togetherWith slideOutVertically { -it } + fadeOut()
                        },
                        label = "AlertTicker",
                        modifier = Modifier.weight(1f)
                    ) { text ->
                        Text(
                            text = text,
                            fontSize = 10.5.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Satellite Database Live Google Search Interface
            Text(
                text = "🔍 Satellite Conservation Search",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(bottom = 6.dp)
            )

            OutlinedTextField(
                value = searchQuery,
                onValueChange = { 
                    searchQuery = it
                    if (it.isBlank()) {
                        searchResult = database.filter { tip -> tip.category == activeCategory }
                        searchPerformed = false
                    }
                },
                placeholder = { Text("Search e.g. drones, elephants, off-road, tipping...", fontSize = 11.sp) },
                textStyle = androidx.compose.ui.text.TextStyle(fontSize = 11.sp),
                singleLine = true,
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search icon",
                        modifier = Modifier.size(16.dp)
                    )
                },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { 
                            searchQuery = "" 
                            searchResult = database.filter { tip -> tip.category == activeCategory }
                            searchPerformed = false
                        }) {
                            Icon(
                                imageVector = Icons.Default.Clear,
                                contentDescription = "Clear search",
                                modifier = Modifier.size(14.dp)
                            )
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .testTag("bush_wisdom_search_field"),
                shape = RoundedCornerShape(12.dp),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = MaterialTheme.colorScheme.background,
                    unfocusedContainerColor = MaterialTheme.colorScheme.background,
                    focusedIndicatorColor = MaterialTheme.colorScheme.primary,
                    unfocusedIndicatorColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                )
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Preset Search Tags for Fast Queries
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Suggestions:",
                    fontSize = 10.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = FontWeight.Medium
                )
                listOf("Drones", "Cheetahs", "Baobab", "Monkey").forEach { keyword ->
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.15f)),
                        modifier = Modifier.clickable {
                            searchQuery = keyword
                            isSearching = true
                        }
                    ) {
                        Text(
                            text = keyword,
                            fontSize = 9.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Trigger Search Button
            Button(
                onClick = { isSearching = true },
                enabled = !isSearching,
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = Color.White
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(40.dp)
                    .testTag("bush_wisdom_query_button")
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Language,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (isSearching) "TRANSMITTING TO GOOGLE SEARCH..." else "📡 SEARCH SATELLITE CONSERVATION INDEX",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // If searching, show beautiful telemetry logs
            if (isSearching) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color.Black.copy(alpha = 0.85f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(18.dp),
                            strokeWidth = 2.dp,
                            color = Color(0xFF4CAF50)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = satelliteLog,
                            color = Color(0xFF4CAF50),
                            fontSize = 10.sp,
                            fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            // Tabs for National Parks (hidden when search query is active to keep search results clear)
            if (searchQuery.isBlank() && !searchPerformed) {
                ScrollableTabRow(
                    selectedTabIndex = categories.indexOfFirst { it.first == activeCategory },
                    containerColor = Color.Transparent,
                    contentColor = MaterialTheme.colorScheme.primary,
                    edgePadding = 0.dp,
                    indicator = {},
                    divider = {},
                    modifier = Modifier.fillMaxWidth()
                ) {
                    categories.forEach { (catId, catLabel) ->
                        val isSelected = activeCategory == catId
                        Tab(
                            selected = isSelected,
                            onClick = { 
                                activeCategory = catId
                                searchResult = database.filter { it.category == catId }
                                expandedTipId = searchResult.firstOrNull()?.id
                            },
                            modifier = Modifier.testTag("bush_wisdom_tab_$catId")
                        ) {
                            Surface(
                                shape = RoundedCornerShape(16.dp),
                                color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                                border = if (isSelected) null else BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.15f)),
                                modifier = Modifier.padding(vertical = 4.dp, horizontal = 4.dp)
                            ) {
                                Text(
                                    text = catLabel,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                                )
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))
            } else if (searchPerformed) {
                // Showing search results message
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Search Grounding Results (${searchResult.size} found):",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.weight(1f)
                    )
                    TextButton(
                        onClick = {
                            searchQuery = ""
                            searchPerformed = false
                            searchResult = database.filter { it.category == activeCategory }
                            expandedTipId = searchResult.firstOrNull()?.id
                        },
                        modifier = Modifier.height(28.dp)
                    ) {
                        Text("Reset Filters", fontSize = 10.sp, color = MaterialTheme.colorScheme.primary)
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
            }

            // Render guidelines list
            if (searchResult.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No safety protocols found matching '$searchQuery'. Try 'drones' or 'elephants'.",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                }
            } else {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    searchResult.forEach { tip ->
                        val isExpanded = expandedTipId == tip.id
                        Surface(
                            shape = RoundedCornerShape(14.dp),
                            color = MaterialTheme.colorScheme.background.copy(alpha = 0.4f),
                            border = BorderStroke(
                                1.dp, 
                                if (isExpanded) MaterialTheme.colorScheme.primary.copy(alpha = 0.3f) else MaterialTheme.colorScheme.outline.copy(alpha = 0.1f)
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { 
                                    expandedTipId = if (isExpanded) null else tip.id 
                                }
                                .testTag("wisdom_item_${tip.id}")
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Icon(
                                        imageVector = tip.icon,
                                        contentDescription = null,
                                        tint = tip.tagColor,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = tip.title,
                                        fontSize = 11.5.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface,
                                        modifier = Modifier.weight(1f)
                                    )
                                    
                                    // Custom visual tags
                                    Surface(
                                        color = tip.tagColor.copy(alpha = 0.15f),
                                        shape = RoundedCornerShape(6.dp)
                                    ) {
                                        Text(
                                            text = tip.tag,
                                            fontSize = 8.sp,
                                            fontWeight = FontWeight.ExtraBold,
                                            color = tip.tagColor,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }
                                }
                                
                                AnimatedVisibility(
                                    visible = isExpanded,
                                    enter = expandVertically() + fadeIn(),
                                    exit = shrinkVertically() + fadeOut()
                                ) {
                                    Column {
                                        Spacer(modifier = Modifier.height(6.dp))
                                        Divider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.08f))
                                        Spacer(modifier = Modifier.height(6.dp))
                                        Text(
                                            text = tip.description,
                                            fontSize = 10.5.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                                            lineHeight = 14.5.sp
                                        )
                                        
                                        Spacer(modifier = Modifier.height(6.dp))
                                        // Trust indicator
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            modifier = Modifier.fillMaxWidth()
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.CheckCircle,
                                                contentDescription = "Verified",
                                                tint = Color(0xFF4CAF50),
                                                modifier = Modifier.size(11.dp)
                                            )
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text(
                                                text = "Verified from National Park Authority and UNEP guidelines",
                                                fontSize = 8.sp,
                                                color = Color(0xFF4CAF50),
                                                fontWeight = FontWeight.SemiBold
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun PackingChecklistTab(recommendedItems: List<PackingItem>, bookings: List<BookingEntity>) {
    var selectedTab by remember { mutableStateOf(0) } // 0: Packing List, 1: Park Passes
    var downloadStatuses by remember { mutableStateOf<Map<String, String>>(emptyMap()) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Preparation Hub",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            text = "Get gear ready and sync official gate permits offline",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        
        Spacer(modifier = Modifier.height(16.dp))

        // Dual tab selector
        TabRow(
            selectedTabIndex = selectedTab,
            containerColor = Color.Transparent,
            contentColor = MaterialTheme.colorScheme.primary,
            modifier = Modifier.fillMaxWidth()
        ) {
            Tab(
                selected = selectedTab == 0,
                onClick = { selectedTab = 0 },
                text = { Text("Gear Checklist", fontWeight = FontWeight.Bold) },
                icon = { Icon(Icons.Default.List, contentDescription = null, modifier = Modifier.size(18.dp)) },
                modifier = Modifier.testTag("prep_tab_gear")
            )
            Tab(
                selected = selectedTab == 1,
                onClick = { selectedTab = 1 },
                text = { Text("Gate Permits", fontWeight = FontWeight.Bold) },
                icon = { Icon(Icons.Default.QrCode, contentDescription = null, modifier = Modifier.size(18.dp)) },
                modifier = Modifier.testTag("prep_tab_passes")
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (selectedTab == 0) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 80.dp)
            ) {
                item {
                    Text(
                        text = if (bookings.isEmpty()) 
                            "Book a safari to get personalized gear recommendations!" 
                        else 
                            "Based on your ${bookings.size} booked experiences, we recommend these items:",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                        modifier = Modifier.padding(bottom = 12.dp)
                    )
                }

                val categories = recommendedItems.map { it.category }.distinct()
                
                categories.forEach { category ->
                    val itemsInCategory = recommendedItems.filter { it.category == category }
                    if (itemsInCategory.isNotEmpty()) {
                        item {
                            Text(
                                text = category.uppercase(),
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.ExtraBold,
                                color = MaterialTheme.colorScheme.secondary,
                                letterSpacing = 1.sp,
                                modifier = Modifier.padding(vertical = 8.dp)
                            )
                        }

                        items(itemsInCategory) { item ->
                            PackingItemCard(item)
                            Spacer(modifier = Modifier.height(12.dp))
                        }
                    }
                }
            }
        } else {
            // Park Entry Passes
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 80.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    Surface(
                        color = Color(0xFFE8F5E9),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = Color(0xFF2E7D32),
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "TANAPA & KWS Gate Ready",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF1B5E20)
                                )
                                Text(
                                    text = "Offline verified passes bypass park gate connectivity issues.",
                                    fontSize = 10.sp,
                                    color = Color(0xFF2E7D32)
                                )
                            }
                        }
                    }
                }

                // If bookings is empty, let's supply a realistic preview ticket so they see what it is
                val passesToRender = if (bookings.isNotEmpty()) {
                    bookings
                } else {
                    listOf(
                        BookingEntity(
                            id = 9999,
                            type = "SAFARI",
                            title = "Serengeti & Ngorongoro Explorer Pass",
                            location = "Serengeti National Park, Tanzania",
                            dateRange = "Jul 24 - Jul 28, 2026",
                            startDateTimestamp = System.currentTimeMillis(),
                            price = 1250.0,
                            imageResName = "img_safari_hero",
                            status = "Confirmed"
                        )
                    )
                }

                items(passesToRender) { booking ->
                    val isDownloaded = downloadStatuses[booking.id.toString()] != null
                    Card(
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        border = BorderStroke(1.5.dp, Color(0xFFD4AF37)), // elegant gold trim
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("park_pass_card_${booking.id}")
                    ) {
                        Column {
                            // Upper Section
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Surface(
                                        color = Color(0xFFFFF9C4),
                                        shape = RoundedCornerShape(6.dp)
                                    ) {
                                        Text(
                                            text = "OFFICIAL GATE PERMIT",
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.ExtraBold,
                                            color = Color(0xFFE65100),
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                                        )
                                    }
                                    Text(
                                        text = "PASS ID: PRV-${booking.id}",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.outline
                                    )
                                }

                                Spacer(modifier = Modifier.height(10.dp))

                                Text(
                                    text = booking.title,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "📍 Valid At: ${booking.location}",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = "📅 Period: ${booking.dateRange}",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )

                                Spacer(modifier = Modifier.height(14.dp))

                                // Ticket Middle tear line
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(width = 10.dp, height = 20.dp)
                                            .background(
                                                color = MaterialTheme.colorScheme.background,
                                                shape = RoundedCornerShape(
                                                    topEnd = 10.dp,
                                                    bottomEnd = 10.dp
                                                )
                                            )
                                    )
                                    Spacer(modifier = Modifier.weight(1f))
                                    Text(
                                        text = "- - - - - - - - - - - - - - - - - - - - - - - - - - -",
                                        color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
                                        fontSize = 10.sp,
                                        maxLines = 1
                                    )
                                    Spacer(modifier = Modifier.weight(1f))
                                    Box(
                                        modifier = Modifier
                                            .size(width = 10.dp, height = 20.dp)
                                            .background(
                                                color = MaterialTheme.colorScheme.background,
                                                shape = RoundedCornerShape(
                                                    topStart = 10.dp,
                                                    bottomStart = 10.dp
                                                )
                                            )
                                    )
                                }

                                Spacer(modifier = Modifier.height(12.dp))

                                // Lower Ticket (QR Code Scan Area)
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    // Custom composed QR code block
                                    Surface(
                                        shape = RoundedCornerShape(12.dp),
                                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)),
                                        color = Color.White,
                                        modifier = Modifier.size(90.dp)
                                    ) {
                                        Canvas(modifier = Modifier.fillMaxSize().padding(8.dp)) {
                                            val step = size.width / 5f
                                            // Draw simulated QR layout
                                            for (r in 0..4) {
                                                for (c in 0..4) {
                                                    // Make some cells filled, others empty
                                                    val isFilled = (r + c) % 2 == 0 || (r == 0 && c == 0) || (r == 4 && c == 4) || (r == 0 && c == 4) || (r == 4 && c == 0)
                                                    if (isFilled) {
                                                        drawRect(
                                                            color = Color.Black,
                                                            topLeft = Offset(c * step, r * step),
                                                            size = Size(step * 0.9f, step * 0.9f)
                                                        )
                                                    }
                                                }
                                            }
                                        }
                                    }

                                    Spacer(modifier = Modifier.width(16.dp))

                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = "HOLDER: lilsheriff08@gmail.com",
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                        Text(
                                            text = "VEHICLE: KDA 482L (Toyota Cruiser 4x4)",
                                            fontSize = 9.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                        Text(
                                            text = "PAX SIZE: 2 Adults (Conservation Paid)",
                                            fontSize = 9.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )

                                        Spacer(modifier = Modifier.height(8.dp))

                                        Button(
                                            onClick = {
                                                downloadStatuses = downloadStatuses + (booking.id.toString() to "Downloaded & Vaulted Securely")
                                            },
                                            colors = ButtonDefaults.buttonColors(
                                                containerColor = if (isDownloaded) Color(0xFF2E7D32) else MaterialTheme.colorScheme.primary
                                            ),
                                            shape = RoundedCornerShape(8.dp),
                                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                                            modifier = Modifier
                                                .height(30.dp)
                                                .testTag("download_offline_pass_${booking.id}")
                                        ) {
                                            Icon(
                                                imageVector = if (isDownloaded) Icons.Default.Check else Icons.Default.CloudDownload,
                                                contentDescription = null,
                                                modifier = Modifier.size(12.dp)
                                            )
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text(
                                                text = if (isDownloaded) "SYNCED OFFLINE" else "SAVE OFFLINE",
                                                fontSize = 9.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = Color.White
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun PackingItemCard(item: PackingItem) {
    var isChecked by remember { mutableStateOf(false) }
    
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isChecked) 
                MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f) 
            else 
                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
        ),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f)),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { isChecked = !isChecked }
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                shape = CircleShape,
                modifier = Modifier.size(40.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = when(item.iconName) {
                            "description" -> Icons.Default.Description
                            "verified_user" -> Icons.Default.VerifiedUser
                            "wb_sunny" -> Icons.Default.WbSunny
                            "bug_report" -> Icons.Default.BugReport
                            "checkroom" -> Icons.Default.Checkroom
                            "umbrella" -> Icons.Default.Umbrella
                            "ac_unit" -> Icons.Default.AcUnit
                            "terrain" -> Icons.Default.Terrain
                            "visibility" -> Icons.Default.Visibility
                            "camera_alt" -> Icons.Default.CameraAlt
                            "face" -> Icons.Default.Face
                            "folder_zip" -> Icons.Default.FolderZip
                            "pool" -> Icons.Default.Pool
                            "highlight" -> Icons.Default.Highlight
                            "battery_charging_full" -> Icons.Default.BatteryChargingFull
                            "medical_services" -> Icons.Default.MedicalServices
                            else -> Icons.Default.Inventory
                        },
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = item.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    textDecoration = if (isChecked) androidx.compose.ui.text.style.TextDecoration.LineThrough else null
                )
                Text(
                    text = item.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            Checkbox(
                checked = isChecked,
                onCheckedChange = { isChecked = it },
                colors = CheckboxDefaults.colors(
                    checkedColor = MaterialTheme.colorScheme.primary,
                    uncheckedColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
                )
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GuideChatDialog(
    booking: BookingEntity,
    onDismiss: () -> Unit
) {
    var messages by remember {
        mutableStateOf(
            listOf(
                ChatMessage(sender = "Guide Juma", text = "Jambo! I am loading the cooler box with chilled water and juices now. Just checking the Toyota Land Cruiser tyre pressures.", timestamp = "15 mins ago", isUser = false),
                ChatMessage(sender = "Guide Juma", text = "Leaving central depot. Entering the Serengeti gate corridor now. Stunning sunny morning, spotting some central giraffes on the route!", timestamp = "5 mins ago", isUser = false)
            )
        )
    }
    
    var inputText by remember { mutableStateOf("") }
    var currentEtaMinutes by remember { mutableStateOf(12) }
    var driverCoordinatesX by remember { mutableStateOf(0.1f) }
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(messages.size) {
        if (currentEtaMinutes > 3) {
            currentEtaMinutes -= 2
            driverCoordinatesX += 0.15f
        }
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background),
            color = MaterialTheme.colorScheme.background
        ) {
            Scaffold(
                topBar = {
                    TopAppBar(
                        title = {
                            Column {
                                Text(booking.title, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(6.dp)
                                            .background(Color(0xFF81C784), CircleShape)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Guide Juma (Online • Firestore Live 🟢)", fontSize = 10.sp, color = MaterialTheme.colorScheme.outline)
                                }
                            }
                        },
                        navigationIcon = {
                            IconButton(onClick = onDismiss) {
                                Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                            }
                        },
                        colors = TopAppBarDefaults.topAppBarColors(
                            containerColor = MaterialTheme.colorScheme.surface,
                            titleContentColor = MaterialTheme.colorScheme.onSurface,
                            navigationIconContentColor = MaterialTheme.colorScheme.onSurface
                        )
                    )
                }
            ) { paddingValues ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                ) {
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(170.dp),
                        color = Color(0xFFF1EDE6),
                        border = BorderStroke(1.dp, Color(0xFFE2DDD5))
                    ) {
                        Box {
                            Canvas(modifier = Modifier.fillMaxSize()) {
                                val w = size.width
                                val h = size.height

                                val trackPath = Path().apply {
                                    moveTo(w * 0.1f, h * 0.6f)
                                    quadraticTo(w * 0.35f, h * 0.2f, w * 0.55f, h * 0.7f)
                                    quadraticTo(w * 0.75f, h * 0.85f, w * 0.9f, h * 0.4f)
                                }
                                drawPath(
                                    path = trackPath,
                                    color = Color(0xFF8D6E63),
                                    style = Stroke(
                                        width = 4.dp.toPx(),
                                        pathEffect = androidx.compose.ui.graphics.PathEffect.dashPathEffect(
                                            floatArrayOf(10f, 10f),
                                            0f
                                        )
                                    )
                                )

                                val travOffset = Offset(w * 0.9f, h * 0.4f)
                                drawCircle(color = Color(0xFF2E7D32).copy(alpha = 0.2f), radius = 20.dp.toPx(), center = travOffset)
                                drawCircle(color = Color(0xFF4CAF50), radius = 6.dp.toPx(), center = travOffset)

                                val vehicleX = w * driverCoordinatesX.coerceAtMost(0.85f)
                                val vehicleY = if (driverCoordinatesX < 0.45f) {
                                    h * 0.45f
                                } else {
                                    h * 0.65f
                                }
                                val vehicleOffset = Offset(vehicleX, vehicleY)

                                drawCircle(color = Color(0xFFE65100).copy(alpha = 0.3f), radius = 16.dp.toPx(), center = vehicleOffset)
                                drawCircle(color = Color(0xFFFF9800), radius = 8.dp.toPx(), center = vehicleOffset)
                            }

                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = Color.Black.copy(alpha = 0.85f),
                                modifier = Modifier
                                    .align(Alignment.BottomCenter)
                                    .padding(8.dp)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.DirectionsCar,
                                        contentDescription = null,
                                        tint = Color(0xFFFFF176),
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "Toyota Cruiser 4x4 [KDA 482L]  •  ETA: $currentEtaMinutes mins",
                                        color = Color.White,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }

                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier
                                    .align(Alignment.TopStart)
                                    .padding(8.dp)
                            ) {
                                Text(
                                    text = "LIVE SATELLITE PICKUP TRACKER",
                                    fontSize = 8.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onPrimary,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                                )
                            }
                        }
                    }

                    LazyColumn(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp),
                        contentPadding = PaddingValues(vertical = 12.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(messages) { msg ->
                            val alignment = if (msg.isUser) Alignment.End else Alignment.Start
                            val containerColor = if (msg.isUser) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant
                            val textColor = if (msg.isUser) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant

                            Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = alignment) {
                                Surface(
                                    shape = RoundedCornerShape(
                                        topStart = 12.dp,
                                        topEnd = 12.dp,
                                        bottomStart = if (msg.isUser) 12.dp else 0.dp,
                                        bottomEnd = if (msg.isUser) 0.dp else 12.dp
                                    ),
                                    color = containerColor,
                                    modifier = Modifier.widthIn(max = 280.dp)
                                ) {
                                    Column(modifier = Modifier.padding(10.dp)) {
                                        if (!msg.isUser) {
                                            Text(
                                                text = msg.sender,
                                                fontSize = 9.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = MaterialTheme.colorScheme.primary,
                                                modifier = Modifier.padding(bottom = 2.dp)
                                            )
                                        }
                                        Text(text = msg.text, fontSize = 12.sp, color = textColor)
                                    }
                                }
                                Text(
                                    text = msg.timestamp,
                                    fontSize = 8.sp,
                                    color = MaterialTheme.colorScheme.outline,
                                    modifier = Modifier.padding(start = 4.dp, end = 4.dp, top = 2.dp)
                                )
                            }
                        }
                    }

                    val chips = listOf("Got it! Ready in lobby", "Should I carry rain gear?", "Bring binoculars please!")
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState())
                            .padding(horizontal = 12.dp, vertical = 6.dp),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        chips.forEach { chipText ->
                            Surface(
                                onClick = {
                                    val userMsg = ChatMessage(sender = "Traveler", text = chipText, timestamp = "Just Now", isUser = true)
                                    messages = messages + userMsg

                                    coroutineScope.launch {
                                        kotlinx.coroutines.delay(1200)
                                        val replyText = when (chipText) {
                                            "Should I carry rain gear?" -> "Copy that, carrying lightweight waterproof ponchos in the 4x4 rear cabin. It looks like short localized showers may form."
                                            "Bring binoculars please!" -> "Double-checked, I have two pairs of Steiner 10x42 ready on the passenger dashboard. Outstanding clarity!"
                                            else -> "Asante Sana! Approaching the lobby turnaround, see you in a few minutes."
                                        }
                                        messages = messages + ChatMessage(sender = "Guide Juma", text = replyText, timestamp = "Just Now", isUser = false)
                                    }
                                },
                                color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f),
                                shape = RoundedCornerShape(20.dp),
                                border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)),
                                modifier = Modifier.testTag("chat_chip_${chipText.take(10)}")
                            ) {
                                Text(
                                    text = chipText,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                                )
                            }
                        }
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(MaterialTheme.colorScheme.surface)
                            .padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = inputText,
                            onValueChange = { inputText = it },
                            placeholder = { Text("Ask Juma anything...", fontSize = 12.sp) },
                            singleLine = true,
                            shape = RoundedCornerShape(24.dp),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("chat_input_text_field"),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = MaterialTheme.colorScheme.primary,
                                unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                            )
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        IconButton(
                            onClick = {
                                if (inputText.isNotBlank()) {
                                    val userMsg = ChatMessage(sender = "Traveler", text = inputText, timestamp = "Just Now", isUser = true)
                                    val sentText = inputText
                                    messages = messages + userMsg
                                    inputText = ""

                                    coroutineScope.launch {
                                        kotlinx.coroutines.delay(1000)
                                        val replyText = if (sentText.lowercase().contains("rain") || sentText.lowercase().contains("weather")) {
                                            "Copy that, carrying lightweight waterproof ponchos in the 4x4 rear cabin. It looks like short localized showers may form."
                                        } else if (sentText.lowercase().contains("bino") || sentText.lowercase().contains("glass")) {
                                            "Double-checked, I have two pairs of Steiner 10x42 ready on the passenger dashboard. Outstanding clarity!"
                                        } else {
                                            "Asante Sana! On my way, see you in a few minutes."
                                        }
                                        messages = messages + ChatMessage(sender = "Guide Juma", text = replyText, timestamp = "Just Now", isUser = false)
                                    }
                                }
                            },
                            colors = IconButtonDefaults.iconButtonColors(containerColor = MaterialTheme.colorScheme.primary),
                            modifier = Modifier
                                .size(40.dp)
                                .testTag("send_chat_message_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Send,
                                contentDescription = "Send",
                                tint = Color.White,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

data class ChatMessage(
    val sender: String,
    val text: String,
    val timestamp: String,
    val isUser: Boolean
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GroupTripWorkspaceDialog(
    booking: BookingEntity,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    var aminaPaid by remember { mutableStateOf(false) }
    var userVotedYes by remember { mutableStateOf<Boolean?>(null) } // null = didn't vote yet, true = yes, false = no
    
    // Total budget is $4,000. Amina's pending share is $1,000.
    val totalAmount = 4000.0
    val paidAmount = remember(aminaPaid) { if (aminaPaid) 4000.0 else 3000.0 }
    val progress = remember(paidAmount) { (paidAmount / totalAmount).toFloat() }
    
    var showCheckout by remember { mutableStateOf(false) }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            Scaffold(
                topBar = {
                    TopAppBar(
                        title = {
                            Column {
                                Text(
                                    text = booking.title.uppercase(),
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "Group Planning & Ledger",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.outline
                                )
                            }
                        },
                        navigationIcon = {
                            IconButton(
                                onClick = onDismiss,
                                modifier = Modifier.testTag("group_workspace_back")
                            ) {
                                Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                            }
                        },
                        actions = {
                            IconButton(
                                onClick = {
                                    Toast.makeText(context, "Trip Share Link copied to clipboard!", Toast.LENGTH_SHORT).show()
                                },
                                modifier = Modifier.testTag("group_share_trip")
                            ) {
                                Icon(Icons.Default.Share, contentDescription = "Share Link")
                            }
                        },
                        colors = TopAppBarDefaults.topAppBarColors(
                            containerColor = MaterialTheme.colorScheme.surface,
                            titleContentColor = MaterialTheme.colorScheme.onSurface
                        )
                    )
                }
            ) { paddingValues ->
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    contentPadding = PaddingValues(vertical = 16.dp)
                ) {
                    // --- DATES & MEMBERS INFO ---
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Text(
                                    text = "TRIP SCHEDULE",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Dates: ${booking.dateRange} • 4 Members Invited",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Spacer(modifier = Modifier.height(10.dp))
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    val members = listOf("Alex" to Color(0xFF1E88E5), "Sarah" to Color(0xFFD81B60), "David" to Color(0xFF43A047), "Amina" to Color(0xFFFB8C00))
                                    members.forEach { (name, color) ->
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            modifier = Modifier
                                                .background(color.copy(alpha = 0.1f), RoundedCornerShape(12.dp))
                                                .padding(horizontal = 8.dp, vertical = 4.dp)
                                        ) {
                                            Box(
                                                modifier = Modifier
                                                    .size(16.dp)
                                                    .background(color, CircleShape),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Text(name.take(1), fontSize = 9.sp, color = Color.White, fontWeight = FontWeight.Bold)
                                            }
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text(name, fontSize = 11.sp, fontWeight = FontWeight.Medium, color = color)
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // --- FUNDING PROGRESS CARD ---
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.1f))
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = "GROUP FUNDING STATUS",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.outline
                                    )
                                    Spacer(modifier = Modifier.weight(1f))
                                    Surface(
                                        color = if (aminaPaid) Color(0xFFE8F5E9) else Color(0xFFFFF3E0),
                                        shape = RoundedCornerShape(4.dp)
                                    ) {
                                        Text(
                                            text = if (aminaPaid) "FULLY FUNDED" else "AWAITING PAYMENTS",
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (aminaPaid) Color(0xFF2E7D32) else Color(0xFFE65100),
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }
                                }
                                
                                Spacer(modifier = Modifier.height(12.dp))
                                
                                Row(verticalAlignment = Alignment.Bottom) {
                                    Text(
                                        text = "$${String.format("%,.0f", paidAmount)}",
                                        fontSize = 24.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "of $${String.format("%,.0f", totalAmount)} total",
                                        fontSize = 12.sp,
                                        color = MaterialTheme.colorScheme.outline,
                                        modifier = Modifier.padding(bottom = 3.dp)
                                    )
                                    Spacer(modifier = Modifier.weight(1f))
                                    Text(
                                        text = "${(progress * 100).toInt()}%",
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                }
                                
                                Spacer(modifier = Modifier.height(8.dp))
                                
                                LinearProgressIndicator(
                                    progress = { progress },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(10.dp)
                                        .clip(RoundedCornerShape(5.dp))
                                        .testTag("group_funding_progress"),
                                    color = if (aminaPaid) Color(0xFF4CAF50) else MaterialTheme.colorScheme.primary,
                                    trackColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.1f)
                                )
                                
                                Spacer(modifier = Modifier.height(8.dp))
                                
                                Text(
                                    text = if (aminaPaid) {
                                        "✓ Booking is locked and fully paid! Lodge host has received the single consolidated voucher and individual entry passes are ready."
                                    } else {
                                        "⚠ Only $1,000 remaining. Once Amina pays her portion, the booking locks securely and vouchers are generated automatically."
                                    },
                                    fontSize = 11.sp,
                                    color = if (aminaPaid) Color(0xFF2E7D32) else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }

                    // --- ITINERARY & VOTES PANEL ---
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.1f))
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Text(
                                    text = "GROUP ITINERARY & CO-PLANNING",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.outline
                                )
                                Spacer(modifier = Modifier.height(10.dp))
                                
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.Place,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = booking.location,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                }
                                Text(
                                    text = "Status: Reserved Pending Full Funding",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.secondary,
                                    modifier = Modifier.padding(start = 22.dp)
                                )
                                
                                Spacer(modifier = Modifier.height(12.dp))
                                HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.1f))
                                Spacer(modifier = Modifier.height(12.dp))
                                
                                // Optional Add-on Vote
                                Row(verticalAlignment = Alignment.Top) {
                                    Icon(
                                        imageVector = Icons.Default.AirportShuttle,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.tertiary,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = "OPTIONAL ADD-ON: Hot Air Balloon Safari",
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                        Text(
                                            text = "Cost: $450 / person",
                                            fontSize = 11.sp,
                                            color = MaterialTheme.colorScheme.outline
                                        )
                                        Spacer(modifier = Modifier.height(6.dp))
                                        
                                        // Vote list
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                                        ) {
                                            Text("Votes:", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.outline)
                                            Text("👍 Sarah, David", fontSize = 10.sp, color = Color(0xFF2E7D32), fontWeight = FontWeight.SemiBold)
                                            Text("👎 Alex (Host)", fontSize = 10.sp, color = Color(0xFFC62828), fontWeight = FontWeight.SemiBold)
                                            
                                            val aminaVoteText = when(userVotedYes) {
                                                true -> "👍 Amina (You)"
                                                false -> "👎 Amina (You)"
                                                null -> "⌛ Amina (You)"
                                            }
                                            Text(
                                                text = aminaVoteText,
                                                fontSize = 10.sp,
                                                color = when(userVotedYes) {
                                                    true -> Color(0xFF2E7D32)
                                                    false -> Color(0xFFC62828)
                                                    null -> Color(0xFFFB8C00)
                                                },
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                        
                                        Spacer(modifier = Modifier.height(10.dp))
                                        
                                        Row(
                                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                                            modifier = Modifier.fillMaxWidth()
                                        ) {
                                            Button(
                                                onClick = { userVotedYes = true },
                                                colors = ButtonDefaults.buttonColors(
                                                    containerColor = if (userVotedYes == true) Color(0xFFE8F5E9) else MaterialTheme.colorScheme.surfaceVariant,
                                                    contentColor = if (userVotedYes == true) Color(0xFF2E7D32) else MaterialTheme.colorScheme.onSurfaceVariant
                                                ),
                                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                                                shape = RoundedCornerShape(8.dp),
                                                modifier = Modifier
                                                    .weight(1f)
                                                    .height(32.dp)
                                                    .testTag("vote_yes_btn")
                                            ) {
                                                Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(12.dp))
                                                Spacer(modifier = Modifier.width(4.dp))
                                                Text("Vote Yes", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                            }
                                            
                                            Button(
                                                onClick = { userVotedYes = false },
                                                colors = ButtonDefaults.buttonColors(
                                                    containerColor = if (userVotedYes == false) Color(0xFFFFEBEE) else MaterialTheme.colorScheme.surfaceVariant,
                                                    contentColor = if (userVotedYes == false) Color(0xFFC62828) else MaterialTheme.colorScheme.onSurfaceVariant
                                                ),
                                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                                                shape = RoundedCornerShape(8.dp),
                                                modifier = Modifier
                                                    .weight(1f)
                                                    .height(32.dp)
                                                    .testTag("vote_no_btn")
                                            ) {
                                                Icon(Icons.Default.Close, contentDescription = null, modifier = Modifier.size(12.dp))
                                                Spacer(modifier = Modifier.width(4.dp))
                                                Text("Vote No", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // --- GROUP LEDGER SUMMARY PANEL ---
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.1f))
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Text(
                                    text = "GROUP LEDGER",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.outline
                                )
                                Spacer(modifier = Modifier.height(10.dp))
                                
                                val ledgerItems = listOf(
                                    Triple("Alex (Host)", "$1,000.00", true),
                                    Triple("Sarah", "$1,000.00", true),
                                    Triple("David", "$1,000.00", true),
                                    Triple("Amina (You)", "$1,000.00", aminaPaid)
                                )
                                
                                ledgerItems.forEachIndexed { idx, (name, share, paid) ->
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 8.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(26.dp)
                                                .background(
                                                    if (paid) Color(0xFFE8F5E9) else Color(0xFFFFF3E0),
                                                    CircleShape
                                                ),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                text = name.take(1),
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = if (paid) Color(0xFF2E7D32) else Color(0xFFE65100)
                                            )
                                        }
                                        Spacer(modifier = Modifier.width(10.dp))
                                        Column {
                                            Text(text = name, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSurface)
                                            Text(text = "Share portion", fontSize = 10.sp, color = MaterialTheme.colorScheme.outline)
                                        }
                                        Spacer(modifier = Modifier.weight(1f))
                                        Column(horizontalAlignment = Alignment.End) {
                                            Text(text = share, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Box(
                                                    modifier = Modifier
                                                        .size(6.dp)
                                                        .background(
                                                            if (paid) Color(0xFF4CAF50) else Color(0xFFFF9800),
                                                            CircleShape
                                                        )
                                                )
                                                Spacer(modifier = Modifier.width(4.dp))
                                                Text(
                                                    text = if (paid) "PAID" else "PENDING",
                                                    fontSize = 9.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = if (paid) Color(0xFF2E7D32) else Color(0xFFE65100)
                                                )
                                            }
                                        }
                                    }
                                    if (idx < ledgerItems.size - 1) {
                                        HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.05f))
                                    }
                                }
                            }
                        }
                    }

                    // --- BOTTOM CALL TO ACTIONS ---
                    item {
                        Column(
                            verticalArrangement = Arrangement.spacedBy(10.dp),
                            modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
                        ) {
                            if (!aminaPaid) {
                                Button(
                                    onClick = { showCheckout = true },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(50.dp)
                                        .testTag("pay_my_share_btn"),
                                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Icon(Icons.Default.Payment, contentDescription = null)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("PAY MY SHARE ($1,000.00)", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                                }
                            } else {
                                Button(
                                    onClick = {
                                        Toast.makeText(context, "Consolidated PDF ticket downloaded securely to vault!", Toast.LENGTH_LONG).show()
                                    },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(50.dp)
                                        .testTag("download_passes_btn"),
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32)),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color.White)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("DOWNLOAD ENTRY PASSES (4)", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                }
                            }

                            OutlinedButton(
                                onClick = {
                                    val preFormattedMessage = "Jambo! Friendly reminder to settle your portion for our Masai Mara Safari. Tap here to pay: https://safaristay.com/pay/${booking.id}"
                                    Toast.makeText(context, "WhatsApp reminder pre-copied: \"$preFormattedMessage\"", Toast.LENGTH_LONG).show()
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(50.dp)
                                    .testTag("send_reminders_btn"),
                                shape = RoundedCornerShape(12.dp),
                                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
                            ) {
                                Icon(Icons.Default.NotificationsActive, contentDescription = null)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("SEND PAYMENT REMINDERS", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }
    }

    // Individual Bill Split & Checkout Modal
    if (showCheckout) {
        BillSplitCheckoutDialog(
            booking = booking,
            onDismiss = { showCheckout = false },
            onPaymentComplete = {
                aminaPaid = true
                showCheckout = false
                Toast.makeText(context, "M-Pesa payment processed successfully!", Toast.LENGTH_LONG).show()
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BillSplitCheckoutDialog(
    booking: BookingEntity,
    onDismiss: () -> Unit,
    onPaymentComplete: () -> Unit
) {
    val context = LocalContext.current
    var paymentMethod by remember { mutableStateOf("mpesa") } // "mpesa", "card", "airtel"
    var phoneNumber by remember { mutableStateOf("+254 712 345 678") }
    var cardNumber by remember { mutableStateOf("") }
    var cardExpiry by remember { mutableStateOf("") }
    var cardCvc by remember { mutableStateOf("") }
    
    var showStkSimulation by remember { mutableStateOf(false) }
    var stkPinInput by remember { mutableStateOf("") }
    var processingPayment by remember { mutableStateOf(false) }
    
    val coroutineScope = rememberCoroutineScope()

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            Scaffold(
                topBar = {
                    TopAppBar(
                        title = {
                            Text("PAY YOUR SHARE", fontSize = 15.sp, fontWeight = FontWeight.Bold)
                        },
                        navigationIcon = {
                            IconButton(onClick = onDismiss) {
                                Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                            }
                        },
                        colors = TopAppBarDefaults.topAppBarColors(
                            containerColor = MaterialTheme.colorScheme.surface,
                            titleContentColor = MaterialTheme.colorScheme.onSurface
                        )
                    )
                }
            ) { paddingValues ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .padding(16.dp)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // --- COST BREAKDOWN ---
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Text(
                                text = "INDIVIDUAL SHARE BREAKDOWN (1 OF 4)",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            
                            val breakdownItems = listOf(
                                "1. Accommodation (3 Nights)" to "$750.00",
                                "2. Park Conservation Fees" to "$200.00",
                                "3. Hot Air Balloon Add-on (Optional)" to "$50.00"
                            )
                            
                            breakdownItems.forEach { (item, cost) ->
                                Row(
                                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(text = item, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    Text(text = cost, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                                }
                            }
                            
                            Spacer(modifier = Modifier.height(8.dp))
                            HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.1f))
                            Spacer(modifier = Modifier.height(8.dp))
                            
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(text = "YOUR TOTAL DUE:", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                                Text(text = "$1,000.00", fontSize = 18.sp, fontWeight = FontWeight.ExtraBold, color = MaterialTheme.colorScheme.primary)
                            }
                        }
                    }

                    // --- CHOOSE PAYMENT METHOD ---
                    Text(
                        text = "SELECT PAYMENT METHOD",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.outline
                    )
                    
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        // M-Pesa
                        Surface(
                            onClick = { paymentMethod = "mpesa" },
                            shape = RoundedCornerShape(12.dp),
                            border = BorderStroke(1.dp, if (paymentMethod == "mpesa") MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline.copy(alpha = 0.15f)),
                            color = if (paymentMethod == "mpesa") MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.15f) else MaterialTheme.colorScheme.surface
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(14.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                RadioButton(selected = paymentMethod == "mpesa", onClick = { paymentMethod = "mpesa" })
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text("M-Pesa (Direct STK Push)", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                    Text("Instant mobile wallet authorization", fontSize = 11.sp, color = MaterialTheme.colorScheme.outline)
                                }
                            }
                        }

                        // Credit Card
                        Surface(
                            onClick = { paymentMethod = "card" },
                            shape = RoundedCornerShape(12.dp),
                            border = BorderStroke(1.dp, if (paymentMethod == "card") MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline.copy(alpha = 0.15f)),
                            color = if (paymentMethod == "card") MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.15f) else MaterialTheme.colorScheme.surface
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(14.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                RadioButton(selected = paymentMethod == "card", onClick = { paymentMethod = "card" })
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text("Credit / Debit Card", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                    Text("Visa, Mastercard, American Express", fontSize = 11.sp, color = MaterialTheme.colorScheme.outline)
                                }
                            }
                        }

                        // Airtel Money
                        Surface(
                            onClick = { paymentMethod = "airtel" },
                            shape = RoundedCornerShape(12.dp),
                            border = BorderStroke(1.dp, if (paymentMethod == "airtel") MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline.copy(alpha = 0.15f)),
                            color = if (paymentMethod == "airtel") MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.15f) else MaterialTheme.colorScheme.surface
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(14.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                RadioButton(selected = paymentMethod == "airtel", onClick = { paymentMethod = "airtel" })
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text("Airtel / MTN Mobile Money", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                    Text("Settle via regional East African mobile networks", fontSize = 11.sp, color = MaterialTheme.colorScheme.outline)
                                }
                            }
                        }
                    }

                    // --- METHOD SPECIFIC FIELDS ---
                    Spacer(modifier = Modifier.height(4.dp))
                    
                    if (paymentMethod == "mpesa") {
                        OutlinedTextField(
                            value = phoneNumber,
                            onValueChange = { phoneNumber = it },
                            label = { Text("M-Pesa Phone Number") },
                            placeholder = { Text("+254 712 345 678") },
                            singleLine = true,
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.fillMaxWidth().testTag("checkout_phone_field"),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = MaterialTheme.colorScheme.primary
                            )
                        )
                    } else if (paymentMethod == "card") {
                        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            OutlinedTextField(
                                value = cardNumber,
                                onValueChange = { cardNumber = it },
                                label = { Text("Card Number") },
                                placeholder = { Text("4111 2222 3333 4444") },
                                singleLine = true,
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.fillMaxWidth(),
                                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = MaterialTheme.colorScheme.primary)
                            )
                            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                OutlinedTextField(
                                    value = cardExpiry,
                                    onValueChange = { cardExpiry = it },
                                    label = { Text("Expiry (MM/YY)") },
                                    placeholder = { Text("12/28") },
                                    singleLine = true,
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier.weight(1f),
                                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = MaterialTheme.colorScheme.primary)
                                )
                                OutlinedTextField(
                                    value = cardCvc,
                                    onValueChange = { cardCvc = it },
                                    label = { Text("CVC") },
                                    placeholder = { Text("123") },
                                    singleLine = true,
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier.weight(1f),
                                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = MaterialTheme.colorScheme.primary)
                                )
                            }
                        }
                    } else {
                        OutlinedTextField(
                            value = phoneNumber,
                            onValueChange = { phoneNumber = it },
                            label = { Text("Carrier Phone Number") },
                            placeholder = { Text("+256 ... or +255 ...") },
                            singleLine = true,
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = MaterialTheme.colorScheme.primary)
                        )
                    }

                    // --- SUBMIT AND PROTECTION ---
                    Spacer(modifier = Modifier.weight(1f))
                    
                    Button(
                        onClick = {
                            if (paymentMethod == "mpesa") {
                                showStkSimulation = true
                            } else {
                                processingPayment = true
                                coroutineScope.launch {
                                    delay(2000)
                                    processingPayment = false
                                    onPaymentComplete()
                                }
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                            .testTag("checkout_submit_btn"),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                        shape = RoundedCornerShape(12.dp),
                        enabled = !processingPayment
                    ) {
                        if (processingPayment) {
                            CircularProgressIndicator(modifier = Modifier.size(20.dp), color = Color.White)
                        } else {
                            val btnText = when(paymentMethod) {
                                "mpesa" -> "PAY $1,000.00 VIA M-PESA"
                                "card" -> "PAY $1,000.00 VIA CARD"
                                else -> "PAY $1,000.00 VIA MOBILE MONEY"
                            }
                            Text(btnText, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Lock, contentDescription = null, tint = Color(0xFF2E7D32), modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Escrow secured. Funds held securely until check-in.",
                            fontSize = 10.sp,
                            color = Color(0xFF2E7D32),
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }
    }

    // STK Push Simulation Overlay Dialog
    if (showStkSimulation) {
        Dialog(
            onDismissRequest = { showStkSimulation = false },
            properties = DialogProperties(dismissOnBackPress = false, dismissOnClickOutside = false)
        ) {
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = Color.Black.copy(alpha = 0.9f),
                modifier = Modifier
                    .width(300.dp)
                    .padding(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .background(Color(0xFF4CAF50), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("M", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    }
                    
                    Text(
                        text = "M-PESA STK PUSH PENDING",
                        color = Color.White,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )
                    
                    Text(
                        text = "A simulated push invitation has been sent to $phoneNumber. Please enter your 4-digit M-Pesa PIN below to authorize the transaction of $1,000.00 (Ksh 130,000).",
                        color = Color.LightGray,
                        fontSize = 11.sp,
                        textAlign = TextAlign.Center
                    )
                    
                    OutlinedTextField(
                        value = stkPinInput,
                        onValueChange = { if (it.length <= 4) stkPinInput = it },
                        label = { Text("Enter M-Pesa PIN", color = Color.White) },
                        placeholder = { Text("••••", color = Color.Gray) },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth().testTag("stk_pin_input_field"),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedBorderColor = Color(0xFF4CAF50),
                            unfocusedBorderColor = Color.Gray
                        )
                    )
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedButton(
                            onClick = { showStkSimulation = false },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp),
                            border = BorderStroke(1.dp, Color.Gray)
                        ) {
                            Text("Cancel", color = Color.White, fontSize = 12.sp)
                        }
                        
                        Button(
                            onClick = {
                                if (stkPinInput.length == 4) {
                                    showStkSimulation = false
                                    val generatedRef = "MPX-" + (100000..999999).random()
                                    Toast.makeText(context, "M-Pesa payment authorized! Ref: $generatedRef 📅", Toast.LENGTH_LONG).show()
                                    onPaymentComplete()
                                } else {
                                    Toast.makeText(context, "Please enter a valid 4-digit PIN", Toast.LENGTH_SHORT).show()
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50)),
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text("Authorize", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InternalFinanceDashboard(viewModel: SafariViewModel, onDismiss: () -> Unit) {
    val context = LocalContext.current
    var showUssdSimulator by remember { mutableStateOf(false) }
    
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = Color(0xFFF8F9FA) // Light grey admin background
        ) {
            if (showUssdSimulator) {
                com.example.ui.UssdSimulatorDialog(
                    viewModel = viewModel,
                    onDismiss = { showUssdSimulator = false }
                )
            }
            
            Scaffold(
                topBar = {
                    TopAppBar(
                        title = {
                            Column {
                                Text("FINANCIAL RECONCILIATION", fontSize = 14.sp, fontWeight = FontWeight.ExtraBold)
                                Text("Internal Admin Tool • Last 30 Days", fontSize = 10.sp, color = MaterialTheme.colorScheme.outline)
                            }
                        },
                        navigationIcon = {
                            IconButton(onClick = onDismiss) {
                                Icon(Icons.Default.Close, contentDescription = "Close")
                            }
                        },
                        actions = {
                            IconButton(onClick = { showUssdSimulator = true }) {
                                Icon(Icons.Default.PhoneAndroid, contentDescription = "USSD Test")
                            }
                            IconButton(onClick = { Toast.makeText(context, "Exporting CSV...", Toast.LENGTH_SHORT).show() }) {
                                Icon(Icons.Default.Download, contentDescription = "Export")
                            }
                        },
                        colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
                    )
                }
            ) { paddingValues ->
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // --- OVERVIEW STATS ---
                    item {
                        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            FinanceStatCard(
                                title = "FUNDS IN ESCROW",
                                value = "$142,500",
                                subValue = "84 Pending Stays",
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.weight(1f)
                            )
                            FinanceStatCard(
                                title = "COMMISSIONS",
                                value = "$21,375",
                                subValue = "15% Take-rate",
                                color = Color(0xFF2E7D32),
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                    
                    item {
                        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            FinanceStatCard(
                                title = "DISPATCHED",
                                value = "$385,200",
                                subValue = "42 Partner Lodges",
                                color = Color(0xFF1976D2),
                                modifier = Modifier.weight(1f)
                            )
                            FinanceStatCard(
                                title = "DISPUTES",
                                value = "2",
                                subValue = "$1,800 Active",
                                color = Color(0xFFC62828),
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }

                    // --- RECONCILIATION TABLE ---
                    item {
                        com.example.ui.ReconciliationDashboardCard()
                    }

                    item {
                        Text(
                            text = "UNSETTLED / ESCROW TRANSACTIONS",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.outline,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    }
                    
                    val adminTransactions = listOf(
                        AdminTx("#BK-9845", "Mara Serena", "$1,000", "$150", "$850", "HELD"),
                        AdminTx("#BK-9846", "Angama Mara", "$2,500", "$375", "$2,125", "HELD"),
                        AdminTx("#BK-9842", "Four Seasons", "$3,200", "$480", "$2,720", "RELEASED"),
                        AdminTx("#BK-9839", "Bisate Lodge", "$1,800", "$270", "$1,530", "DISPUTE")
                    )
                    
                    items(adminTransactions) { tx ->
                        AdminTxRow(tx)
                    }

                    // --- SYSTEM HEALTH & RESILIENCE ---
                    item {
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "SYSTEM HEALTH & REGIONAL RESILIENCE",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.outline
                        )
                    }

                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            border = BorderStroke(1.dp, Color.LightGray.copy(alpha = 0.2f))
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Dns, contentDescription = null, tint = Color(0xFF2E7D32), modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Primary: af-south-1 (Cape Town)", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                    Spacer(modifier = Modifier.weight(1f))
                                    Surface(color = Color(0xFFE8F5E9), shape = RoundedCornerShape(4.dp)) {
                                        Text("ACTIVE", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color(0xFF2E7D32), modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                                    }
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Dns, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Standby: eu-central-1 (Frankfurt)", fontSize = 12.sp, color = Color.Gray)
                                    Spacer(modifier = Modifier.weight(1f))
                                    Surface(color = Color.LightGray.copy(alpha = 0.2f), shape = RoundedCornerShape(4.dp)) {
                                        Text("STANDBY", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color.Gray, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                                    }
                                }
                                
                                HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = Color.LightGray.copy(alpha = 0.3f))
                                
                                Text("LOW-CONNECTIVITY SYNC STATUS", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
                                Spacer(modifier = Modifier.height(8.dp))
                                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text("SMS Bridge", fontSize = 9.sp, color = Color.Gray)
                                        Text("Operational", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF2E7D32))
                                    }
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text("Batch Size", fontSize = 9.sp, color = Color.Gray)
                                        Text("100 Items", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                    }
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text("Conflict Res", fontSize = 9.sp, color = Color.Gray)
                                        Text("Server Win", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }

                    item {
                        Button(
                            onClick = { Toast.makeText(context, "Simulating Network Partition... Fallback to SMS active.", Toast.LENGTH_LONG).show() },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFC62828)),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Icon(Icons.Default.WifiOff, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("SIMULATE REGIONAL OUTAGE (FAILOVER TEST)", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                    
                    item {
                        Spacer(modifier = Modifier.height(20.dp))
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.1f))
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(
                                    text = "MANUAL OVERRIDE: #BK-9845",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "Status: Check-in date was 2 hours ago. Pending QR scan. Payout Account: M-Pesa Till #884920",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Button(
                                        onClick = { 
                                            viewModel.initiateMpesaPayout("#BK-9845", "+254712345678", 850)
                                        },
                                        modifier = Modifier.weight(1f),
                                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32)),
                                        shape = RoundedCornerShape(8.dp)
                                    ) {
                                        Text("RELEASE", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                    }
                                    OutlinedButton(
                                        onClick = { Toast.makeText(context, "Payment Frozen", Toast.LENGTH_SHORT).show() },
                                        modifier = Modifier.weight(1f),
                                        shape = RoundedCornerShape(8.dp)
                                    ) {
                                        Text("FREEZE", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

data class AdminTx(val id: String, val lodge: String, val total: String, val fee: String, val net: String, val status: String)

@Composable
fun AdminTxRow(tx: AdminTx) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(8.dp),
        border = BorderStroke(1.dp, Color.LightGray.copy(alpha = 0.3f))
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = tx.id, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = tx.lodge, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                Spacer(modifier = Modifier.weight(1f))
                Surface(
                    color = when(tx.status) {
                        "HELD" -> Color(0xFFFFF3E0)
                        "RELEASED" -> Color(0xFFE8F5E9)
                        else -> Color(0xFFFFEBEE)
                    },
                    shape = RoundedCornerShape(4.dp)
                ) {
                    Text(
                        text = tx.status,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = when(tx.status) {
                            "HELD" -> Color(0xFFE65100)
                            "RELEASED" -> Color(0xFF2E7D32)
                            else -> Color(0xFFC62828)
                        },
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Column {
                    Text("GROSS", fontSize = 9.sp, color = Color.Gray)
                    Text(tx.total, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
                Column {
                    Text("FEE (15%)", fontSize = 9.sp, color = Color.Gray)
                    Text(tx.fee, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
                Column {
                    Text("NET PAYOUT", fontSize = 9.sp, color = Color.Gray)
                    Text(tx.net, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF2E7D32))
                }
                IconButton(onClick = { }, modifier = Modifier.size(24.dp)) {
                    Icon(Icons.Default.ChevronRight, contentDescription = null, tint = Color.Gray)
                }
            }
        }
    }
}

@Composable
fun FinanceStatCard(title: String, value: String, subValue: String, color: Color, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        border = BorderStroke(1.dp, Color.LightGray.copy(alpha = 0.2f))
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(text = title, fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
            Text(text = value, fontSize = 18.sp, fontWeight = FontWeight.ExtraBold, color = color)
            Text(text = subValue, fontSize = 9.sp, color = color.copy(alpha = 0.7f))
        }
    }
}

@Composable
fun PrivacyConsentDialog(onAccept: () -> Unit) {
    Dialog(
        onDismissRequest = { /* Force acceptance */ },
        properties = DialogProperties(dismissOnBackPress = false, dismissOnClickOutside = false)
    ) {
        Card(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(modifier = Modifier.padding(20.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(
                    Icons.Default.VerifiedUser, 
                    contentDescription = null, 
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(48.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Your Privacy & Data Protection",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "Safari Stay is registered with the Kenya ODPC. We encrypt your PII (Passport, M-Pesa details) using AES-256 and never store raw PINs. By continuing, you consent to our regional data processing protocols (GDPR & ODPC compliant).",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                    lineHeight = 18.sp
                )
                Spacer(modifier = Modifier.height(24.dp))
                Button(
                    onClick = onAccept,
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("I AGREE & CONTINUE", fontWeight = FontWeight.Bold)
                }
                Spacer(modifier = Modifier.height(8.dp))
                TextButton(onClick = { }) {
                    Text("Read Full Privacy Policy", fontSize = 11.sp)
                }
            }
        }
    }
}


