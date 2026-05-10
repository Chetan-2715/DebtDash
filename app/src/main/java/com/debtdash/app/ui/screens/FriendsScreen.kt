package com.debtdash.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.debtdash.app.ui.components.DebtCard
import com.debtdash.app.ui.components.GlassmorphicCard
import com.debtdash.app.ui.theme.*
import com.debtdash.app.viewmodel.DashboardViewModel

/**
 * "Friends" Tab — Cumulative Ledger.
 * Displays total outstanding debts per contact.
 */
@Composable
fun FriendsScreen(
    viewModel: DashboardViewModel = hiltViewModel(),
    onFriendClick: (Long) -> Unit = {}
) {
    val debtSummary by viewModel.debtSummary.collectAsStateWithLifecycle()
    val totalLedger = debtSummary.sumOf { it.netDebt }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundPure),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // ── Header ──
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "CUMULATIVE_LEDGER",
                        style = MaterialTheme.typography.titleLarge,
                        color = TextPrimary,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "TOTAL_OUTSTANDING: ₹${String.format("%,.2f", totalLedger)}",
                        style = MaterialTheme.typography.labelSmall,
                        color = if (totalLedger >= 0) NeonTeal else NeonCrimson
                    )
                }
                Icon(
                    imageVector = Icons.Default.Group,
                    contentDescription = null,
                    tint = NeonTeal,
                    modifier = Modifier.size(24.dp)
                )
            }
        }

        // ── Summary List ──
        if (debtSummary.isEmpty()) {
            item {
                GlassmorphicCard {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.fillMaxWidth().padding(vertical = 32.dp)
                    ) {
                        Text(
                            text = "NO_ACTIVE_DEBTS",
                            style = MaterialTheme.typography.labelLarge,
                            color = TextMuted
                        )
                    }
                }
            }
        } else {
            items(debtSummary) { summary ->
                DebtCard(
                    summary = summary,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        item { Spacer(modifier = Modifier.height(80.dp)) }
    }
}
