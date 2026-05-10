package com.debtdash.app.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.debtdash.app.data.local.dao.TransactionDao
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

/**
 * Nag Engine — Periodic Worker.
 *
 * Runs every 60 minutes to check for transactions with null reasons.
 * If any are found, fires a local notification reminding the user to tag them.
 */
@HiltWorker
class NagWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val transactionDao: TransactionDao
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        return try {
            val unreasonedCount = transactionDao.getUnreasonedCount()

            if (unreasonedCount > 0) {
                NagNotificationHelper.show(
                    context = applicationContext,
                    count = unreasonedCount
                )
            }

            Result.success()
        } catch (e: Exception) {
            // Retry on failure (WorkManager will respect backoff)
            Result.retry()
        }
    }
}
