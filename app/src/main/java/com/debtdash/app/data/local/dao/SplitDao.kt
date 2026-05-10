package com.debtdash.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.debtdash.app.data.local.entity.SplitEntity
import kotlinx.coroutines.flow.Flow

/**
 * DAO for split operations.
 */
@Dao
interface SplitDao {

    // ── Reads ──

    @Query("SELECT * FROM splits WHERE transactionId = :transactionId")
    fun getSplitsForTransaction(transactionId: Long): Flow<List<SplitEntity>>

    @Query("SELECT * FROM splits WHERE friendId = :friendId AND isPaid = 0")
    fun getUnpaidSplitsForFriend(friendId: Long): Flow<List<SplitEntity>>

    @Query("""
        SELECT COALESCE(SUM(amount), 0) 
        FROM splits 
        WHERE friendId = :friendId AND isPaid = 0
    """)
    suspend fun getTotalUnpaidForFriend(friendId: Long): Double

    // ── Writes ──

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(split: SplitEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(splits: List<SplitEntity>)

    @Query("UPDATE splits SET isPaid = 1 WHERE id = :id")
    suspend fun markPaid(id: Long)

    @Query("UPDATE splits SET isPaid = 1 WHERE friendId = :friendId")
    suspend fun markAllPaidForFriend(friendId: Long)

    @Query("DELETE FROM splits WHERE transactionId = :transactionId")
    suspend fun deleteForTransaction(transactionId: Long)
}
