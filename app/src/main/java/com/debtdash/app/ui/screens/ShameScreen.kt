package com.debtdash.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
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
import com.debtdash.app.ui.theme.*
import com.debtdash.app.viewmodel.ShameViewModel
import kotlin.math.abs

@Composable
fun ShameScreen(viewModel: ShameViewModel = hiltViewModel()) {
    val debtors by viewModel.debtors.collectAsStateWithLifecycle()
    val creditors by viewModel.creditors.collectAsStateWithLifecycle()

    LazyColumn(
        modifier = Modifier.fillMaxSize().background(BackgroundPure),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Warning, null, tint = NeonCrimson, modifier = Modifier.size(24.dp))
                Spacer(Modifier.width(8.dp))
                Text("SHAME_BOARD", style = MaterialTheme.typography.headlineMedium, color = NeonCrimson, fontWeight = FontWeight.Bold)
            }
            Spacer(Modifier.height(4.dp))
            Text("Outstanding debts ranked by severity", style = MaterialTheme.typography.labelSmall, color = TextMuted)
        }

        if (debtors.isNotEmpty()) {
            item {
                Text("THEY_OWE_YOU", style = MaterialTheme.typography.labelLarge, color = NeonTeal, modifier = Modifier.padding(top = 8.dp))
            }
            items(debtors) { d ->
                GlassmorphicCard(borderColor = GlassBorder, contentPadding = 16.dp) {
                    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                        Box(Modifier.size(40.dp).background(NeonTeal.copy(0.1f), MaterialTheme.shapes.extraLarge), contentAlignment = Alignment.Center) {
                            Text(d.avatarInitials, style = MaterialTheme.typography.labelMedium, color = NeonTeal, fontWeight = FontWeight.Bold)
                        }
                        Spacer(Modifier.width(12.dp))
                        Column(Modifier.weight(1f)) {
                            Text(d.name, style = MaterialTheme.typography.bodyMedium, color = TextPrimary, fontWeight = FontWeight.Medium)
                            Text(d.upiId ?: "No UPI linked", style = MaterialTheme.typography.labelSmall, color = TextMuted)
                        }
                        Text("₹${String.format("%,.0f", d.netDebt)}", style = MaterialTheme.typography.titleMedium, color = NeonTeal, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        if (creditors.isNotEmpty()) {
            item {
                Text("YOU_OWE_THEM", style = MaterialTheme.typography.labelLarge, color = NeonCrimson, modifier = Modifier.padding(top = 8.dp))
            }
            items(creditors) { c ->
                GlassmorphicCard(borderColor = GlassBorderCrimson, contentPadding = 16.dp) {
                    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                        Box(Modifier.size(40.dp).background(NeonCrimson.copy(0.1f), MaterialTheme.shapes.extraLarge), contentAlignment = Alignment.Center) {
                            Text(c.avatarInitials, style = MaterialTheme.typography.labelMedium, color = NeonCrimson, fontWeight = FontWeight.Bold)
                        }
                        Spacer(Modifier.width(12.dp))
                        Column(Modifier.weight(1f)) {
                            Text(c.name, style = MaterialTheme.typography.bodyMedium, color = TextPrimary, fontWeight = FontWeight.Medium)
                            Text(c.upiId ?: "No UPI linked", style = MaterialTheme.typography.labelSmall, color = TextMuted)
                        }
                        Text("₹${String.format("%,.0f", abs(c.netDebt))}", style = MaterialTheme.typography.titleMedium, color = NeonCrimson, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        if (debtors.isEmpty() && creditors.isEmpty()) {
            item {
                GlassmorphicCard {
                    Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("ALL_CLEAR", style = MaterialTheme.typography.labelLarge, color = NeonTeal)
                        Spacer(Modifier.height(4.dp))
                        Text("No outstanding debts detected", style = MaterialTheme.typography.bodySmall, color = TextMuted)
                    }
                }
            }
        }

        item { Spacer(Modifier.height(8.dp)) }
    }
}
