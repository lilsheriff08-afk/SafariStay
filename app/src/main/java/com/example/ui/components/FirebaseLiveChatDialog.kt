package com.example.ui.components

import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.FirestoreChatManager
import com.example.data.LiveChatMessage
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FirebaseLiveChatDialog(
    inquiryId: String,
    recipientTitle: String,
    recipientSubtitle: String = "Lodge Host & Safari Concierge",
    initialCategory: String? = null,
    onDismiss: () -> Unit,
    onBookClick: (() -> Unit)? = null
) {
    val context = LocalContext.current
    val chatManager = remember { FirestoreChatManager.getInstance(context) }
    val messagesState by chatManager.getChatMessagesFlow(inquiryId, recipientTitle, initialCategory).collectAsState()

    var inputText by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf(initialCategory) }
    var isRecordingAudio by remember { mutableStateOf(false) }

    val listState = rememberLazyListState()
    val scope = rememberCoroutineScope()

    // Auto scroll to bottom when new messages arrive
    LaunchedEffect(messagesState.size) {
        if (messagesState.isNotEmpty()) {
            listState.animateScrollToItem(messagesState.size - 1)
        }
    }

    val quickInquiryChips = listOf(
        "🛬 Airstrip Pick-up",
        "🔑 Late Check-in",
        "🥗 Dietary Options",
        "🐆 Game Drive Timings",
        "🐘 Wildlife Sightings",
        "🧾 M-Pesa Inquiry"
    )

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .testTag("firebase_live_chat_dialog"),
            color = MaterialTheme.colorScheme.background
        ) {
            Scaffold(
                topBar = {
                    TopAppBar(
                        title = {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Box {
                                    Box(
                                        modifier = Modifier
                                            .size(40.dp)
                                            .background(
                                                MaterialTheme.colorScheme.primaryContainer,
                                                CircleShape
                                            ),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = if (recipientTitle.contains("Guide", ignoreCase = true)) Icons.Default.DirectionsCar else Icons.Default.Cabin,
                                            contentDescription = null,
                                            tint = MaterialTheme.colorScheme.onPrimaryContainer,
                                            modifier = Modifier.size(22.dp)
                                        )
                                    }
                                    // Live Green Indicator
                                    Box(
                                        modifier = Modifier
                                            .size(12.dp)
                                            .background(Color(0xFF4CAF50), CircleShape)
                                            .border(2.dp, MaterialTheme.colorScheme.surface, CircleShape)
                                            .align(Alignment.BottomEnd)
                                    )
                                }

                                Column {
                                    Text(
                                        text = recipientTitle,
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Bold,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = "$recipientSubtitle • ",
                                            fontSize = 11.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                        Surface(
                                            color = Color(0xFF1B3B1A),
                                            shape = RoundedCornerShape(4.dp)
                                        ) {
                                            Text(
                                                text = "Firestore Live 🟢",
                                                fontSize = 9.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = Color(0xFF81C784),
                                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        },
                        navigationIcon = {
                            IconButton(
                                onClick = onDismiss,
                                modifier = Modifier.testTag("close_chat_button")
                            ) {
                                Icon(Icons.Default.ArrowBack, contentDescription = "Close Chat")
                            }
                        },
                        actions = {
                            if (onBookClick != null) {
                                FilledTonalButton(
                                    onClick = {
                                        onDismiss()
                                        onBookClick()
                                    },
                                    modifier = Modifier.padding(end = 8.dp),
                                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                                ) {
                                    Text("Book Lodge", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
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
                ) {
                    // Quick inquiry chips header bar
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                            .padding(vertical = 8.dp)
                    ) {
                        Text(
                            text = "QUICK LAST-MINUTE INQUIRIES",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 6.dp)
                        )
                        LazyRow(
                            contentPadding = PaddingValues(horizontal = 16.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(quickInquiryChips) { chipText ->
                                FilterChip(
                                    selected = selectedCategory == chipText,
                                    onClick = {
                                        selectedCategory = chipText
                                        chatManager.sendMessage(
                                            inquiryId = inquiryId,
                                            recipientTitle = recipientTitle,
                                            text = "Inquiry regarding $chipText: Can you confirm details for our upcoming stay?",
                                            categoryTag = chipText
                                        )
                                    },
                                    label = { Text(chipText, fontSize = 12.sp) },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = MaterialTheme.colorScheme.primary,
                                        selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                                    )
                                )
                            }
                        }
                    }

                    // Messages List
                    LazyColumn(
                        state = listState,
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                        contentPadding = PaddingValues(vertical = 12.dp)
                    ) {
                        items(messagesState, key = { it.id }) { msg ->
                            ChatMessageBubble(msg = msg)
                        }
                    }

                    // Audio recording indicator
                    AnimatedVisibility(visible = isRecordingAudio) {
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 4.dp),
                            color = MaterialTheme.colorScheme.errorContainer,
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Icon(
                                        Icons.Default.Mic,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.onErrorContainer
                                    )
                                    Text(
                                        text = "Recording voice note for $recipientTitle...",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onErrorContainer
                                    )
                                }
                                TextButton(
                                    onClick = {
                                        isRecordingAudio = false
                                        chatManager.sendMessage(
                                            inquiryId = inquiryId,
                                            recipientTitle = recipientTitle,
                                            text = "🎤 [Voice Note Attached - 0:14s] Last minute audio inquiry regarding park entrance permits.",
                                            categoryTag = "Voice Note"
                                        )
                                        Toast.makeText(context, "Voice note sent to host! 🎙️", Toast.LENGTH_SHORT).show()
                                    }
                                ) {
                                    Text("Send Note", color = MaterialTheme.colorScheme.onErrorContainer, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }

                    // Input Row
                    Surface(
                        shadowElevation = 8.dp,
                        color = MaterialTheme.colorScheme.surface
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 12.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            IconButton(
                                onClick = {
                                    isRecordingAudio = !isRecordingAudio
                                },
                                modifier = Modifier.size(42.dp)
                            ) {
                                Icon(
                                    imageVector = if (isRecordingAudio) Icons.Default.MicOff else Icons.Default.Mic,
                                    contentDescription = "Voice Note",
                                    tint = if (isRecordingAudio) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
                                )
                            }

                            OutlinedTextField(
                                value = inputText,
                                onValueChange = { inputText = it },
                                placeholder = { Text("Ask host about room, meals, transfer...", fontSize = 13.sp) },
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("chat_input_textfield"),
                                shape = RoundedCornerShape(24.dp),
                                maxLines = 3,
                                colors = OutlinedTextFieldDefaults.colors(
                                    unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                                )
                            )

                            IconButton(
                                onClick = {
                                    if (inputText.isNotBlank()) {
                                        val textToSend = inputText
                                        inputText = ""
                                        chatManager.sendMessage(
                                            inquiryId = inquiryId,
                                            recipientTitle = recipientTitle,
                                            text = textToSend,
                                            categoryTag = selectedCategory
                                        )
                                    }
                                },
                                modifier = Modifier
                                    .size(42.dp)
                                    .background(
                                        if (inputText.isNotBlank()) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                                        CircleShape
                                    )
                                    .testTag("send_chat_button")
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.Send,
                                    contentDescription = "Send",
                                    tint = if (inputText.isNotBlank()) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ChatMessageBubble(msg: LiveChatMessage) {
    val alignment = if (msg.isUser) Alignment.End else Alignment.Start
    val bgColor = if (msg.isUser) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant
    val textColor = if (msg.isUser) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = alignment
    ) {
        Row(
            verticalAlignment = Alignment.Top,
            horizontalArrangement = if (msg.isUser) Arrangement.End else Arrangement.Start,
            modifier = Modifier.fillMaxWidth(0.88f)
        ) {
            if (!msg.isUser) {
                Box(
                    modifier = Modifier
                        .padding(end = 6.dp, top = 2.dp)
                        .size(28.dp)
                        .background(MaterialTheme.colorScheme.primaryContainer, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (msg.senderRole == "guide") Icons.Default.DirectionsCar else Icons.Default.Cabin,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            Column(horizontalAlignment = alignment) {
                // Sender label & category tag
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = msg.senderName,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    if (msg.categoryTag != null) {
                        Surface(
                            color = MaterialTheme.colorScheme.secondaryContainer,
                            shape = RoundedCornerShape(4.dp)
                        ) {
                            Text(
                                text = msg.categoryTag,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Medium,
                                color = MaterialTheme.colorScheme.onSecondaryContainer,
                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                            )
                        }
                    }
                }

                Spacer(Modifier.height(2.dp))

                // Bubble Card
                Surface(
                    shape = RoundedCornerShape(
                        topStart = 16.dp,
                        topEnd = 16.dp,
                        bottomStart = if (msg.isUser) 16.dp else 4.dp,
                        bottomEnd = if (msg.isUser) 4.dp else 16.dp
                    ),
                    color = bgColor,
                    shadowElevation = 1.dp
                ) {
                    Column(modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)) {
                        Text(
                            text = msg.messageText,
                            fontSize = 13.1.sp,
                            color = textColor,
                            lineHeight = 18.sp
                        )
                        Spacer(Modifier.height(2.dp))
                        Text(
                            text = msg.timestampFormatted,
                            fontSize = 9.sp,
                            color = textColor.copy(alpha = 0.7f),
                            modifier = Modifier.align(Alignment.End)
                        )
                    }
                }
            }
        }
    }
}
