package com.appfit.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.Instant

@Entity(tableName = "favorite_recipes")
data class FavoriteRecipe(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val mealType: MealType,
    val ingredients: List<String> = emptyList(),
    val caloriesKcal: Int = 0,
    val proteinG: Int = 0,
    val carbsG: Int = 0,
    val fatG: Int = 0,
    val addedAt: Instant = Instant.now()
)
