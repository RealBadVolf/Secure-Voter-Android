package com.securevote.remote.ui.theme

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

// ============================================================
// SecureVote Color Palette — from UI_UX_SPEC.md
// ============================================================

val Navy = Color(0xFF1B2A4A)
val CivicBlue = Color(0xFF2563EB)
val CivicLight = Color(0xFFEFF6FF)
val ForestGreen = Color(0xFF166534)
val GreenLight = Color(0xFFDCFCE7)
val Amber = Color(0xFFD97706)
val AmberLight = Color(0xFFFEF3C7)
val Crimson = Color(0xFFDC2626)
val CrimsonLight = Color(0xFFFEE2E2)
val LightGray = Color(0xFFF3F4F6)
val MedGray = Color(0xFF6B7280)
val DarkGray = Color(0xFF1F2937)

private val SVRLightColorScheme = lightColorScheme(
    primary = CivicBlue,
    onPrimary = Color.White,
    primaryContainer = CivicLight,
    onPrimaryContainer = Navy,
    secondary = ForestGreen,
    onSecondary = Color.White,
    secondaryContainer = GreenLight,
    onSecondaryContainer = ForestGreen,
    tertiary = Amber,
    onTertiary = Color.White,
    tertiaryContainer = AmberLight,
    onTertiaryContainer = Color(0xFF7C2D12),
    error = Crimson,
    onError = Color.White,
    errorContainer = CrimsonLight,
    background = Color.White,
    onBackground = Navy,
    surface = Color.White,
    onSurface = Navy,
    surfaceVariant = LightGray,
    onSurfaceVariant = MedGray,
    outline = Color(0xFFD1D5DB),
)

// Atkinson Hyperlegible is the spec'd font (designed by Braille Institute for low-vision)
// Falls back to system sans-serif if not bundled
val AtkinsonHyperlegible = FontFamily.Default

val SVRTypography = Typography(
    headlineLarge = TextStyle(
        fontFamily = AtkinsonHyperlegible,
        fontWeight = FontWeight.Bold,
        fontSize = 28.sp,
        lineHeight = 36.sp,
        color = Navy,
    ),
    headlineMedium = TextStyle(
        fontFamily = AtkinsonHyperlegible,
        fontWeight = FontWeight.Bold,
        fontSize = 22.sp,
        lineHeight = 28.sp,
        color = Navy,
    ),
    headlineSmall = TextStyle(
        fontFamily = AtkinsonHyperlegible,
        fontWeight = FontWeight.SemiBold,
        fontSize = 18.sp,
        lineHeight = 24.sp,
        color = Navy,
    ),
    titleLarge = TextStyle(
        fontFamily = AtkinsonHyperlegible,
        fontWeight = FontWeight.Bold,
        fontSize = 20.sp,
        lineHeight = 26.sp,
    ),
    titleMedium = TextStyle(
        fontFamily = AtkinsonHyperlegible,
        fontWeight = FontWeight.SemiBold,
        fontSize = 16.sp,
        lineHeight = 22.sp,
    ),
    bodyLarge = TextStyle(
        fontFamily = AtkinsonHyperlegible,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
    ),
    bodyMedium = TextStyle(
        fontFamily = AtkinsonHyperlegible,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        color = MedGray,
    ),
    bodySmall = TextStyle(
        fontFamily = AtkinsonHyperlegible,
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp,
        lineHeight = 16.sp,
        color = MedGray,
    ),
    labelLarge = TextStyle(
        fontFamily = AtkinsonHyperlegible,
        fontWeight = FontWeight.Bold,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.5.sp,
    ),
    labelSmall = TextStyle(
        fontFamily = AtkinsonHyperlegible,
        fontWeight = FontWeight.Medium,
        fontSize = 11.sp,
        lineHeight = 14.sp,
        letterSpacing = 1.sp,
    ),
)

@Composable
fun SecureVoteTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = SVRLightColorScheme,
        typography = SVRTypography,
        content = content,
    )
}
