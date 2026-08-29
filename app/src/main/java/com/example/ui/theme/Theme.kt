package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

fun getPresetColorScheme(preset: AppThemePreset): ColorScheme {
    return if (preset.isDark) {
        darkColorScheme(
            primary = preset.primaryColor,
            onPrimary = Color.White,
            primaryContainer = preset.primaryColor.copy(alpha = 0.25f),
            onPrimaryContainer = Color.White,
            secondary = preset.secondaryColor,
            onSecondary = Color.Black,
            secondaryContainer = preset.secondaryColor.copy(alpha = 0.2f),
            onSecondaryContainer = Color.White,
            tertiary = PurpleBadge,
            background = preset.backgroundColor,
            onBackground = TextPrimaryDark,
            surface = preset.surfaceColor,
            onSurface = TextPrimaryDark,
            surfaceVariant = preset.surfaceColor.copy(alpha = 0.7f),
            onSurfaceVariant = TextSecondaryDark,
            outline = preset.primaryColor.copy(alpha = 0.35f),
            outlineVariant = Color(0xFF1E293B),
            error = RoseError,
            onError = Color.White
        )
    } else {
        lightColorScheme(
            primary = preset.primaryColor,
            onPrimary = Color.White,
            primaryContainer = preset.primaryColor.copy(alpha = 0.12f),
            onPrimaryContainer = preset.primaryColor,
            secondary = preset.secondaryColor,
            onSecondary = Color.White,
            secondaryContainer = preset.secondaryColor.copy(alpha = 0.15f),
            onSecondaryContainer = Color(0xFF006064),
            tertiary = PurpleBadge,
            background = preset.backgroundColor,
            onBackground = TextPrimaryLight,
            surface = preset.surfaceColor,
            onSurface = TextPrimaryLight,
            surfaceVariant = SurfaceElevatedLight,
            onSurfaceVariant = TextSecondaryLight,
            outline = CardBorderLight,
            outlineVariant = Color(0xFFCBD5E1),
            error = RoseError,
            onError = Color.White
        )
    }
}

@Composable
fun MyApplicationTheme(
    preset: AppThemePreset = AppThemePreset.CYBER_INDIGO,
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // false to preserve custom brand colors
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        else -> getPresetColorScheme(preset)
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
