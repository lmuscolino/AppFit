package com.appfit.data.local.dao

import androidx.room.*
import com.appfit.data.model.DietPlan
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

@Dao
interface DietPlanDao {

    @Query("SELECT * FROM diet_plans WHERE isActive = 1 AND startDate <= :date AND endDate >= :date LIMIT 1")
    fun getActivePlanForDate(date: LocalDate): Flow<DietPlan?>

    @Query("SELECT * FROM diet_plans ORDER BY startDate DESC")
    fun getAllPlans(): Flow<List<DietPlan>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlan(plan: DietPlan): Long

    @Update
    suspend fun updatePlan(plan: DietPlan)

    @Query("UPDATE diet_plans SET isActive = 0")
    suspend fun deactivateAllPlans()

    @Query("UPDATE diet_plans SET isActive = 1 WHERE id = :id")
    suspend fun activatePlan(id: Long)

    @Query("DELETE FROM diet_plans WHERE id = :id")
    suspend fun deletePlan(id: Long)
}
