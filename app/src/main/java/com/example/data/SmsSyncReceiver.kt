package com.example.data

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.provider.Telephony
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SmsSyncReceiver : BroadcastReceiver() {
    companion object {
        private const val TAG = "SmsSyncReceiver"
        private const val SHORT_CODE = "22345"
    }

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Telephony.Sms.Intents.SMS_RECEIVED_ACTION) {
            val messages = Telephony.Sms.Intents.getMessagesFromIntent(intent)
            for (sms in messages) {
                val sender = sms.displayOriginatingAddress
                val body = sms.displayMessageBody

                if (sender == SHORT_CODE) {
                    Log.d(TAG, "Received sync ACK from $SHORT_CODE: $body")
                    processAck(context, body)
                    // Optional: Abort broadcast to prevent it from reaching the default SMS app
                    // abortBroadcast() // Requires priority in manifest
                }
            }
        }
    }

    private fun processAck(context: Context, body: String) {
        // Expected format: ACK|BOOKING_ID|STATUS|SIGNATURE
        // Example: ACK|9845|SYNCED|sig8char
        
        if (!SmsSyncManager.verifyAckSignature(body)) {
            Log.w(TAG, "Invalid HMAC signature on SMS ACK. Potential spoofing attempt.")
            return
        }

        val parts = body.split("|")
        if (parts[0] == "ACK") {
            val bookingId = parts[1].toIntOrNull() ?: return
            val status = parts[2]

            if (status == "SYNCED") {
                updateBookingInDatabase(context, bookingId)
            }
        }
    }

    private fun updateBookingInDatabase(context: Context, bookingId: Int) {
        val database = AppDatabase.getDatabase(context)
        val appDao = database.appDao()
        val syncDao = database.syncDao()

        CoroutineScope(Dispatchers.IO).launch {
            // Update the local booking status to Confirmed
            appDao.updateBookingStatus(bookingId, "Confirmed")
            
            // Clean up the sync queue for this booking if it exists
            // Since we use a simple payload map in sync queue, we'd need to search or just rely on the ID
            // In a real app, we'd use a more robust mapping
            Log.d(TAG, "Successfully synced booking $bookingId via SMS.")
        }
    }
}
