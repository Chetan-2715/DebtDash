package com.debtdash.app.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

// ══════════════════════════════════════════════════
//  Transaction Types
// ══════════════════════════════════════════════════

enum class TransactionType { SENT, RECEIVED }

// ══════════════════════════════════════════════════
//  Split Models
// ══════════════════════════════════════════════════

enum class SplitModel { EQUAL, CUSTOM }

// ══════════════════════════════════════════════════
//  Transaction Entity
//  Stores every intercepted GPay notification.
//  A null `reason` triggers the Nag Engine.
// ══════════════════════════════════════════════════

@Entity(
    tableName = "transactions",
    indices = [
        Index(value = ["friendId"]),
        Index(value = ["upiId"]),
        Index(value = ["isSettled"]),
        Index(value = ["type"])
    ],
    foreignKeys = [
        ForeignKey(
            entity = FriendEntity::class,
            parentColumns = ["id"],
            childColumns = ["friendId"],
            onDelete = ForeignKey.SET_NULL
        )
    ]
)
data class TransactionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    /** The full raw notification text as received from GPay */
    val rawNotificationText: String,

    /** Parsed amount in INR */
    val amount: Double,

    /** SENT or RECEIVED */
    val type: TransactionType,

    /** User-assigned reason — null means "needs nagging" */
    val reason: String? = null,

    /** The UPI ID extracted from the notification (e.g., "arjun@okaxis") */
    val upiId: String? = null,

    /** Epoch millis when the transaction was intercepted */
    val timestamp: Long = System.currentTimeMillis(),

    /** Whether this transaction has been settled via reverse matching */
    val isSettled: Boolean = false,

    /** FK to friends table (nullable — might not be linked yet) */
    val friendId: Long? = null
)
