package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.graphics.Color
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme =
  darkColorScheme(
    primary = GoldSunburstPrimaryDark,
    secondary = PaleOliveSecondaryDark,
    tertiary = PaleSandTertiaryDark,
    background = SafariNightBackground,
    surface = SafariCanopySurface,
    onPrimary = Color(0xFF1B120C),
    onSecondary = Color(0xFF1B120C),
    onBackground = Color(0xFFEFE6DD),
    onSurface = Color(0xFFEFE6DD)
  )

private val LightColorScheme =
  lightColorScheme(
    primary = TerracottaPrimary,
    secondary = OliveSecondary,
    tertiary = OchreTertiary,
    background = SoftSandBackground,
    surface = WarmCreamSurface,
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = Color(0xFF2C1B10),
    onSurface = Color(0xFF2C1B10)
  )

@Composable
fun MyApplicationTheme(
  darkTheme: Boolean = true,
  // Dynamic color is available on Android 12+
  dynamicColor: Boolean = false,
  content: @Composable () -> Unit,
) {
  val colorScheme =
    when {
      dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
        val context = LocalContext.current
        if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
      }

      darkTheme -> DarkColorScheme
      else -> LightColorScheme
    }

  MaterialTheme(colorScheme = colorScheme, typography = Typography, content = content)
}
