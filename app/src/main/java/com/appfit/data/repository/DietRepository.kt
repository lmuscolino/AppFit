package com.appfit.data.repository

import com.appfit.data.local.dao.DietPlanDao
import com.appfit.data.local.dao.MealDao
import com.appfit.data.model.DietPlan
import com.appfit.data.model.Meal
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DietRepository @Inject constructor(
    private val mealDao: MealDao,
    private val dietPlanDao: DietPlanDao
) {
    fun getMealsForDate(date: LocalDate): Flow<List<Meal>> =
        mealDao.getMealsForDate(date)

    fun getMealsForRange(start: LocalDate, end: LocalDate): Flow<List<Meal>> =
        mealDao.getMealsForRange(start, end)

    suspend fun getMealById(id: Long): Meal? = mealDao.getMealById(id)

    suspend fun insertMeal(meal: Meal): Long = mealDao.insertMeal(meal)

    suspend fun updateMeal(meal: Meal) = mealDao.updateMeal(meal)

    suspend fun setConsumed(id: Long, consumed: Boolean) = mealDao.setConsumed(id, consumed)

    suspend fun deleteMeal(id: Long) = mealDao.deleteMeal(id)

    fun getDatesWithMeals(start: LocalDate, end: LocalDate): Flow<List<LocalDate>> =
        mealDao.getDatesWithMeals(start, end)

    fun getActivePlanForDate(date: LocalDate): Flow<DietPlan?> =
        dietPlanDao.getActivePlanForDate(date)

    fun getAllPlans(): Flow<List<DietPlan>> = dietPlanDao.getAllPlans()

    suspend fun insertPlan(plan: DietPlan): Long = dietPlanDao.insertPlan(plan)

    suspend fun activatePlan(id: Long) {
        dietPlanDao.deactivateAllPlans()
        dietPlanDao.activatePlan(id)
    }

    suspend fun deletePlan(id: Long) = dietPlanDao.deletePlan(id)
}
