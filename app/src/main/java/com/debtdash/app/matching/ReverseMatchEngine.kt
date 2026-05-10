package com.debtdash.app.matching

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.debtdash.app.MainActivity
import com.debtdash.app.R
import com.debtdash.app.data.local.dao.TransactionDao
import com.debtdash.app.data.local.entity.TransactionEntity
import com.debtdash.app.service.ParsedTransaction
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Reverse Match Engine.
 *
 * When a "Received" notification arrives, this engine checks for
 * matching unsettled "Sent" transactions to the same UPI ID
 * and offers to auto-settle them.
 */
@Singleton
class ReverseMatchEngine @Inject constructor(
    private val transactionDao: TransactionDao
) {

    companion object {
        private const val MATCH_CHANNEL_ID = "debtdash_match_channel"
        private const val MATCH_NOTIFICATION_ID = 2001
    }

    /**
     * Finds unsettled SENT transactions matching the received payment's UPI ID.
     */
    suspend fun findMatches(upiId: String): List<TransactionEntity> {
        return transactionDao.getUnsettledSentToUpi(upiId)
    }

    /**
     * Settles a pair of transactions (marks both as settled).
     */
    suspend fun settleMatch(receivedId: Long, sentId: Long) {
        transactionDao.markSettled(receivedId)
        transactionDao.markSettled(sentId)
    }

    /**
     * Settles all matching sent transactions up to the received amount.
     * Returns the remaining amount (if received > total sent).
     */
    suspend fun settleAll(receivedId: Long, matches: List<TransactionEntity>, receivedAmount: Double): Double {
        var remaining = receivedAmount

        for (match in matches) {
            if (remaining <= 0) break

            if (remaining >= match.amount) {
                // Full settle
                transactionDao.markSettled(match.id)
                remaining -= match.amount
            } else {
                // Partial — we can't partially settle in this version,
                // so we leave it unsettled but note the partial payment
                break
            }
        }

        // Mark the received transaction as settled
        transactionDao.markSettled(receivedId)

        return remaining
    }

    /**
     * Checks for matches and shows a notification if found.
     * Called from the NotificationListenerService.
     */
    suspend fun checkAndNotify(
        context: Context,
        received: ParsedTransaction,
        receivedTransactionId: Long
    ) {
        val upiId = received.upiId ?: return
        val matches = findMatches(upiId)

        if (matches.isEmpty()) return

        val totalOwed = matches.sumOf { it.amount }
        val displayAmount = formatAmount(received.amount)
        val displayName = upiId.substringBefore("@")

        showMatchNotification(
            context = context,
            amount = displayAmount,
            name = displayName,
            matchCount = matches.size,
            totalOwed = formatAmount(totalOwed)
        )
    }

    private fun showMatchNotification(
        context: Context,
        amount: String,
        name: String,
        matchCount: Int,
        totalOwed: String
    ) {
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Create channel
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                MATCH_CHANNEL_ID,
                "Payment Matches",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Notifications when incoming payments match existing debts"
                enableLights(true)
                lightColor = 0xFF0DFFDA.toInt()
            }
            notificationManager.createNotificationChannel(channel)
        }

        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra("navigate_to", "match")
        }
        val pendingIntent = PendingIntent.getActivity(
            context, 1, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, MATCH_CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_nag_notification)
            .setContentTitle(context.getString(R.string.match_found_title))
            .setContentText("₹$amount from $name matches $matchCount open debt(s) totaling ₹$totalOwed")
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText("₹$amount received from $name.\nThis matches $matchCount open debt(s) totaling ₹$totalOwed.\nTap to review and settle.")
            )
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setCategory(NotificationCompat.CATEGORY_SOCIAL)
            .setColor(0xFF0DFFDA.toInt())
            .build()

        notificationManager.notify(MATCH_NOTIFICATION_ID, notification)
    }

    private fun formatAmount(amount: Double): String {
        return if (amount == amount.toLong().toDouble()) {
            String.format("%,.0f", amount)
        } else {
            String.format("%,.2f", amount)
        }
    }
}
