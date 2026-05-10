package com.debtdash.app.ui.screens

import android.content.ComponentName
import android.content.Intent
import android.provider.Settings
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.debtdash.app.service.DebtDashNotificationService
import com.debtdash.app.ui.components.GlassmorphicCard
import com.debtdash.app.ui.theme.*

@Composable
fun SettingsScreen() {
    val context = LocalContext.current

    // Check if notification listener is enabled
    val isNLEnabled = remember {
        val flat = Settings.Secure.getString(context.contentResolver, "enabled_notification_listeners")
        flat?.contains(ComponentName(context, DebtDashNotificationService::class.java).flattenToString()) == true
    }

    Column(
        modifier = Modifier.fillMaxSize().background(BackgroundPure).padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Header
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.Settings, null, tint = NeonTeal, modifier = Modifier.size(24.dp))
            Spacer(Modifier.width(8.dp))
            Text("SYSTEM_CONFIG", style = MaterialTheme.typography.headlineMedium, color = NeonTeal, fontWeight = FontWeight.Bold)
        }
        Text("DebtDash v1.0 — Deep Space Stealth", style = MaterialTheme.typography.labelSmall, color = TextMuted)

        Spacer(Modifier.height(8.dp))

        // Notification Access
        GlassmorphicCard(
            borderColor = if (isNLEnabled) GlassBorder else GlassBorderCrimson,
            modifier = Modifier.clickable {
                context.startActivity(Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS))
            }
        ) {
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Box(
                    Modifier.size(44.dp).background(
                        if (isNLEnabled) NeonTeal.copy(0.1f) else NeonCrimson.copy(0.1f),
                        MaterialTheme.shapes.medium
                    ), contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.Notifications,
                        null, tint = if (isNLEnabled) NeonTeal else NeonCrimson,
                        modifier = Modifier.size(24.dp)
                    )
                }
                Spacer(Modifier.width(12.dp))
                Column(Modifier.weight(1f)) {
                    Text("Notification Access", style = MaterialTheme.typography.bodyMedium, color = TextPrimary, fontWeight = FontWeight.Medium)
                    Text(
                        if (isNLEnabled) "ENABLED — Intercepting GPay" else "DISABLED — Tap to enable",
                        style = MaterialTheme.typography.labelSmall,
                        color = if (isNLEnabled) NeonTeal else NeonCrimson
                    )
                }
                Icon(
                    if (isNLEnabled) Icons.Default.CheckCircle else Icons.Default.Error,
                    null, tint = if (isNLEnabled) NeonTeal else NeonCrimson,
                    modifier = Modifier.size(24.dp)
                )
            }
        }

        // GPay Package Info
        GlassmorphicCard(contentPadding = 16.dp) {
            Column {
                Text("TARGET_PACKAGE", style = MaterialTheme.typography.labelLarge, color = TextMuted)
                Spacer(Modifier.height(4.dp))
                Text("com.google.android.apps.nbu.paisa.user", style = MaterialTheme.typography.bodyMedium, color = NeonTeal)
                Spacer(Modifier.height(8.dp))
                Text("FILTER_MODE", style = MaterialTheme.typography.labelLarge, color = TextMuted)
                Spacer(Modifier.height(4.dp))
                Text("Sent / Received pattern matching", style = MaterialTheme.typography.bodyMedium, color = TextSecondary)
            }
        }

        // Nag Engine Status
        GlassmorphicCard(contentPadding = 16.dp) {
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Box(Modifier.size(44.dp).background(ElectricPurple.copy(0.1f), MaterialTheme.shapes.medium), contentAlignment = Alignment.Center) {
                    Icon(Icons.Default.Schedule, null, tint = ElectricPurple, modifier = Modifier.size(24.dp))
                }
                Spacer(Modifier.width(12.dp))
                Column(Modifier.weight(1f)) {
                    Text("NAG_ENGINE", style = MaterialTheme.typography.bodyMedium, color = TextPrimary, fontWeight = FontWeight.Medium)
                    Text("Interval: 60 minutes", style = MaterialTheme.typography.labelSmall, color = TextMuted)
                }
                Text("ACTIVE", style = MaterialTheme.typography.labelMedium, color = NeonTeal, fontWeight = FontWeight.Bold)
            }
        }

        // About
        GlassmorphicCard(contentPadding = 16.dp) {
            Column {
                Text("ABOUT", style = MaterialTheme.typography.labelLarge, color = TextMuted)
                Spacer(Modifier.height(8.dp))
                Text("DebtDash — UPI Debt Tracker", style = MaterialTheme.typography.bodyMedium, color = TextPrimary)
                Text("Intercept • Split • Settle", style = MaterialTheme.typography.labelSmall, color = NeonTeal)
                Spacer(Modifier.height(4.dp))
                Text("Built with Kotlin, Jetpack Compose, Room & WorkManager", style = MaterialTheme.typography.labelSmall, color = TextMuted)
            }
        }
    }
}
