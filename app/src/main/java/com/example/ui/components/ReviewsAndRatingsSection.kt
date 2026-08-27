package com.example.ui.components

import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material.icons.outlined.StarBorder
import androidx.compose.material.icons.outlined.ThumbUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.FirestoreReviewsManager
import com.example.data.SafariReview
import com.example.data.TargetRatingSummary
import com.example.viewmodel.SafariViewModel
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ReviewsAndRatingsSection(
    viewModel: SafariViewModel,
    targetId: String,
    targetType: String, // "stay" or "safari"
    targetTitle: String,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val reviewsManager = remember { FirestoreReviewsManager.getInstance(context) }
    val reviewsFlow = remember(targetId) {
        reviewsManager.getReviewsFlow(targetId, targetType, targetTitle)
    }
    val reviews by reviewsFlow.collectAsState()
    val ratingSummary = remember(reviews) {
        reviewsManager.computeRatingSummary(reviews)
    }

    var showWriteReviewDialog by remember { mutableStateOf(false) }
    var selectedFilterTripType by remember { mutableStateOf("All") }
    var selectedStarFilter by remember { mutableStateOf(0) } // 0 means all stars

    val coroutineScope = rememberCoroutineScope()

    val filteredReviews = remember(reviews, selectedFilterTripType, selectedStarFilter) {
        reviews.filter { review ->
            val matchesType = selectedFilterTripType == "All" || review.tripType.contains(selectedFilterTripType, ignoreCase = true)
            val matchesStar = selectedStarFilter == 0 || review.rating.toInt() == selectedStarFilter
            matchesType && matchesStar
        }
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .testTag("reviews_ratings_section_${targetId}")
    ) {
        // Section Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Guest Reviews & Ratings",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "Real feedback powered by Cloud Firestore 🟢",
                    fontSize = 11.5.sp,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Medium
                )
            }

            Button(
                onClick = { showWriteReviewDialog = true },
                shape = RoundedCornerShape(12.dp),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                ),
                modifier = Modifier.testTag("write_review_button_${targetId}")
            ) {
                Icon(
                    imageVector = Icons.Default.RateReview,
                    contentDescription = "Write a Review",
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text("Write Review", fontSize = 12.5.sp, fontWeight = FontWeight.Bold)
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Rating Overview Breakdown Card
        RatingSummaryCard(summary = ratingSummary, onWriteReviewClick = { showWriteReviewDialog = true })

        Spacer(modifier = Modifier.height(16.dp))

        // Filter Chips Row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            val tripFilters = listOf("All", "Couples", "Family with Kids", "Solo Adventurer", "Photography Expedition")
            tripFilters.forEach { filter ->
                FilterChip(
                    selected = (selectedFilterTripType == filter),
                    onClick = { selectedFilterTripType = filter },
                    label = { Text(filter, fontSize = 12.sp) },
                    shape = RoundedCornerShape(20.dp),
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                        selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Star Filters
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            listOf(0 to "All Stars", 5 to "5 ★", 4 to "4 ★", 3 to "3 ★").forEach { (star, label) ->
                FilterChip(
                    selected = (selectedStarFilter == star),
                    onClick = { selectedStarFilter = star },
                    label = { Text(label, fontSize = 11.5.sp) },
                    modifier = Modifier.weight(1f)
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Reviews List
        if (filteredReviews.isEmpty()) {
            OutlinedCard(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Reviews,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.6f),
                        modifier = Modifier.size(36.dp)
                    )
                    Text(
                        text = "No reviews match selected filter",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    TextButton(onClick = {
                        selectedFilterTripType = "All"
                        selectedStarFilter = 0
                    }) {
                        Text("Reset Filters")
                    }
                }
            }
        } else {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                filteredReviews.forEach { review ->
                    ReviewItemCard(
                        review = review,
                        onLikeClick = {
                            coroutineScope.launch {
                                reviewsManager.toggleLikeReview(targetId, review.id)
                            }
                        }
                    )
                }
            }
        }
    }

    // Modal Dialog to write a new review
    if (showWriteReviewDialog) {
        WriteReviewDialog(
            targetTitle = targetTitle,
            targetId = targetId,
            targetType = targetType,
            onDismiss = { showWriteReviewDialog = false },
            onSubmit = { newReview ->
                coroutineScope.launch {
                    reviewsManager.submitReview(newReview)
                    showWriteReviewDialog = false
                    Toast.makeText(context, "Thank you! Review submitted to Firestore 🦁", Toast.LENGTH_LONG).show()
                }
            }
        )
    }
}

@Composable
fun RatingSummaryCard(
    summary: TargetRatingSummary,
    onWriteReviewClick: () -> Unit
) {
    ElevatedCard(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        ),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Score Big Number
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(end = 16.dp)
                ) {
                    Text(
                        text = String.format(Locale.getDefault(), "%.1f", summary.averageRating),
                        fontSize = 38.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    InteractiveStarRow(rating = summary.averageRating, starSize = 15.dp)
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "${summary.totalReviews} verified reviews",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontWeight = FontWeight.Medium
                    )
                }

                // Star distribution bars
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(3.dp)
                ) {
                    val total = summary.totalReviews.coerceAtLeast(1)
                    for (star in 5 downTo 1) {
                        val count = summary.starCounts[star] ?: 0
                        val progress = (count.toFloat() / total).coerceIn(0f, 1f)
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "$star ★",
                                fontSize = 10.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.width(24.dp)
                            )
                            LinearProgressIndicator(
                                progress = { progress },
                                modifier = Modifier
                                    .weight(1f)
                                    .height(6.dp)
                                    .clip(RoundedCornerShape(4.dp)),
                                color = MaterialTheme.colorScheme.primary,
                                trackColor = MaterialTheme.colorScheme.surface
                            )
                            Text(
                                text = "$count",
                                fontSize = 10.5.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier
                                    .width(24.dp)
                                    .padding(start = 6.dp)
                            )
                        }
                    }
                }
            }

            HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.15f))

            // Sub-ratings grid
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    text = "Expedition Sub-Scores",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    SubScorePill(
                        label = "🦁 Wildlife",
                        score = summary.subRatingAverages["Wildlife Sightings"] ?: summary.averageRating,
                        modifier = Modifier.weight(1f)
                    )
                    SubScorePill(
                        label = "🧭 Guides",
                        score = summary.subRatingAverages["Guide Knowledge"] ?: summary.averageRating,
                        modifier = Modifier.weight(1f)
                    )
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    SubScorePill(
                        label = "🛖 Hospitality",
                        score = summary.subRatingAverages["Lodge & Hospitality"] ?: summary.averageRating,
                        modifier = Modifier.weight(1f)
                    )
                    SubScorePill(
                        label = "🌿 Eco Pledge",
                        score = summary.subRatingAverages["Eco & Conservation"] ?: summary.averageRating,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

@Composable
fun SubScorePill(
    label: String,
    score: Double,
    modifier: Modifier = Modifier
) {
    Surface(
        color = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(8.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.12f)),
        modifier = modifier
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(label, fontSize = 11.5.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text(
                text = String.format(Locale.getDefault(), "%.1f ★", score),
                fontSize = 11.5.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ReviewItemCard(
    review: SafariReview,
    onLikeClick: () -> Unit
) {
    ElevatedCard(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 1.dp),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("review_card_${review.id}")
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Author row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Surface(
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.primaryContainer,
                        modifier = Modifier.size(38.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                text = review.authorName.take(1).uppercase(),
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                    }

                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = review.authorName,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            if (review.isVerifiedBooking) {
                                Spacer(modifier = Modifier.width(6.dp))
                                Surface(
                                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f),
                                    shape = RoundedCornerShape(4.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            Icons.Default.Verified,
                                            contentDescription = "Verified Stay",
                                            tint = MaterialTheme.colorScheme.primary,
                                            modifier = Modifier.size(11.dp)
                                        )
                                        Spacer(modifier = Modifier.width(2.dp))
                                        Text(
                                            "Verified Stay",
                                            fontSize = 9.5.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                    }
                                }
                            }
                        }
                        Text(
                            text = "${review.tripType} • ${review.travelDate}",
                            fontSize = 11.5.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                // Star Rating
                InteractiveStarRow(rating = review.rating, starSize = 14.dp)
            }

            // Review Title
            if (review.reviewTitle.isNotBlank()) {
                Text(
                    text = review.reviewTitle,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.5.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            // Review Body Text
            Text(
                text = review.reviewText,
                fontSize = 13.5.sp,
                lineHeight = 19.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            // Sub-scores pills row
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                review.subRatings.forEach { (category, score) ->
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f)
                    ) {
                        Text(
                            text = "$category: ${String.format(Locale.getDefault(), "%.1f", score)}★",
                            fontSize = 10.5.sp,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                        )
                    }
                }
            }

            HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.08f))

            // Footer: Helpful like action & Firestore live sync pill
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        Icons.Default.CloudDone,
                        contentDescription = "Firestore Live Sync",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(13.dp)
                    )
                    Text(
                        text = "Firestore Cloud Sync 🟢",
                        fontSize = 10.sp,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Medium
                    )
                }

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                    modifier = Modifier.clickable { onLikeClick() }
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.ThumbUp,
                            contentDescription = "Helpful review",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(13.dp)
                        )
                        Text(
                            text = "Helpful (${review.likesCount})",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun WriteReviewDialog(
    targetTitle: String,
    targetId: String,
    targetType: String,
    onDismiss: () -> Unit,
    onSubmit: (SafariReview) -> Unit
) {
    var reviewerName by remember { mutableStateOf("") }
    var reviewTitle by remember { mutableStateOf("") }
    var reviewText by remember { mutableStateOf("") }
    var overallRating by remember { mutableStateOf(5.0) }

    var wildlifeRating by remember { mutableStateOf(5.0) }
    var guideRating by remember { mutableStateOf(5.0) }
    var lodgeRating by remember { mutableStateOf(5.0) }
    var ecoRating by remember { mutableStateOf(5.0) }

    val tripTypes = listOf(
        "Couples 💍",
        "Family with Kids 👨‍👩‍👧‍👦",
        "Solo Adventurer 🧭",
        "Photography Expedition 📸",
        "Luxury Eco-Tour 👑"
    )
    var selectedTripType by remember { mutableStateOf("Couples 💍") }

    val currentMonthYear = remember {
        SimpleDateFormat("MMMM yyyy", Locale.getDefault()).format(Date())
    }
    var travelDate by remember { mutableStateOf(currentMonthYear) }

    val ratingLabels = mapOf(
        5.0 to "5.0 - Exceptional & Unforgettable 🌟",
        4.0 to "4.0 - Very Good & Memorable 👍",
        3.0 to "3.0 - Average Experience 😐",
        2.0 to "2.0 - Below Expectations ⚠️",
        1.0 to "1.0 - Poor Experience ❌"
    )

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .fillMaxHeight(0.90f)
                .clip(RoundedCornerShape(24.dp)),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 8.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Write Safari Review",
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = targetTitle,
                            fontSize = 12.5.sp,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close")
                    }
                }

                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    // 1. Overall Star Selector
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.35f)
                        ),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                text = "Overall Experience Rating",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.5.sp
                            )
                            InteractiveStarSelector(
                                rating = overallRating,
                                onRatingChange = { overallRating = it }
                            )
                            Text(
                                text = ratingLabels[overallRating] ?: "${overallRating.toInt()} Stars",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }

                    // 2. Sub-Category Star Selectors
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            text = "Expedition Sub-Category Ratings",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        SubRatingSelectorRow(
                            label = "🦁 Wildlife & Game Drives",
                            rating = wildlifeRating,
                            onRatingChange = { wildlifeRating = it }
                        )
                        SubRatingSelectorRow(
                            label = "🧭 Guide & Naturalist Knowledge",
                            rating = guideRating,
                            onRatingChange = { guideRating = it }
                        )
                        SubRatingSelectorRow(
                            label = "🛖 Lodge & Hospitality Comfort",
                            rating = lodgeRating,
                            onRatingChange = { lodgeRating = it }
                        )
                        SubRatingSelectorRow(
                            label = "🌿 Conservation & Eco-Practices",
                            rating = ecoRating,
                            onRatingChange = { ecoRating = it }
                        )
                    }

                    // 3. Trip Type Selector
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text(
                            text = "Travel Party / Trip Style",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            tripTypes.forEach { type ->
                                FilterChip(
                                    selected = (selectedTripType == type),
                                    onClick = { selectedTripType = type },
                                    label = { Text(type, fontSize = 11.5.sp) }
                                )
                            }
                        }
                    }

                    // 4. Reviewer Name & Travel Date
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedTextField(
                            value = reviewerName,
                            onValueChange = { reviewerName = it },
                            label = { Text("Your Name") },
                            placeholder = { Text("e.g. John K.") },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp),
                            singleLine = true
                        )
                        OutlinedTextField(
                            value = travelDate,
                            onValueChange = { travelDate = it },
                            label = { Text("Travel Date") },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp),
                            singleLine = true
                        )
                    }

                    // 5. Review Title & Description
                    OutlinedTextField(
                        value = reviewTitle,
                        onValueChange = { reviewTitle = it },
                        label = { Text("Review Headline") },
                        placeholder = { Text("e.g. Unbelievable sunrise game drives!") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = reviewText,
                        onValueChange = { reviewText = it },
                        label = { Text("Detailed Review") },
                        placeholder = { Text("Share what made your safari guides, game drives, lodge rooms, or wildlife encounters memorable...") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(110.dp),
                        shape = RoundedCornerShape(12.dp),
                        maxLines = 5
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Submit Button
                Button(
                    onClick = {
                        val finalName = reviewerName.ifBlank { "Safari Traveler" }
                        val finalTitle = reviewTitle.ifBlank { "Great Safari Experience" }
                        val finalBody = reviewText.ifBlank { "Had a wonderful time exploring the wildlife and wilderness!" }
                        val cleanTripType = selectedTripType.split(" ").first()

                        val newReview = SafariReview(
                            targetId = targetId,
                            targetType = targetType,
                            targetTitle = targetTitle,
                            authorName = finalName,
                            rating = overallRating,
                            reviewTitle = finalTitle,
                            reviewText = finalBody,
                            travelDate = travelDate,
                            tripType = cleanTripType,
                            subRatings = mapOf(
                                "Wildlife Sightings" to wildlifeRating,
                                "Guide Knowledge" to guideRating,
                                "Lodge & Hospitality" to lodgeRating,
                                "Eco & Conservation" to ecoRating
                            ),
                            likesCount = 0,
                            isVerifiedBooking = true,
                            timestampMillis = System.currentTimeMillis()
                        )
                        onSubmit(newReview)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("submit_review_firestore_button"),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.CloudUpload, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Publish Review to Firestore", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun InteractiveStarSelector(
    rating: Double,
    onRatingChange: (Double) -> Unit
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        for (i in 1..5) {
            val isFilled = i <= rating.toInt()
            IconButton(
                onClick = { onRatingChange(i.toDouble()) },
                modifier = Modifier.size(38.dp)
            ) {
                Icon(
                    imageVector = if (isFilled) Icons.Default.Star else Icons.Outlined.StarBorder,
                    contentDescription = "$i Stars",
                    tint = if (isFilled) Color(0xFFF39C12) else MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(32.dp)
                )
            }
        }
    }
}

@Composable
fun SubRatingSelectorRow(
    label: String,
    rating: Double,
    onRatingChange: (Double) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            fontSize = 11.5.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.weight(1f)
        )
        Row(horizontalArrangement = Arrangement.spacedBy(2.dp)) {
            for (i in 1..5) {
                val isFilled = i <= rating.toInt()
                Icon(
                    imageVector = if (isFilled) Icons.Default.Star else Icons.Outlined.StarBorder,
                    contentDescription = null,
                    tint = if (isFilled) Color(0xFFF39C12) else MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
                    modifier = Modifier
                        .size(20.dp)
                        .clickable { onRatingChange(i.toDouble()) }
                )
            }
        }
    }
}

@Composable
fun InteractiveStarRow(
    rating: Double,
    starSize: androidx.compose.ui.unit.Dp = 14.dp
) {
    Row(horizontalArrangement = Arrangement.spacedBy(2.dp)) {
        for (i in 1..5) {
            val icon = when {
                i <= rating -> Icons.Default.Star
                i - 0.5 <= rating -> Icons.Default.StarHalf
                else -> Icons.Default.StarBorder
            }
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = Color(0xFFF39C12),
                modifier = Modifier.size(starSize)
            )
        }
    }
}
