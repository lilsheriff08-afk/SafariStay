package com.example.data

import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path

interface PlacesApiService {
    @GET("v1/places/{placeId}")
    suspend fun getPlaceDetails(
        @Path("placeId") placeId: String,
        @Header("X-Goog-Api-Key") apiKey: String,
        @Header("X-Goog-FieldMask") fieldMask: String = "id,displayName,internationalPhoneNumber,photos"
    ): PlaceResponse
}
