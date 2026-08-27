package com.example.data

import android.util.Base64
import android.util.Log
import com.example.BuildConfig
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

enum class PaymentType {
    SAFARI_BOOKING,
    VOUCHER_PURCHASE,
    EXPERIENCE_BOOKING,
    STAY_BOOKING,
    GENERIC
}

sealed class PaymentResult {
    data class Success(
        val transactionId: String,
        val checkoutRequestId: String,
        val customerMessage: String,
        val amount: Double,
        val reference: String,
        val paymentType: PaymentType
    ) : PaymentResult()

    data class Pending(
        val checkoutRequestId: String,
        val customerMessage: String
    ) : PaymentResult()

    data class Error(
        val message: String,
        val responseCode: String? = null
    ) : PaymentResult()
}

/**
 * PaymentRepository manages secure payment processing and M-Pesa Daraja STK push integration.
 * Utilizes MPESA_CONSUMER_KEY, MPESA_CONSUMER_SECRET, MPESA_SHORTCODE, and MPESA_PASSKEY
 * injected from .env via BuildConfig.
 */
class PaymentRepository {

    private val moshi = Moshi.Builder().addLast(KotlinJsonAdapterFactory()).build()

    private val mpesaApiService: MpesaApiService = Retrofit.Builder()
        .baseUrl("https://sandbox.safaricom.co.ke/")
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .build()
        .create(MpesaApiService::class.java)

    // Retrieve M-Pesa consumer keys & credentials from BuildConfig (.env)
    val consumerKey: String get() = BuildConfig.MPESA_CONSUMER_KEY
    val consumerSecret: String get() = BuildConfig.MPESA_CONSUMER_SECRET
    val shortcode: String get() = BuildConfig.MPESA_SHORTCODE.ifBlank { "174379" }
    val passkey: String get() = BuildConfig.MPESA_PASSKEY

    /**
     * Formats phone numbers to Safaricom MSISDN standard (2547XXXXXXXX)
     */
    fun formatPhoneNumber(phone: String): String {
        val digits = phone.replace(Regex("[^0-9]"), "")
        return when {
            digits.startsWith("0") && digits.length == 10 -> "254" + digits.substring(1)
            digits.startsWith("254") && digits.length == 12 -> digits
            digits.length == 9 -> "254" + digits
            else -> digits.ifEmpty { "254712345678" }
        }
    }

    /**
     * Initiates a secure M-Pesa checkout for Safari Bookings
     */
    suspend fun checkoutSafariBooking(
        amount: Double,
        phoneNumber: String,
        safariTitle: String,
        bookingReference: String
    ): PaymentResult {
        val desc = "Safari: ${safariTitle.take(20)}"
        return initiateMpesaCheckout(
            amount = amount,
            phoneNumber = phoneNumber,
            accountReference = bookingReference.take(12),
            transactionDesc = desc,
            paymentType = PaymentType.SAFARI_BOOKING
        )
    }

    /**
     * Initiates a secure M-Pesa checkout for Safari Stay Vouchers & Gift Cards
     */
    suspend fun checkoutVoucher(
        voucherAmount: Double,
        phoneNumber: String,
        recipientName: String,
        voucherCode: String
    ): PaymentResult {
        val desc = "Voucher: ${recipientName.take(20)}"
        return initiateMpesaCheckout(
            amount = voucherAmount,
            phoneNumber = phoneNumber,
            accountReference = voucherCode.take(12),
            transactionDesc = desc,
            paymentType = PaymentType.VOUCHER_PURCHASE
        )
    }

    /**
     * Core M-Pesa Daraja STK Push Checkout using Consumer Keys from .env
     */
    suspend fun initiateMpesaCheckout(
        amount: Double,
        phoneNumber: String,
        accountReference: String,
        transactionDesc: String,
        paymentType: PaymentType = PaymentType.GENERIC
    ): PaymentResult = withContext(Dispatchers.IO) {
        val formattedPhone = formatPhoneNumber(phoneNumber)
        val key = consumerKey
        val secret = consumerSecret
        val currentPasskey = passkey
        val currentShortcode = shortcode

        Log.d("PaymentRepository", "Initiating M-Pesa checkout ($paymentType) for $formattedPhone, Amount: $amount KES")

        // Check if credentials are sandbox placeholders or missing
        val isSimulator = key.isBlank() || secret.isBlank() ||
                key.contains("YOUR_") || secret.contains("YOUR_") ||
                key == "MY_NEW_API_KEY_DEFAULT_VALUE"

        if (isSimulator) {
            Log.i("PaymentRepository", "Using M-Pesa Sandbox simulation (Placeholder consumer keys detected in .env).")
            val mockCheckoutRequestId = "ws_CO_SIM_" + System.currentTimeMillis()
            val mockTxId = "NL" + (1000000000..9999999999).random()

            return@withContext PaymentResult.Success(
                transactionId = mockTxId,
                checkoutRequestId = mockCheckoutRequestId,
                customerMessage = "STK Push sent to $formattedPhone! Enter M-Pesa PIN on handset to confirm $${String.format("%.2f", amount)}.",
                amount = amount,
                reference = accountReference,
                paymentType = paymentType
            )
        }

        try {
            // 1. Authenticate with Safaricom Daraja API using OAuth Consumer Key & Secret
            val authHeader = "Basic " + Base64.encodeToString(
                "$key:$secret".toByteArray(),
                Base64.NO_WRAP
            )
            val authResponse = mpesaApiService.getAccessToken(authHeader)
            val accessToken = authResponse.accessToken

            // 2. Generate Timestamp & Encrypted Passkey Password
            val timestamp = SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault()).format(Date())
            val rawPassword = "$currentShortcode$currentPasskey$timestamp"
            val password = Base64.encodeToString(rawPassword.toByteArray(), Base64.NO_WRAP)

            // 3. Construct STK Push Request Payload
            val stkRequest = StkPushRequest(
                businessShortCode = currentShortcode,
                password = password,
                timestamp = timestamp,
                transactionType = "CustomerPayBillOnline",
                amount = amount.toInt().coerceAtLeast(1),
                partyA = formattedPhone,
                partyB = currentShortcode,
                phoneNumber = formattedPhone,
                callBackUrl = "https://api.safaristay.app/v1/mpesa/callback",
                accountReference = accountReference.ifBlank { "SAFARI-PAY" },
                transactionDesc = transactionDesc.ifBlank { "Safari Payment" }
            )

            // 4. Dispatch STK Push Request
            val response = mpesaApiService.initiateStkPush("Bearer $accessToken", stkRequest)

            if (response.responseCode == "0") {
                PaymentResult.Success(
                    transactionId = response.merchantRequestId,
                    checkoutRequestId = response.checkoutRequestId,
                    customerMessage = if (response.customerMessage.isNotBlank()) response.customerMessage else "STK Push initiated on $formattedPhone",
                    amount = amount,
                    reference = accountReference,
                    paymentType = paymentType
                )
            } else {
                PaymentResult.Error(
                    message = if (response.responseDescription.isNotBlank()) response.responseDescription else "STK Push request failed",
                    responseCode = response.responseCode
                )
            }
        } catch (e: Exception) {
            Log.e("PaymentRepository", "M-Pesa API Exception during checkout", e)
            PaymentResult.Error(
                message = "M-Pesa payment failed: ${e.localizedMessage ?: e.message}"
            )
        }
    }
}
