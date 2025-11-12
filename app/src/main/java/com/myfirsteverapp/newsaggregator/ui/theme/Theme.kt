package com.myfirsteverapp.newsaggregator.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColors: ColorScheme = lightColorScheme(
    primary = CyanBlue,
    onPrimary = DeepNavy,
    primaryContainer = ElectricBlue,
    onPrimaryContainer = DeepNavy,
    secondary = ElectricBlue,
    onSecondary = DeepNavy,
    secondaryContainer = ElectricBlue.copy(alpha = 0.15f),
    onSecondaryContainer = DeepNavy,
    tertiary = PurpleMagenta,
    onTertiary = Color.White,
    tertiaryContainer = PurpleMagenta.copy(alpha = 0.15f),
    onTertiaryContainer = PurpleMagenta,
    background = Color(0xFFF3FAFD),
    onBackground = OnLightHighEmphasis,
    surface = Color(0xFFFFFFFF),
    onSurface = OnLightHighEmphasis,
    surfaceVariant = LightSurfaceVariant,
    onSurfaceVariant = MutedGray,
    outline = MutedGray,
    error = ErrorRed,
    onError = Color.White,
    inversePrimary = ElectricBlue
)

private val DarkColors: ColorScheme = darkColorScheme(
    primary = CyanBlue,
    onPrimary = DeepNavy,
    primaryContainer = ElectricBlue,
    onPrimaryContainer = DeepNavy,
    secondary = ElectricBlue,
    onSecondary = DeepNavy,
    secondaryContainer = ElectricBlue.copy(alpha = 0.25f),
    onSecondaryContainer = Color.White,
    tertiary = PurpleMagenta,
    onTertiary = Color.White,
    tertiaryContainer = PurpleMagenta.copy(alpha = 0.25f),
    onTertiaryContainer = Color.White,
    background = DeepNavy,
    onBackground = OnDarkHighEmphasis,
    surface = DarkBlue,
    onSurface = OnDarkHighEmphasis,
    surfaceVariant = DarkSurfaceVariant,
    onSurfaceVariant = MutedGray,
    outline = MutedGray,
    error = ErrorRed,
    onError = Color.White,
    inversePrimary = ElectricBlue
)

@Composable
fun WeflutLiveTheme(
    useDarkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colors = if (useDarkTheme) DarkColors else LightColors
    MaterialTheme(
        colorScheme = colors,
        typography = Typography(),
        content = content
    )
}
