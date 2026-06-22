package com.appfit.data.model

import java.time.LocalDate

// Modello aggregato usato dalla UI — non una Room Entity
data class DailyPlan(
    val date: LocalDate,
    val activities: List<Activity> = emptyList(),
    val meals: List<Meal> = emptyList(),
    val activeDietPlan: DietPlan? = null,
    val reminders: List<Reminder> = emptyList()
) {
    val totalCaloriesConsumed: Int
        get() = meals.filter { it.isConsumed }.sumOf { it.caloriesKcal }

    val totalCaloriesPlanned: Int
        get() = meals.sumOf { it.caloriesKcal }

    val totalCaloriesBurned: Int
        get() = activities.filter { it.isCompleted }.sumOf { it.caloriesBurned }

    val totalProteinG: Int
        get() = meals.filter { it.isConsumed }.sumOf { it.proteinG }

    val totalCarbsG: Int
        get() = meals.filter { it.isConsumed }.sumOf { it.carbsG }

    val totalFatG: Int
        get() = meals.filter { it.isConsumed }.sumOf { it.fatG }
}
