package com.appfit.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDate

@Entity(tableName = "diet_plans")
data class DietPlan(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val description: String = "",
    val startDate: LocalDate,
    val endDate: LocalDate,
    val dailyCalorieGoal: Int = 2000,
    val dailyProteinGoalG: Int = 150,
    val dailyCarbsGoalG: Int = 200,
    val dailyFatGoalG: Int = 65,
    val isActive: Boolean = false
)
