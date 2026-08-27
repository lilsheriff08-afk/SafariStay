package com.example.data

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class MpesaAuthResponse(
    @Json(name = "access_token") val accessToken: String,
    @Json(name = "expires_in") val expiresIn: String
)

@JsonClass(generateAdapter = true)
data class StkPushRequest(
    @Json(name = "BusinessShortCode") val businessShortCode: String,
    @Json(name = "Password") val password: String,
    @Json(name = "Timestamp") val timestamp: String,
    @Json(name = "TransactionType") val transactionType: String = "CustomerPayBillOnline",
    @Json(name = "Amount") val amount: Int,
    @Json(name = "PartyA") val partyA: String, // Phone number
    @Json(name = "PartyB") val partyB: String, // Shortcode
    @Json(name = "PhoneNumber") val phoneNumber: String,
    @Json(name = "CallBackURL") val callBackUrl: String,
    @Json(name = "AccountReference") val accountReference: String,
    @Json(name = "TransactionDesc") val transactionDesc: String
)

@JsonClass(generateAdapter = true)
data class StkPushResponse(
    @Json(name = "MerchantRequestID") val merchantRequestId: String,
    @Json(name = "CheckoutRequestID") val checkoutRequestId: String,
    @Json(name = "ResponseCode") val responseCode: String,
    @Json(name = "ResponseDescription") val responseDescription: String,
    @Json(name = "CustomerMessage") val customerMessage: String
)
