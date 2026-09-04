package com.crustybakery.mobile.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

val CrustyBrown = Color(0xFF604734)
val CrustyCream = Color(0xFFFCF7D9)
val CrustyBlush = Color(0xFFF6D8D6)
val CrustyPink = Color(0xFFF0C5D4)
val CrustyBackground = Color(0xFFFFFCF8)
val CrustySurface = Color(0xFFFFFFFF)
val CrustyMuted = Color(0xFF8A7465)
val CrustySoftBrown = Color(0xFFEEE5DF)
val CrustyError = Color(0xFFB3261E)

private val CrustyColorScheme = lightColorScheme(
    primary = CrustyBrown,
    onPrimary = Color.White,
    primaryContainer = CrustyPink,
    onPrimaryContainer = CrustyBrown,
    secondary = CrustyPink,
    onSecondary = CrustyBrown,
    secondaryContainer = CrustyBlush,
    onSecondaryContainer = CrustyBrown,
    tertiary = CrustyCream,
    onTertiary = CrustyBrown,
    background = CrustyBackground,
    onBackground = CrustyBrown,
    surface = CrustySurface,
    onSurface = CrustyBrown,
    surfaceVariant = CrustyCream,
    onSurfaceVariant = CrustyMuted,
    outline = CrustySoftBrown,
    error = CrustyError
)

@Composable
fun CrustyBakeryTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = CrustyColorScheme,
        content = content
    )
}
