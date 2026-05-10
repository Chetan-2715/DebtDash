package com.debtdash.app.di

import com.debtdash.app.worker.NagWorker
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

/**
 * Hilt module for WorkManager integration.
 * The @HiltWorker annotation on NagWorker handles assisted injection.
 * This module ensures the WorkManager component is available.
 */
@Module
@InstallIn(SingletonComponent::class)
object WorkerModule {
    // HiltWorkerFactory is automatically provided by the
    // androidx.hilt:hilt-work library when using @HiltWorker.
    // No explicit bindings needed here.
}
