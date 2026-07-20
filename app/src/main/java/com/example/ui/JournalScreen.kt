package com.example.ui

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.CloudQueue
import androidx.compose.material.icons.outlined.DeleteOutline
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.JournalEntry
import com.example.viewmodel.JournalViewModel
import java.io.ByteArrayOutputStream
import java.text.SimpleDateFormat
import java.util.*

// Camera Presets for Safari Gallery Simulation
data class CameraPreset(
    val id: String,
    val name: String,
    val icon: String,
    val primaryColor: Color,
    val secondaryColor: Color,
    val hexPrimary: Int,
    val hexSecondary: Int
)

val SAVANNA_PRESETS = listOf(
    CameraPreset("lion", "Lion Pride", "🦁", Color(0xFFFFB74D), Color(0xFFF57C00), 0xFFFFB74D.toInt(), 0xFFF57C00.toInt()),
    CameraPreset("elephant", "Elephant Herd", "🐘", Color(0xFF90A4AE), Color(0xFF37474F), 0xFF90A4AE.toInt(), 0xFF37474F.toInt()),
    CameraPreset("leopard", "Acacia Leopard", "🐆", Color(0xFFFFD54F), Color(0xFFFF8F00), 0xFFFFD54F.toInt(), 0xFFFF8F00.toInt()),
    CameraPreset("sunset", "Serengeti Sunset", "🌅", Color(0xFFBA68C8), Color(0xFFE91E63), 0xFFBA68C8.toInt(), 0xFFE91E63.toInt()),
    CameraPreset("giraffe", "Tall Giraffe", "🦒", Color(0xFFFFF176), Color(0xFFFBC02D), 0xFFFFF176.toInt(), 0xFFFBC02D.toInt()),
    CameraPreset("zebra", "Plain Zebra", "🦓", Color(0xFFE0E0E0), Color(0xFF212121), 0xFFE0E0E0.toInt(), 0xFF212121.toInt())
)

// Dynamic Bitmap generator to simulate offline camera capturing
fun generateLowResPhoto(animal: String, primaryColor: Int, secondaryColor: Int): String {
    val size = 120
    val bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
    val canvas = android.graphics.Canvas(bitmap)
    val paint = android.graphics.Paint()
    
    // Draw linear gradient background
    val shader = android.graphics.LinearGradient(
        0f, 0f, size.toFloat(), size.toFloat(),
        primaryColor, secondaryColor,
        android.graphics.Shader.TileMode.CLAMP
    )
    paint.shader = shader
    canvas.drawRect(0f, 0f, size.toFloat(), size.toFloat(), paint)
    
    // Draw decorative light background ring
    paint.shader = null
    paint.color = android.graphics.Color.WHITE
    paint.alpha = 45
    canvas.drawCircle(size / 2f, size / 2f, size / 3f, paint)
    
    // Draw animal emoji text
    paint.alpha = 255
    paint.textSize = 42f
    paint.textAlign = android.graphics.Paint.Align.CENTER
    val fontMetrics = paint.fontMetrics
    val y = (size / 2f) - (fontMetrics.ascent + fontMetrics.descent) / 2f
    canvas.drawText(animal, size / 2f, y, paint)
    
    // Compress and encode to Base64
    val outputStream = ByteArrayOutputStream()
    bitmap.compress(Bitmap.CompressFormat.JPEG, 70, outputStream)
    val bytes = outputStream.toByteArray()
    return Base64.encodeToString(bytes, Base64.DEFAULT)
}

@Composable
fun rememberBase64Image(base64Str: String?): ImageBitmap? {
    if (base64Str == null) return null
    return remember(base64Str) {
        try {
            val bytes = Base64.decode(base64Str, Base64.DEFAULT)
            val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
            bitmap?.asImageBitmap()
        } catch (e: Exception) {
            null
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JournalScreen(viewModel: JournalViewModel = viewModel()) {
    val entries by viewModel.entries.collectAsStateWithLifecycle(initialValue = emptyList())
    val isOnline by viewModel.isOnline.collectAsStateWithLifecycle()
    val isSyncing by viewModel.isSyncing.collectAsStateWithLifecycle()
    val syncProgress by viewModel.syncProgress.collectAsStateWithLifecycle()

    var noteText by remember { mutableStateOf("") }
    var locationText by remember { mutableStateOf("Maasai Mara National Reserve") }
    
    // Selected camera preset
    var selectedPreset by remember { mutableStateOf<CameraPreset?>(SAVANNA_PRESETS[0]) }
    var showPreviewPhoto by remember { mutableStateOf(true) }

    // Filter status for gallery
    var galleryFilter by remember { mutableStateOf("ALL") } // "ALL", "UNSYNCED", "SYNCED"

    val filteredEntries = remember(entries, galleryFilter) {
        when (galleryFilter) {
            "UNSYNCED" -> entries.filter { !it.isSynced }
            "SYNCED" -> entries.filter { it.isSynced }
            else -> entries
        }
    }

    // Dynamic live preview photo Base64
    val previewBase64 = remember(selectedPreset) {
        selectedPreset?.let {
            generateLowResPhoto(it.icon, it.hexPrimary, it.hexSecondary)
        }
    }

    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        contentPadding = PaddingValues(16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier
            .fillMaxSize()
            .testTag("safari_journal_gallery_grid")
    ) {
        // --- 1. SATELLITE TRANSCEIVER & TELEMETRY CARD (Full Width) ---
        item(span = { GridItemSpan(2) }) {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.15f)),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("satellite_sync_control_card")
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    // Header Status
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = if (isOnline) Color(0xFFE8F5E9) else Color(0xFFFFEBEE),
                            modifier = Modifier.size(36.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = if (isOnline) Icons.Default.CloudDone else Icons.Default.CloudQueue,
                                    contentDescription = "Connection Status",
                                    tint = if (isOnline) Color(0xFF2E7D32) else Color(0xFFC62828),
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Satellite Cloud Transceiver",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = if (isOnline) "Terminal ONLINE • Direct Cloud Upload" else "Terminal OFFLINE • Queued in Room Database",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = if (isOnline) Color(0xFF2E7D32) else Color(0xFFC62828)
                            )
                        }
                        
                        // Online / Offline Toggle Switch
                        Button(
                            onClick = { viewModel.toggleOnlineStatus() },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isOnline) MaterialTheme.colorScheme.errorContainer else MaterialTheme.colorScheme.secondaryContainer,
                                contentColor = if (isOnline) MaterialTheme.colorScheme.onErrorContainer else MaterialTheme.colorScheme.onSecondaryContainer
                            ),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .height(32.dp)
                                .testTag("toggle_satellite_online_btn")
                        ) {
                            Text(
                                text = if (isOnline) "Go Offline" else "Go Online",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Queued stats display
                    val totalQueuedCount = entries.count { !it.isSynced }
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                color = MaterialTheme.colorScheme.background.copy(alpha = 0.5f),
                                shape = RoundedCornerShape(12.dp)
                            )
                            .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.08f), RoundedCornerShape(12.dp))
                            .padding(10.dp)
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "IndexedDB Queue Status",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = if (totalQueuedCount > 0) "$totalQueuedCount Photo Journal entries waiting for sync..." else "All photos successfully synced to Savannah Cloud Storage",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        
                        // Cloud Sync Button
                        Button(
                            onClick = { viewModel.syncUnsyncedPhotos() },
                            enabled = !isSyncing,
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primary,
                                contentColor = MaterialTheme.colorScheme.onPrimary
                            ),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                            modifier = Modifier
                                .height(36.dp)
                                .testTag("sync_queue_btn")
                        ) {
                            if (isSyncing) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(14.dp),
                                    strokeWidth = 2.dp,
                                    color = MaterialTheme.colorScheme.onPrimary
                                )
                            } else {
                                Icon(
                                    imageVector = Icons.Default.Sync,
                                    contentDescription = "Sync Now",
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Sync Now", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    // Background sync status trace log
                    syncProgress?.let { progress ->
                        Spacer(modifier = Modifier.height(12.dp))
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.1f)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.Timeline,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(12.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "Satellite Terminal Trace Log",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                    Spacer(modifier = Modifier.weight(1f))
                                    IconButton(
                                        onClick = { viewModel.clearSyncProgress() },
                                        modifier = Modifier.size(16.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Close,
                                            contentDescription = "Clear",
                                            modifier = Modifier.size(12.dp),
                                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = progress,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }
        }

        // --- 2. CAMERA SNAPSHOT CAPTURE PORTAL (Full Width) ---
        item(span = { GridItemSpan(2) }) {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.15f)),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("camera_capture_portal_card")
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    // Title and description
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Camera,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Safari Camera Snapshot",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                    Text(
                        text = "Point camera, choose savanna wildlife subject, add notes, and save.",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )

                    // Capture interface Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Left: Presets Selection
                        Column(
                            modifier = Modifier.weight(1f),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                text = "Camera Targets",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            
                            // Horizontal preset selector flow
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .horizontalScroll(rememberScrollState()),
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                SAVANNA_PRESETS.forEach { preset ->
                                    val isSelected = selectedPreset?.id == preset.id
                                    Surface(
                                        shape = RoundedCornerShape(10.dp),
                                        color = if (isSelected) preset.primaryColor.copy(alpha = 0.25f) else MaterialTheme.colorScheme.background,
                                        border = BorderStroke(
                                            width = if (isSelected) 1.5.dp else 1.dp,
                                            color = if (isSelected) preset.primaryColor else MaterialTheme.colorScheme.outline.copy(alpha = 0.12f)
                                        ),
                                        modifier = Modifier
                                            .clickable { selectedPreset = preset }
                                            .padding(vertical = 2.dp)
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(preset.icon, fontSize = 14.sp)
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text(
                                                text = preset.name,
                                                fontSize = 11.sp,
                                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                                color = if (isSelected) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(4.dp))

                            // Note Field
                            OutlinedTextField(
                                value = noteText,
                                onValueChange = { noteText = it },
                                placeholder = { Text("Describe your wildlife capture...", fontSize = 11.sp) },
                                label = { Text("Wildlife Notes", fontSize = 10.sp, fontWeight = FontWeight.Bold) },
                                shape = RoundedCornerShape(12.dp),
                                maxLines = 2,
                                textStyle = LocalTextStyle.current.copy(fontSize = 12.sp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("gallery_note_field")
                            )

                            // Location Field
                            OutlinedTextField(
                                value = locationText,
                                onValueChange = { locationText = it },
                                placeholder = { Text("Location context...", fontSize = 11.sp) },
                                label = { Text("Sighting Location", fontSize = 10.sp, fontWeight = FontWeight.Bold) },
                                shape = RoundedCornerShape(12.dp),
                                singleLine = true,
                                textStyle = LocalTextStyle.current.copy(fontSize = 11.sp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("gallery_location_field")
                            )
                        }

                        // Right: Simulated Camera Live Frame / Capture Overlay
                        selectedPreset?.let { preset ->
                            Box(
                                modifier = Modifier
                                    .size(110.dp)
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(
                                        brush = Brush.verticalGradient(
                                            colors = listOf(preset.primaryColor, preset.secondaryColor)
                                        )
                                    )
                                    .border(2.dp, Color.White, RoundedCornerShape(16.dp))
                                    .testTag("simulated_camera_lens")
                            ) {
                                // Dynamic internal circle background
                                Box(
                                    modifier = Modifier
                                        .size(64.dp)
                                        .clip(CircleShape)
                                        .background(Color.White.copy(alpha = 0.2f))
                                        .align(Alignment.Center)
                                )
                                // Emoji
                                Text(
                                    text = preset.icon,
                                    fontSize = 44.sp,
                                    modifier = Modifier.align(Alignment.Center)
                                )
                                // Camera lens overlay
                                Canvas(modifier = Modifier.fillMaxSize()) {
                                    // Draw subtle camera reticle corners
                                    val strokeW = 2.dp.toPx()
                                    val len = 12.dp.toPx()
                                    val size = size.width
                                    val c = Color.White.copy(alpha = 0.6f)
                                    
                                    // Top-Left
                                    drawRect(c, topLeft = androidx.compose.ui.geometry.Offset(4.dp.toPx(), 4.dp.toPx()), size = androidx.compose.ui.geometry.Size(len, strokeW))
                                    drawRect(c, topLeft = androidx.compose.ui.geometry.Offset(4.dp.toPx(), 4.dp.toPx()), size = androidx.compose.ui.geometry.Size(strokeW, len))
                                    
                                    // Top-Right
                                    drawRect(c, topLeft = androidx.compose.ui.geometry.Offset(size - len - 4.dp.toPx(), 4.dp.toPx()), size = androidx.compose.ui.geometry.Size(len, strokeW))
                                    drawRect(c, topLeft = androidx.compose.ui.geometry.Offset(size - strokeW - 4.dp.toPx(), 4.dp.toPx()), size = androidx.compose.ui.geometry.Size(strokeW, len))
                                    
                                    // Bottom-Left
                                    drawRect(c, topLeft = androidx.compose.ui.geometry.Offset(4.dp.toPx(), size - strokeW - 4.dp.toPx()), size = androidx.compose.ui.geometry.Size(len, strokeW))
                                    drawRect(c, topLeft = androidx.compose.ui.geometry.Offset(4.dp.toPx(), size - len - 4.dp.toPx()), size = androidx.compose.ui.geometry.Size(strokeW, len))
                                    
                                    // Bottom-Right
                                    drawRect(c, topLeft = androidx.compose.ui.geometry.Offset(size - len - 4.dp.toPx(), size - strokeW - 4.dp.toPx()), size = androidx.compose.ui.geometry.Size(len, strokeW))
                                    drawRect(c, topLeft = androidx.compose.ui.geometry.Offset(size - strokeW - 4.dp.toPx(), size - len - 4.dp.toPx()), size = androidx.compose.ui.geometry.Size(strokeW, len))
                                }

                                Surface(
                                    color = Color.Black.copy(alpha = 0.6f),
                                    shape = RoundedCornerShape(topStart = 8.dp),
                                    modifier = Modifier.align(Alignment.BottomEnd)
                                ) {
                                    Text(
                                        text = "120p Low-res",
                                        color = Color.White,
                                        fontSize = 8.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // ACTION BUTTON: CAPTURE AND CACHE OFFLINE
                    Button(
                        onClick = {
                            if (noteText.isBlank()) {
                                noteText = "Sighted a gorgeous ${selectedPreset?.name ?: "Wildlife species"} in its pristine natural habitat!"
                            }
                            viewModel.addNoteWithPhoto(noteText, locationText, previewBase64)
                            noteText = ""
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = MaterialTheme.colorScheme.onPrimary
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(44.dp)
                            .testTag("capture_photo_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Camera,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (isOnline) "Capture & Sync Instantly" else "Capture & Store Offline-First (Room SQLite)",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        // --- 3. PHOTO GALLERY CONTROLS / FILTER LINE ---
        item(span = { GridItemSpan(2) }) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp, bottom = 4.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Collections,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Safari Photo Journal",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.weight(1f)
                )

                // Filter Buttons
                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    val filters = listOf("ALL", "UNSYNCED", "SYNCED")
                    filters.forEach { filter ->
                        val isSelected = galleryFilter == filter
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                            modifier = Modifier
                                .clickable { galleryFilter = filter }
                                .height(26.dp)
                        ) {
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier.padding(horizontal = 8.dp)
                            ) {
                                Text(
                                    text = filter,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }
        }

        // --- EMPTY STATE GALLERY IF EMPTY ---
        if (filteredEntries.isEmpty()) {
            item(span = { GridItemSpan(2) }) {
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = MaterialTheme.colorScheme.surface.copy(alpha = 0.5f),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.08f)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp)
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(24.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Pets,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f),
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "No Gallery Journal Entries Found",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = when (galleryFilter) {
                                "UNSYNCED" -> "All local captures have been successfully pushed to the satellite cloud server."
                                "SYNCED" -> "No synced captures found. Run the transceiver to synchronize your items."
                                else -> "Your safari gallery is empty. Pick a camera target above and snap a beautiful wildlife sighting!"
                            },
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }
            }
        }

        // --- 4. THE PHOTO JOURNAL TILES GRID (2 Column Grid Items) ---
        items(filteredEntries, key = { it.id }) { entry ->
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.1f)),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("gallery_item_card_${entry.id}")
            ) {
                Column {
                    // Photo Banner
                    val imageBitmap = rememberBase64Image(entry.imageBase64)
                    if (imageBitmap != null) {
                        Image(
                            bitmap = imageBitmap,
                            contentDescription = entry.note,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(100.dp)
                                .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
                        )
                    } else {
                        // Default solid background
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(100.dp)
                                .background(
                                    brush = Brush.verticalGradient(
                                        colors = listOf(Color(0xFFE0F7FA), Color(0xFF80DEEA))
                                    )
                                )
                        ) {
                            Icon(
                                imageVector = Icons.Default.Landscape,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier
                                    .size(36.dp)
                                    .align(Alignment.Center)
                            )
                        }
                    }

                    // Content details
                    Column(modifier = Modifier.padding(10.dp)) {
                        // Sync Badge
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = if (entry.isSynced) Color(0xFFE8F5E9) else Color(0xFFFFF8E1),
                                modifier = Modifier.testTag("sync_badge_${entry.id}")
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(6.dp)
                                            .clip(CircleShape)
                                            .background(if (entry.isSynced) Color(0xFF2E7D32) else Color(0xFFFFB300))
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = if (entry.isSynced) "Synced" else "Offline Queue",
                                        fontSize = 8.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (entry.isSynced) Color(0xFF2E7D32) else Color(0xFFB78103)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.weight(1f))
                            
                            // Delete button
                            IconButton(
                                onClick = { viewModel.deleteEntry(entry) },
                                modifier = Modifier
                                    .size(24.dp)
                                    .testTag("delete_gallery_entry_${entry.id}")
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.DeleteOutline,
                                    contentDescription = "Delete entry",
                                    tint = MaterialTheme.colorScheme.error,
                                    modifier = Modifier.size(14.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        // Notes text
                        Text(
                            text = entry.note,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSurface,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        // Location
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Place,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(10.dp)
                            )
                            Spacer(modifier = Modifier.width(2.dp))
                            Text(
                                text = entry.location,
                                fontSize = 9.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier.weight(1f)
                            )
                        }

                        // Timestamp
                        val sdf = remember { SimpleDateFormat("HH:mm, dd MMM yyyy", Locale.getDefault()) }
                        Text(
                            text = sdf.format(Date(entry.timestamp)),
                            fontSize = 8.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }
            }
        }
    }
}
