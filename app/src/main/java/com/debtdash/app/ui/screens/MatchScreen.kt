package com.debtdash.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.automirrored.filled.CompareArrows
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.debtdash.app.data.local.entity.TransactionType
import com.debtdash.app.ui.components.GlassmorphicCard
import com.debtdash.app.ui.components.TransactionItem
import com.debtdash.app.ui.theme.*
import com.debtdash.app.viewmodel.MatchViewModel

@Composable
fun MatchScreen(viewModel: MatchViewModel = hiltViewModel()) {
    val unmatchedReceived by viewModel.unmatchedReceived.collectAsStateWithLifecycle()

    LazyColumn(
        modifier = Modifier.fillMaxSize().background(BackgroundPure),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.AutoMirrored.Filled.CompareArrows, null, tint = NeonTeal, modifier = Modifier.size(24.dp))
                Spacer(Modifier.width(8.dp))
                Text("REVERSE_MATCH", style = MaterialTheme.typography.headlineMedium, color = NeonTeal, fontWeight = FontWeight.Bold)
            }
            Spacer(Modifier.height(4.dp))
            Text("Auto-detect incoming payments and settle debts", style = MaterialTheme.typography.labelSmall, color = TextMuted)
        }

        // Incoming unmatched
        if (unmatchedReceived.isNotEmpty()) {
            item {
                Text("INCOMING_UNMATCHED", style = MaterialTheme.typography.labelLarge, color = NeonTeal, modifier = Modifier.padding(top = 8.dp))
            }
            items(unmatchedReceived) { tx ->
                GlassmorphicCard(borderColor = GlassBorder, contentPadding = 12.dp) {
                    Column {
                        TransactionItem(transaction = tx)
                        if (tx.upiId != null) {
                            Spacer(Modifier.height(8.dp))
                            Button(
                                onClick = { viewModel.autoSettleAll(tx.id, tx.upiId!!, tx.amount) },
                                colors = ButtonDefaults.buttonColors(containerColor = NeonTeal, contentColor = BackgroundPure),
                                shape = com.debtdash.app.ui.theme.ButtonShape,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Icon(Icons.Default.CheckCircle, null, Modifier.size(16.dp))
                                Spacer(Modifier.width(8.dp))
                                Text("AUTO_SETTLE", fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }

        // Unsettled outgoing
        // MOVED TO FRIENDS/BUSINESS TABS AS PER STEALTH PROTOCOL

        if (unmatchedReceived.isEmpty()) {
            item {
                GlassmorphicCard {
                    Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("ALL_CLEAR", style = MaterialTheme.typography.labelLarge, color = NeonTeal)
                        Spacer(Modifier.height(4.dp))
                        Text("No unmatched incoming payments detected", style = MaterialTheme.typography.bodySmall, color = TextMuted)
                    }
                }
            }
        }

        item { Spacer(Modifier.height(8.dp)) }
    }
}
