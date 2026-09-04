package com.example.data

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "safari_offline_cache")

class BookingDataStore(private val context: Context) {
    private val moshi = Moshi.Builder().addLast(KotlinJsonAdapterFactory()).build()
    
    private val bookingsListType = Types.newParameterizedType(List::class.java, BookingEntity::class.java)
    private val vouchersListType = Types.newParameterizedType(List::class.java, VoucherEntity::class.java)
    
    private val bookingsAdapter = moshi.adapter<List<BookingEntity>>(bookingsListType)
    private val vouchersAdapter = moshi.adapter<List<VoucherEntity>>(vouchersListType)

    companion object {
        private val BOOKINGS_KEY = stringPreferencesKey("cached_bookings_json")
        private val VOUCHERS_KEY = stringPreferencesKey("cached_vouchers_json")
    }

    val cachedBookingsFlow: Flow<List<BookingEntity>> = context.dataStore.data
        .map { preferences ->
            val encryptedJson = preferences[BOOKINGS_KEY]
            if (!encryptedJson.isNullOrEmpty()) {
                try {
                    val json = EncryptionUtils.decrypt(encryptedJson)
                    bookingsAdapter.fromJson(json) ?: emptyList()
                } catch (e: Exception) {
                    emptyList()
                }
            } else {
                emptyList()
            }
        }

    val cachedVouchersFlow: Flow<List<VoucherEntity>> = context.dataStore.data
        .map { preferences ->
            val encryptedJson = preferences[VOUCHERS_KEY]
            if (!encryptedJson.isNullOrEmpty()) {
                try {
                    val json = EncryptionUtils.decrypt(encryptedJson)
                    vouchersAdapter.fromJson(json) ?: emptyList()
                } catch (e: Exception) {
                    emptyList()
                }
            } else {
                emptyList()
            }
        }

    suspend fun saveBookings(bookings: List<BookingEntity>) {
        try {
            val json = bookingsAdapter.toJson(bookings)
            val encryptedJson = EncryptionUtils.encrypt(json)
            context.dataStore.edit { preferences ->
                preferences[BOOKINGS_KEY] = encryptedJson
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    suspend fun saveVouchers(vouchers: List<VoucherEntity>) {
        try {
            val json = vouchersAdapter.toJson(vouchers)
            val encryptedJson = EncryptionUtils.encrypt(json)
            context.dataStore.edit { preferences ->
                preferences[VOUCHERS_KEY] = encryptedJson
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
