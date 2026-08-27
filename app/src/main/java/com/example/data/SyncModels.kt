package com.example.data

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class BatchSyncRequest(
    @Json(name = "device_id") val deviceId: String,
    @Json(name = "client_sync_timestamp") val clientSyncTimestamp: String,
    val items: List<SyncBatchItem>
)

@JsonClass(generateAdapter = true)
data class SyncBatchItem(
    @Json(name = "local_id") val localId: String,
    @Json(name = "idempotency_key") val idempotencyKey: String,
    @Json(name = "action_type") val actionType: String,
    @Json(name = "entity_id") val entityId: String,
    @Json(name = "client_created_at") val clientCreatedAt: String,
    val payload: Map<String, Any>
)

@JsonClass(generateAdapter = true)
data class BatchSyncResponse(
    @Json(name = "server_processed_at") val serverProcessedAt: String,
    @Json(name = "processed_count") val processedCount: Int,
    val results: List<SyncResultItem>
)

@JsonClass(generateAdapter = true)
data class SyncResultItem(
    @Json(name = "local_id") val localId: String,
    val status: String, // SUCCESS, CONFLICT_RESOLVED, REJECTED, RETRY
    @Json(name = "resolution_strategy") val resolutionStrategy: String? = null,
    @Json(name = "entity_state") val entityState: Map<String, Any>? = null,
    @Json(name = "error_message") val errorMessage: String? = null
)
