package com.securevote.remote.ui.theme

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

val Navy = Color(0xFF1B2A4A)
val CivicBlue = Color(0xFF2563EB)
val CivicLight = Color(0xFFEFF6FF)
val ForestGreen = Color(0xFF166534)
val GreenLight = Color(0xFFDCFCE7)
val Amber = Color(0xFFD97706)
val AmberLight = Color(0xFFFEF3C7)
val Crimson = Color(0xFFDC2626)
val MedGray = Color(0xFF6B7280)
val LightGray = Color(0xFFF3F4F6)

private val SVColorScheme = lightColorScheme(
    primary = CivicBlue,
    onPrimary = Color.White,
    secondary = Navy,
    background = Color.White,
    surface = Color.White,
    error = Crimson,
)

@Composable
fun SecureVoteTheme(content: @Composable () -> Unit) {
    MaterialTheme(colorScheme = SVColorScheme, content = content)
}
