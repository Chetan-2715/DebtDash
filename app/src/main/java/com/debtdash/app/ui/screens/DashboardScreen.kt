package com.debtdash.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
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
import com.debtdash.app.ui.theme.*
import com.debtdash.app.viewmodel.DashboardViewModel
import kotlin.math.abs

/**
 * "Nerve" Tab — The main dashboard.
 * Optimized for the "Deep Space Stealth" aesthetic.
 */
@Composable
fun DashboardScreen(
    viewModel: DashboardViewModel = hiltViewModel(),
    onTransactionClick: (transactionId: Long, amount: String, reason: String?) -> Unit = { _, _, _ -> },
    onManualEntry: () -> Unit = {}
) {
    val transactions by viewModel.transactions.collectAsStateWithLifecycle()
    val debtSummary by viewModel.debtSummary.collectAsStateWithLifecycle()
    val unreasoned by viewModel.unreasonedTransactions.collectAsStateWithLifecycle()

    val totalLedger = debtSummary.sumOf { abs(it.netDebt) }

    Box(modifier = Modifier.fillMaxSize().background(BackgroundPure)) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 80.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // ── Header ──
            item {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.ElectricBolt, null, tint = NeonTeal, modifier = Modifier.size(24.dp))
                        Spacer(Modifier.width(8.dp))
                        Text("DEBTDASH_V1.1", style = MaterialTheme.typography.titleLarge, color = TextPrimary, fontWeight = FontWeight.Bold)
                        Spacer(Modifier.width(4.dp))
                        Text("STEALTH_MODE", style = MaterialTheme.typography.labelSmall, color = TextMuted)
                    }
                    Box(
                        Modifier.size(36.dp).background(NeonTeal.copy(0.1f), MaterialTheme.shapes.medium),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Person, "Profile", tint = NeonTeal, modifier = Modifier.size(20.dp))
                    }
                }
            }

            // ── Total Ledger Card ──
            item {
                GlassmorphicCard {
                    Column {
                        Text("TOTAL_LEDGER_VALUE", style = MaterialTheme.typography.labelLarge, color = TextMuted)
                        Spacer(Modifier.height(8.dp))
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                            Text("₹${formatLedger(totalLedger)}", style = MaterialTheme.typography.displayLarge, color = NeonTeal, fontWeight = FontWeight.Bold)
                            Icon(Icons.Default.CameraAlt, "Scan", tint = NeonTeal, modifier = Modifier.size(28.dp))
                        }
                    }
                }
            }

            // ── Active Debts ──
            if (debtSummary.isNotEmpty()) {
                item {
                    Text("⚡ ACTIVE_DEBTS", style = MaterialTheme.typography.labelLarge, color = NeonTeal)
                }
                item {
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        items(debtSummary.filter { it.netDebt != 0.0 }) { summary ->
                            DebtCard(summary = summary)
                        }
                    }
                }
            }

            // ── Recent Intercepts ──
            item {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("📋 RECENT_INTERCEPTS", style = MaterialTheme.typography.labelLarge, color = NeonTeal)
                    if (unreasoned.isNotEmpty()) {
                        Spacer(Modifier.width(8.dp))
                        Text("(${unreasoned.size} pending)", style = MaterialTheme.typography.labelSmall, color = NeonCrimson)
                    }
                }
            }

            if (transactions.isEmpty()) {
                item {
                    GlassmorphicCard {
                        Text("NO_INTERCEPTS_DETECTED", style = MaterialTheme.typography.labelLarge, color = TextMuted, modifier = Modifier.fillMaxWidth())
                    }
                }
            } else {
                items(transactions.take(10)) { transaction ->
                    TransactionItem(
                        transaction = transaction,
                        onClick = { onTransactionClick(transaction.id, formatAmount(transaction.amount), transaction.reason) }
                    )
                }
            }
        }

        // ── Main FAB ──
        FloatingActionButton(
            onClick = onManualEntry,
            containerColor = NeonTeal,
            contentColor = BackgroundPure,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(bottom = 24.dp, end = 24.dp),
            shape = MaterialTheme.shapes.large
        ) {
            Icon(Icons.Default.Add, "Manual Entry")
        }
    }
}

private fun formatLedger(amount: Double): String = if (amount == 0.0) "0.00" else String.format("%,.2f", amount)
private fun formatAmount(amount: Double): String = if (amount == amount.toLong().toDouble()) String.format("%,.0f", amount) else String.format("%,.2f", amount)
