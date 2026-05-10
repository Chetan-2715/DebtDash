package com.debtdash.app.service

import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import android.util.Log
import com.debtdash.app.data.local.entity.TransactionType
import com.debtdash.app.data.repository.DebtRepository
import com.debtdash.app.matching.ReverseMatchEngine
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

/**
 * Notification Interceptor Service.
 *
 * Listens for GPay notifications, parses them, stores in Room,
 * and triggers reverse matching for incoming payments.
 *
 * Filtered package: com.google.android.apps.nbu.paisa.user (Google Pay India)
 *
 * NOTE: NotificationListenerService cannot use @AndroidEntryPoint directly
 * because Hilt doesn't support the NLS lifecycle. Instead, we use
 * EntryPointAccessors to grab dependencies from the application component.
 */
class DebtDashNotificationService : NotificationListenerService() {

    companion object {
        private const val TAG = "DebtDashNLS"
        private const val GPAY_PACKAGE = "com.google.android.apps.nbu.paisa.user"
    }

    /**
     * Hilt EntryPoint for manual DI into NotificationListenerService.
     */
    @EntryPoint
    @InstallIn(SingletonComponent::class)
    interface NotificationServiceEntryPoint {
        fun repository(): DebtRepository
        fun reverseMatchEngine(): ReverseMatchEngine
    }

    private lateinit var repository: DebtRepository
    private lateinit var reverseMatchEngine: ReverseMatchEngine
    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun onCreate() {
        super.onCreate()
        // Manually inject dependencies via Hilt EntryPoint
        try {
            val entryPoint = EntryPointAccessors.fromApplication(
                applicationContext,
                NotificationServiceEntryPoint::class.java
            )
            repository = entryPoint.repository()
            reverseMatchEngine = entryPoint.reverseMatchEngine()
            Log.i(TAG, "Notification service initialized with DI")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to initialize DI: ${e.message}", e)
        }
    }

    override fun onNotificationPosted(sbn: StatusBarNotification?) {
        sbn ?: return

        // Safety: ensure DI was successful
        if (!::repository.isInitialized) {
            Log.w(TAG, "Repository not initialized, skipping notification")
            return
        }

        // ── Filter: Only process GPay notifications ──
        if (sbn.packageName != GPAY_PACKAGE) return

        val notification = sbn.notification ?: return
        val extras = notification.extras ?: return

        // Extract ALL text fields from the notification
        val title = extras.getCharSequence("android.title")?.toString() ?: ""
        val text = extras.getCharSequence("android.text")?.toString() ?: ""
        val bigText = extras.getCharSequence("android.bigText")?.toString() ?: ""
        val subText = extras.getCharSequence("android.subText")?.toString() ?: ""
        val infoText = extras.getCharSequence("android.infoText")?.toString() ?: ""
        val summaryText = extras.getCharSequence("android.summaryText")?.toString() ?: ""

        // Build the most comprehensive text available
        val fullText = bigText.ifBlank { text }.ifBlank { title }

        Log.d(TAG, "─── GPay Notification Intercepted ───")
        Log.d(TAG, "  title   : $title")
        Log.d(TAG, "  text    : $text")
        Log.d(TAG, "  bigText : $bigText")
        Log.d(TAG, "  subText : $subText")
        Log.d(TAG, "  info    : $infoText")
        Log.d(TAG, "  summary : $summaryText")
        Log.d(TAG, "─────────────────────────────────────")

        if (fullText.isBlank() && title.isBlank()) {
            Log.d(TAG, "Empty GPay notification, skipping")
            return
        }

        // ── Parse using the improved multi-strategy parser ──
        val parsed = TransactionParser.parseFromTitleAndText(
            title = title,
            text = text,
            bigText = bigText
        )

        if (parsed == null) {
            Log.w(TAG, "Could not parse GPay notification. Storing raw text.")
            // Store as raw unparsed transaction so the user can see it
            serviceScope.launch {
                repository.insertTransaction(
                    com.debtdash.app.data.local.entity.TransactionEntity(
                        rawNotificationText = "[$title] $text".trim(),
                        amount = 0.0,
                        type = TransactionType.SENT,
                        reason = null
                    )
                )
            }
            return
        }

        Log.i(TAG, "✅ Parsed: ${parsed.type} ₹${parsed.amount} | " +
                "UPI: ${parsed.upiId ?: "N/A"} | Name: ${parsed.contactName ?: "N/A"}")

        // ── Store in Room DB ──
        serviceScope.launch {
            val transactionId = repository.insertFromNotification(
                rawText = "[$title] $text".trim(),
                amount = parsed.amount,
                type = parsed.type,
                upiId = parsed.upiId
            )

            Log.i(TAG, "Stored transaction #$transactionId: ${parsed.type} ₹${parsed.amount}")

            // ── Reverse Match for incoming payments ──
            if (parsed.type == TransactionType.RECEIVED && parsed.upiId != null) {
                reverseMatchEngine.checkAndNotify(
                    context = applicationContext,
                    received = parsed,
                    receivedTransactionId = transactionId
                )
            }
        }
    }

    override fun onNotificationRemoved(sbn: StatusBarNotification?) {
        // No action needed when notifications are dismissed
    }

    override fun onDestroy() {
        super.onDestroy()
        serviceScope.cancel()
    }
}
