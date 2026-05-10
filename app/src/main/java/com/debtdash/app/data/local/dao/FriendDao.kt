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
     * Cumulative debt tracker (High Performance):
     * Returns net debt per friend by summing direct transactions AND splits.
     * Positive netDebt = they owe you.
     */
    @Query("""
        SELECT 
            f.id,
            f.name,
            f.avatarInitials,
            f.upiId,
            (
                COALESCE((SELECT SUM(t.amount) FROM transactions t WHERE t.friendId = f.id AND t.type = 'SENT' AND t.isSettled = 0), 0) +
                COALESCE((SELECT SUM(s.amount) FROM splits s WHERE s.friendId = f.id AND s.isPaid = 0), 0) -
                COALESCE((SELECT SUM(t.amount) FROM transactions t WHERE t.friendId = f.id AND t.type = 'RECEIVED' AND t.isSettled = 0), 0)
            ) AS netDebt
        FROM friends f
        WHERE f.contactType = 'FRIEND'
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
