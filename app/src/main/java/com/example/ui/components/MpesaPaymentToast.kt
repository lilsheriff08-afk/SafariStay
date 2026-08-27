package com.example.ui.components

import android.content.Context
import android.content.Intent
import android.provider.CalendarContract
import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.Smartphone
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

data class MpesaSuccessData(
    val transactionRef: String = "MPX-" + (100000..999999).random(),
    val amountFormatted: String,
    val itemName: String,
    val location: String = "Serengeti & Mara Conservancy, East Africa",
    val startDateMillis: Long? = null,
    val endDateMillis: Long? = null,
    val phoneNumber: String = ""
)

/**
 * Custom Toast Notification banner for M-Pesa Payment Success
 */
@Composable
fun MpesaPaymentSuccessToastOverlay(
    data: MpesaSuccessData?,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    AnimatedVisibility(
        visible = data != null,
        enter = slideInVertically(initialOffsetY = { -it }) + fadeIn(),
        exit = slideOutVertically(targetOffsetY = { -it }) + fadeOut(),
        modifier = modifier
    ) {
        if (data != null) {
            ElevatedCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp)
                    .testTag("mpesa_success_toast_banner"),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.elevatedCardColors(
                    containerColor = Color(0xFF1B3B1A), // Safari Dark Green
                    contentColor = Color.White
                ),
                elevation = CardDefaults.elevatedCardElevation(defaultElevation = 8.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Top header line
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .background(Color(0xFF4CAF50), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = "Success",
                                    tint = Color.White,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Column {
                                Text(
                                    text = "M-PESA PAYMENT CONFIRMED",
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 13.sp,
                                    color = Color(0xFF81C784)
                                )
                                Text(
                                    text = "Ref: ${data.transactionRef}",
                                    fontSize = 11.sp,
                                    color = Color.LightGray
                                )
                            }
                        }

                        IconButton(
                            onClick = onDismiss,
                            modifier = Modifier.size(28.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Dismiss notification",
                                tint = Color.White
                            )
                        }
                    }

                    // Payment details
                    Text(
                        text = "Successfully paid ${data.amountFormatted} for ${data.itemName}",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.White
                    )

                    // Action buttons: Add to Calendar
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Button(
                            onClick = {
                                addBookingToCalendar(
                                    context = context,
                                    title = "Safari Stay: ${data.itemName}",
                                    description = "Confirmed M-Pesa Booking (${data.transactionRef}). Amount Paid: ${data.amountFormatted}. Enjoy your trip!",
                                    location = data.location,
                                    startDateMillis = data.startDateMillis,
                                    endDateMillis = data.endDateMillis
                                )
                            },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("toast_add_calendar_button"),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFFFFB300), // M-Pesa Gold
                                contentColor = Color(0xFF2E1500)
                            ),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.CalendarMonth,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(Modifier.width(6.dp))
                            Text(
                                text = "Add to Calendar 📅",
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            )
                        }

                        OutlinedButton(
                            onClick = onDismiss,
                            shape = RoundedCornerShape(10.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color.Gray)
                        ) {
                            Text("Done", color = Color.White, fontSize = 12.sp)
                        }
                    }
                }
            }
        }
    }
}

/**
 * Inline card version for Booking Dialog or Confirmation Screens
 */
@Composable
fun MpesaCalendarPromptCard(
    data: MpesaSuccessData,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .border(1.dp, Color(0xFF4CAF50).copy(alpha = 0.5f), RoundedCornerShape(12.dp)),
        color = Color(0xFF132B12), // Deep safari green
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Smartphone,
                    contentDescription = null,
                    tint = Color(0xFF81C784),
                    modifier = Modifier.size(20.dp)
                )
                Text(
                    text = "M-Pesa Receipt: ${data.transactionRef}",
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = Color(0xFF81C784)
                )
            }

            Text(
                text = "Paid ${data.amountFormatted}. Your reservation is officially secured!",
                fontSize = 12.sp,
                color = Color.LightGray
            )

            Button(
                onClick = {
                    addBookingToCalendar(
                        context = context,
                        title = "Safari Expedition: ${data.itemName}",
                        description = "M-Pesa Confirmed Booking (${data.transactionRef}). Total Paid: ${data.amountFormatted}. Pack your camera and safari gear!",
                        location = data.location,
                        startDateMillis = data.startDateMillis,
                        endDateMillis = data.endDateMillis
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("dialog_add_to_calendar_btn"),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF4CAF50),
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(10.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Event,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    text = "Add Booking to Personal Calendar 📅",
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp
                )
            }
        }
    }
}

fun addBookingToCalendar(
    context: Context,
    title: String,
    description: String,
    location: String,
    startDateMillis: Long? = null,
    endDateMillis: Long? = null
) {
    val start = startDateMillis ?: (System.currentTimeMillis() + 86400000L * 7)
    val end = endDateMillis ?: (start + 86400000L * 3)

    val intent = Intent(Intent.ACTION_INSERT).apply {
        data = CalendarContract.Events.CONTENT_URI
        putExtra(CalendarContract.Events.TITLE, title)
        putExtra(CalendarContract.Events.DESCRIPTION, description)
        putExtra(CalendarContract.Events.EVENT_LOCATION, location)
        putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, start)
        putExtra(CalendarContract.EXTRA_EVENT_END_TIME, end)
        putExtra(CalendarContract.Events.ACCESS_LEVEL, CalendarContract.Events.ACCESS_PRIVATE)
        putExtra(CalendarContract.Events.AVAILABILITY, CalendarContract.Events.AVAILABILITY_BUSY)
    }

    try {
        context.startActivity(intent)
        Toast.makeText(context, "Opening Digital Calendar to add stay... 📅", Toast.LENGTH_SHORT).show()
    } catch (e: Exception) {
        Toast.makeText(context, "No calendar application found", Toast.LENGTH_SHORT).show()
    }
}
