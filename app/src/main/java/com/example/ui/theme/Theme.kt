package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp

private val DarkColorScheme =
  darkColorScheme(
    primary = DarkCoral,
    onPrimary = OnDarkCoral,
    primaryContainer = DarkCoralContainer,
    onPrimaryContainer = OnDarkCoralContainer,
    secondary = DarkJade,
    onSecondary = OnDarkJade,
    secondaryContainer = DarkJadeContainer,
    onSecondaryContainer = OnDarkJadeContainer,
    tertiary = DarkGold,
    onTertiary = OnDarkGold,
    tertiaryContainer = DarkGoldContainer,
    onTertiaryContainer = OnDarkGoldContainer,
    background = CharcoalInkBg,
    onBackground = SoftCreamText,
    surface = CharcoalInkSurface,
    onSurface = SoftCreamText,
    surfaceVariant = CharcoalCard,
    onSurfaceVariant = MutedSandText,
    surfaceContainerLow = CharcoalInkSurface,
    surfaceContainer = CharcoalCard,
    surfaceContainerHigh = CharcoalCardHigh,
    outline = DarkWarmOutline,
  )

private val LightColorScheme =
  lightColorScheme(
    primary = CinnabarRed,
    onPrimary = OnCinnabarRed,
    primaryContainer = CinnabarContainer,
    onPrimaryContainer = OnCinnabarContainer,
    secondary = JadeGreen,
    onSecondary = OnJadeGreen,
    secondaryContainer = JadeContainer,
    onSecondaryContainer = OnJadeContainer,
    tertiary = ImperialGold,
    onTertiary = OnImperialGold,
    tertiaryContainer = ImperialGoldContainer,
    onTertiaryContainer = OnImperialGoldContainer,
    background = RicePaperCream,
    onBackground = DeepInkCharcoal,
    surface = WarmIvorySurface,
    onSurface = DeepInkCharcoal,
    surfaceVariant = WarmSandContainer,
    onSurfaceVariant = MutedInkGray,
    surfaceContainerLow = WarmIvorySurface,
    surfaceContainer = WarmSandContainer,
    surfaceContainerHigh = WarmSandHigh,
    outline = WarmOutline,
  )

val AppShapes =
  Shapes(
    small = RoundedCornerShape(10.dp),
    medium = RoundedCornerShape(16.dp),
    large = RoundedCornerShape(24.dp),
    extraLarge = RoundedCornerShape(28.dp),
  )

@Composable
fun NihaoTalkTheme(
  darkTheme: Boolean = isSystemInDarkTheme(),
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

  MaterialTheme(
    colorScheme = colorScheme,
    typography = Typography,
    shapes = AppShapes,
    content = content,
  )
}
