package com.example.data

import android.content.Context
import android.telephony.SmsManager
import android.util.Base64
import android.util.Log
import java.util.*
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

object SmsSyncManager {
    private const val TAG = "SmsSyncManager"
    private const val SHORT_CODE = "22345" // Your Africa's Talking Short Code
    private const val SHARED_SECRET = "safari_stay_secure_sync_2026" // Should be moved to BuildConfig/Secrets

    // Compact Protocol: [ACTION_CODE]|[IDENTIFIER]|[DATA_PAYLOAD]|[SIGNATURE]
    // CFM|BK-9845|RM-02|1|sig

    fun sendBookingSms(context: Context, booking: BookingEntity) {
        try {
            val actionCode = if (booking.type == "STAY") "BST" else "BSF"
            val identifier = booking.id.toString()
            // Positional data: Type|Price|VoucherCode
            val payload = "${booking.type}|${booking.price.toInt()}|${booking.voucherCodeUsed ?: ""}"
            
            val messageWithoutSignature = "$actionCode|$identifier|$payload"
            val signature = generateHmac(messageWithoutSignature)
            val finalMessage = "$messageWithoutSignature|$signature"

            val smsManager = context.getSystemService(SmsManager::class.java)
            smsManager.sendTextMessage(SHORT_CODE, null, finalMessage, null, null)
            
            Log.d(TAG, "Sent SMS Sync: $finalMessage")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to send SMS sync", e)
        }
    }

    fun verifyAckSignature(message: String): Boolean {
        val parts = message.split("|")
        if (parts.size < 4) return false // ACK|ID|STATUS|SIG
        
        val content = parts.subList(0, 3).joinToString("|")
        val providedSig = parts[3]
        
        return generateHmac(content) == providedSig
    }

    private fun generateHmac(data: String): String {
        val mac = Mac.getInstance("HmacSHA256")
        val secretKey = SecretKeySpec(SHARED_SECRET.toByteArray(), "HmacSHA256")
        mac.init(secretKey)
        val hash = mac.doFinal(data.toByteArray())
        // Take first 8 chars of Base64 to save space in SMS
        return Base64.encodeToString(hash, Base64.NO_WRAP).substring(0, 8)
    }
}
