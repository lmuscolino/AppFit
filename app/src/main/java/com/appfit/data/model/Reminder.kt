package com.appfit.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDate

@Entity(tableName = "reminders")
data class Reminder(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val description: String = "",
    val category: ReminderCategory,
    val dueDate: LocalDate? = null,
    val amount: Float? = null,
    val isCompleted: Boolean = false,
    val isImportant: Boolean = false,
    val sourceEmail: String? = null,
    val createdAt: LocalDate = LocalDate.now()
)

enum class ReminderCategory {
    BILL, CAR, DOCUMENT, HEALTH, SUBSCRIPTION, OTHER;

    fun displayName(): String = when (this) {
        BILL -> "Bolletta"
        CAR -> "Auto"
        DOCUMENT -> "Documento"
        HEALTH -> "Salute"
        SUBSCRIPTION -> "Abbonamento"
        OTHER -> "Altro"
    }

    fun emoji(): String = when (this) {
        BILL -> "💡"
        CAR -> "🚗"
        DOCUMENT -> "📄"
        HEALTH -> "🏥"
        SUBSCRIPTION -> "📱"
        OTHER -> "📌"
    }
}
