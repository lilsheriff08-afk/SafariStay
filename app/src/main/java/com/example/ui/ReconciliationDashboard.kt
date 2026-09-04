package com.example.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.WarningAmber
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.filled.Close
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.viewmodel.SafariViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InternalFinanceDashboard(
    viewModel: SafariViewModel,
    onDismiss: () -> Unit
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            Scaffold(
                topBar = {
                    TopAppBar(
                        title = { Text("INTERNAL FINANCE & RECONCILIATION", fontSize = 14.sp, fontWeight = FontWeight.Bold) },
                        navigationIcon = {
                            IconButton(onClick = onDismiss) {
                                Icon(Icons.Default.Close, contentDescription = "Close")
                            }
                        }
                    )
                }
            ) { padding ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                        .padding(16.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    ReconciliationDashboardCard()
                }
            }
        }
    }
}

@Composable
fun PrivacyConsentDialog(onAccept: () -> Unit) {
    AlertDialog(
        onDismissRequest = { },
        title = { Text("Privacy & Escrow Terms", fontWeight = FontWeight.Bold, fontSize = 16.sp) },
        text = {
            Text(
                "Welcome to Safari Expeditions. We collect booking information to deliver instant reservations, verified M-Pesa payments, and protected partner commission escrow. By continuing, you agree to our Terms of Service & Privacy Policy.",
                fontSize = 12.sp,
                lineHeight = 16.sp
            )
        },
        confirmButton = {
            Button(onClick = onAccept) {
                Text("Accept & Continue")
            }
        }
    )
}

@Composable
fun ReconciliationDashboardCard() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "NIGHTLY LEDGER RECONCILIATION",
                fontSize = 12.sp,
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            // Recharts-style Data Visualization
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Pie Chart
                Box(modifier = Modifier.size(120.dp), contentAlignment = Alignment.Center) {
                    Canvas(modifier = Modifier.size(100.dp)) {
                        // Background circle (total)
                        drawArc(
                            color = Color(0xFFEEEEEE),
                            startAngle = 0f,
                            sweepAngle = 360f,
                            useCenter = false,
                            style = Stroke(width = 30f, cap = StrokeCap.Round)
                        )
                        // Dispatched (Success)
                        drawArc(
                            color = Color(0xFF2E7D32),
                            startAngle = -90f,
                            sweepAngle = 270f, // 75%
                            useCenter = false,
                            style = Stroke(width = 30f, cap = StrokeCap.Round)
                        )
                        // Stuck / Unresolved
                        drawArc(
                            color = Color(0xFFD32F2F),
                            startAngle = 180f,
                            sweepAngle = 45f, // 12.5%
                            useCenter = false,
                            style = Stroke(width = 30f, cap = StrokeCap.Round)
                        )
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("24h", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        Text("Audit", fontSize = 10.sp, color = Color.Gray)
                    }
                }

                Spacer(modifier = Modifier.width(24.dp))

                // Legend and Stats
                Column(modifier = Modifier.weight(1f)) {
                    LegendItem("Gross Escrow Held", "KES 4,500,000", Color(0xFF1976D2))
                    Spacer(modifier = Modifier.height(8.dp))
                    LegendItem("Dispatched", "KES 1,200,000", Color(0xFF2E7D32))
                    Spacer(modifier = Modifier.height(8.dp))
                    LegendItem("Unresolved Payouts", "KES 150,000", Color(0xFFD32F2F))
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Discrepancy Alerts
            Surface(
                color = Color(0xFFFFEBEE),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.WarningAmber,
                        contentDescription = "Alert",
                        tint = Color(0xFFD32F2F),
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "3 Unresolved Payouts Detected",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFB71C1C)
                        )
                        Text(
                            text = "M-Pesa Gateway callbacks pending. Manual review required for transaction batch.",
                            fontSize = 11.sp,
                            color = Color(0xFFC62828),
                            lineHeight = 14.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun LegendItem(label: String, value: String, color: Color) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(10.dp)
                .background(color, RoundedCornerShape(2.dp))
        )
        Spacer(modifier = Modifier.width(8.dp))
        Column {
            Text(label, fontSize = 10.sp, color = Color.Gray)
            Text(value, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.DarkGray)
        }
    }
}
