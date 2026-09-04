package com.example.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [BookingEntity::class, VoucherEntity::class, FavoriteEntity::class, JournalEntry::class, SightingEntity::class, WeatherCacheEntity::class, NotificationEntity::class, FeedbackEntity::class, ChecklistItem::class, SyncAction::class], version = 12, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun appDao(): AppDao
    abstract fun bookingDao(): BookingDao
    abstract fun journalDao(): JournalDao
    abstract fun sightingDao(): SightingDao
    abstract fun syncDao(): SyncDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "safari_stay_database"
                )
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
