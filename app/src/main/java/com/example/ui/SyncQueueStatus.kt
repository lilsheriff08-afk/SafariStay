package com.example.ui

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.CloudOff
import androidx.compose.material.icons.filled.Sms
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material.icons.filled.WifiOff
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.viewmodel.SafariViewModel

@Composable
fun SyncQueueStatus(viewModel: SafariViewModel) {
    val isOnline by viewModel.isOnline.collectAsState()
    val pendingSyncCount by viewModel.pendingSyncCount.collectAsState()
    val isSyncing by viewModel.isSyncing.collectAsState()

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .padding(horizontal = 4.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(
                if (isOnline) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
                else MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.5f)
            )
            .clickable { viewModel.toggleOnline() }
            .padding(horizontal = 8.dp, vertical = 4.dp)
            .testTag("sync_status_indicator")
    ) {
        Icon(
            imageVector = if (isOnline) Icons.Default.Wifi else Icons.Default.WifiOff,
            contentDescription = if (isOnline) "Online" else "Offline",
            modifier = Modifier.size(16.dp),
            tint = if (isOnline) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
        )
        
        Spacer(modifier = Modifier.width(4.dp))
        
        Text(
            text = if (isOnline) "Online" else "Offline",
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            color = if (isOnline) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onErrorContainer
        )

        if (!isOnline) {
            Spacer(modifier = Modifier.width(6.dp))
            Icon(
                imageVector = Icons.Default.Sms,
                contentDescription = "SMS Fallback Active",
                modifier = Modifier.size(12.dp),
                tint = MaterialTheme.colorScheme.error.copy(alpha = 0.7f)
            )
        }

        AnimatedVisibility(
            visible = pendingSyncCount > 0,
            enter = fadeIn() + expandHorizontally(),
            exit = fadeOut() + shrinkHorizontally()
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Spacer(modifier = Modifier.width(8.dp))
                VerticalDivider(
                    modifier = Modifier.height(12.dp),
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f)
                )
                Spacer(modifier = Modifier.width(8.dp))
                
                Icon(
                    imageVector = if (isSyncing) Icons.Default.Sync else Icons.Default.CloudOff,
                    contentDescription = "Pending Sync",
                    modifier = Modifier.size(14.dp),
                    tint = MaterialTheme.colorScheme.secondary
                )
                
                Spacer(modifier = Modifier.width(4.dp))
                
                Text(
                    text = "$pendingSyncCount pending",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        
        if (isOnline && pendingSyncCount == 0) {
            Spacer(modifier = Modifier.width(4.dp))
            Icon(
                imageVector = Icons.Default.CloudDone,
                contentDescription = "Synced",
                modifier = Modifier.size(14.dp),
                tint = Color(0xFF4CAF50).copy(alpha = 0.7f)
            )
        }
    }
}
