package com.example.data

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class SmsRequest(
    val username: String,
    val to: String,
    val message: String,
    val from: String? = null
)

@JsonClass(generateAdapter = true)
data class SmsResponse(
    @Json(name = "SMSMessageData") val smsMessageData: SmsMessageData
)

@JsonClass(generateAdapter = true)
data class SmsMessageData(
    val Message: String,
    val Recipients: List<SmsRecipient>
)

@JsonClass(generateAdapter = true)
data class SmsRecipient(
    val statusCode: Int,
    val number: String,
    val status: String,
    val cost: String,
    val messageId: String
)
