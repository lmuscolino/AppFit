package com.appfit.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDate
import java.time.LocalTime

@Entity(tableName = "meals")
data class Meal(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val description: String = "",
    val type: MealType,
    val scheduledDate: LocalDate,
    val scheduledTime: LocalTime? = null,
    val ingredients: List<String> = emptyList(),
    val caloriesKcal: Int = 0,
    val proteinG: Int = 0,
    val carbsG: Int = 0,
    val fatG: Int = 0,
    val isConsumed: Boolean = false,
    val aiGenerated: Boolean = false
)

enum class MealType {
    BREAKFAST, LUNCH, DINNER, SNACK;

    fun displayName(): String = when (this) {
        BREAKFAST -> "Colazione"
        LUNCH -> "Pranzo"
        DINNER -> "Cena"
        SNACK -> "Spuntino"
    }

    fun order(): Int = when (this) {
        BREAKFAST -> 0
        LUNCH -> 1
        SNACK -> 2
        DINNER -> 3
    }
}
