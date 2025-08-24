package com.app.bookfinder.ui.theme

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

// Modern Light Theme inspired by Dribbble design
private val LightColorScheme = lightColorScheme(
    primary = Color(0xFF6366F1), // Indigo
    onPrimary = Color.White,
    primaryContainer = Color(0xFFE0E7FF),
    onPrimaryContainer = Color(0xFF3730A3),
    secondary = Color(0xFF8B5CF6), // Purple
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFEDE9FE),
    onSecondaryContainer = Color(0xFF5B21B6),
    tertiary = Color(0xFF06B6D4), // Cyan
    onTertiary = Color.White,
    tertiaryContainer = Color(0xFFCFFAFE),
    onTertiaryContainer = Color(0xFF0E7490),
    background = Color(0xFFFAFAFA),
    onBackground = Color(0xFF1F2937),
    surface = Color.White,
    onSurface = Color(0xFF1F2937),
    surfaceVariant = Color(0xFFF8FAFC),
    onSurfaceVariant = Color(0xFF64748B),
    outline = Color(0xFFE2E8F0),
    outlineVariant = Color(0xFFF1F5F9),
    error = Color(0xFFEF4444),
    onError = Color.White,
    errorContainer = Color(0xFFFEE2E2),
    onErrorContainer = Color(0xFFDC2626)
)

// Modern Dark Theme inspired by Dribbble design
private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFF818CF8), // Lighter Indigo
    onPrimary = Color(0xFF1E1B4B),
    primaryContainer = Color(0xFF4338CA),
    onPrimaryContainer = Color(0xFFE0E7FF),
    secondary = Color(0xFFA78BFA), // Lighter Purple
    onSecondary = Color(0xFF2E1065),
    secondaryContainer = Color(0xFF7C3AED),
    onSecondaryContainer = Color(0xFFEDE9FE),
    tertiary = Color(0xFF22D3EE), // Lighter Cyan
    onTertiary = Color(0xFF164E63),
    tertiaryContainer = Color(0xFF0891B2),
    onTertiaryContainer = Color(0xFFCFFAFE),
    background = Color(0xFF0F172A), // Slate 900
    onBackground = Color(0xFFF8FAFC),
    surface = Color(0xFF1E293B), // Slate 800
    onSurface = Color(0xFFF8FAFC),
    surfaceVariant = Color(0xFF334155), // Slate 700
    onSurfaceVariant = Color(0xFFCBD5E1),
    outline = Color(0xFF475569), // Slate 600
    outlineVariant = Color(0xFF334155), // Slate 700
    error = Color(0xFFF87171),
    onError = Color(0xFF450A0A),
    errorContainer = Color(0xFFDC2626),
    onErrorContainer = Color(0xFFFEE2E2)
)

@Composable
fun BookFinderTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    val view = LocalView.current
    
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.background.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}