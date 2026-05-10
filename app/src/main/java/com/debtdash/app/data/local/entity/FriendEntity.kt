package com.debtdash.app.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

// ══════════════════════════════════════════════════
//  Friend Entity
//  Represents a contact in the user's debt network.
// ══════════════════════════════════════════════════

@Entity(
    tableName = "friends",
    indices = [
        Index(value = ["name"]),
        Index(value = ["phone"], unique = true)
    ]
)
data class FriendEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    /** Display name */
    val name: String,

    /** Phone number (optional) */
    val phone: String? = null,

    /** Two-letter initials for the avatar circle (e.g., "AK") */
    val avatarInitials: String,

    /** UPI ID associated with this friend (for auto-matching) */
    val upiId: String? = null,

    /** Epoch millis when this friend was added */
    val createdAt: Long = System.currentTimeMillis()
)
