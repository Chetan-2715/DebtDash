package com.debtdash.app.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.googlefonts.Font
import androidx.compose.ui.text.googlefonts.GoogleFont
import androidx.compose.ui.unit.sp
import com.debtdash.app.R

// ══════════════════════════════════════════════════
//  DEEP SPACE STEALTH — Typography System
//  Font: Space Grotesk (via Google Fonts)
// ══════════════════════════════════════════════════

val googleFontProvider = GoogleFont.Provider(
    providerAuthority = "com.google.android.gms.fonts",
    providerPackage = "com.google.android.gms",
    certificates = R.array.com_google_android_gms_fonts_certs
)

val SpaceGrotesk = FontFamily(
    Font(
        googleFont = GoogleFont("Space Grotesk"),
        fontProvider = googleFontProvider,
        weight = FontWeight.Normal
    ),
    Font(
        googleFont = GoogleFont("Space Grotesk"),
        fontProvider = googleFontProvider,
        weight = FontWeight.Medium
    ),
    Font(
        googleFont = GoogleFont("Space Grotesk"),
        fontProvider = googleFontProvider,
        weight = FontWeight.SemiBold
    ),
    Font(
        googleFont = GoogleFont("Space Grotesk"),
        fontProvider = googleFontProvider,
        weight = FontWeight.Bold
    )
)

// Display — 48sp Bold, tight tracking
val DisplayStyle = TextStyle(
    fontFamily = SpaceGrotesk,
    fontSize = 48.sp,
    fontWeight = FontWeight.Bold,
    lineHeight = 52.sp,
    letterSpacing = (-0.5).sp
)

// Headline Large — 32sp SemiBold
val HeadlineLargeStyle = TextStyle(
    fontFamily = SpaceGrotesk,
    fontSize = 32.sp,
    fontWeight = FontWeight.SemiBold,
    lineHeight = 38.sp
)

// Headline Medium — 24sp SemiBold
val HeadlineMediumStyle = TextStyle(
    fontFamily = SpaceGrotesk,
    fontSize = 24.sp,
    fontWeight = FontWeight.SemiBold,
    lineHeight = 31.sp
)

// Body Large — 18sp Normal
val BodyLargeStyle = TextStyle(
    fontFamily = SpaceGrotesk,
    fontSize = 18.sp,
    fontWeight = FontWeight.Normal,
    lineHeight = 27.sp
)

// Body Medium — 16sp Normal
val BodyMediumStyle = TextStyle(
    fontFamily = SpaceGrotesk,
    fontSize = 16.sp,
    fontWeight = FontWeight.Normal,
    lineHeight = 24.sp
)

// Label (Mono-style) — 14sp Medium, wide tracking
val MonoLabelStyle = TextStyle(
    fontFamily = SpaceGrotesk,
    fontSize = 14.sp,
    fontWeight = FontWeight.Medium,
    lineHeight = 17.sp,
    letterSpacing = 0.8.sp
)

// Caption — 12sp Normal
val CaptionStyle = TextStyle(
    fontFamily = SpaceGrotesk,
    fontSize = 12.sp,
    fontWeight = FontWeight.Normal,
    lineHeight = 14.sp
)

val DebtDashTypography = Typography(
    displayLarge = DisplayStyle,
    headlineLarge = HeadlineLargeStyle,
    headlineMedium = HeadlineMediumStyle,
    headlineSmall = TextStyle(
        fontFamily = SpaceGrotesk,
        fontSize = 20.sp,
        fontWeight = FontWeight.SemiBold,
        lineHeight = 26.sp
    ),
    titleLarge = TextStyle(
        fontFamily = SpaceGrotesk,
        fontSize = 22.sp,
        fontWeight = FontWeight.SemiBold,
        lineHeight = 28.sp
    ),
    titleMedium = TextStyle(
        fontFamily = SpaceGrotesk,
        fontSize = 16.sp,
        fontWeight = FontWeight.Medium,
        lineHeight = 24.sp,
        letterSpacing = 0.15.sp
    ),
    titleSmall = TextStyle(
        fontFamily = SpaceGrotesk,
        fontSize = 14.sp,
        fontWeight = FontWeight.Medium,
        lineHeight = 20.sp,
        letterSpacing = 0.1.sp
    ),
    bodyLarge = BodyLargeStyle,
    bodyMedium = BodyMediumStyle,
    bodySmall = TextStyle(
        fontFamily = SpaceGrotesk,
        fontSize = 12.sp,
        fontWeight = FontWeight.Normal,
        lineHeight = 16.sp,
        letterSpacing = 0.4.sp
    ),
    labelLarge = MonoLabelStyle,
    labelMedium = TextStyle(
        fontFamily = SpaceGrotesk,
        fontSize = 12.sp,
        fontWeight = FontWeight.Medium,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp
    ),
    labelSmall = CaptionStyle
)
