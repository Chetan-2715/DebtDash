package com.debtdash.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.debtdash.app.ui.theme.GlassBorder
import com.debtdash.app.ui.theme.GlassCardShape
import com.debtdash.app.ui.theme.GlassWhite
import com.debtdash.app.ui.theme.SurfaceContainerLow

/**
 * Glassmorphic Card — The signature DebtDash component.
 *
 * Simulates a frosted-glass effect with:
 * - Semi-transparent white fill (~3%)
 * - Subtle gradient overlay for depth
 * - Thin neon border (teal by default)
 * - 16dp rounded corners
 */
@Composable
fun GlassmorphicCard(
    modifier: Modifier = Modifier,
    borderColor: Color = GlassBorder,
    borderWidth: Dp = 1.dp,
    contentPadding: Dp = 20.dp,
    content: @Composable BoxScope.() -> Unit
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(GlassCardShape)
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        GlassWhite,
                        SurfaceContainerLow.copy(alpha = 0.4f)
                    )
                )
            )
            .border(
                width = borderWidth,
                color = borderColor,
                shape = GlassCardShape
            )
            .padding(contentPadding),
        content = content
    )
}
