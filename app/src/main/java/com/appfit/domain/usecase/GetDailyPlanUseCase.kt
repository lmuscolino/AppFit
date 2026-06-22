package com.appfit.domain.usecase

import com.appfit.data.model.DailyPlan
import com.appfit.data.repository.ActivityRepository
import com.appfit.data.repository.DietRepository
import com.appfit.data.repository.ReminderRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import java.time.LocalDate
import javax.inject.Inject

class GetDailyPlanUseCase @Inject constructor(
    private val activityRepository: ActivityRepository,
    private val dietRepository: DietRepository,
    private val reminderRepository: ReminderRepository
) {
    operator fun invoke(date: LocalDate): Flow<DailyPlan> =
        combine(
            activityRepository.getActivitiesForDate(date),
            dietRepository.getMealsForDate(date),
            dietRepository.getActivePlanForDate(date),
            reminderRepository.getRemindersForDate(date)
        ) { activities, meals, activePlan, reminders ->
            DailyPlan(
                date = date,
                activities = activities,
                meals = meals.sortedBy { it.type.order() },
                activeDietPlan = activePlan,
                reminders = reminders
            )
        }
}
