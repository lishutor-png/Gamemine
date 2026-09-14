package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = CyberCyan,
    onPrimary = Color(0xFF00363D),
    primaryContainer = Color(0xFF004F58),
    onPrimaryContainer = Color(0xFF97F0FF),
    secondary = NeonEmerald,
    onSecondary = Color(0xFF00381B),
    secondaryContainer = Color(0xFF00532B),
    onSecondaryContainer = Color(0xFF6BFFAC),
    tertiary = HazardAmber,
    onTertiary = Color(0xFF452B00),
    error = CyberRed,
    onError = Color(0xFF690017),
    background = CyberBackgroundDark,
    onBackground = Color(0xFFF1F5F9),
    surface = CyberSurfaceDark,
    onSurface = Color(0xFFF1F5F9),
    surfaceVariant = CyberSurfaceElevatedDark,
    onSurfaceVariant = Color(0xFFCBD5E1),
    outline = CyberSurfaceBorderDark
)

private val LightColorScheme = lightColorScheme(
    primary = CyberCyanDim,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFBAF3FD),
    onPrimaryContainer = Color(0xFF001F24),
    secondary = Color(0xFF059669),
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFA7F3D0),
    onSecondaryContainer = Color(0xFF064E3B),
    tertiary = Color(0xFFD97706),
    onTertiary = Color.White,
    error = Color(0xFFDC2626),
    onError = Color.White,
    background = CyberBackgroundLight,
    onBackground = Color(0xFF0F172A),
    surface = CyberSurfaceLight,
    onSurface = Color(0xFF0F172A),
    surfaceVariant = CyberSurfaceElevatedLight,
    onSurfaceVariant = Color(0xFF334155),
    outline = CyberSurfaceBorderLight
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
