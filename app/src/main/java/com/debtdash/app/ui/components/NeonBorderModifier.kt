package com.debtdash.app.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.debtdash.app.ui.theme.NeonTeal

/**
 * Modifier that draws a pulsing neon border around a composable.
 * Used for "Pending" items that need attention.
 */
fun Modifier.neonBorder(
    color: Color = NeonTeal,
    borderWidth: Dp = 1.dp,
    cornerRadius: Dp = 16.dp
): Modifier = composed {
    val infiniteTransition = rememberInfiniteTransition(label = "neonPulse")
    val alpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "neonAlpha"
    )

    this.drawBehind {
        drawRoundRect(
            color = color.copy(alpha = alpha),
            cornerRadius = CornerRadius(cornerRadius.toPx()),
            style = Stroke(width = borderWidth.toPx())
        )
    }
}

/**
 * Static neon glow border (no animation).
 */
fun Modifier.staticNeonBorder(
    color: Color = NeonTeal,
    borderWidth: Dp = 1.dp,
    cornerRadius: Dp = 16.dp
): Modifier = this.drawBehind {
    drawRoundRect(
        color = color.copy(alpha = 0.5f),
        cornerRadius = CornerRadius(cornerRadius.toPx()),
        style = Stroke(width = borderWidth.toPx())
    )
}
