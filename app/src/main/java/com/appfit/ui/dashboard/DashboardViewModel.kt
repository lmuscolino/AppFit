package com.appfit.ui.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.appfit.data.model.DailyPlan
import com.appfit.data.repository.ActivityRepository
import com.appfit.data.repository.DietRepository
import com.appfit.domain.usecase.GetDailyPlanUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import java.time.DayOfWeek
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val getDailyPlanUseCase: GetDailyPlanUseCase,
    private val activityRepository: ActivityRepository,
    private val dietRepository: DietRepository
) : ViewModel() {

    private val today = LocalDate.now()
    private val weekStart = today.with(DayOfWeek.MONDAY)
    private val weekEnd = weekStart.plusDays(6)

    /** Piano completo di oggi (3 flow Room). */
    val todayPlan: StateFlow<DailyPlan> = getDailyPlanUseCase(today)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), DailyPlan(today))

    /**
     * Giorni della settimana corrente che hanno almeno un'attività o un pasto.
     * Usa solo 2 flow Room (range) invece di 21 (7 × 3 DailyPlan combine).
     */
    val weeklyDaysWithContent: StateFlow<Set<LocalDate>> = combine(
        activityRepository.getActivitiesForRange(weekStart, weekEnd),
        dietRepository.getMealsForRange(weekStart, weekEnd)
    ) { activities, meals ->
        buildSet {
            activities.forEach { add(it.scheduledDate) }
            meals.forEach { add(it.scheduledDate) }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptySet())
}
