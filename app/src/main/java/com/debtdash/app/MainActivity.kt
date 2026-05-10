package com.debtdash.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.debtdash.app.ui.navigation.DebtDashApp
import com.debtdash.app.ui.theme.BackgroundPure
import com.debtdash.app.ui.theme.DebtDashTheme
import com.debtdash.app.worker.NagWorker
import dagger.hilt.android.AndroidEntryPoint
import java.util.concurrent.TimeUnit

/**
 * Single-Activity host for the entire DebtDash Compose UI.
 * Schedules the Nag Engine on first launch.
 */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Schedule the Nag Engine (60-minute periodic check)
        scheduleNagEngine()

        setContent {
            DebtDashTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = BackgroundPure
                ) {
                    DebtDashApp()
                }
            }
        }
    }

    private fun scheduleNagEngine() {
        try {
            val constraints = Constraints.Builder()
                .setRequiresBatteryNotLow(true)
                .build()

            val nagRequest = PeriodicWorkRequestBuilder<NagWorker>(
                60, TimeUnit.MINUTES
            )
                .setConstraints(constraints)
                .addTag("nag_engine")
                .build()

            WorkManager.getInstance(applicationContext).enqueueUniquePeriodicWork(
                "nag_engine",
                ExistingPeriodicWorkPolicy.KEEP,
                nagRequest
            )
        } catch (e: Exception) {
            // WorkManager might not be initialized yet — will be scheduled next time
            android.util.Log.w("DebtDash", "WorkManager not ready: ${e.message}")
        }
    }
}
