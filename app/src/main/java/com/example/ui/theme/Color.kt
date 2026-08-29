package com.example.ui.theme

import androidx.compose.ui.graphics.Color

// Premium SaaS & Futuristic Architect Palette
val CanvasDark = Color(0xFF090D16)
val SurfaceDark = Color(0xFF111726)
val SurfaceElevatedDark = Color(0xFF1B2236)
val CardBorderDark = Color(0xFF26334D)

val CanvasLight = Color(0xFFF8FAFC)
val SurfaceLight = Color(0xFFFFFFFF)
val SurfaceElevatedLight = Color(0xFFF1F5F9)
val CardBorderLight = Color(0xFFE2E8F0)

// Brand Neon & Accent Colors
val IndigoPrimary = Color(0xFF6366F1)
val IndigoLight = Color(0xFF818CF8)
val IndigoDark = Color(0xFF4F46E5)

val CyanAccent = Color(0xFF06B6D4)
val EmeraldSuccess = Color(0xFF10B981)
val AmberWarning = Color(0xFFF59E0B)
val RoseError = Color(0xFFF43F5E)
val PurpleBadge = Color(0xFF8B5CF6)

// Text Colors
val TextPrimaryDark = Color(0xFFF1F5F9)
val TextSecondaryDark = Color(0xFF94A3B8)
val TextMutedDark = Color(0xFF64748B)

val TextPrimaryLight = Color(0xFF0F172A)
val TextSecondaryLight = Color(0xFF475569)
val TextMutedLight = Color(0xFF94A3B8)

// Code Syntax Colors
val CodeBgDark = Color(0xFF0D1117)
val CodeKeyword = Color(0xFFFF7B72)
val CodeString = Color(0xFFA5D6FF)
val CodeComment = Color(0xFF8B949E)
val CodeFunction = Color(0xFFD2A8FF)

enum class AppThemePreset(
    val id: String,
    val title: String,
    val subtitle: String,
    val primaryColor: Color,
    val secondaryColor: Color,
    val backgroundColor: Color,
    val surfaceColor: Color,
    val isDark: Boolean
) {
    CYBER_INDIGO(
        id = "cyber_indigo",
        title = "Cyber Indigo",
        subtitle = "Futuristic deep slate with luminous indigo & cyan",
        primaryColor = Color(0xFF6366F1),
        secondaryColor = Color(0xFF06B6D4),
        backgroundColor = Color(0xFF090D16),
        surfaceColor = Color(0xFF111726),
        isDark = true
    ),
    EMERALD_MATRIX(
        id = "emerald_matrix",
        title = "Emerald Matrix",
        subtitle = "Cyberpunk dark canvas with mint green accents",
        primaryColor = Color(0xFF10B981),
        secondaryColor = Color(0xFF2DD4BF),
        backgroundColor = Color(0xFF0B1311),
        surfaceColor = Color(0xFF12201D),
        isDark = true
    ),
    SUNSET_AMBER(
        id = "sunset_amber",
        title = "Sunset Amber",
        subtitle = "Warm midnight canvas with amber & coral glow",
        primaryColor = Color(0xFFF59E0B),
        secondaryColor = Color(0xFFF43F5E),
        backgroundColor = Color(0xFF141014),
        surfaceColor = Color(0xFF221A22),
        isDark = true
    ),
    OBSIDIAN_VIOLET(
        id = "obsidian_violet",
        title = "Obsidian Violet",
        subtitle = "Pitch black OLED darkness with electric violet",
        primaryColor = Color(0xFF8B5CF6),
        secondaryColor = Color(0xFF38BDF8),
        backgroundColor = Color(0xFF07060A),
        surfaceColor = Color(0xFF13101C),
        isDark = true
    ),
    BLUEPRINT_LIGHT(
        id = "blueprint_light",
        title = "Blueprint Light",
        subtitle = "Clean architectural light theme with cobalt blue",
        primaryColor = Color(0xFF2563EB),
        secondaryColor = Color(0xFF0D9488),
        backgroundColor = Color(0xFFF8FAFC),
        surfaceColor = Color(0xFFFFFFFF),
        isDark = false
    ),
    NORDIC_SLATE(
        id = "nordic_slate",
        title = "Nordic Slate",
        subtitle = "Minimalist steel gray canvas with ice blue",
        primaryColor = Color(0xFF38BDF8),
        secondaryColor = Color(0xFF34D399),
        backgroundColor = Color(0xFF0F172A),
        surfaceColor = Color(0xFF1E293B),
        isDark = true
    ),
    ROSE_QUARTZ(
        id = "rose_quartz",
        title = "Rose Quartz",
        subtitle = "Soft, elegant light theme with rose and gold",
        primaryColor = Color(0xFFEC4899),
        secondaryColor = Color(0xFFF59E0B),
        backgroundColor = Color(0xFFFFF1F2),
        surfaceColor = Color(0xFFFFFFFF),
        isDark = false
    ),
    MIDNIGHT_ROYAL(
        id = "midnight_royal",
        title = "Midnight Royal",
        subtitle = "Deep navy blue theme with royal gold accents",
        primaryColor = Color(0xFFEAB308),
        secondaryColor = Color(0xFF3B82F6),
        backgroundColor = Color(0xFF020617),
        surfaceColor = Color(0xFF0F172A),
        isDark = true
    )
}

