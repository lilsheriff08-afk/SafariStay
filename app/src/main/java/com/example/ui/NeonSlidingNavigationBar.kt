package com.example.ui

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

data class NeonNavItem(
    val id: String,
    val label: String,
    val icon: ImageVector,
    val neonColor: Color,
    val testTag: String
)

@Composable
fun NeonSlidingNavigationBar(
    currentTab: String,
    onTabSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val navItems = remember {
        listOf(
            NeonNavItem("stays", "HOME", Icons.Default.Home, Color(0xFFFFD700), "tab_stays"),
            NeonNavItem("safaris", "SAFARIS", Icons.Default.Explore, Color(0xFF00F0FF), "tab_safaris"),
            NeonNavItem("events", "EVENTS", Icons.Default.Event, Color(0xFFFF00E5), "tab_events"),
            NeonNavItem("wildlife", "WILDLIFE", Icons.Default.Pets, Color(0xFF00FF88), "tab_wildlife"),
            NeonNavItem("vouchers", "VOUCHERS", Icons.Default.CardMembership, Color(0xFFFF9900), "tab_vouchers"),
            NeonNavItem("bookings", "TRIPS", Icons.Default.BookOnline, Color(0xFF7000FF), "tab_bookings")
        )
    }

    val selectedIndex = navItems.indexOfFirst { it.id == currentTab }.coerceAtLeast(0)
    val activeNeonColor = navItems[selectedIndex].neonColor

    // Smooth sliding animation for indicator index
    val animatedIndex by animateFloatAsState(
        targetValue = selectedIndex.toFloat(),
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioLowBouncy,
            stiffness = Spring.StiffnessMediumLow
        ),
        label = "slidingIndex"
    )

    // Pulsating neon glow intensity animation
    val infiniteTransition = rememberInfiniteTransition(label = "neonPulse")
    val pulseGlowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.45f,
        targetValue = 0.85f,
        animationSpec = infiniteRepeatable(
            animation = tween(1400, easing = LinearOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseAlpha"
    )

    val density = LocalDensity.current

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .drawBehind {
                // Outer top ambient neon border shimmer line
                drawLine(
                    brush = Brush.horizontalGradient(
                        colors = listOf(
                            Color.Transparent,
                            activeNeonColor.copy(alpha = 0.8f),
                            Color(0xFF00F0FF).copy(alpha = 0.8f),
                            activeNeonColor.copy(alpha = 0.8f),
                            Color.Transparent
                        )
                    ),
                    start = Offset(0f, 0f),
                    end = Offset(this.size.width, 0f),
                    strokeWidth = with(density) { 2.dp.toPx() }
                )
            },
        color = Color(0xFF0B0F17), // Ultra dark cyber canvas background
        tonalElevation = 12.dp
    ) {
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 6.dp, vertical = 8.dp)
        ) {
            val totalWidth = maxWidth
            val itemWidth = totalWidth / navItems.size

            // --- 1. SLIDING NEON INDICATOR PILL ---
            val indicatorOffset = itemWidth * animatedIndex

            Box(
                modifier = Modifier
                    .offset(x = indicatorOffset)
                    .width(itemWidth)
                    .height(54.dp)
                    .padding(horizontal = 4.dp),
                contentAlignment = Alignment.Center
            ) {
                // Background Layer 1: Outer Neon Halo Glow Canvas
                Canvas(
                    modifier = Modifier.fillMaxSize()
                ) {
                    val canvasWidth = this.size.width
                    val canvasHeight = this.size.height
                    val cornerRadiusPx = with(density) { 18.dp.toPx() }

                    // Outer diffuse neon aura
                    drawRoundRect(
                        color = activeNeonColor.copy(alpha = pulseGlowAlpha * 0.35f),
                        topLeft = Offset(-8f, -4f),
                        size = Size(canvasWidth + 16f, canvasHeight + 8f),
                        cornerRadius = CornerRadius(with(density) { 24.dp.toPx() }, with(density) { 24.dp.toPx() })
                    )

                    // Crisp inner neon glow core
                    drawRoundRect(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                activeNeonColor.copy(alpha = pulseGlowAlpha * 0.5f),
                                activeNeonColor.copy(alpha = 0.15f),
                                Color.Transparent
                            ),
                            center = Offset(canvasWidth / 2, canvasHeight / 2),
                            radius = canvasWidth.coerceAtLeast(canvasHeight)
                        ),
                        cornerRadius = CornerRadius(cornerRadiusPx, cornerRadiusPx)
                    )

                    // Bottom sliding neon beam line
                    val lineMargin = with(density) { 6.dp.toPx() }
                    val lineY = canvasHeight - with(density) { 2.dp.toPx() }
                    drawLine(
                        brush = Brush.horizontalGradient(
                            colors = listOf(
                                Color.Transparent,
                                activeNeonColor,
                                Color.White,
                                activeNeonColor,
                                Color.Transparent
                            )
                        ),
                        start = Offset(lineMargin, lineY),
                        end = Offset(canvasWidth - lineMargin, lineY),
                        strokeWidth = with(density) { 3.dp.toPx() }
                    )
                }

                // Foreground Layer 2: Styled Capsule Pill
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(16.dp))
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    activeNeonColor.copy(alpha = 0.22f),
                                    Color(0xFF161C26).copy(alpha = 0.85f)
                                )
                            )
                        )
                        .border(
                            width = 1.5.dp,
                            brush = Brush.verticalGradient(
                                colors = listOf(
                                    activeNeonColor.copy(alpha = 0.9f),
                                    activeNeonColor.copy(alpha = 0.3f)
                                )
                            ),
                            shape = RoundedCornerShape(16.dp)
                        )
                )
            }

            // --- 2. TABS ROW ---
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                navItems.forEachIndexed { index, item ->
                    val isSelected = index == selectedIndex

                    // Animated scale on tab selection
                    val tabScale by animateFloatAsState(
                        targetValue = if (isSelected) 1.08f else 0.95f,
                        animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
                        label = "tabScale"
                    )

                    val interactionSource = remember { MutableInteractionSource() }

                    Box(
                        modifier = Modifier
                            .width(itemWidth)
                            .height(54.dp)
                            .clickable(
                                interactionSource = interactionSource,
                                indication = null
                            ) {
                                onTabSelected(item.id)
                            }
                            .testTag(item.testTag),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center,
                            modifier = Modifier.graphicsLayer {
                                scaleX = tabScale
                                scaleY = tabScale
                            }
                        ) {
                            // Icon with ambient neon shadow if selected
                            Box(contentAlignment = Alignment.Center) {
                                if (isSelected) {
                                    Icon(
                                        imageVector = item.icon,
                                        contentDescription = null,
                                        tint = item.neonColor.copy(alpha = pulseGlowAlpha * 0.7f),
                                        modifier = Modifier
                                            .size(24.dp)
                                            .graphicsLayer {
                                                scaleX = 1.25f
                                                scaleY = 1.25f
                                                alpha = 0.6f
                                            }
                                    )
                                }

                                Icon(
                                    imageVector = item.icon,
                                    contentDescription = item.label,
                                    tint = if (isSelected) item.neonColor else Color.LightGray.copy(alpha = 0.7f),
                                    modifier = Modifier.size(20.dp)
                                )
                            }

                            Spacer(modifier = Modifier.height(3.dp))

                            Text(
                                text = item.label,
                                fontSize = 9.sp,
                                fontWeight = if (isSelected) FontWeight.Black else FontWeight.Bold,
                                fontFamily = FontFamily.Monospace,
                                letterSpacing = 0.5.sp,
                                color = if (isSelected) Color.White else Color.Gray
                            )
                        }
                    }
                }
            }
        }
    }
}
