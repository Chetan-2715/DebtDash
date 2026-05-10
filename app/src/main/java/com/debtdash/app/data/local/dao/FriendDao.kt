package com.debtdash.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.debtdash.app.data.local.entity.FriendEntity
import kotlinx.coroutines.flow.Flow

/**
 * Data class for the cumulative debt summary per friend.
 */
data class FriendDebtSummary(
    val id: Long,
    val name: String,
    val avatarInitials: String,
    val upiId: String?,
    val netDebt: Double   // positive = they owe you, negative = you owe them
)

/**
 * DAO for friend / contact operations.
 */
@Dao
interface FriendDao {

    // ── Reads ──

    @Query("SELECT * FROM friends ORDER BY name ASC")
    fun getAll(): Flow<List<FriendEntity>>

    @Query("SELECT * FROM friends WHERE name LIKE '%' || :query || '%' ORDER BY name ASC")
    fun searchByName(query: String): Flow<List<FriendEntity>>

    @Query("SELECT * FROM friends WHERE id = :id")
    suspend fun getById(id: Long): FriendEntity?

    @Query("SELECT * FROM friends WHERE upiId = :upiId LIMIT 1")
    suspend fun getByUpiId(upiId: String): FriendEntity?

    /**
     * Cumulative debt tracker:
     * Returns net debt per friend.
     * Positive netDebt = they owe you (you sent more than received).
     * Negative netDebt = you owe them.
     */
    @Query("""
        SELECT 
            f.id,
            f.name,
            f.avatarInitials,
            f.upiId,
            COALESCE(SUM(CASE WHEN t.type = 'SENT' THEN t.amount ELSE 0 END), 0) -
            COALESCE(SUM(CASE WHEN t.type = 'RECEIVED' THEN t.amount ELSE 0 END), 0) 
            AS netDebt
        FROM friends f
        LEFT JOIN transactions t ON t.friendId = f.id AND t.isSettled = 0
        GROUP BY f.id
        ORDER BY netDebt DESC
    """)
    fun getDebtSummary(): Flow<List<FriendDebtSummary>>

    // ── Writes ──

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(friend: FriendEntity): Long

    @Query("DELETE FROM friends WHERE id = :id")
    suspend fun delete(id: Long)

    @Query("UPDATE friends SET upiId = :upiId WHERE id = :id")
    suspend fun linkUpiId(id: Long, upiId: String)
}
