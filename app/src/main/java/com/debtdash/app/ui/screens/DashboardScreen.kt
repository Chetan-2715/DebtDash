package com.debtdash.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.debtdash.app.ui.components.DebtCard
import com.debtdash.app.ui.components.GlassmorphicCard
import com.debtdash.app.ui.components.TransactionItem
import com.debtdash.app.ui.theme.BackgroundPure
import com.debtdash.app.ui.theme.NeonCrimson
import com.debtdash.app.ui.theme.NeonTeal
import com.debtdash.app.ui.theme.TextMuted
import com.debtdash.app.ui.theme.TextPrimary
import com.debtdash.app.viewmodel.DashboardViewModel
import kotlin.math.abs

/**
 * "Nerve" Tab — The main dashboard.
 *
 * Shows:
 * - App header with version and operator label
 * - Total ledger value card
 * - Horizontal scroll of active debt cards
 * - Recent intercepted transactions feed
 *
 * Clicking a transaction navigates to Split screen with pre-filled data.
 */
@Composable
fun DashboardScreen(
    viewModel: DashboardViewModel = hiltViewModel(),
    onTransactionClick: (transactionId: Long, amount: String, reason: String?) -> Unit = { _, _, _ -> }
) {
    val transactions by viewModel.transactions.collectAsStateWithLifecycle()
    val debtSummary by viewModel.debtSummary.collectAsStateWithLifecycle()
    val unreasoned by viewModel.unreasonedTransactions.collectAsStateWithLifecycle()

    val totalLedger = debtSummary.sumOf { abs(it.netDebt) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundPure),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // ── Header ──
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.ElectricBolt,
                        contentDescription = null,
                        tint = NeonTeal,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "DEBTDASH_V1.0",
                        style = MaterialTheme.typography.titleLarge,
                        color = TextPrimary,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "OPERATOR_01",
                        style = MaterialTheme.typography.labelSmall,
                        color = TextMuted
                    )
                }
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .background(
                            color = NeonTeal.copy(alpha = 0.1f),
                            shape = MaterialTheme.shapes.medium
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = "Profile",
                        tint = NeonTeal,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }

        // ── Total Ledger Value Card ──
        item {
            GlassmorphicCard {
                Column {
                    Text(
                        text = "TOTAL_LEDGER_VALUE",
                        style = MaterialTheme.typography.labelLarge,
                        color = TextMuted
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "₹${formatLedger(totalLedger)}",
                            style = MaterialTheme.typography.displayLarge,
                            color = NeonTeal,
                            fontWeight = FontWeight.Bold
                        )
                        Icon(
                            imageVector = Icons.Default.CameraAlt,
                            contentDescription = "Scan",
                            tint = NeonTeal,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                }
            }
        }

        // ── Active Debts ──
        if (debtSummary.isNotEmpty()) {
            item {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(vertical = 4.dp)
                ) {
                    Text(
                        text = "⚡",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "ACTIVE_DEBTS",
                        style = MaterialTheme.typography.labelLarge,
                        color = NeonTeal
                    )
                }
            }

            item {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(debtSummary.filter { it.netDebt != 0.0 }) { summary ->
                        DebtCard(summary = summary)
                    }
                }
            }
        }

        // ── Recent Intercepts ──
        item {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(vertical = 4.dp)
            ) {
                Text(
                    text = "📋",
                    style = MaterialTheme.typography.titleMedium
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "RECENT_INTERCEPTS",
                    style = MaterialTheme.typography.labelLarge,
                    color = NeonTeal
                )
                if (unreasoned.isNotEmpty()) {
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "(${unreasoned.size} pending)",
                        style = MaterialTheme.typography.labelSmall,
                        color = NeonCrimson
                    )
                }
            }
        }

        // Transaction list — clicking navigates to Split
        if (transactions.isEmpty()) {
            item {
                GlassmorphicCard {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "NO_INTERCEPTS_DETECTED",
                            style = MaterialTheme.typography.labelLarge,
                            color = TextMuted
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Enable notification access in System tab",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextMuted
                        )
                    }
                }
            }
        } else {
            items(transactions.take(10)) { transaction ->
                TransactionItem(
                    transaction = transaction,
                    onClick = {
                        onTransactionClick(
                            transaction.id,
                            formatAmount(transaction.amount),
                            transaction.reason
                        )
                    }
                )
            }
        }

        // Bottom spacer
        item { Spacer(modifier = Modifier.height(8.dp)) }
    }
}

private fun formatLedger(amount: Double): String {
    return if (amount == 0.0) "0.00"
    else String.format("%,.2f", amount)
}

private fun formatAmount(amount: Double): String {
    return if (amount == amount.toLong().toDouble()) {
        String.format("%,.0f", amount)
    } else {
        String.format("%,.2f", amount)
    }
}
