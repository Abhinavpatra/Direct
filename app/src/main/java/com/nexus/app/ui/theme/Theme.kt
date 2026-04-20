package com.nexus.app.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightScheme = lightColorScheme(
    primary = Color(0xFF165B47),
    onPrimary = Color(0xFFFFFFFF),
    secondary = Color(0xFF3D5A80),
    onSecondary = Color(0xFFFFFFFF),
    tertiary = Color(0xFFB65A3C),
    background = Color(0xFFF7F2EA),
    onBackground = Color(0xFF17212B),
    surface = Color(0xFFFFFBF5),
    onSurface = Color(0xFF17212B),
    surfaceVariant = Color(0xFFE8DED1),
)

private val DarkScheme = darkColorScheme(
    primary = Color(0xFF86EBA7), // Brighter green for contrast
    secondary = Color(0xFFA6C8FF),
    tertiary = Color(0xFFFFB59F),
    background = Color(0xFF0F141B), // Deeper dark
    onBackground = Color(0xFFE1E7EE),
    surface = Color(0xFF1B232E), // Distinct from background
    onSurface = Color(0xFFE1E7EE),
    surfaceVariant = Color(0xFF2B3746), // Distinct variant
    onSurfaceVariant = Color(0xFFC4CED9),
)

@Composable
fun NexusTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkScheme else LightScheme,
        typography = NexusTypography,
        content = content,
    )
}
