package com.nexus.app.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

// Deep Monochrome palette — Cred/Grok-inspired
private val NexusDarkScheme = darkColorScheme(
    primary = Color(0xFFFFFFFF),           // Crisp White
    onPrimary = Color(0xFF000000),          // Pure Black
    primaryContainer = Color(0xFF1C1C1E),   // Elevated container
    onPrimaryContainer = Color(0xFFFFFFFF),
    secondary = Color(0xFF8E8E93),          // iOS-style Muted Grey
    onSecondary = Color(0xFF000000),
    secondaryContainer = Color(0xFF2C2C2E),
    onSecondaryContainer = Color(0xFFE5E5EA),
    tertiary = Color(0xFF48484A),           // Subtle accent grey
    background = Color(0xFF000000),         // Pure Deep Black
    onBackground = Color(0xFFFFFFFF),
    surface = Color(0xFF121212),            // Soft Dark Grey
    onSurface = Color(0xFFFFFFFF),
    surfaceVariant = Color(0xFF1C1C1E),     // Card surfaces
    onSurfaceVariant = Color(0xFF8E8E93),
    outline = Color(0xFF38383A),            // Borders
    outlineVariant = Color(0xFF2C2C2E),
    error = Color(0xFFFF453A),              // iOS-style red
    onError = Color(0xFFFFFFFF),
    surfaceContainerHighest = Color(0xFF1C1C1E),
    surfaceContainerHigh = Color(0xFF161616),
    surfaceContainer = Color(0xFF121212),
    surfaceContainerLow = Color(0xFF0A0A0A),
    surfaceContainerLowest = Color(0xFF000000),
)

// Light scheme kept minimal — same monochrome spirit
private val NexusLightScheme = lightColorScheme(
    primary = Color(0xFF000000),
    onPrimary = Color(0xFFFFFFFF),
    primaryContainer = Color(0xFFF2F2F7),
    onPrimaryContainer = Color(0xFF000000),
    secondary = Color(0xFF8E8E93),
    onSecondary = Color(0xFFFFFFFF),
    secondaryContainer = Color(0xFFE5E5EA),
    onSecondaryContainer = Color(0xFF1C1C1E),
    tertiary = Color(0xFFC7C7CC),
    background = Color(0xFFFFFFFF),
    onBackground = Color(0xFF000000),
    surface = Color(0xFFF2F2F7),
    onSurface = Color(0xFF000000),
    surfaceVariant = Color(0xFFE5E5EA),
    onSurfaceVariant = Color(0xFF8E8E93),
    outline = Color(0xFFC7C7CC),
    outlineVariant = Color(0xFFE5E5EA),
    error = Color(0xFFFF3B30),
    onError = Color(0xFFFFFFFF),
)

@Composable
fun NexusTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    val colorScheme = if (darkTheme) NexusDarkScheme else NexusLightScheme

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            @Suppress("DEPRECATION")
            window.statusBarColor = colorScheme.background.toArgb()
            @Suppress("DEPRECATION")
            window.navigationBarColor = colorScheme.background.toArgb()
            WindowCompat.getInsetsController(window, view).apply {
                isAppearanceLightStatusBars = !darkTheme
                isAppearanceLightNavigationBars = !darkTheme
            }
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = NexusTypography,
        content = content,
    )
}
