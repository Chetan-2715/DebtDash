package com.debtdash.app.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// ══════════════════════════════════════════════════
//  DEEP SPACE STEALTH — Theme (Dark Only)
// ══════════════════════════════════════════════════

private val DebtDashColorScheme = darkColorScheme(
    // Backgrounds
    background = BackgroundPure,
    surface = Surface,
    surfaceDim = SurfaceDim,
    surfaceBright = SurfaceBright,
    surfaceContainerLowest = SurfaceContainerLowest,
    surfaceContainerLow = SurfaceContainerLow,
    surfaceContainer = SurfaceContainer,
    surfaceContainerHigh = SurfaceContainerHigh,
    surfaceContainerHighest = SurfaceContainerHighest,
    surfaceVariant = SurfaceVariant,

    // Primary — Neon Teal
    primary = NeonTeal,
    onPrimary = TextOnTeal,
    primaryContainer = NeonTealBright,
    onPrimaryContainer = TextOnTeal,
    inversePrimary = Color(0xFF006B5A),

    // Secondary — Neon Crimson
    secondary = Secondary,
    onSecondary = OnSecondary,
    secondaryContainer = SecondaryContainer,
    onSecondaryContainer = OnSecondaryContainer,

    // Tertiary — Electric Purple
    tertiary = Tertiary,
    onTertiary = OnTertiary,
    tertiaryContainer = TertiaryContainer,
    onTertiaryContainer = OnTertiaryContainer,

    // Text on surfaces
    onBackground = TextSecondary,
    onSurface = OnSurface,
    onSurfaceVariant = OnSurfaceVariant,
    inverseSurface = InverseSurface,
    inverseOnSurface = InverseOnSurface,

    // Outlines
    outline = Outline,
    outlineVariant = OutlineVariant,

    // Error
    error = Error,
    onError = OnError,
    errorContainer = ErrorContainer,
    onErrorContainer = OnErrorContainer,

    // Tint
    surfaceTint = NeonTealDim
)

@Composable
fun DebtDashTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = DebtDashColorScheme,
        typography = DebtDashTypography,
        shapes = DebtDashShapes,
        content = content
    )
}
