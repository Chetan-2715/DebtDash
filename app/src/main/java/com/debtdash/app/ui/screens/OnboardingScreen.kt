package com.debtdash.app.ui.screens

import android.Manifest
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Build
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.foundation.BorderStroke
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.debtdash.app.ui.theme.*
import kotlinx.coroutines.delay

/**
 * Onboarding / Permissions Gate Screen.
 *
 * Shown on first launch (or when critical permissions are missing).
 * Explains WHY each permission is needed with consequences of skipping.
 */
@Composable
fun OnboardingScreen(
    onAllGranted: () -> Unit
) {
    val context = LocalContext.current

    // ── Permission States ──
    var notificationAccessGranted by remember {
        mutableStateOf(isNotificationAccessEnabled(context))
    }
    var postNotificationsGranted by remember {
        mutableStateOf(true) // Default true for pre-Android 13
    }

    // Check POST_NOTIFICATIONS for Android 13+
    val notifPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        postNotificationsGranted = granted
    }

    // Refresh states when returning from settings
    val lifecycleRefresh = remember { mutableIntStateOf(0) }
    LaunchedEffect(lifecycleRefresh.intValue) {
        // Poll every second to catch when user returns from settings
        while (true) {
            delay(1000)
            notificationAccessGranted = isNotificationAccessEnabled(context)
        }
    }

    // ── Entrance animation ──
    var showContent by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        delay(300)
        showContent = true
        // Request POST_NOTIFICATIONS on Android 13+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            postNotificationsGranted = false
            try {
                delay(200) // Let the launcher fully register
                notifPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            } catch (_: Exception) {
                // Launcher not ready yet — user can tap the button manually
            }
        }
    }

    // ── Pulse animation for the logo ──
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 0.8f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseAlpha"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundPure)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(60.dp))

        // ── Logo with glow ──
        AnimatedVisibility(
            visible = showContent,
            enter = fadeIn(animationSpec = tween(800)) +
                    slideInVertically(animationSpec = tween(800)) { -40 }
        ) {
            Box(contentAlignment = Alignment.Center) {
                // Glow circle
                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.radialGradient(
                                colors = listOf(
                                    NeonTeal.copy(alpha = pulseAlpha * 0.4f),
                                    Color.Transparent
                                )
                            )
                        )
                )
                // Inner icon
                Icon(
                    imageVector = Icons.Default.Security,
                    contentDescription = "DebtDash Setup",
                    tint = NeonTeal,
                    modifier = Modifier.size(48.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        AnimatedVisibility(
            visible = showContent,
            enter = fadeIn(tween(800, delayMillis = 200)) +
                    slideInVertically(tween(800, delayMillis = 200)) { 30 }
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "SYSTEM_INIT",
                    style = MaterialTheme.typography.headlineMedium,
                    color = NeonTeal,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 3.sp
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "DebtDash needs these permissions to operate at full capacity.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(40.dp))

        // ── Permission Card 1: Notification Access (Read) ──
        AnimatedVisibility(
            visible = showContent,
            enter = fadeIn(tween(600, delayMillis = 400)) +
                    slideInVertically(tween(600, delayMillis = 400)) { 40 }
        ) {
            PermissionCard(
                icon = Icons.Default.Notifications,
                title = "NOTIFICATION_ACCESS",
                subtitle = "Read GPay notifications",
                isGranted = notificationAccessGranted,
                grantedLabel = "INTERCEPTING",
                warningText = "Without this, DebtDash cannot automatically detect your UPI " +
                        "transactions. You'll need to add every payment manually.",
                buttonLabel = "ENABLE",
                accentColor = NeonTeal,
                onRequestPermission = {
                    val intent = Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS)
                    context.startActivity(intent)
                }
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // ── Permission Card 2: Show Notifications (Post) ──
        AnimatedVisibility(
            visible = showContent,
            enter = fadeIn(tween(600, delayMillis = 600)) +
                    slideInVertically(tween(600, delayMillis = 600)) { 40 }
        ) {
            PermissionCard(
                icon = Icons.Default.NotificationsActive,
                title = "POST_NOTIFICATIONS",
                subtitle = "Show reminder alerts",
                isGranted = postNotificationsGranted,
                grantedLabel = "ACTIVE",
                warningText = "Without this, the Nag Engine cannot remind you to add reasons " +
                        "for your transactions. You might forget to tag them, and your ledger " +
                        "will stay messy.",
                buttonLabel = "ALLOW",
                accentColor = ElectricPurple,
                onRequestPermission = {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        notifPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                    }
                }
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        // ── Continue Button ──
        AnimatedVisibility(
            visible = showContent,
            enter = fadeIn(tween(600, delayMillis = 800)) +
                    slideInVertically(tween(600, delayMillis = 800)) { 60 }
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                // Status summary
                val allGranted = notificationAccessGranted && postNotificationsGranted
                if (!allGranted) {
                    Text(
                        text = "⚠ Some features will be limited without all permissions",
                        style = MaterialTheme.typography.bodySmall,
                        color = NeonCrimson.copy(alpha = 0.8f),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )
                }

                Button(
                    onClick = onAllGranted,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (allGranted) NeonTeal else SurfaceContainerHigh,
                        contentColor = if (allGranted) TextOnTeal else TextPrimary
                    ),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text(
                        text = if (allGranted) "LAUNCH DEBTDASH" else "CONTINUE ANYWAY",
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 2.sp
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(
                        imageVector = Icons.Default.ChevronRight,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

/**
 * Glassmorphic permission card with warning text for denied state.
 */
@Composable
private fun PermissionCard(
    icon: ImageVector,
    title: String,
    subtitle: String,
    isGranted: Boolean,
    grantedLabel: String,
    warningText: String,
    buttonLabel: String,
    accentColor: Color,
    onRequestPermission: () -> Unit
) {
    val borderColor = if (isGranted) accentColor.copy(alpha = 0.4f) else NeonCrimson.copy(alpha = 0.3f)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        GlassWhite,
                        Color.Transparent
                    )
                )
            )
            .border(1.dp, borderColor, RoundedCornerShape(16.dp))
            .padding(20.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            // Icon
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(accentColor.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = accentColor,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Title + subtitle
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall,
                    color = TextPrimary,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = TextMuted
                )
            }

            // Status indicator
            if (isGranted) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(accentColor.copy(alpha = 0.15f))
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = "Granted",
                            tint = accentColor,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = grantedLabel,
                            style = MaterialTheme.typography.labelSmall,
                            color = accentColor,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        // ── Warning text (shown when NOT granted) ──
        if (!isGranted) {
            Spacer(modifier = Modifier.height(14.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(NeonCrimson.copy(alpha = 0.08f))
                    .padding(12.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Warning",
                    tint = NeonCrimson,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = warningText,
                    style = MaterialTheme.typography.bodySmall,
                    color = NeonCrimson.copy(alpha = 0.9f),
                    lineHeight = 18.sp
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Enable button
            OutlinedButton(
                onClick = onRequestPermission,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(
                    width = 1.dp,
                    brush = Brush.horizontalGradient(
                        listOf(accentColor.copy(alpha = 0.5f), accentColor.copy(alpha = 0.3f))
                    )
                ),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = accentColor
                )
            ) {
                Text(
                    text = buttonLabel,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 2.sp
                )
            }
        }
    }
}

/**
 * Checks if DebtDash has Notification Listener access.
 */
private fun isNotificationAccessEnabled(context: Context): Boolean {
    val componentName = ComponentName(
        context.packageName,
        "com.debtdash.app.service.DebtDashNotificationService"
    )
    val enabledListeners = Settings.Secure.getString(
        context.contentResolver,
        "enabled_notification_listeners"
    ) ?: return false

    return enabledListeners.contains(componentName.flattenToString())
}
