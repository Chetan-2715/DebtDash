package com.debtdash.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.debtdash.app.data.local.entity.TransactionEntity
import kotlinx.coroutines.flow.Flow

/**
 * DAO for transaction operations.
 */
@Dao
interface TransactionDao {

    // ── Reads ──

    @Query("SELECT * FROM transactions ORDER BY timestamp DESC")
    fun getAll(): Flow<List<TransactionEntity>>

    @Query("SELECT * FROM transactions WHERE friendId = :friendId ORDER BY timestamp DESC")
    fun getByFriend(friendId: Long): Flow<List<TransactionEntity>>

    @Query("SELECT * FROM transactions WHERE reason IS NULL ORDER BY timestamp DESC")
    fun getUnreasonedTransactions(): Flow<List<TransactionEntity>>

    @Query("SELECT COUNT(*) FROM transactions WHERE reason IS NULL")
    suspend fun getUnreasonedCount(): Int

    @Query("SELECT * FROM transactions WHERE id = :id")
    suspend fun getById(id: Long): TransactionEntity?

    @Query("""
        SELECT * FROM transactions 
        WHERE upiId = :upiId 
        AND type = 'SENT' 
        AND isSettled = 0 
        ORDER BY timestamp ASC
    """)
    suspend fun getUnsettledSentToUpi(upiId: String): List<TransactionEntity>

    @Query("SELECT * FROM transactions WHERE isSettled = 0 ORDER BY timestamp DESC")
    fun getUnsettledTransactions(): Flow<List<TransactionEntity>>

    @Query("""
        SELECT * FROM transactions 
        WHERE type = 'RECEIVED' AND isSettled = 0 
        ORDER BY timestamp DESC
    """)
    fun getUnmatchedReceived(): Flow<List<TransactionEntity>>

    // ── Writes ──

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(transaction: TransactionEntity): Long

    @Query("UPDATE transactions SET reason = :reason WHERE id = :id")
    suspend fun updateReason(id: Long, reason: String)

    @Query("UPDATE transactions SET isSettled = 1 WHERE id = :id")
    suspend fun markSettled(id: Long)

    @Query("UPDATE transactions SET friendId = :friendId WHERE id = :id")
    suspend fun linkFriend(id: Long, friendId: Long)

    @Query("DELETE FROM transactions WHERE id = :id")
    suspend fun delete(id: Long)
}
