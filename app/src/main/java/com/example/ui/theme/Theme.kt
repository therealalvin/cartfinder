package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

val LocalFloridaTheme = staticCompositionLocalOf { FloridaTheme.CITRUS }

@Composable
fun CartFinderTheme(
    floridaTheme: FloridaTheme = FloridaTheme.CITRUS,
    content: @Composable () -> Unit
) {
    val palette = floridaTheme.palette
    val colorScheme = lightColorScheme(
        primary = palette.primary,
        onPrimary = palette.onPrimary,
        primaryContainer = palette.primaryContainer,
        onPrimaryContainer = palette.onPrimaryContainer,
        secondary = palette.secondary,
        onSecondary = palette.onSecondary,
        secondaryContainer = palette.secondaryContainer,
        onSecondaryContainer = palette.onSecondaryContainer,
        tertiary = palette.pinAccent,
        onTertiary = Color.White,
        background = palette.background,
        onBackground = palette.onBackground,
        surface = palette.surface,
        onSurface = palette.onSurface,
        surfaceVariant = palette.primaryContainer.copy(alpha = 0.35f),
        onSurfaceVariant = palette.onSurface
    )

    CompositionLocalProvider(LocalFloridaTheme provides floridaTheme) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            content = content
        )
    }
}
