package com.debtdash.app.worker

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.debtdash.app.MainActivity
import com.debtdash.app.R

/**
 * Helper for building and showing Nag Engine notifications.
 * Creates a dedicated notification channel for transaction reminders.
 */
object NagNotificationHelper {

    private const val CHANNEL_ID = "debtdash_nag_channel"
    private const val NOTIFICATION_ID = 1001

    /**
     * Shows a notification reminding the user about untagged transactions.
     */
    fun show(context: Context, count: Int) {
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Create channel (required for API 26+)
        createChannelIfNeeded(notificationManager, context)

        // Tap opens the app
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra("navigate_to", "pending")
        }
        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_nag_notification)
            .setContentTitle(context.getString(R.string.nag_title))
            .setContentText(context.getString(R.string.nag_text, count))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setCategory(NotificationCompat.CATEGORY_REMINDER)
            .setColor(0xFF0DFFDA.toInt()) // Neon Teal
            .build()

        notificationManager.notify(NOTIFICATION_ID, notification)
    }

    private fun createChannelIfNeeded(manager: NotificationManager, context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                context.getString(R.string.nag_channel_name),
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = context.getString(R.string.nag_channel_description)
                enableLights(true)
                lightColor = 0xFF0DFFDA.toInt()
            }
            manager.createNotificationChannel(channel)
        }
    }
}
