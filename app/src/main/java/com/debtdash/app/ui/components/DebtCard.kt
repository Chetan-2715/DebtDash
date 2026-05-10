package com.debtdash.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.debtdash.app.data.local.dao.FriendDebtSummary
import com.debtdash.app.ui.theme.AvatarShape
import com.debtdash.app.ui.theme.GlassBorder
import com.debtdash.app.ui.theme.GlassBorderCrimson
import com.debtdash.app.ui.theme.NeonCrimson
import com.debtdash.app.ui.theme.NeonTeal
import com.debtdash.app.ui.theme.SurfaceContainer
import com.debtdash.app.ui.theme.TextMuted
import com.debtdash.app.ui.theme.TextPrimary
import kotlin.math.abs

/**
 * Debt Card — Shows a friend's net balance in a compact glassmorphic card.
 *
 * - Teal + upward arrow → they owe you
 * - Crimson + downward arrow → you owe them
 */
@Composable
fun DebtCard(
    summary: FriendDebtSummary,
    modifier: Modifier = Modifier
) {
    val isPositive = summary.netDebt >= 0
    val accentColor = if (isPositive) NeonTeal else NeonCrimson
    val borderColor = if (isPositive) GlassBorder else GlassBorderCrimson
    val arrowIcon = if (isPositive) Icons.Default.ArrowUpward else Icons.Default.ArrowDownward

    GlassmorphicCard(
        modifier = modifier.width(160.dp),
        borderColor = borderColor,
        contentPadding = 16.dp
    ) {
        Column {
            // Avatar + Name
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Avatar circle with initials
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .background(
                            color = accentColor.copy(alpha = 0.15f),
                            shape = AvatarShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = summary.avatarInitials,
                        style = MaterialTheme.typography.labelSmall,
                        color = accentColor,
                        fontWeight = FontWeight.Bold
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = summary.name.replace(" ", "_"),
                    style = MaterialTheme.typography.labelLarge,
                    color = TextPrimary,
                    maxLines = 1
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Amount with direction indicator
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = "₹${formatDebt(abs(summary.netDebt))}",
                    style = MaterialTheme.typography.headlineMedium,
                    color = accentColor,
                    fontWeight = FontWeight.Bold
                )
                Icon(
                    imageVector = arrowIcon,
                    contentDescription = if (isPositive) "Owes you" else "You owe",
                    tint = accentColor,
                    modifier = Modifier.size(18.dp)
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = if (isPositive) "owes you" else "you owe",
                style = MaterialTheme.typography.labelSmall,
                color = TextMuted
            )
        }
    }
}

private fun formatDebt(amount: Double): String {
    return if (amount == amount.toLong().toDouble()) {
        String.format("%,.0f", amount)
    } else {
        String.format("%,.2f", amount)
    }
}
