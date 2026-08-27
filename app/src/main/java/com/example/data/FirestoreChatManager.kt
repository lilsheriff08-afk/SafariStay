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
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class LiveChatMessage(
    val id: String = System.currentTimeMillis().toString(),
    val inquiryId: String = "",
    val recipientTitle: String = "",
    val senderName: String = "You (Guest)",
    val senderRole: String = "user", // "user", "host", "guide"
    val messageText: String = "",
    val timestampFormatted: String = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date()),
    val categoryTag: String? = null,
    val isUser: Boolean = true
)

class FirestoreChatManager private constructor(private val context: Context) {

    private val scope = CoroutineScope(Dispatchers.IO)
    private val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())

    // Map of inquiryId -> StateFlow<List<LiveChatMessage>>
    private val chatFlows = mutableMapOf<String, MutableStateFlow<List<LiveChatMessage>>>()

    private var firestoreInstance: FirebaseFirestore? = null

    init {
        initFirestoreIfAvailable()
    }

    private fun initFirestoreIfAvailable() {
        try {
            if (FirebaseApp.getApps(context).isNotEmpty()) {
                firestoreInstance = Firebase.firestore
                Log.d("FirestoreChat", "Firebase Firestore initialized successfully")
            } else {
                Log.w("FirestoreChat", "FirebaseApp is not initialized. Using reactive local sync mode with auto-replies.")
            }
        } catch (e: Exception) {
            Log.e("FirestoreChat", "Firestore init exception: ${e.message}. Falling back to local reactive mode.")
            firestoreInstance = null
        }
    }

    fun getChatMessagesFlow(inquiryId: String, recipientTitle: String, initialCategory: String? = null): StateFlow<List<LiveChatMessage>> {
        if (!chatFlows.containsKey(inquiryId)) {
            val initialList = getInitialWelcomeMessages(inquiryId, recipientTitle, initialCategory)
            val flow = MutableStateFlow(initialList)
            chatFlows[inquiryId] = flow

            // Attach Firestore snapshot listener if available
            attachFirestoreListener(inquiryId, flow)
        }
        return chatFlows[inquiryId]!!.asStateFlow()
    }

    private fun attachFirestoreListener(inquiryId: String, flow: MutableStateFlow<List<LiveChatMessage>>) {
        val firestore = firestoreInstance ?: return
        try {
            firestore.collection("chat_inquiries")
                .document(inquiryId)
                .collection("messages")
                .orderBy("timestampFormatted", Query.Direction.ASCENDING)
                .addSnapshotListener { snapshot, error ->
                    if (error != null) {
                        Log.e("FirestoreChat", "Error listening to Firestore chat: ${error.message}")
                        return@addSnapshotListener
                    }

                    if (snapshot != null && !snapshot.isEmpty) {
                        val fsMessages = snapshot.documents.mapNotNull { doc ->
                            try {
                                val text = doc.getString("messageText") ?: ""
                                val sender = doc.getString("senderName") ?: "Host/Guide"
                                val role = doc.getString("senderRole") ?: "host"
                                val isUser = doc.getBoolean("isUser") ?: (role == "user")
                                val time = doc.getString("timestampFormatted") ?: timeFormat.format(Date())
                                val cat = doc.getString("categoryTag")

                                LiveChatMessage(
                                    id = doc.id,
                                    inquiryId = inquiryId,
                                    recipientTitle = doc.getString("recipientTitle") ?: "",
                                    senderName = sender,
                                    senderRole = role,
                                    messageText = text,
                                    timestampFormatted = time,
                                    categoryTag = cat,
                                    isUser = isUser
                                )
                            } catch (e: Exception) {
                                null
                            }
                        }

                        if (fsMessages.isNotEmpty()) {
                            // Merge Firestore messages with any local welcome messages
                            val existing = flow.value.filter { it.id.startsWith("welcome_") }
                            flow.value = existing + fsMessages
                        }
                    }
                }
        } catch (e: Exception) {
            Log.e("FirestoreChat", "Failed to attach snapshot listener: ${e.message}")
        }
    }

    fun sendMessage(
        inquiryId: String,
        recipientTitle: String,
        text: String,
        categoryTag: String? = null
    ) {
        if (text.isBlank()) return

        val userMessage = LiveChatMessage(
            id = "msg_" + System.currentTimeMillis(),
            inquiryId = inquiryId,
            recipientTitle = recipientTitle,
            senderName = "You (Guest)",
            senderRole = "user",
            messageText = text.trim(),
            timestampFormatted = timeFormat.format(Date()),
            categoryTag = categoryTag,
            isUser = true
        )

        val flow = chatFlows.getOrPut(inquiryId) { MutableStateFlow(emptyList()) }
        flow.value = flow.value + userMessage

        // Try pushing to Firestore
        scope.launch {
            try {
                firestoreInstance?.collection("chat_inquiries")
                    ?.document(inquiryId)
                    ?.collection("messages")
                    ?.add(
                        mapOf(
                            "inquiryId" to inquiryId,
                            "recipientTitle" to recipientTitle,
                            "senderName" to "You (Guest)",
                            "senderRole" to "user",
                            "messageText" to text.trim(),
                            "timestampFormatted" to userMessage.timestampFormatted,
                            "categoryTag" to categoryTag,
                            "isUser" to true,
                            "createdAt" to com.google.firebase.Timestamp.now()
                        )
                    )
            } catch (e: Exception) {
                Log.e("FirestoreChat", "Firestore push error: ${e.message}")
            }

            // Simulate Host / Guide reply after 1.8 seconds for interactive response
            delay(1800)
            generateSimulatedResponse(inquiryId, recipientTitle, text, categoryTag, flow)
        }
    }

    private fun generateSimulatedResponse(
        inquiryId: String,
        recipientTitle: String,
        userQuery: String,
        categoryTag: String?,
        flow: MutableStateFlow<List<LiveChatMessage>>
    ) {
        val queryLower = userQuery.lowercase()
        val isGuide = recipientTitle.contains("Guide", ignoreCase = true) || recipientTitle.contains("Ranger", ignoreCase = true)
        val senderRole = if (isGuide) "guide" else "host"
        val senderName = if (isGuide) recipientTitle else "$recipientTitle Host"

        val replyText = when {
            queryLower.contains("check-in") || queryLower.contains("check in") || queryLower.contains("late") || queryLower.contains("arrival") -> {
                "Karibu! Late check-ins are no problem. Our 24/7 lodge gate ranger will welcome you with cold hibiscus tea and assist with your luggage to the suite."
            }
            queryLower.contains("pick") || queryLower.contains("transfer") || queryLower.contains("airstrip") || queryLower.contains("airport") -> {
                "Habari! Our custom 4x4 Land Cruiser transfer is ready at Seronera / Keekorok Airstrip. Our driver will meet you right as your flight touches down."
            }
            queryLower.contains("food") || queryLower.contains("meal") || queryLower.contains("diet") || queryLower.contains("vegan") || queryLower.contains("vegetarian") -> {
                "Jambo! Our executive safari chef prepares organic, dietary-tailored bush meals daily. Gluten-free, vegetarian, and halal options are all available."
            }
            queryLower.contains("drive") || queryLower.contains("game") || queryLower.contains("lion") || queryLower.contains("wildlife") || queryLower.contains("safari") -> {
                "Asante! This morning's dawn patrol spotted a coalition of three cheetahs near the kopjes and a large wildebeest river crossing. The 6:00 AM drive is set!"
            }
            queryLower.contains("room") || queryLower.contains("view") || queryLower.contains("suite") || queryLower.contains("bed") -> {
                "All our luxury canvas tents overlook the river migration path with private infinity decks and outdoor showers."
            }
            queryLower.contains("price") || queryLower.contains("cost") || queryLower.contains("discount") || queryLower.contains("voucher") -> {
                "Your M-Pesa or card booking includes all conservancy fees, 3 gourmet meals per day, and unlimited game drives!"
            }
            else -> {
                "Jambo! Thank you for your inquiry regarding $recipientTitle. Our lodge manager and head ranger have logged your request and are ensuring everything is prepared for your safari arrival."
            }
        }

        val replyMsg = LiveChatMessage(
            id = "reply_" + System.currentTimeMillis(),
            inquiryId = inquiryId,
            recipientTitle = recipientTitle,
            senderName = senderName,
            senderRole = senderRole,
            messageText = replyText,
            timestampFormatted = timeFormat.format(Date()),
            categoryTag = categoryTag,
            isUser = false
        )

        flow.value = flow.value + replyMsg

        // Also save host reply to Firestore if connected
        try {
            firestoreInstance?.collection("chat_inquiries")
                ?.document(inquiryId)
                ?.collection("messages")
                ?.add(
                    mapOf(
                        "inquiryId" to inquiryId,
                        "recipientTitle" to recipientTitle,
                        "senderName" to senderName,
                        "senderRole" to senderRole,
                        "messageText" to replyText,
                        "timestampFormatted" to replyMsg.timestampFormatted,
                        "categoryTag" to categoryTag,
                        "isUser" to false,
                        "createdAt" to com.google.firebase.Timestamp.now()
                    )
                )
        } catch (e: Exception) {
            Log.e("FirestoreChat", "Error pushing reply to Firestore: ${e.message}")
        }
    }

    private fun getInitialWelcomeMessages(inquiryId: String, recipientTitle: String, category: String?): List<LiveChatMessage> {
        val isGuide = recipientTitle.contains("Guide", ignoreCase = true) || recipientTitle.contains("Ranger", ignoreCase = true)
        val role = if (isGuide) "guide" else "host"
        val greeting = if (isGuide) {
            "Jambo! I am your lead safari guide for $recipientTitle. Ask me anything about last-minute game drive timings, wildlife sightings today, or airstrip pickup!"
        } else {
            "Karibu Sana! I am the lodge manager at $recipientTitle. How can I assist with your stay, dietary requests, or late check-in today?"
        }

        return listOf(
            LiveChatMessage(
                id = "welcome_1",
                inquiryId = inquiryId,
                recipientTitle = recipientTitle,
                senderName = if (isGuide) recipientTitle else "$recipientTitle Concierge",
                senderRole = role,
                messageText = greeting,
                timestampFormatted = timeFormat.format(Date()),
                categoryTag = category,
                isUser = false
            )
        )
    }

    companion object {
        @Volatile
        private var instance: FirestoreChatManager? = null

        fun getInstance(context: Context): FirestoreChatManager {
            return instance ?: synchronized(this) {
                instance ?: FirestoreChatManager(context.applicationContext).also { instance = it }
            }
        }
    }
}
