package com.appfit.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "pending_email_items")
data class PendingEmailItem(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val sourceEmailId: String,
    val sourceEmailSubject: String,
    val sourceEmailFrom: String,
    val itemType: PendingItemType,
    val title: String,
    val payloadJson: String,
    val aiReason: String = "",
    val createdAt: Long = System.currentTimeMillis(),
    val status: PendingItemStatus = PendingItemStatus.PENDING
)

enum class PendingItemType {
    ACTIVITY, REMINDER, TODO;

    fun displayName() = when (this) {
        ACTIVITY -> "Attività"
        REMINDER -> "Promemoria"
        TODO -> "Da fare"
    }

    fun emoji() = when (this) {
        ACTIVITY -> "🏃"
        REMINDER -> "🔔"
        TODO -> "✅"
    }
}

enum class PendingItemStatus { PENDING, APPROVED, REJECTED }
