package com.example.data

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class PlaceResponse(
    val id: String,
    val displayName: DisplayName,
    val internationalPhoneNumber: String? = null,
    val photos: List<PlacePhoto>? = null,
    val rating: Double? = null,
    val userRatingCount: Int? = null
)

@JsonClass(generateAdapter = true)
data class DisplayName(
    val text: String
)

@JsonClass(generateAdapter = true)
data class PlacePhoto(
    val name: String,
    val widthPx: Int,
    val heightPx: Int,
    val authorAttributions: List<AuthorAttribution>? = null
)

@JsonClass(generateAdapter = true)
data class AuthorAttribution(
    val displayName: String,
    val uri: String? = null,
    val photoUri: String? = null
)
