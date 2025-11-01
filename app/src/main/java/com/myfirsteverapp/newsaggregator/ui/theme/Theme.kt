package com.myfirsteverapp.newsaggregator.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

// Your color tokens from spec
val PrimaryColor = Color(0xFF1F6FEB)
val AccentColor = Color(0xFFFF6B6B)
val SurfaceLight = Color(0xFFFFFFFF)
val SurfaceDark = Color(0xFF0F1720)
val TextPrimary = Color(0xFF0B1220)
val TextSecondary = Color(0xFF546070)

private val LightColorScheme = lightColorScheme(
    primary = PrimaryColor,
//    primaryVariant = PrimaryColor,
    secondary = AccentColor,
    background = SurfaceLight,
    surface = SurfaceLight,
    onPrimary = Color.White,
    onBackground = TextPrimary,
    onSurface = TextPrimary,
)

private val DarkColorScheme = darkColorScheme(
    primary = PrimaryColor,
//    primaryVariant = PrimaryColor,
    secondary = AccentColor,
    background = SurfaceDark,
    surface = SurfaceDark,
    onPrimary = Color.White,
    onBackground = Color.White,
    onSurface = Color.White,
)

// Your typography from spec
val TypoText = androidx.compose.material3.Typography(
    displayLarge = TextStyle(
        fontSize = 20.sp,
        fontWeight = FontWeight.Medium
    ),
    headlineMedium = TextStyle(
        fontSize = 16.sp,
        fontWeight = FontWeight.SemiBold
    ),
    bodyLarge = TextStyle(
        fontSize = 14.sp,
        fontWeight = FontWeight.Normal
    ),
    labelSmall = TextStyle(
        fontSize = 12.sp,
        fontWeight = FontWeight.Normal
    )
)

@Composable
fun NewsAggregatorTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = TypoText,
        content = content
    )
}