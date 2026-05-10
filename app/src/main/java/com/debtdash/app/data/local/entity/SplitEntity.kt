package com.debtdash.app.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

// ══════════════════════════════════════════════════
//  Split Entity
//  Represents a single person's share in a split transaction.
// ══════════════════════════════════════════════════

@Entity(
    tableName = "splits",
    indices = [
        Index(value = ["transactionId"]),
        Index(value = ["friendId"]),
        Index(value = ["isPaid"])
    ],
    foreignKeys = [
        ForeignKey(
            entity = TransactionEntity::class,
            parentColumns = ["id"],
            childColumns = ["transactionId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = FriendEntity::class,
            parentColumns = ["id"],
            childColumns = ["friendId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class SplitEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    /** FK to the parent transaction */
    val transactionId: Long,

    /** FK to the friend who owes / is owed */
    val friendId: Long,

    /** This person's share amount */
    val amount: Double,

    /** How the split was calculated */
    val model: SplitModel = SplitModel.EQUAL,

    /** Whether this individual split has been settled */
    val isPaid: Boolean = false,

    /** Epoch millis when this split was created */
    val createdAt: Long = System.currentTimeMillis()
)
