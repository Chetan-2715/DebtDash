package com.debtdash.app.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

// ══════════════════════════════════════════════════
//  DEEP SPACE STEALTH — Shape System
// ══════════════════════════════════════════════════

val DebtDashShapes = Shapes(
    extraSmall = RoundedCornerShape(4.dp),
    small = RoundedCornerShape(8.dp),
    medium = RoundedCornerShape(12.dp),
    large = RoundedCornerShape(16.dp),
    extraLarge = RoundedCornerShape(24.dp)
)

// Specific component shapes
val GlassCardShape = RoundedCornerShape(16.dp)
val ChipShape = RoundedCornerShape(20.dp)
val ButtonShape = RoundedCornerShape(12.dp)
val SearchBarShape = RoundedCornerShape(12.dp)
val AvatarShape = RoundedCornerShape(50) // Full circle
