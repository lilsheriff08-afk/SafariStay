package com.example.data

import retrofit2.http.*

interface AfricaTalkingApiService {
    @FormUrlEncoded
    @POST("version1/messaging")
    suspend fun sendSms(
        @Header("apiKey") apiKey: String,
        @Field("username") username: String,
        @Field("to") to: String,
        @Field("message") message: String,
        @Field("from") from: String? = null
    ): SmsResponse

    @FormUrlEncoded
    @POST("v1/whatsapp/messages")
    suspend fun sendWhatsApp(
        @Header("apiKey") apiKey: String,
        @Field("username") username: String,
        @Field("to") to: String,
        @Field("message") message: String
    ): SmsResponse // Reusing model for demo
}
