package com.example.ui.theme

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

private val DarkColorScheme =
    darkColorScheme(
        primary = ElectricCyan,
        onPrimary = ObsidianBg,
        primaryContainer = SlateCardHover,
        onPrimaryContainer = ElectricCyan,
        secondary = RadiantGold,
        onSecondary = ObsidianBg,
        secondaryContainer = SlateCard,
        onSecondaryContainer = RadiantGold,
        tertiary = EmeraldGreen,
        background = ObsidianBg,
        onBackground = TextPrimaryDark,
        surface = DeepNavySurface,
        onSurface = TextPrimaryDark,
        surfaceVariant = SlateCard,
        onSurfaceVariant = TextSecondaryDark,
        outline = SlateCardBorder,
        error = CoralError,
        onError = Color.White
    )

private val LightColorScheme =
    lightColorScheme(
        primary = Color(0xFF0284C7),
        onPrimary = Color.White,
        primaryContainer = Color(0xFFE0F2FE),
        onPrimaryContainer = Color(0xFF0369A1),
        secondary = Color(0xFFD97706),
        onSecondary = Color.White,
        secondaryContainer = Color(0xFFFEF3C7),
        onSecondaryContainer = Color(0xFF92400E),
        tertiary = Color(0xFF059669),
        background = LightBg,
        onBackground = LightTextPrimary,
        surface = LightSurface,
        onSurface = LightTextPrimary,
        surfaceVariant = Color(0xFFE2E8F0),
        onSurfaceVariant = LightTextSecondary,
        outline = LightCardBorder,
        error = CoralError,
        onError = Color.White
    )

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = true, // Default to stunning dark mathematical theme
    dynamicColor: Boolean = false, // Keep intentional sci-fi mathematical branding
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
