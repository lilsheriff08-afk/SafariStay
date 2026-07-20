package com.example.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class JournalViewModel(application: Application) : AndroidViewModel(application) {
    private val journalDao = AppDatabase.getDatabase(application).journalDao()

    val entries = journalDao.getAllEntries()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    // --- OFFLINE-FIRST PHOTO GALLERY & JOURNAL TELEMETRY ---
    private val _isOnline = MutableStateFlow(true)
    val isOnline: StateFlow<Boolean> = _isOnline.asStateFlow()

    private val _isSyncing = MutableStateFlow(false)
    val isSyncing: StateFlow<Boolean> = _isSyncing.asStateFlow()

    private val _syncProgress = MutableStateFlow<String?>(null)
    val syncProgress: StateFlow<String?> = _syncProgress.asStateFlow()

    fun toggleOnlineStatus() {
        _isOnline.value = !_isOnline.value
    }

    fun addNote(note: String, location: String) {
        viewModelScope.launch {
            journalDao.insertEntry(
                JournalEntry(
                    note = note,
                    timestamp = System.currentTimeMillis(),
                    location = location,
                    isSynced = _isOnline.value // If online, synced immediately. Otherwise offline-first.
                )
            )
        }
    }

    fun addNoteWithPhoto(note: String, location: String, imageBase64: String?) {
        viewModelScope.launch {
            journalDao.insertEntry(
                JournalEntry(
                    note = note,
                    timestamp = System.currentTimeMillis(),
                    location = location,
                    isSynced = _isOnline.value && imageBase64 == null, // Photos require background sync if captured
                    imageBase64 = imageBase64
                )
            )
        }
    }

    fun syncUnsyncedPhotos() {
        viewModelScope.launch {
            if (!_isOnline.value) {
                _syncProgress.value = "❌ Sync failed: Terminal is OFFLINE. Connect to satellite network to sync gallery."
                return@launch
            }

            _isSyncing.value = true
            _syncProgress.value = "📡 Initializing cloud sync background task..."
            delay(1000)

            val unsynced = journalDao.getUnsyncedEntries()
            if (unsynced.isEmpty()) {
                _syncProgress.value = "✅ Gallery fully synced. No pending photos in IndexedDB queue!"
                _isSyncing.value = false
                return@launch
            }

            _syncProgress.value = "🔄 Discovered ${unsynced.size} unsynced items in local Room queue..."
            delay(1200)

            var successCount = 0
            unsynced.forEachIndexed { index, entry ->
                _syncProgress.value = "📤 Syncing item ${index + 1}/${unsynced.size}: \"${entry.note.take(15)}...\""
                delay(800) // Simulate packet transfer latency
                
                val updated = entry.copy(isSynced = true)
                journalDao.updateEntry(updated)
                successCount++
            }

            _syncProgress.value = "✅ Background sync task finished!\n" +
                    "• Successfully uploaded $successCount photos to Cloud Storage.\n" +
                    "• Local Room storage synchronized."
            _isSyncing.value = false
        }
    }

    fun deleteEntry(entry: JournalEntry) {
        viewModelScope.launch {
            journalDao.deleteEntry(entry)
        }
    }

    fun clearSyncProgress() {
        _syncProgress.value = null
    }
}
