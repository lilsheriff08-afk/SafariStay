package com.example.ui

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.StayItem
import com.example.ui.components.ReviewsAndRatingsSection
import com.example.viewmodel.SafariViewModel
import coil.compose.AsyncImage

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StayDetailsScreen(
    stay: StayItem,
    onDismiss: () -> Unit,
    onBookClick: () -> Unit,
    onLiveChatClick: ((stayTitle: String) -> Unit)? = null,
    viewModel: SafariViewModel? = null
) {
    val context = LocalContext.current
    var showBookingOptionsDialog by remember { mutableStateOf(false) }
    var showCallReferralDialog by remember { mutableStateOf(false) }
    val referralToken = remember(stay.id) { "SAFARI-COM-${stay.id.take(4).uppercase()}-${(1000..9999).random()}" }

    fun triggerCallHotel() {
        val rawPhone = stay.phoneNumber?.ifBlank { null } ?: "+254 712 345 678"
        val cleaned = rawPhone.replace(" ", "").replace("-", "").replace("(", "").replace(")", "")
        try {
            val intent = Intent(Intent.ACTION_DIAL).apply {
                data = Uri.parse("tel:$cleaned")
            }
            context.startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(context, "Dialing $rawPhone", Toast.LENGTH_SHORT).show()
        }
    }

    if (showCallReferralDialog) {
        AlertDialog(
            onDismissRequest = { showCallReferralDialog = false },
            icon = {
                Icon(
                    Icons.Default.VerifiedUser,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(32.dp)
                )
            },
            title = {
                Text(
                    text = "Commission-Protected Direct Call",
                    fontWeight = FontWeight.Bold,
                    fontSize = 17.sp,
                    textAlign = TextAlign.Center
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = "To guarantee traveler protection and lock in our standard 15% platform partner commission with ${stay.title}, quote your referral token when connected:",
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Surface(
                        color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f),
                        shape = RoundedCornerShape(10.dp),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(12.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "PLATFORM REFERRAL & COMMISSION TOKEN",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Spacer(Modifier.height(4.dp))
                            Text(
                                text = referralToken,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer,
                                letterSpacing = 1.sp
                            )
                            Text(
                                text = "15% Partner Commission Guaranteed by Escrow Agreement",
                                fontSize = 10.sp,
                                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                            )
                        }
                    }

                    Text(
                        text = "• Dialing: ${stay.phoneNumber?.ifBlank { null } ?: "+254 712 345 678"}\n• Host is bound by Safari Escrow Terms.\n• Alternatively, book in-app for automatic instant escrow.",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.9f),
                        lineHeight = 16.sp
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        showCallReferralDialog = false
                        triggerCallHotel()
                    },
                    modifier = Modifier.testTag("confirm_call_with_token_btn")
                ) {
                    Icon(Icons.Default.Phone, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(6.dp))
                    Text("Dial Now")
                }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = {
                        showCallReferralDialog = false
                        onBookClick()
                    }
                ) {
                    Text("Book In-App Instead")
                }
            }
        )
    }

    if (showBookingOptionsDialog) {
        AlertDialog(
            onDismissRequest = { showBookingOptionsDialog = false },
            title = {
                Text(
                    text = "Book ${stay.title}",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(
                        text = "Choose your booking method with guaranteed platform commission & traveler protection:",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 13.sp
                    )

                    // Option 1: Instant In-App Booking (Recommended - 100% Commission Protected)
                    Card(
                        onClick = {
                            showBookingOptionsDialog = false
                            onBookClick()
                        },
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.7f)),
                        border = BorderStroke(1.5.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)),
                        modifier = Modifier.fillMaxWidth().testTag("option_mock_booking_flow")
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Surface(
                                    shape = CircleShape,
                                    color = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(36.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Icon(
                                            Icons.Default.Lock,
                                            contentDescription = null,
                                            tint = MaterialTheme.colorScheme.onPrimary,
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }
                                }
                                Spacer(Modifier.width(10.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = "Instant Escrow Booking",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 15.sp,
                                            color = MaterialTheme.colorScheme.onPrimaryContainer
                                        )
                                        Spacer(Modifier.width(6.dp))
                                        Surface(
                                            color = MaterialTheme.colorScheme.primary,
                                            shape = RoundedCornerShape(4.dp)
                                        ) {
                                            Text(
                                                text = "BEST",
                                                color = MaterialTheme.colorScheme.onPrimary,
                                                fontSize = 9.sp,
                                                fontWeight = FontWeight.Bold,
                                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                            )
                                        }
                                    }
                                    Text(
                                        text = "Direct M-Pesa/Card. 15% platform commission automatically retained & held in escrow.",
                                        fontSize = 11.sp,
                                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.85f),
                                        lineHeight = 14.sp
                                    )
                                }
                            }
                        }
                    }

                    // Option 2: Direct Hotel Call (with Referral Token Anti-Bypass)
                    Card(
                        onClick = {
                            showBookingOptionsDialog = false
                            showCallReferralDialog = true
                        },
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5f)),
                        modifier = Modifier.fillMaxWidth().testTag("option_direct_hotel_call")
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                shape = CircleShape,
                                color = MaterialTheme.colorScheme.secondary,
                                modifier = Modifier.size(36.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        Icons.Default.Phone,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.onSecondary,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }
                            Spacer(Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "Call Front Desk (Token Protected)",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp,
                                    color = MaterialTheme.colorScheme.onSecondaryContainer
                                )
                                Text(
                                    text = "Uses Token $referralToken to preserve 15% partner commission.",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.85f),
                                    lineHeight = 14.sp
                                )
                            }
                        }
                    }
                }
            },
            confirmButton = {},
            dismissButton = {
                TextButton(onClick = { showBookingOptionsDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Details", fontWeight = FontWeight.Bold) },
                    navigationIcon = {
                        IconButton(onClick = onDismiss) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    )
                )
            },
            bottomBar = {
                Surface(
                    color = MaterialTheme.colorScheme.surface,
                    shadowElevation = 8.dp,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .padding(16.dp)
                            .navigationBarsPadding(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            val isKsh = stay.country == "Kenya"
                            val displayPrice = if (isKsh) stay.pricePerNight * 130.0 else stay.pricePerNight
                            val sym = if (isKsh) "Ksh " else "$"
                            Text(
                                text = "$sym${String.format("%,d", displayPrice.toInt())}",
                                fontWeight = FontWeight.Bold,
                                fontSize = 20.sp,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                text = "per night",
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontSize = 12.sp
                            )
                        }
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedButton(
                                onClick = { triggerCallHotel() },
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.testTag("stay_details_call_button")
                            ) {
                                Icon(Icons.Default.Phone, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(Modifier.width(4.dp))
                                Text("Call")
                            }
                            if (onLiveChatClick != null) {
                                OutlinedButton(
                                    onClick = {
                                        onDismiss()
                                        onLiveChatClick(stay.title)
                                    },
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier.testTag("stay_details_chat_button")
                                ) {
                                    Icon(Icons.Default.Chat, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(Modifier.width(4.dp))
                                    Text("Inquire")
                                }
                            }
                            Button(
                                onClick = { showBookingOptionsDialog = true },
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.testTag("stay_details_book_now_button")
                            ) {
                                Icon(Icons.Default.CheckCircle, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(Modifier.width(4.dp))
                                Text("Book Now")
                            }
                        }
                    }
                }
            }
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .verticalScroll(rememberScrollState())
                    .background(MaterialTheme.colorScheme.background)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp)
                ) {
                    ImageCarousel(
                        imageUrls = stay.imageUrls,
                        modifier = Modifier.fillMaxSize()
                    )
                }
                
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = stay.title,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Place,
                            contentDescription = "Location",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "${stay.location}, ${stay.country}",
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 14.sp
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        StarRating(rating = stay.rating, modifier = Modifier.height(16.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "${stay.rating} (${stay.reviewsCount} reviews)",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                        if (stay.hotelClass != null) {
                            Text(
                                text = " • ${stay.hotelClass}",
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontSize = 14.sp
                            )
                        }
                    }

                    if (stay.isEcoCertified) {
                        Spacer(modifier = Modifier.height(12.dp))
                        Surface(
                            color = MaterialTheme.colorScheme.primaryContainer,
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Eco,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onPrimaryContainer,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Eco-Certified Partner",
                                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                    
                    // Feature 2: Immersive 360° Room & Bush Previews
                    InteractiveRoomPreviewCard(stay = stay)
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // --- IMPACT & TRANSPARENCY SECTION ---
                    Surface(
                        color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.3f),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "Impact & Transparency",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.secondary
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            
                            val isKsh = stay.country == "Kenya"
                            val rate = if (isKsh) 130.0 else 1.0
                            val sym = if (isKsh) "Ksh " else "$"

                            val basePrice = stay.pricePerNight * rate
                            val commission = basePrice * stay.commissionRate
                            val conservationPledge = commission * 0.10
                            
                            ImpactRow(
                                label = "Lodge Revenue",
                                value = "$sym${String.format("%,d", (basePrice - commission).toInt())}",
                                description = "85-88% remains with the local lodge"
                            )
                            ImpactRow(
                                label = "Conservation Pledge",
                                value = "$sym${String.format("%,d", conservationPledge.toInt())}",
                                description = "10% of our commission goes to verified local causes",
                                isHighlight = true
                            )
                            ImpactRow(
                                label = "Platform Support",
                                value = "$sym${String.format("%,d", (commission - conservationPledge).toInt())}",
                                description = "For maintenance & guide scholarships"
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))
                    
                    Text(
                        text = "About",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = stay.description,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 15.sp,
                        lineHeight = 22.sp
                    )
                    
                    Spacer(modifier = Modifier.height(24.dp))

                    // --- UPLOADED MULTIPLE PHOTOS GALLERY ---
                    Text(
                        text = "Photo Gallery (${stay.imageUrls.size} Photos)",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        stay.imageUrls.forEachIndexed { index, url ->
                            Card(
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier
                                    .size(120.dp, 90.dp)
                            ) {
                                Box(modifier = Modifier.fillMaxSize()) {
                                    AsyncImage(
                                        model = url,
                                        contentDescription = "Photo ${index + 1}",
                                        modifier = Modifier.fillMaxSize(),
                                        contentScale = androidx.compose.ui.layout.ContentScale.Crop
                                    )
                                    Surface(
                                        color = Color.Black.copy(alpha = 0.6f),
                                        shape = RoundedCornerShape(bottomStart = 8.dp),
                                        modifier = Modifier.align(Alignment.TopEnd)
                                    ) {
                                        Text(
                                            text = "#${index + 1}",
                                            color = Color.White,
                                            fontSize = 10.sp,
                                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // --- PRICING & RATES CARD ---
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Tariffs & Pricing",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold
                                )
                                Surface(
                                    color = MaterialTheme.colorScheme.primaryContainer,
                                    shape = RoundedCornerShape(6.dp)
                                ) {
                                    Text(
                                        text = stay.availabilityStatus,
                                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            val isKsh = stay.country == "Kenya"
                            val priceVal = if (isKsh) stay.pricePerNight * 130.0 else stay.pricePerNight
                            val currencySym = if (isKsh) "Ksh " else "$"
                            Text(
                                text = "$currencySym${String.format("%,d", priceVal.toInt())} / night",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Includes all local taxes, breakfast, and standard conservation fees.",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Button(
                                    onClick = onBookClick,
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier.weight(1f).testTag("tariffs_book_now_button")
                                ) {
                                    Icon(Icons.Default.CalendarMonth, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(Modifier.width(6.dp))
                                    Text("Book Now")
                                }
                                OutlinedButton(
                                    onClick = { triggerCallHotel() },
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier.testTag("tariffs_call_hotel_button")
                                ) {
                                    Icon(Icons.Default.Phone, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(Modifier.width(6.dp))
                                    Text("Call")
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // --- CONTACT INFORMATION CARD ---
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Lodge Contact Information",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold
                                )
                                FilledTonalButton(
                                    onClick = { triggerCallHotel() },
                                    shape = RoundedCornerShape(8.dp),
                                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                                    modifier = Modifier.testTag("contact_card_call_now_button")
                                ) {
                                    Icon(Icons.Default.Phone, contentDescription = "Call", modifier = Modifier.size(14.dp))
                                    Spacer(Modifier.width(4.dp))
                                    Text("Call Lodge", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                            Spacer(modifier = Modifier.height(12.dp))
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .clickable { triggerCallHotel() }
                                    .padding(vertical = 4.dp)
                            ) {
                                Icon(Icons.Default.Phone, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = stay.phoneNumber?.ifBlank { null } ?: "+254 712 345 678 (Direct Desk)",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Email, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "reservations@${stay.title.lowercase().replace(Regex("[^a-z]"), "")}.safaristay.com",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.LocationOn, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "${stay.location}, ${stay.country}",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))
                    
                    Text(
                        text = "Amenities",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    stay.amenities.forEach { amenity ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(vertical = 4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(text = amenity, fontSize = 15.sp)
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))
                    HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.15f))
                    Spacer(modifier = Modifier.height(16.dp))

                    // Reviews & Ratings Section (Powered by Cloud Firestore)
                    viewModel?.let { vm ->
                        ReviewsAndRatingsSection(
                            viewModel = vm,
                            targetId = stay.id,
                            targetType = "stay",
                            targetTitle = stay.title
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))
                }
            }
        }
    }
}

@Composable
fun ImpactRow(
    label: String,
    value: String,
    description: String,
    isHighlight: Boolean = false
) {
    Column(modifier = Modifier.padding(vertical = 4.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = label,
                fontSize = 13.sp,
                color = if (isHighlight) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = value,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = if (isHighlight) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
            )
        }
        Text(
            text = description,
            fontSize = 11.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

fun Double.format(digits: Int) = "%.${digits}f".format(this)

@Composable
fun InteractiveRoomPreviewCard(stay: StayItem) {
    var selectedViewIndex by remember { mutableStateOf(0) } // 0: Luxury Tent, 1: Outdoor Shower, 2: Viewing Deck
    val viewLabels = listOf("Luxury Tent", "Outdoor Shower", "Viewing Deck")
    
    var dragOffset by remember { mutableStateOf(0f) }
    var soundEnabled by remember { mutableStateOf(false) }

    // Waveform heights animated infinitely
    val infiniteTransition = rememberInfiniteTransition(label = "audio_bars")
    val heights = (0..4).map { index ->
        infiniteTransition.animateFloat(
            initialValue = 4f,
            targetValue = 24f,
            animationSpec = infiniteRepeatable(
                animation = tween(durationMillis = 350 + (index * 120), easing = FastOutSlowInEasing),
                repeatMode = RepeatMode.Reverse
            ),
            label = "bar_$index"
        )
    }

    Card(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.15f)),
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 16.dp, bottom = 16.dp)
            .testTag("immersive_360_preview_card")
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.secondaryContainer,
                    modifier = Modifier.size(36.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.CropRotate,
                            contentDescription = "360 Preview",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = "Immersive 360° Room Previews",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Swipe to pan interior & check structures",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Sub-tabs
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                viewLabels.forEachIndexed { index, label ->
                    val isSelected = selectedViewIndex == index
                    Surface(
                        onClick = { 
                            selectedViewIndex = index
                            dragOffset = 0f // reset drag
                        },
                        color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                        contentColor = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface,
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("preview_tab_$index")
                    ) {
                        Text(
                            text = label,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // 360 Canvas Viewport
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = Color(0xFF151515), // Elegant dark slate theater back
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
            ) {
                Box {
                    Canvas(
                        modifier = Modifier
                            .fillMaxSize()
                            .pointerInput(Unit) {
                                detectDragGestures { change, dragAmount ->
                                    change.consume()
                                    dragOffset += dragAmount.x
                                }
                            }
                            .testTag("preview_viewport")
                    ) {
                        val w = size.width
                        val h = size.height
                        
                        // Limit/wrap dragOffset to wrap panorama
                        val normalizedOffset = (dragOffset % w)

                        when (selectedViewIndex) {
                            0 -> { // Luxury Tent View
                                // Background sky through the opening flaps
                                val sunsetColor = Color(0xFFFFB74D)
                                drawRect(color = Color(0xFFE65100), size = size)
                                drawCircle(color = sunsetColor, radius = h * 0.4f, center = Offset(w * 0.5f + normalizedOffset * 0.4f, h * 0.5f))

                                // Distant Acacia trees
                                drawLine(color = Color(0xFF3E2723), start = Offset(w * 0.3f + normalizedOffset * 0.8f, h * 0.55f), end = Offset(w * 0.3f + normalizedOffset * 0.8f, h * 0.75f), strokeWidth = 3.dp.toPx())
                                drawCircle(color = Color(0xFF3E2723), radius = 12.dp.toPx(), center = Offset(w * 0.3f + normalizedOffset * 0.8f, h * 0.55f))

                                drawLine(color = Color(0xFF3E2723), start = Offset(w * 0.7f + normalizedOffset * 0.8f, h * 0.6f), end = Offset(w * 0.7f + normalizedOffset * 0.8f, h * 0.75f), strokeWidth = 4.dp.toPx())
                                drawCircle(color = Color(0xFF3E2723), radius = 18.dp.toPx(), center = Offset(w * 0.7f + normalizedOffset * 0.8f, h * 0.6f))

                                // Floor wooden texture
                                drawRect(color = Color(0xFF5D4037), topLeft = Offset(0f, h * 0.75f), size = Size(w, h * 0.25f))

                                // Draw Safari bed (Foreground - moves slower)
                                val bedX = w * 0.2f + normalizedOffset * 0.2f
                                drawRect(color = Color(0xFFD7CCC8), topLeft = Offset(bedX, h * 0.62f), size = Size(w * 0.6f, h * 0.2f)) // mattress
                                drawRect(color = Color(0xFF8D6E63), topLeft = Offset(bedX - 10.dp.toPx(), h * 0.55f), size = Size(10.dp.toPx(), h * 0.3f)) // left bedpost
                                drawRect(color = Color(0xFF8D6E63), topLeft = Offset(bedX + w * 0.6f, h * 0.55f), size = Size(10.dp.toPx(), h * 0.3f)) // right bedpost

                                // Pillows
                                drawCircle(color = Color.White, radius = 10.dp.toPx(), center = Offset(bedX + w * 0.15f, h * 0.62f))
                                drawCircle(color = Color.White, radius = 10.dp.toPx(), center = Offset(bedX + w * 0.45f, h * 0.62f))

                                // Net hanging down
                                drawLine(color = Color.White.copy(alpha = 0.5f), start = Offset(bedX - 5.dp.toPx(), h * 0.4f), end = Offset(bedX - 5.dp.toPx(), h * 0.75f), strokeWidth = 1.dp.toPx())
                                drawLine(color = Color.White.copy(alpha = 0.5f), start = Offset(bedX + w * 0.65f, h * 0.4f), end = Offset(bedX + w * 0.65f, h * 0.75f), strokeWidth = 1.dp.toPx())

                                // Triangular tent flaps framing the scene (static foreground vignette)
                                val leftFlap = Path().apply {
                                    moveTo(0f, 0f)
                                    lineTo(w * 0.15f, 0f)
                                    lineTo(w * 0.05f, h)
                                    lineTo(0f, h)
                                    close()
                                }
                                val rightFlap = Path().apply {
                                    moveTo(w, 0f)
                                    lineTo(w * 0.85f, 0f)
                                    lineTo(w * 0.95f, h)
                                    lineTo(w, h)
                                    close()
                                }
                                drawPath(leftFlap, color = Color(0xFF4E342E))
                                drawPath(rightFlap, color = Color(0xFF4E342E))
                            }
                            1 -> { // Outdoor Bush Shower
                                // Sky
                                drawRect(color = Color(0xFF0D47A1), size = size) // deep blue starry dusk sky
                                
                                // Stars
                                drawCircle(color = Color.White, radius = 1.5.dp.toPx(), center = Offset(w * 0.2f + normalizedOffset * 0.9f, h * 0.2f))
                                drawCircle(color = Color.White, radius = 1.5.dp.toPx(), center = Offset(w * 0.6f + normalizedOffset * 0.9f, h * 0.1f))
                                drawCircle(color = Color.White, radius = 1.5.dp.toPx(), center = Offset(w * 0.8f + normalizedOffset * 0.9f, h * 0.3f))

                                // Distant bushes / elephants
                                val elephantX = w * 0.5f + normalizedOffset * 0.5f
                                drawCircle(color = Color(0xFF37474F), radius = 24.dp.toPx(), center = Offset(elephantX, h * 0.65f)) // body
                                drawCircle(color = Color(0xFF37474F), radius = 12.dp.toPx(), center = Offset(elephantX + 22.dp.toPx(), h * 0.6f)) // head
                                drawRect(color = Color(0xFF37474F), topLeft = Offset(elephantX - 15.dp.toPx(), h * 0.65f), size = Size(6.dp.toPx(), h * 0.2f)) // back leg
                                drawRect(color = Color(0xFF37474F), topLeft = Offset(elephantX + 5.dp.toPx(), h * 0.65f), size = Size(6.dp.toPx(), h * 0.2f)) // front leg

                                // Bamboo screen floor and walls
                                drawRect(color = Color(0xFF263238), topLeft = Offset(0f, h * 0.78f), size = Size(w, h * 0.22f)) // floor

                                // Shower Pipe (Foreground)
                                val pipeX = w * 0.35f + normalizedOffset * 0.1f
                                drawRect(color = Color(0xFFB0BEC5), topLeft = Offset(pipeX, 0f), size = Size(6.dp.toPx(), h * 0.5f))
                                drawRect(color = Color(0xFFB0BEC5), topLeft = Offset(pipeX - 20.dp.toPx(), h * 0.5f), size = Size(50.dp.toPx(), 8.dp.toPx()))

                                // Water drops
                                for (i in 0..5) {
                                    val dropY = (h * 0.52f + (i * 20.dp.toPx()) + (normalizedOffset * 0.2f)) % (h * 0.45f)
                                    drawCircle(color = Color(0xFFE1F5FE), radius = 2.dp.toPx(), center = Offset(pipeX + (i * 4.dp.toPx() - 10.dp.toPx()), h * 0.55f + dropY))
                                }

                                // Bamboo side pole
                                drawRect(color = Color(0xFF4E342E), topLeft = Offset(0f, 0f), size = Size(w * 0.12f, h))
                                drawRect(color = Color(0xFF4E342E), topLeft = Offset(w * 0.88f, 0f), size = Size(w * 0.12f, h))
                            }
                            else -> { // Private Viewing Deck
                                // Golden savanna watering hole background
                                drawRect(color = Color(0xFFFFF9C4), size = size) // golden glow sky
                                drawCircle(color = Color(0xFFFFCC80), radius = h * 0.5f, center = Offset(w * 0.5f + normalizedOffset * 0.3f, h * 0.5f)) // setting sun

                                // Watering hole
                                drawCircle(color = Color(0xFF90CAF9), radius = w * 0.3f, center = Offset(w * 0.5f + normalizedOffset * 0.7f, h * 0.82f))

                                // Giraffe silhouette at watering hole
                                val giraffeX = w * 0.45f + normalizedOffset * 0.7f
                                drawRect(color = Color(0xFF5D4037), topLeft = Offset(giraffeX, h * 0.55f), size = Size(20.dp.toPx(), 24.dp.toPx())) // torso
                                drawRect(color = Color(0xFF5D4037), topLeft = Offset(giraffeX + 14.dp.toPx(), h * 0.3f), size = Size(5.dp.toPx(), 30.dp.toPx())) // neck
                                drawCircle(color = Color(0xFF5D4037), radius = 4.dp.toPx(), center = Offset(giraffeX + 16.dp.toPx(), h * 0.3f)) // head
                                drawRect(color = Color(0xFF5D4037), topLeft = Offset(giraffeX + 2.dp.toPx(), h * 0.65f), size = Size(3.dp.toPx(), h * 0.15f)) // back leg
                                drawRect(color = Color(0xFF5D4037), topLeft = Offset(giraffeX + 14.dp.toPx(), h * 0.65f), size = Size(3.dp.toPx(), h * 0.15f)) // front leg

                                // Foreground viewing deck
                                drawRect(color = Color(0xFF4E342E), topLeft = Offset(0f, h * 0.72f), size = Size(w, h * 0.28f)) // deck floor

                                // Binoculars lying on deck table
                                val binoX = w * 0.2f + normalizedOffset * 0.1f
                                drawRect(color = Color(0xFF212121), topLeft = Offset(binoX, h * 0.75f), size = Size(30.dp.toPx(), 10.dp.toPx())) // left tube
                                drawRect(color = Color(0xFF212121), topLeft = Offset(binoX, h * 0.8f), size = Size(30.dp.toPx(), 10.dp.toPx())) // right tube
                                drawRect(color = Color(0xFF757575), topLeft = Offset(binoX + 10.dp.toPx(), h * 0.78f), size = Size(8.dp.toPx(), 4.dp.toPx())) // bridge

                                // Railing post (Foreground static)
                                drawRect(color = Color(0xFF3E2723), topLeft = Offset(w * 0.05f, 0f), size = Size(14.dp.toPx(), h))
                                drawRect(color = Color(0xFF3E2723), topLeft = Offset(w * 0.9f, 0f), size = Size(14.dp.toPx(), h))
                                drawRect(color = Color(0xFF3E2723), topLeft = Offset(0f, h * 0.6f), size = Size(w, 8.dp.toPx())) // railing hand rest
                            }
                        }
                    }

                    // Swipe helper prompt
                    Text(
                        text = "◀   DRAG TO LOOK AROUND   ▶",
                        fontSize = 8.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White.copy(alpha = 0.5f),
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .padding(bottom = 8.dp),
                        textAlign = TextAlign.Center
                    )

                    // Overlay tag of selected view
                    Surface(
                        color = Color.Black.copy(alpha = 0.7f),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(8.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Public,
                                contentDescription = null,
                                tint = Color(0xFF81C784),
                                modifier = Modifier.size(10.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "3D PERSPECTIVE ACTIVE",
                                fontSize = 8.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF81C784)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Ambient Soundscapes Toggle (Feature 2 - Bullet 2)
            Surface(
                color = if (soundEnabled) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(
                    width = 1.dp,
                    color = if (soundEnabled) MaterialTheme.colorScheme.primary.copy(alpha = 0.2f) else MaterialTheme.colorScheme.outline.copy(alpha = 0.1f)
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { soundEnabled = !soundEnabled }
                    .testTag("soundscape_toggle_row")
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = { soundEnabled = !soundEnabled },
                        colors = IconButtonDefaults.iconButtonColors(
                            containerColor = if (soundEnabled) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                            contentColor = if (soundEnabled) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                        ),
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            imageVector = if (soundEnabled) Icons.Default.VolumeUp else Icons.Default.VolumeMute,
                            contentDescription = "Toggle Ambient Sound",
                            modifier = Modifier.size(16.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Ambient Wilderness Soundscape",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = if (soundEnabled) 
                                "🔊 Playing crickets, night breeze & distant lion calls..." 
                            else 
                                "Listen to real property ambient soundscapes",
                            fontSize = 10.sp,
                            color = if (soundEnabled) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    if (soundEnabled) {
                        // Animated Audio Waveforms
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(2.dp),
                            verticalAlignment = Alignment.Bottom,
                            modifier = Modifier.height(24.dp)
                        ) {
                            heights.forEach { h ->
                                Box(
                                    modifier = Modifier
                                        .width(3.dp)
                                        .height(h.value.dp)
                                        .background(
                                            color = MaterialTheme.colorScheme.primary,
                                            shape = RoundedCornerShape(1.dp)
                                        )
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
