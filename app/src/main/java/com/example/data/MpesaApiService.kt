package com.example.data

import retrofit2.http.*

interface MpesaApiService {
    @GET("oauth/v1/generate?grant_type=client_credentials")
    suspend fun getAccessToken(
        @Header("Authorization") basicAuth: String
    ): MpesaAuthResponse

    @POST("mpesa/stkpush/v1/processrequest")
    suspend fun initiateStkPush(
        @Header("Authorization") bearerToken: String,
        @Body request: StkPushRequest
    ): StkPushResponse
}
