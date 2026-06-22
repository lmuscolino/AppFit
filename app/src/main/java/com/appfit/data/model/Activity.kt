package com.appfit.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.Instant
import java.time.LocalDate
import java.time.LocalTime

@Entity(tableName = "activities")
data class Activity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val description: String,
    val type: ActivityType,
    val durationMinutes: Int,
    val scheduledDate: LocalDate,
    val scheduledTime: LocalTime? = null,
    val isCompleted: Boolean = false,
    val isImportant: Boolean = false,
    val caloriesBurned: Int = 0,
    val aiGenerated: Boolean = false,
    val createdAt: Instant = Instant.now()
)

enum class ActivityType {
    CARDIO, STRENGTH, FLEXIBILITY, YOGA, REST, CUSTOM;

    fun displayName(): String = when (this) {
        CARDIO -> "Cardio"
        STRENGTH -> "Forza"
        FLEXIBILITY -> "Flessibilità"
        YOGA -> "Yoga"
        REST -> "Riposo"
        CUSTOM -> "Personalizzata"
    }
}
