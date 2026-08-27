package com.example.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil.compose.AsyncImage
import com.example.data.PlaceResponse
import com.example.data.StayItem
import com.example.viewmodel.SafariViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LodgeSidePanel(
    stay: StayItem,
    placeDetails: PlaceResponse?,
    viewModel: SafariViewModel,
    onDismiss: () -> Unit,
    onBookClick: () -> Unit,
    onAiItineraryClick: ((String) -> Unit)? = null,
    onLiveChatClick: ((String) -> Unit)? = null,
    onViewReviewsClick: ((StayItem) -> Unit)? = null
) {
    // Instead of a true side panel (which is hard on small screens without a scaffold),
    // we use a bottom sheet or a dialog that looks like a panel.
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp, start = 16.dp, end = 16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stay.title,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
                IconButton(onClick = onDismiss) {
                    Icon(Icons.Default.Close, contentDescription = "Close")
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Map data
            Text(text = stay.location, color = MaterialTheme.colorScheme.onSurfaceVariant)
            
            Spacer(modifier = Modifier.height(16.dp))

            // Google Places Data
            if (placeDetails != null && placeDetails.id == stay.googlePlaceId) {
                if (placeDetails.rating != null) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Star, contentDescription = "Rating", tint = MaterialTheme.colorScheme.primary)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "${placeDetails.rating} (${placeDetails.userRatingCount ?: 0} reviews)",
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                }

                if (placeDetails.internationalPhoneNumber != null) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Phone, contentDescription = "Phone", tint = MaterialTheme.colorScheme.primary)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(text = placeDetails.internationalPhoneNumber)
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                }

                if (!placeDetails.photos.isNullOrEmpty()) {
                    Text("Photos", fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = 8.dp))
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(placeDetails.photos) { photo ->
                            val url = viewModel.getPlacePhotoUrl(photo.name, 400)
                            AsyncImage(
                                model = url,
                                contentDescription = "Lodge photo",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .size(200.dp, 150.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(MaterialTheme.colorScheme.surfaceVariant)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                }
            } else if (stay.googlePlaceId != null) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
                Spacer(modifier = Modifier.height(16.dp))
            } else {
                // Fallback to static image
                Text("Photos", fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = 8.dp))
                AsyncImage(
                    model = stay.imageUrls.firstOrNull(),
                    contentDescription = "Lodge photo",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                )
                Spacer(modifier = Modifier.height(16.dp))
            }

            if (onViewReviewsClick != null) {
                OutlinedButton(
                    onClick = {
                        onDismiss()
                        onViewReviewsClick(stay)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp)
                        .testTag("lodge_side_panel_reviews_button")
                ) {
                    Icon(Icons.Default.Star, contentDescription = null, tint = Color(0xFFF39C12), modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("Guest Reviews & Ratings (Firestore) ⭐", fontWeight = FontWeight.Bold)
                }
            }

            if (onLiveChatClick != null) {
                ElevatedButton(
                    onClick = {
                        onDismiss()
                        onLiveChatClick(stay.title)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp)
                        .testTag("lodge_side_panel_chat_button"),
                    colors = ButtonDefaults.elevatedButtonColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                ) {
                    Icon(Icons.Default.Chat, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("Live Chat with Host (Firestore Inquiry) 💬", fontWeight = FontWeight.Bold)
                }
            }

            if (onAiItineraryClick != null) {
                OutlinedButton(
                    onClick = {
                        onDismiss()
                        onAiItineraryClick(stay.title)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp)
                ) {
                    Icon(Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("Generate 3-Day Itinerary with Gemini AI ✨")
                }
            }

            Button(
                onClick = onBookClick,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Book Now - $${stay.pricePerNight} / night")
            }
        }
    }
}
