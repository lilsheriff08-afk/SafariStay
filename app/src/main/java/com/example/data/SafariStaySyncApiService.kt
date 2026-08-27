package com.example.data

import retrofit2.http.*

interface SafariStaySyncApiService {
    @POST("sync/batch")
    suspend fun syncBatch(
        @Header("Authorization") bearerToken: String,
        @Header("X-Safari-HMAC") hmacSignature: String,
        @Body request: BatchSyncRequest
    ): BatchSyncResponse
}
