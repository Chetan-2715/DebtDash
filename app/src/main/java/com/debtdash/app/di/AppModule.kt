package com.debtdash.app.di

import android.content.Context
import androidx.room.Room
import com.debtdash.app.data.local.DebtDashDatabase
import com.debtdash.app.data.local.dao.FriendDao
import com.debtdash.app.data.local.dao.SplitDao
import com.debtdash.app.data.local.dao.TransactionDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Hilt module providing Room database and DAO instances.
 */
@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideDatabase(
        @ApplicationContext context: Context
    ): DebtDashDatabase {
        return Room.databaseBuilder(
            context,
            DebtDashDatabase::class.java,
            "debtdash_db"
        )
            .fallbackToDestructiveMigration(true)
            .build()
    }

    @Provides
    fun provideTransactionDao(db: DebtDashDatabase): TransactionDao =
        db.transactionDao()

    @Provides
    fun provideFriendDao(db: DebtDashDatabase): FriendDao =
        db.friendDao()

    @Provides
    fun provideSplitDao(db: DebtDashDatabase): SplitDao =
        db.splitDao()
}
