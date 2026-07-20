package com.example.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.Alignment
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Share
import com.example.data.StayItem

@Composable
fun StaysTab(
    stays: List<StayItem>,
    favorites: List<String>,
    onStayClick: (StayItem) -> Unit,
    onBookClick: (StayItem) -> Unit,
    onFavoriteClick: (StayItem) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "${stays.size} Results",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        TextButton(onClick = { }) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text("Sort by")
                                Icon(Icons.Default.ArrowDropDown, contentDescription = null)
                            }
                        }
                        IconButton(onClick = { }) {
                            Icon(Icons.Default.Share, contentDescription = "Share")
                        }
                    }
                }
                Divider(modifier = Modifier.padding(top = 8.dp), color = MaterialTheme.colorScheme.outline.copy(alpha = 0.1f))
            }
        }
        
        items(stays) { stay ->
            Box(modifier = Modifier.padding(horizontal = 16.dp)) {
                StayCard(
                    stay = stay,
                    isFavorite = favorites.contains(stay.id),
                    onStayClick = { onStayClick(stay) },
                    onBookClick = { onBookClick(stay) },
                    onFavoriteClick = { onFavoriteClick(stay) }
                )
            }
        }
    }
}
