package com.debtdash.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.CallMade
import androidx.compose.material.icons.automirrored.filled.CallReceived
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.debtdash.app.data.local.entity.TransactionEntity
import com.debtdash.app.data.local.entity.TransactionType
import com.debtdash.app.ui.theme.GlassBorder
import com.debtdash.app.ui.theme.GlassBorderCrimson
import com.debtdash.app.ui.theme.NeonCrimson
import com.debtdash.app.ui.theme.NeonTeal
import com.debtdash.app.ui.theme.SurfaceContainer
import com.debtdash.app.ui.theme.TextMuted
import com.debtdash.app.ui.theme.TextPrimary
import com.debtdash.app.ui.theme.TextSecondary
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Transaction list item — shows a single intercepted transaction.
 *
 * - Sent = Crimson accent + outgoing arrow
 * - Received = Teal accent + incoming arrow
 * - No reason = pulsing warning indicator
 */
@Composable
fun TransactionItem(
    transaction: TransactionEntity,
    onClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val isSent = transaction.type == TransactionType.SENT
    val accentColor = if (isSent) NeonCrimson else NeonTeal
    val borderColor = if (isSent) GlassBorderCrimson else GlassBorder
    val icon = if (isSent) Icons.AutoMirrored.Filled.CallMade else Icons.AutoMirrored.Filled.CallReceived
    val prefix = if (isSent) "-" else "+"

    GlassmorphicCard(
        modifier = modifier.clickable(onClick = onClick),
        borderColor = borderColor,
        contentPadding = 16.dp
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Direction icon
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(
                        color = accentColor.copy(alpha = 0.1f),
                        shape = MaterialTheme.shapes.medium
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = if (isSent) "Sent" else "Received",
                    tint = accentColor,
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Transaction info
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    // No-reason warning
                    if (transaction.reason == null) {
                        PulseIndicator(
                            color = NeonCrimson,
                            size = 6.dp
                        )
                    }

                    Text(
                        text = transaction.reason
                            ?: transaction.upiId
                            ?: "Unknown",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextPrimary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        fontWeight = FontWeight.Medium
                    )
                }

                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    text = formatTimestamp(transaction.timestamp),
                    style = MaterialTheme.typography.labelSmall,
                    color = TextMuted
                )
            }

            // Amount
            Text(
                text = "$prefix₹${formatAmount(transaction.amount)}",
                style = MaterialTheme.typography.titleMedium,
                color = accentColor,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

private fun formatTimestamp(timestamp: Long): String {
    val sdf = SimpleDateFormat("dd MMM, HH:mm", Locale.getDefault())
    return sdf.format(Date(timestamp))
}

private fun formatAmount(amount: Double): String {
    return if (amount == amount.toLong().toDouble()) {
        String.format("%,.0f", amount)
    } else {
        String.format("%,.2f", amount)
    }
}
