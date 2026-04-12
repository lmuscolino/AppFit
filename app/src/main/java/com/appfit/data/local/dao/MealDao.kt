package com.appfit.data.local.dao

import androidx.room.*
import com.appfit.data.model.Meal
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

@Dao
interface MealDao {

    @Query("SELECT * FROM meals WHERE scheduledDate = :date ORDER BY type ASC")
    fun getMealsForDate(date: LocalDate): Flow<List<Meal>>

    @Query("SELECT * FROM meals WHERE scheduledDate BETWEEN :start AND :end ORDER BY scheduledDate ASC, type ASC")
    fun getMealsForRange(start: LocalDate, end: LocalDate): Flow<List<Meal>>

    @Query("SELECT * FROM meals WHERE id = :id")
    suspend fun getMealById(id: Long): Meal?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMeal(meal: Meal): Long

    @Update
    suspend fun updateMeal(meal: Meal)

    @Query("UPDATE meals SET isConsumed = :consumed WHERE id = :id")
    suspend fun setConsumed(id: Long, consumed: Boolean)

    @Query("DELETE FROM meals WHERE id = :id")
    suspend fun deleteMeal(id: Long)

    @Query("SELECT DISTINCT scheduledDate FROM meals WHERE scheduledDate BETWEEN :start AND :end")
    fun getDatesWithMeals(start: LocalDate, end: LocalDate): Flow<List<LocalDate>>
}
