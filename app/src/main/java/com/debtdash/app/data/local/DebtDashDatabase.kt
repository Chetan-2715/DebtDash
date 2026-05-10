package com.debtdash.app.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.debtdash.app.data.local.converter.Converters
import com.debtdash.app.data.local.dao.FriendDao
import com.debtdash.app.data.local.dao.SplitDao
import com.debtdash.app.data.local.dao.TransactionDao
import com.debtdash.app.data.local.entity.FriendEntity
import com.debtdash.app.data.local.entity.SplitEntity
import com.debtdash.app.data.local.entity.TransactionEntity

/**
 * DebtDash Room Database — Version 1
 *
 * Entities:
 *  - TransactionEntity: Intercepted GPay notifications
 *  - FriendEntity: Contacts in the debt network
 *  - SplitEntity: Individual shares in split transactions
 */
@Database(
    entities = [
        TransactionEntity::class,
        FriendEntity::class,
        SplitEntity::class
    ],
    version = 1,
    exportSchema = true
)
@TypeConverters(Converters::class)
abstract class DebtDashDatabase : RoomDatabase() {
    abstract fun transactionDao(): TransactionDao
    abstract fun friendDao(): FriendDao
    abstract fun splitDao(): SplitDao
}
