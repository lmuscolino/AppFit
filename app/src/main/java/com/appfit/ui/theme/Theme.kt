package com.appfit.ui.theme

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val FitnessColorScheme = lightColorScheme(
    primary = TealPrimary,
    onPrimary = OnTealPrimary,
    primaryContainer = TealPrimaryContainer,
    onPrimaryContainer = OnTealPrimaryContainer,
    secondary = BlueSecondary,
    onSecondary = Color.White,
    secondaryContainer = BlueSecondaryContainer,
    onSecondaryContainer = OnBlueSecondaryContainer,
    tertiary = GreenTertiary,
    onTertiary = Color.White,
    tertiaryContainer = GreenTertiaryContainer,
    onTertiaryContainer = OnGreenTertiaryContainer,
    background = BackgroundAqua,
    onBackground = Color(0xFF1A1A1A),
    surface = Color.White,
    onSurface = Color(0xFF1A1A1A),
    surfaceVariant = SurfaceVariantTeal,
    onSurfaceVariant = OnSurfaceVariantSlate,
    error = Color(0xFFB00020),
    onError = Color.White
)

@Composable
fun AppFitTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = FitnessColorScheme,
        typography = Typography,
        content = content
    )
}
