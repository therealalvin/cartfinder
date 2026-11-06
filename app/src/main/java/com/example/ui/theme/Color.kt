package com.example.ui.theme

import androidx.compose.ui.graphics.Color

data class FloridaThemePalette(
    val id: String,
    val name: String,
    val emoji: String,
    val subtitle: String,
    val primary: Color,
    val onPrimary: Color,
    val primaryContainer: Color,
    val onPrimaryContainer: Color,
    val secondary: Color,
    val onSecondary: Color,
    val secondaryContainer: Color,
    val onSecondaryContainer: Color,
    val pinAccent: Color,
    val background: Color,
    val onBackground: Color,
    val surface: Color,
    val onSurface: Color,
    val cardBorder: Color,
    val previewColors: List<Color>
)

enum class FloridaTheme(val palette: FloridaThemePalette) {
    CITRUS(
        FloridaThemePalette(
            id = "citrus",
            name = "Florida Citrus",
            emoji = "🍊",
            subtitle = "Tangerine Orange & Sun Gold",
            primary = Color(0xFFEA580C),
            onPrimary = Color.White,
            primaryContainer = Color(0xFFFFEDD5),
            onPrimaryContainer = Color(0xFF7C2D12),
            secondary = Color(0xFFD97706),
            onSecondary = Color.White,
            secondaryContainer = Color(0xFFFEF3C7),
            onSecondaryContainer = Color(0xFF78350F),
            pinAccent = Color(0xFFDC2626),
            background = Color(0xFFFFFDF8),
            onBackground = Color(0xFF1C1917),
            surface = Color(0xFFFFFFFF),
            onSurface = Color(0xFF1C1917),
            cardBorder = Color(0xFFFED7AA),
            previewColors = listOf(Color(0xFFEA580C), Color(0xFFF59E0B), Color(0xFFFFEDD5))
        )
    ),
    KEY_WEST(
        FloridaThemePalette(
            id = "key_west",
            name = "Key West Aqua",
            emoji = "🌊",
            subtitle = "Tropical Turquoise & Coral",
            primary = Color(0xFF0891B2),
            onPrimary = Color.White,
            primaryContainer = Color(0xFFE0F2FE),
            onPrimaryContainer = Color(0xFF0C4A6E),
            secondary = Color(0xFFF43F5E),
            onSecondary = Color.White,
            secondaryContainer = Color(0xFFFFE4E6),
            onSecondaryContainer = Color(0xFF881337),
            pinAccent = Color(0xFFE11D48),
            background = Color(0xFFF8FAFC),
            onBackground = Color(0xFF0F172A),
            surface = Color(0xFFFFFFFF),
            onSurface = Color(0xFF0F172A),
            cardBorder = Color(0xFFBAE6FD),
            previewColors = listOf(Color(0xFF0891B2), Color(0xFF06B6D4), Color(0xFFF43F5E))
        )
    ),
    TROPICAL_PALMS(
        FloridaThemePalette(
            id = "palms",
            name = "Tropical Palms",
            emoji = "🌴",
            subtitle = "Lush Palm Green & Sunshine",
            primary = Color(0xFF16A34A),
            onPrimary = Color.White,
            primaryContainer = Color(0xFFDCFCE7),
            onPrimaryContainer = Color(0xFF14532D),
            secondary = Color(0xFFCA8A04),
            onSecondary = Color.White,
            secondaryContainer = Color(0xFFFEF9C3),
            onSecondaryContainer = Color(0xFF713F12),
            pinAccent = Color(0xFFDC2626),
            background = Color(0xFFF7FDF9),
            onBackground = Color(0xFF052E16),
            surface = Color(0xFFFFFFFF),
            onSurface = Color(0xFF052E16),
            cardBorder = Color(0xFFBBF7D0),
            previewColors = listOf(Color(0xFF16A34A), Color(0xFF22C55E), Color(0xFFCA8A04))
        )
    ),
    FLAMINGO_SUNSET(
        FloridaThemePalette(
            id = "flamingo",
            name = "Flamingo Sunset",
            emoji = "🦩",
            subtitle = "Vibrant Coral & Sunset Amber",
            primary = Color(0xFFE11D48),
            onPrimary = Color.White,
            primaryContainer = Color(0xFFFFE4E6),
            onPrimaryContainer = Color(0xFF881337),
            secondary = Color(0xFFD97706),
            onSecondary = Color.White,
            secondaryContainer = Color(0xFFFEF3C7),
            onSecondaryContainer = Color(0xFF78350F),
            pinAccent = Color(0xFF7C3AED),
            background = Color(0xFFFFF7F8),
            onBackground = Color(0xFF1F1A24),
            surface = Color(0xFFFFFFFF),
            onSurface = Color(0xFF1F1A24),
            cardBorder = Color(0xFFFECDD3),
            previewColors = listOf(Color(0xFFE11D48), Color(0xFFF43F5E), Color(0xFFF59E0B))
        )
    ),
    DAYTONA_BREEZE(
        FloridaThemePalette(
            id = "daytona",
            name = "Daytona Breeze",
            emoji = "☀️",
            subtitle = "Royal Ocean Blue & Sunburst",
            primary = Color(0xFF2563EB),
            onPrimary = Color.White,
            primaryContainer = Color(0xFFDBEAFE),
            onPrimaryContainer = Color(0xFF1E3A8A),
            secondary = Color(0xFFEAB308),
            onSecondary = Color.White,
            secondaryContainer = Color(0xFFFEF9C3),
            onSecondaryContainer = Color(0xFF713F12),
            pinAccent = Color(0xFFDC2626),
            background = Color(0xFFF8FAFC),
            onBackground = Color(0xFF0F172A),
            surface = Color(0xFFFFFFFF),
            onSurface = Color(0xFF0F172A),
            cardBorder = Color(0xFFBFDBFE),
            previewColors = listOf(Color(0xFF2563EB), Color(0xFF38BDF8), Color(0xFFEAB308))
        )
    );

    companion object {
        fun fromId(id: String?): FloridaTheme =
            entries.firstOrNull { it.palette.id.equals(id, ignoreCase = true) } ?: CITRUS
    }
}
