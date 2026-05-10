package com.debtdash.app.data.repository

import com.debtdash.app.data.local.dao.FriendDao
import com.debtdash.app.data.local.dao.FriendDebtSummary
import com.debtdash.app.data.local.dao.SplitDao
import com.debtdash.app.data.local.dao.TransactionDao
import com.debtdash.app.data.local.entity.FriendEntity
import com.debtdash.app.data.local.entity.SplitEntity
import com.debtdash.app.data.local.entity.SplitModel
import com.debtdash.app.data.local.entity.TransactionEntity
import com.debtdash.app.data.local.entity.TransactionType
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Single source of truth for all DebtDash data operations.
 * Coordinates between Transaction, Friend, and Split DAOs.
 */
@Singleton
class DebtRepository @Inject constructor(
    private val transactionDao: TransactionDao,
    private val friendDao: FriendDao,
    private val splitDao: SplitDao
) {

    // ═══════════════════════════════════════
    //  Transactions
    // ═══════════════════════════════════════

    fun getAllTransactions(): Flow<List<TransactionEntity>> =
        transactionDao.getAll()

    fun getTransactionsByFriend(friendId: Long): Flow<List<TransactionEntity>> =
        transactionDao.getByFriend(friendId)

    fun getUnreasonedTransactions(): Flow<List<TransactionEntity>> =
        transactionDao.getUnreasonedTransactions()

    fun getUnsettledTransactions(): Flow<List<TransactionEntity>> =
        transactionDao.getUnsettledTransactions()

    fun getUnmatchedReceived(): Flow<List<TransactionEntity>> =
        transactionDao.getUnmatchedReceived()

    suspend fun getUnreasonedCount(): Int =
        transactionDao.getUnreasonedCount()

    suspend fun insertTransaction(transaction: TransactionEntity): Long =
        transactionDao.insert(transaction)

    suspend fun updateTransactionReason(id: Long, reason: String?) =
        transactionDao.updateReason(id, reason ?: "")

    suspend fun markTransactionSettled(id: Long) =
        transactionDao.markSettled(id)

    suspend fun linkTransactionToFriend(transactionId: Long, friendId: Long) =
        transactionDao.linkFriend(transactionId, friendId)

    suspend fun getUnsettledSentToUpi(upiId: String): List<TransactionEntity> =
        transactionDao.getUnsettledSentToUpi(upiId)

    // ═══════════════════════════════════════
    //  Friends
    // ═══════════════════════════════════════

    fun getAllFriends(): Flow<List<FriendEntity>> =
        friendDao.getAll()

    fun searchFriends(query: String): Flow<List<FriendEntity>> =
        friendDao.searchByName(query)

    fun getDebtSummary(): Flow<List<FriendDebtSummary>> =
        friendDao.getDebtSummary()

    suspend fun getFriendByUpiId(upiId: String): FriendEntity? =
        friendDao.getByUpiId(upiId)

    suspend fun insertFriend(friend: FriendEntity): Long =
        friendDao.insert(friend)

    // ═══════════════════════════════════════
    //  Splits
    // ═══════════════════════════════════════

    fun getSplitsForTransaction(transactionId: Long): Flow<List<SplitEntity>> =
        splitDao.getSplitsForTransaction(transactionId)

    fun getUnpaidSplitsForFriend(friendId: Long): Flow<List<SplitEntity>> =
        splitDao.getUnpaidSplitsForFriend(friendId)

    /**
     * Creates an equal split for a transaction among the given friends.
     * The transaction amount is divided equally.
     */
    suspend fun createEqualSplit(transactionId: Long, amount: Double, friendIds: List<Long>) {
        val splitAmount = amount / friendIds.size
        val splits = friendIds.map { friendId ->
            SplitEntity(
                transactionId = transactionId,
                friendId = friendId,
                amount = splitAmount,
                model = SplitModel.EQUAL
            )
        }
        splitDao.insertAll(splits)
    }

    /**
     * Creates custom splits with specified amounts per friend.
     */
    suspend fun createCustomSplits(
        transactionId: Long,
        splits: Map<Long, Double>  // friendId -> amount
    ) {
        val splitEntities = splits.map { (friendId, amount) ->
            SplitEntity(
                transactionId = transactionId,
                friendId = friendId,
                amount = amount,
                model = SplitModel.CUSTOM
            )
        }
        splitDao.insertAll(splitEntities)
    }

    suspend fun markSplitPaid(splitId: Long) =
        splitDao.markPaid(splitId)

    // ═══════════════════════════════════════
    //  Composite Operations
    // ═══════════════════════════════════════

    /**
     * Inserts a new transaction from a parsed notification,
     * auto-linking to an existing friend if the UPI ID matches.
     */
    suspend fun insertFromNotification(
        rawText: String,
        amount: Double,
        type: TransactionType,
        upiId: String?
    ): Long {
        // Try to find an existing friend by UPI ID
        val friend = upiId?.let { friendDao.getByUpiId(it) }

        val transaction = TransactionEntity(
            rawNotificationText = rawText,
            amount = amount,
            type = type,
            upiId = upiId,
            friendId = friend?.id
        )

        return transactionDao.insert(transaction)
    }
}
