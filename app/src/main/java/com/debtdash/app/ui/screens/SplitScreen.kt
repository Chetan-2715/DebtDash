package com.debtdash.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.debtdash.app.ui.components.GlassmorphicCard
import com.debtdash.app.ui.components.StealthSearchBar
import com.debtdash.app.ui.theme.*
import com.debtdash.app.viewmodel.SplitViewModel

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SplitScreen(
    viewModel: SplitViewModel = hiltViewModel(),
    prefillTransactionId: Long = -1L,
    prefillAmount: String = "",
    prefillReason: String = ""
) {
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val searchResults by viewModel.searchResults.collectAsStateWithLifecycle()
    val amount by viewModel.amount.collectAsStateWithLifecycle()
    val reason by viewModel.reason.collectAsStateWithLifecycle()
    val selectedFriends by viewModel.selectedFriends.collectAsStateWithLifecycle()
    val isEqualSplit by viewModel.isEqualSplit.collectAsStateWithLifecycle()
    val quickTags = listOf("Dinner", "Rent", "Fuel", "Groceries", "Travel", "Custom+")

    // ── Show "Add Friend" dialog ──
    var showAddFriendDialog by remember { mutableStateOf(false) }

    // ── Pre-fill from navigation ──
    LaunchedEffect(prefillTransactionId) {
        if (prefillTransactionId != -1L) {
            if (prefillAmount.isNotBlank() && prefillAmount != "0") {
                viewModel.updateAmount(prefillAmount)
            }
            if (prefillReason.isNotBlank()) {
                viewModel.updateReason(prefillReason)
            }
            viewModel.setLinkedTransaction(prefillTransactionId)
        }
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize().background(BackgroundPure),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Header
        item {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.Top) {
                Column {
                    Text("INTERCEPT\nTRANSACTION", style = MaterialTheme.typography.headlineLarge, color = NeonTeal, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(4.dp))
                    Text("LOGGING_PROTOCOL:\nSPLIT_REASON_INPUT", style = MaterialTheme.typography.labelSmall, color = TextMuted)
                }
                IconButton(onClick = { viewModel.resetForm() }) {
                    Icon(Icons.Default.Close, "Close", tint = TextMuted)
                }
            }
        }

        // ── Pre-fill indicator (when coming from dashboard) ──
        if (prefillTransactionId != -1L) {
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(NeonTeal.copy(alpha = 0.08f))
                        .border(1.dp, NeonTeal.copy(alpha = 0.2f), RoundedCornerShape(10.dp))
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.Link, null, tint = NeonTeal, modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(8.dp))
                    Text(
                        "Linked to Transaction #$prefillTransactionId — assign friends below",
                        style = MaterialTheme.typography.bodySmall,
                        color = NeonTeal.copy(alpha = 0.8f)
                    )
                }
            }
        }

        // Reason Input
        item {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Description, null, tint = NeonTeal, modifier = Modifier.size(16.dp))
                Spacer(Modifier.width(8.dp))
                Text("WHAT WAS THIS FOR?", style = MaterialTheme.typography.labelLarge, color = NeonTeal)
            }
            Spacer(Modifier.height(8.dp))
            BasicTextField(
                value = reason, onValueChange = viewModel::updateReason, singleLine = true,
                textStyle = MaterialTheme.typography.bodyMedium.copy(color = TextPrimary),
                cursorBrush = SolidColor(NeonTeal),
                modifier = Modifier.fillMaxWidth().background(SurfaceContainerLow, SearchBarShape)
                    .border(1.dp, OutlineVariant, SearchBarShape).padding(16.dp),
                decorationBox = { inner ->
                    Box { if (reason.isEmpty()) Text("Enter transaction intent…", style = MaterialTheme.typography.bodyMedium, color = TextMuted); inner() }
                }
            )
        }

        // Quick Tags
        item {
            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                quickTags.forEach { tag ->
                    val sel = reason == tag
                    Box(Modifier.background(if (sel) NeonTeal.copy(0.15f) else SurfaceContainerLow, ChipShape)
                        .border(1.dp, if (sel) NeonTeal else OutlineVariant, ChipShape)
                        .clickable { viewModel.setQuickReason(tag) }.padding(horizontal = 16.dp, vertical = 8.dp)) {
                        Text(tag, style = MaterialTheme.typography.labelMedium, color = if (sel) NeonTeal else TextSecondary)
                    }
                }
            }
        }

        // Amount
        item {
            GlassmorphicCard(contentPadding = 16.dp) {
                Column {
                    Text("TOTAL_LEDGER_VALUE", style = MaterialTheme.typography.labelLarge, color = TextMuted)
                    Spacer(Modifier.height(4.dp))
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                        Text("₹", style = MaterialTheme.typography.headlineLarge, color = NeonTeal, fontWeight = FontWeight.Bold)
                        BasicTextField(
                            value = amount, onValueChange = viewModel::updateAmount, singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            textStyle = MaterialTheme.typography.headlineLarge.copy(color = NeonTeal, fontWeight = FontWeight.Bold),
                            cursorBrush = SolidColor(NeonTeal), modifier = Modifier.weight(1f).padding(start = 4.dp),
                            decorationBox = { inner ->
                                Box { if (amount.isEmpty()) Text("0.00", style = MaterialTheme.typography.headlineLarge, color = TextMuted, fontWeight = FontWeight.Bold); inner() }
                            }
                        )
                    }
                }
            }
        }

        // ── Selected Contributors Badge Row ──
        if (selectedFriends.isNotEmpty()) {
            item {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.People, null, tint = NeonTeal, modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(8.dp))
                    Text(
                        "CONTRIBUTORS (${selectedFriends.size})",
                        style = MaterialTheme.typography.labelLarge,
                        color = NeonTeal
                    )
                    Spacer(Modifier.weight(1f))
                    // Per-person amount
                    val perPerson = amount.toDoubleOrNull()?.let {
                        if (isEqualSplit && selectedFriends.isNotEmpty()) {
                            it / selectedFriends.size
                        } else null
                    }
                    if (perPerson != null) {
                        Text(
                            "₹${String.format("%,.0f", perPerson)} each",
                            style = MaterialTheme.typography.labelMedium,
                            color = NeonCrimson,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
                Spacer(Modifier.height(8.dp))

                // Chips for each selected friend
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val friendsList = searchResults.filter { selectedFriends.contains(it.id) }
                    friendsList.forEach { friend ->
                        Row(
                            modifier = Modifier
                                .clip(RoundedCornerShape(20.dp))
                                .background(NeonTeal.copy(alpha = 0.12f))
                                .border(1.dp, NeonTeal.copy(alpha = 0.3f), RoundedCornerShape(20.dp))
                                .padding(start = 10.dp, top = 6.dp, bottom = 6.dp, end = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                friend.avatarInitials,
                                style = MaterialTheme.typography.labelSmall,
                                color = NeonTeal,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(Modifier.width(6.dp))
                            Text(
                                friend.name,
                                style = MaterialTheme.typography.labelMedium,
                                color = TextPrimary,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            // Remove button
                            IconButton(
                                onClick = { viewModel.toggleFriend(friend.id) },
                                modifier = Modifier.size(24.dp)
                            ) {
                                Icon(
                                    Icons.Default.Close, "Remove",
                                    tint = NeonCrimson.copy(alpha = 0.7f),
                                    modifier = Modifier.size(14.dp)
                                )
                            }
                        }
                    }
                }
            }
        }

        // Contact Search
        item {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Groups, null, tint = NeonTeal, modifier = Modifier.size(16.dp))
                Spacer(Modifier.width(8.dp))
                Text("TARGET_CONTACTS", style = MaterialTheme.typography.labelLarge, color = NeonTeal)
                Spacer(Modifier.weight(1f))
                // Add Friend button
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(ElectricPurple.copy(alpha = 0.15f))
                        .clickable { showAddFriendDialog = true }
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.PersonAdd, null, tint = ElectricPurple, modifier = Modifier.size(14.dp))
                        Spacer(Modifier.width(4.dp))
                        Text("ADD", style = MaterialTheme.typography.labelSmall, color = ElectricPurple, fontWeight = FontWeight.Bold)
                    }
                }
            }
            Spacer(Modifier.height(8.dp))
            StealthSearchBar(query = searchQuery, onQueryChange = viewModel::updateSearch)
        }

        // Friend Results
        items(searchResults) { friend ->
            val sel = selectedFriends.contains(friend.id)
            GlassmorphicCard(
                borderColor = if (sel) GlassBorder else OutlineVariant.copy(0.3f),
                contentPadding = 12.dp,
                modifier = Modifier.clickable { viewModel.toggleFriend(friend.id) }
            ) {
                Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        Modifier.size(40.dp).background(
                            if (sel) NeonTeal.copy(0.2f) else NeonTeal.copy(0.1f),
                            MaterialTheme.shapes.extraLarge
                        ),
                        contentAlignment = Alignment.Center
                    ) {
                        if (sel) {
                            Icon(Icons.Default.Check, "Selected", tint = NeonTeal, modifier = Modifier.size(20.dp))
                        } else {
                            Text(friend.avatarInitials, style = MaterialTheme.typography.labelMedium, color = NeonTeal, fontWeight = FontWeight.Bold)
                        }
                    }
                    Spacer(Modifier.width(12.dp))
                    Column(Modifier.weight(1f)) {
                        Text(friend.name, style = MaterialTheme.typography.bodyMedium, color = TextPrimary, fontWeight = FontWeight.Medium)
                        Text(
                            friend.upiId ?: "Active Peer",
                            style = MaterialTheme.typography.labelSmall,
                            color = TextMuted,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                    if (sel) {
                        Icon(Icons.Default.CheckCircle, "Selected", tint = NeonTeal, modifier = Modifier.size(24.dp))
                    }
                }
            }
        }

        // Split Mode
        item {
            Spacer(Modifier.height(4.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Tune, null, tint = NeonTeal, modifier = Modifier.size(16.dp))
                Spacer(Modifier.width(8.dp))
                Text("DISTRIBUTION_MODEL", style = MaterialTheme.typography.labelLarge, color = NeonTeal)
            }
            Spacer(Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Box(Modifier.weight(1f).background(if (isEqualSplit) NeonTeal else SurfaceContainerLow, ButtonShape)
                    .border(1.dp, NeonTeal, ButtonShape).clickable { viewModel.setSplitMode(true) }.padding(vertical = 12.dp),
                    contentAlignment = Alignment.Center) {
                    Text("Equal Split", style = MaterialTheme.typography.labelLarge, color = if (isEqualSplit) BackgroundPure else TextSecondary, fontWeight = FontWeight.Bold)
                }
                Box(Modifier.weight(1f).background(if (!isEqualSplit) NeonTeal else SurfaceContainerLow, ButtonShape)
                    .border(1.dp, if (!isEqualSplit) NeonTeal else OutlineVariant, ButtonShape).clickable { viewModel.setSplitMode(false) }.padding(vertical = 12.dp),
                    contentAlignment = Alignment.Center) {
                    Text("Custom", style = MaterialTheme.typography.labelLarge, color = if (!isEqualSplit) BackgroundPure else TextSecondary, fontWeight = FontWeight.Bold)
                }
            }
        }

        // Action Buttons
        item {
            Spacer(Modifier.height(4.dp))
            HorizontalDivider(color = OutlineVariant.copy(0.3f))
            Spacer(Modifier.height(12.dp))
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedButton(onClick = { viewModel.resetForm() }, Modifier.weight(1f), shape = ButtonShape,
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = NeonTeal),
                    border = androidx.compose.foundation.BorderStroke(1.dp, NeonTeal)) {
                    Text("RESET", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
                }
                Button(onClick = { viewModel.initializeSplit() }, Modifier.weight(1.5f), shape = ButtonShape,
                    colors = ButtonDefaults.buttonColors(containerColor = NeonTeal, contentColor = BackgroundPure),
                    enabled = amount.isNotBlank() && selectedFriends.isNotEmpty()) {
                    Text("INITIALIZE SPLIT", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.width(4.dp))
                    Icon(Icons.Default.ElectricBolt, null, Modifier.size(16.dp))
                }
            }
        }

        item { Spacer(Modifier.height(16.dp)) }
    }

    // ── Add Friend Dialog ──
    if (showAddFriendDialog) {
        AddFriendDialog(
            onDismiss = { showAddFriendDialog = false },
            onAdd = { name, phone, upiId, contactType ->
                viewModel.addFriend(name, phone, upiId, contactType)
                showAddFriendDialog = false
            }
        )
    }
}

/**
 * Dialog to add a new contact with categorization choice.
 */
@Composable
private fun AddFriendDialog(
    onDismiss: () -> Unit,
    onAdd: (name: String, phone: String?, upiId: String?, contactType: com.debtdash.app.data.local.entity.ContactType) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var upiId by remember { mutableStateOf("") }
    var contactType by remember { mutableStateOf(com.debtdash.app.data.local.entity.ContactType.FRIEND) }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = SurfaceContainerLow,
        titleContentColor = NeonTeal,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.PersonAdd, null, tint = NeonTeal, modifier = Modifier.size(20.dp))
                Spacer(Modifier.width(8.dp))
                Text("CATEGORIZE_CONTACT", fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
            }
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                // Category Selection
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    val isFriend = contactType == com.debtdash.app.data.local.entity.ContactType.FRIEND
                    Box(Modifier.weight(1f).background(if (isFriend) NeonTeal else SurfaceContainerLow, ButtonShape)
                        .border(1.dp, NeonTeal, ButtonShape).clickable { contactType = com.debtdash.app.data.local.entity.ContactType.FRIEND }.padding(vertical = 8.dp),
                        contentAlignment = Alignment.Center) {
                        Text("FRIEND", style = MaterialTheme.typography.labelSmall, color = if (isFriend) BackgroundPure else TextSecondary)
                    }
                    Box(Modifier.weight(1f).background(if (!isFriend) NeonCrimson else SurfaceContainerLow, ButtonShape)
                        .border(1.dp, if (!isFriend) NeonCrimson else OutlineVariant, ButtonShape).clickable { contactType = com.debtdash.app.data.local.entity.ContactType.BUSINESS }.padding(vertical = 8.dp),
                        contentAlignment = Alignment.Center) {
                        Text("BUSINESS", style = MaterialTheme.typography.labelSmall, color = if (!isFriend) BackgroundPure else TextSecondary)
                    }
                }

                // Name
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Name *") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = NeonTeal,
                        unfocusedBorderColor = OutlineVariant,
                        focusedLabelColor = NeonTeal,
                        cursorColor = NeonTeal
                    )
                )
                // UPI ID
                OutlinedTextField(
                    value = upiId,
                    onValueChange = { upiId = it },
                    label = { Text("UPI ID (optional)") },
                    singleLine = true,
                    placeholder = { Text("name@bankhandle", color = TextMuted) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = NeonTeal,
                        unfocusedBorderColor = OutlineVariant,
                        focusedLabelColor = NeonTeal,
                        cursorColor = NeonTeal
                    )
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (name.isNotBlank()) {
                        onAdd(name, phone.ifBlank { null }, upiId.ifBlank { null }, contactType)
                    }
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (contactType == com.debtdash.app.data.local.entity.ContactType.FRIEND) NeonTeal else NeonCrimson,
                    contentColor = BackgroundPure
                ),
                enabled = name.isNotBlank()
            ) {
                Text("SAVE", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("CANCEL", color = TextMuted)
            }
        }
    )
}
