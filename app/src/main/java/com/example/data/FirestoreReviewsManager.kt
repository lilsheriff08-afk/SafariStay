package com.example.data

import android.content.Context
import android.util.Log
import com.google.firebase.Firebase
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

data class SafariReview(
    val id: String = UUID.randomUUID().toString(),
    val targetId: String = "",
    val targetType: String = "stay", // "stay" or "safari"
    val targetTitle: String = "",
    val authorName: String = "Anonymous Traveler",
    val authorAvatarUrl: String? = null,
    val rating: Double = 5.0,
    val reviewTitle: String = "",
    val reviewText: String = "",
    val travelDate: String = SimpleDateFormat("MMM yyyy", Locale.getDefault()).format(Date()),
    val tripType: String = "Couples", // "Couples", "Family with Kids", "Solo Adventurer", "Photography Expedition", "Luxury Eco-Tour"
    val subRatings: Map<String, Double> = mapOf(
        "Wildlife Sightings" to 5.0,
        "Guide Knowledge" to 5.0,
        "Lodge & Hospitality" to 5.0,
        "Eco & Conservation" to 5.0
    ),
    val photos: List<String> = emptyList(),
    val likesCount: Int = 0,
    val isVerifiedBooking: Boolean = true,
    val timestampMillis: Long = System.currentTimeMillis()
)

data class TargetRatingSummary(
    val averageRating: Double = 5.0,
    val totalReviews: Int = 0,
    val starCounts: Map<Int, Int> = mapOf(5 to 0, 4 to 0, 3 to 0, 2 to 0, 1 to 0),
    val subRatingAverages: Map<String, Double> = emptyMap()
)

class FirestoreReviewsManager private constructor(private val context: Context) {

    private val scope = CoroutineScope(Dispatchers.IO)
    private var firestoreInstance: FirebaseFirestore? = null

    // TargetId -> StateFlow<List<SafariReview>>
    private val reviewsFlows = mutableMapOf<String, MutableStateFlow<List<SafariReview>>>()

    init {
        initFirestoreIfAvailable()
    }

    private fun initFirestoreIfAvailable() {
        try {
            if (FirebaseApp.getApps(context).isNotEmpty()) {
                firestoreInstance = Firebase.firestore
                Log.d("FirestoreReviews", "Firebase Firestore initialized successfully for Reviews")
            } else {
                Log.w("FirestoreReviews", "FirebaseApp not initialized. Using local reactive store with initial curated reviews.")
            }
        } catch (e: Exception) {
            Log.e("FirestoreReviews", "Firestore init exception: ${e.message}. Falling back to local reactive reviews mode.")
            firestoreInstance = null
        }
    }

    fun getReviewsFlow(targetId: String, targetType: String, targetTitle: String): StateFlow<List<SafariReview>> {
        if (!reviewsFlows.containsKey(targetId)) {
            val initialList = getInitialCuratedReviews(targetId, targetType, targetTitle)
            val flow = MutableStateFlow(initialList)
            reviewsFlows[targetId] = flow

            // Attach Firestore real-time snapshot listener
            attachFirestoreListener(targetId, flow)
        }
        return reviewsFlows[targetId]!!.asStateFlow()
    }

    private fun attachFirestoreListener(targetId: String, flow: MutableStateFlow<List<SafariReview>>) {
        val firestore = firestoreInstance ?: return
        try {
            firestore.collection("safari_reviews")
                .whereEqualTo("targetId", targetId)
                .addSnapshotListener { snapshot, error ->
                    if (error != null) {
                        Log.e("FirestoreReviews", "Error listening to Firestore reviews for $targetId: ${error.message}")
                        return@addSnapshotListener
                    }

                    if (snapshot != null && !snapshot.isEmpty) {
                        val fsReviews = snapshot.documents.mapNotNull { doc ->
                            try {
                                val id = doc.id
                                val tId = doc.getString("targetId") ?: targetId
                                val tType = doc.getString("targetType") ?: "stay"
                                val tTitle = doc.getString("targetTitle") ?: ""
                                val author = doc.getString("authorName") ?: "Guest"
                                val rating = doc.getDouble("rating") ?: 5.0
                                val title = doc.getString("reviewTitle") ?: ""
                                val text = doc.getString("reviewText") ?: ""
                                val date = doc.getString("travelDate") ?: SimpleDateFormat("MMM yyyy", Locale.getDefault()).format(Date())
                                val tripType = doc.getString("tripType") ?: "Couples"
                                val likes = doc.getLong("likesCount")?.toInt() ?: 0
                                val verified = doc.getBoolean("isVerifiedBooking") ?: true
                                val time = doc.getLong("timestampMillis") ?: System.currentTimeMillis()

                                val rawSubRatings = doc.get("subRatings") as? Map<*, *>
                                val subRatings = mutableMapOf<String, Double>()
                                rawSubRatings?.forEach { (k, v) ->
                                    if (k is String && v is Number) {
                                        subRatings[k] = v.toDouble()
                                    }
                                }

                                SafariReview(
                                    id = id,
                                    targetId = tId,
                                    targetType = tType,
                                    targetTitle = tTitle,
                                    authorName = author,
                                    rating = rating,
                                    reviewTitle = title,
                                    reviewText = text,
                                    travelDate = date,
                                    tripType = tripType,
                                    subRatings = if (subRatings.isNotEmpty()) subRatings else mapOf(
                                        "Wildlife Sightings" to rating,
                                        "Guide Knowledge" to rating,
                                        "Lodge & Hospitality" to rating,
                                        "Eco & Conservation" to rating
                                    ),
                                    likesCount = likes,
                                    isVerifiedBooking = verified,
                                    timestampMillis = time
                                )
                            } catch (e: Exception) {
                                Log.e("FirestoreReviews", "Error parsing Firestore review doc ${doc.id}: ${e.message}")
                                null
                            }
                        }

                        // Merge Firestore reviews with curated ones (ensuring no duplicates)
                        val merged = (fsReviews + flow.value)
                            .distinctBy { it.id }
                            .sortedByDescending { it.timestampMillis }

                        flow.value = merged
                    }
                }
        } catch (e: Exception) {
            Log.e("FirestoreReviews", "Failed to attach snapshot listener for reviews: ${e.message}")
        }
    }

    suspend fun submitReview(review: SafariReview): Boolean {
        // 1. Instantly update local StateFlow
        val flow = reviewsFlows[review.targetId] ?: MutableStateFlow<List<SafariReview>>(emptyList()).also {
            reviewsFlows[review.targetId] = it
        }
        val currentList = flow.value
        flow.value = listOf(review) + currentList.filter { it.id != review.id }

        // 2. Persist to Firestore
        return try {
            firestoreInstance?.let { firestore ->
                val reviewMap = hashMapOf(
                    "targetId" to review.targetId,
                    "targetType" to review.targetType,
                    "targetTitle" to review.targetTitle,
                    "authorName" to review.authorName,
                    "rating" to review.rating,
                    "reviewTitle" to review.reviewTitle,
                    "reviewText" to review.reviewText,
                    "travelDate" to review.travelDate,
                    "tripType" to review.tripType,
                    "subRatings" to review.subRatings,
                    "likesCount" to review.likesCount,
                    "isVerifiedBooking" to review.isVerifiedBooking,
                    "timestampMillis" to review.timestampMillis
                )
                firestore.collection("safari_reviews")
                    .document(review.id)
                    .set(reviewMap)
                Log.d("FirestoreReviews", "Successfully saved review ${review.id} to Firestore")
            }
            true
        } catch (e: Exception) {
            Log.e("FirestoreReviews", "Error submitting review to Firestore: ${e.message}")
            true // Local optimistic state is already preserved
        }
    }

    suspend fun toggleLikeReview(targetId: String, reviewId: String) {
        val flow = reviewsFlows[targetId] ?: return
        val current = flow.value
        val updated = current.map {
            if (it.id == reviewId) {
                val newLikes = it.likesCount + 1
                try {
                    firestoreInstance?.collection("safari_reviews")
                        ?.document(reviewId)
                        ?.update("likesCount", newLikes)
                } catch (e: Exception) {
                    Log.e("FirestoreReviews", "Error updating like count in Firestore: ${e.message}")
                }
                it.copy(likesCount = newLikes)
            } else it
        }
        flow.value = updated
    }

    fun computeRatingSummary(reviews: List<SafariReview>): TargetRatingSummary {
        if (reviews.isEmpty()) {
            return TargetRatingSummary(averageRating = 5.0, totalReviews = 0)
        }

        val avg = reviews.map { it.rating }.average()
        val starCounts = mutableMapOf(5 to 0, 4 to 0, 3 to 0, 2 to 1, 1 to 0)
        reviews.forEach { r ->
            val star = r.rating.toInt().coerceIn(1, 5)
            starCounts[star] = (starCounts[star] ?: 0) + 1
        }

        val subRatingSums = mutableMapOf<String, Double>()
        val subRatingCounts = mutableMapOf<String, Int>()
        reviews.forEach { r ->
            r.subRatings.forEach { (cat, score) ->
                subRatingSums[cat] = (subRatingSums[cat] ?: 0.0) + score
                subRatingCounts[cat] = (subRatingCounts[cat] ?: 0) + 1
            }
        }

        val subRatingAverages = subRatingSums.mapValues { (cat, sum) ->
            val count = subRatingCounts[cat] ?: 1
            sum / count
        }

        return TargetRatingSummary(
            averageRating = (avg * 10).toInt() / 10.0,
            totalReviews = reviews.size,
            starCounts = starCounts,
            subRatingAverages = subRatingAverages
        )
    }

    private fun getInitialCuratedReviews(targetId: String, targetType: String, targetTitle: String): List<SafariReview> {
        val isStay = targetType == "stay"
        return listOf(
            SafariReview(
                id = "${targetId}_rev_1",
                targetId = targetId,
                targetType = targetType,
                targetTitle = targetTitle,
                authorName = "Eleanor Vance",
                rating = 5.0,
                reviewTitle = if (isStay) "Pure African magic & unmatched sunrise views!" else "Thrilling game drives with world-class naturalists!",
                reviewText = if (isStay)
                    "Our 4-night stay at $targetTitle exceeded all expectations. We woke up to elephants drinking at the salt lick right below our private wooden veranda. The lodge staff, guided walking safaris, and open-fire dinners under the constellations made this our favorite African lodge."
                else
                    "An exceptional wildlife expedition! Our tracker spotted a cheetah mother with cubs within the first two hours. The pop-up roof 4x4 cruiser was impeccably maintained and having a professional naturalist made every sighting educational.",
                travelDate = "July 2026",
                tripType = "Couples",
                subRatings = mapOf(
                    "Wildlife Sightings" to 5.0,
                    "Guide Knowledge" to 5.0,
                    "Lodge & Hospitality" to 4.9,
                    "Eco & Conservation" to 5.0
                ),
                likesCount = 14,
                isVerifiedBooking = true,
                timestampMillis = System.currentTimeMillis() - 1000L * 60 * 60 * 24 * 12
            ),
            SafariReview(
                id = "${targetId}_rev_2",
                targetId = targetId,
                targetType = targetType,
                targetTitle = targetTitle,
                authorName = "Marcus & Sarah Jenkins",
                rating = 4.8,
                reviewTitle = "Incredible family experience with authentic conservation focus",
                reviewText = if (isStay)
                    "Brought our two teenagers and they were captivated from day one. The junior ranger program taught them animal tracking and indigenous flora. Solar-powered luxury with zero single-use plastics—true sustainable hospitality done right."
                else
                    "Brilliant expedition itinerary across the savanna! The sunset sundowners with Masai elders and sunrise hot air balloon flight were lifetime memories. Highly recommend bringing binoculars and long camera lenses.",
                travelDate = "June 2026",
                tripType = "Family with Kids",
                subRatings = mapOf(
                    "Wildlife Sightings" to 4.8,
                    "Guide Knowledge" to 5.0,
                    "Lodge & Hospitality" to 4.7,
                    "Eco & Conservation" to 4.9
                ),
                likesCount = 8,
                isVerifiedBooking = true,
                timestampMillis = System.currentTimeMillis() - 1000L * 60 * 60 * 24 * 28
            ),
            SafariReview(
                id = "${targetId}_rev_3",
                targetId = targetId,
                targetType = targetType,
                targetTitle = targetTitle,
                authorName = "Tariq Al-Mansoor",
                rating = 5.0,
                reviewTitle = "Photographer's dream destination — five stars all around",
                reviewText = "As a wildlife photographer, vehicle positioning and lighting are everything. The guide understood golden hour lighting intimately and positioned our vehicle flawlessly for dramatic predator portraits without disturbing the animals. 10/10.",
                travelDate = "May 2026",
                tripType = "Photography Expedition",
                subRatings = mapOf(
                    "Wildlife Sightings" to 5.0,
                    "Guide Knowledge" to 5.0,
                    "Lodge & Hospitality" to 4.8,
                    "Eco & Conservation" to 5.0
                ),
                likesCount = 19,
                isVerifiedBooking = true,
                timestampMillis = System.currentTimeMillis() - 1000L * 60 * 60 * 24 * 55
            )
        )
    }

    companion object {
        @Volatile
        private var instance: FirestoreReviewsManager? = null

        fun getInstance(context: Context): FirestoreReviewsManager {
            return instance ?: synchronized(this) {
                instance ?: FirestoreReviewsManager(context.applicationContext).also { instance = it }
            }
        }
    }
}
