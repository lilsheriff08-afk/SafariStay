package com.example.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.viewmodel.SafariViewModel

@Composable
fun UssdSimulatorDialog(viewModel: SafariViewModel, onDismiss: () -> Unit) {
    var ussdInput by remember { mutableStateOf("") }
    var currentTextPath by remember { mutableStateOf("") }
    var displayMessage by remember { mutableStateOf(getUssdResponse("")) }
    var isEnd by remember { mutableStateOf(false) }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = Color(0xFFE0E0E0),
            modifier = Modifier.width(320.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(imageVector = Icons.Default.PhoneAndroid, contentDescription = "USSD", tint = Color.DarkGray)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("USSD Simulator *384*77#", fontWeight = FontWeight.Bold, color = Color.DarkGray)
                }

                Spacer(modifier = Modifier.height(16.dp))

                // USSD Screen Display
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .background(Color.Black, RoundedCornerShape(8.dp))
                        .padding(12.dp)
                ) {
                    Text(
                        text = displayMessage.removePrefix("CON ").removePrefix("END "),
                        color = Color.White,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 14.sp
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                if (!isEnd) {
                    OutlinedTextField(
                        value = ussdInput,
                        onValueChange = { ussdInput = it },
                        label = { Text("Reply") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF2E7D32),
                            focusedLabelColor = Color(0xFF2E7D32)
                        )
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        TextButton(onClick = onDismiss) {
                            Text("CANCEL", color = Color.Gray)
                        }
                        Button(
                            onClick = {
                                if (ussdInput.isNotBlank()) {
                                    val newPath = if (currentTextPath.isEmpty()) ussdInput else "$currentTextPath*$ussdInput"
                                    currentTextPath = newPath
                                    val response = getUssdResponse(currentTextPath)
                                    displayMessage = response
                                    ussdInput = ""
                                    
                                    if (response.startsWith("END")) {
                                        isEnd = true
                                        // Execute background actions if needed based on path
                                        handleUssdAction(newPath, viewModel)
                                    }
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32))
                        ) {
                            Text("SEND")
                        }
                    }
                } else {
                    Button(
                        onClick = onDismiss,
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32))
                    ) {
                        Text("DISMISS")
                    }
                }
            }
        }
    }
}

// Logic to parse the Africa's Talking USSD string
fun getUssdResponse(text: String): String {
    if (text.isEmpty()) {
        return "CON Safari Stay Lodge Manager\n\n1. Pending Bookings\n2. Escrow Status\n3. Request Payout"
    }

    val parts = text.split("*")

    // 1. Pending Bookings Menu
    if (parts[0] == "1") {
        if (parts.size == 1) {
            return "CON Select Booking to Check-In:\n\n1. John Doe (Aug 12) - 2 Guests\n2. Jane Smith (Aug 15) - 4 Guests"
        }
        if (parts.size == 2) {
            if (parts[1] == "1") return "CON Booking: John Doe\nDates: Aug 12-14\nEscrow: \$850\n\n1. Confirm Check-in\n2. Back"
            if (parts[1] == "2") return "CON Booking: Jane Smith\nDates: Aug 15-18\nEscrow: \$1200\n\n1. Confirm Check-in\n2. Back"
            return "END Invalid selection."
        }
        if (parts.size == 3) {
            if (parts[2] == "1") {
                return "END Check-in confirmed! Escrow funds marked for release."
            }
        }
    }

    // 2. Escrow Status Menu
    if (parts[0] == "2") {
        return "END Escrow Overview:\nAvailable to Withdraw: \$1,600\nPending Settlement: \$850\nTotal Processed: \$4,200"
    }

    // 3. Request Payout Menu
    if (parts[0] == "3") {
        if (parts.size == 1) {
            return "CON Available to withdraw: \$1,600\n\nEnter amount to withdraw via M-Pesa:"
        }
        if (parts.size == 2) {
            val amount = parts[1]
            return "CON Confirm payout of \$$amount to your registered M-Pesa number (+254712345678)?\n\n1. Confirm\n2. Cancel"
        }
        if (parts.size == 3) {
            if (parts[2] == "1") {
                return "END Payout of \$${parts[1]} initiated. You will receive an M-Pesa confirmation shortly."
            } else {
                return "END Payout cancelled."
            }
        }
    }

    return "END Invalid Input."
}

fun handleUssdAction(path: String, viewModel: SafariViewModel) {
    val parts = path.split("*")
    // Trigger real M-Pesa API payout if payout confirmed
    if (parts.size == 3 && parts[0] == "3" && parts[2] == "1") {
        val amount = parts[1].toIntOrNull() ?: 0
        viewModel.initiateMpesaPayout(bookingId = "USSD-PAYOUT", phone = "+254712345678", amount = amount)
    }
}
