package com.appfit.ui.calendar

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.appfit.data.model.Activity
import com.appfit.data.model.ActivityType
import com.appfit.data.model.DailyPlan
import com.appfit.data.model.Meal
import com.appfit.data.model.MealType
import com.appfit.ai.GoogleCalendarService
import com.appfit.data.repository.ActivityRepository
import com.appfit.data.repository.DietRepository
import com.appfit.data.repository.ReminderRepository
import com.appfit.domain.usecase.GetDailyPlanUseCase
import com.appfit.ui.common.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalTime
import java.time.YearMonth
import javax.inject.Inject

@HiltViewModel
class CalendarViewModel @Inject constructor(
    private val getDailyPlanUseCase: GetDailyPlanUseCase,
    private val activityRepository: ActivityRepository,
    private val dietRepository: DietRepository,
    private val reminderRepository: ReminderRepository,
    private val googleCalendarService: GoogleCalendarService
) : ViewModel() {

    private val _selectedDate = MutableStateFlow(LocalDate.now())
    val selectedDate: StateFlow<LocalDate> = _selectedDate.asStateFlow()

    private val _currentMonth = MutableStateFlow(YearMonth.now())
    val currentMonth: StateFlow<YearMonth> = _currentMonth.asStateFlow()

    @OptIn(ExperimentalCoroutinesApi::class)
    val dailyPlanState: StateFlow<UiState<DailyPlan>> = _selectedDate
        .flatMapLatest { date ->
            getDailyPlanUseCase(date).map { UiState.Success(it) }
        }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            UiState.Loading
        )

    // Dates that have any activity or meal planned (for calendar dot indicators)
    val datesWithContent: StateFlow<Set<LocalDate>> = _currentMonth
        .flatMapLatest { month ->
            val start = month.atDay(1)
            val end = month.atEndOfMonth()
            combine(
                activityRepository.getDatesWithActivities(start, end),
                dietRepository.getDatesWithMeals(start, end),
                reminderRepository.getDatesWithReminders(start, end)
            ) { actDates, mealDates, reminderDates ->
                (actDates + mealDates + reminderDates).toSet()
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptySet())

    fun onDateSelected(date: LocalDate) {
        _selectedDate.value = date
    }

    fun onMonthChanged(month: YearMonth) {
        _currentMonth.value = month
    }

    fun toggleActivityCompleted(id: Long, currentState: Boolean) {
        viewModelScope.launch {
            activityRepository.setCompleted(id, !currentState)
        }
    }

    fun toggleMealConsumed(id: Long, currentState: Boolean) {
        viewModelScope.launch {
            dietRepository.setConsumed(id, !currentState)
        }
    }

    fun addActivityManually(
        title: String,
        type: ActivityType,
        durationMinutes: Int,
        scheduledDate: LocalDate,
        scheduledTime: LocalTime?,
        caloriesBurned: Int,
        description: String
    ) {
        viewModelScope.launch {
            val activity = Activity(
                title = title,
                description = description,
                type = type,
                durationMinutes = durationMinutes,
                scheduledDate = scheduledDate,
                scheduledTime = scheduledTime,
                caloriesBurned = caloriesBurned,
                aiGenerated = false
            )
            val id = activityRepository.insertActivity(activity)
            googleCalendarService.syncActivity(activity.copy(id = id))
        }
    }

    fun addMealManually(
        name: String,
        type: MealType,
        scheduledDate: LocalDate,
        caloriesKcal: Int,
        proteinG: Int,
        carbsG: Int,
        fatG: Int,
        ingredients: List<String>
    ) {
        viewModelScope.launch {
            dietRepository.insertMeal(
                Meal(
                    name = name,
                    type = type,
                    scheduledDate = scheduledDate,
                    caloriesKcal = caloriesKcal,
                    proteinG = proteinG,
                    carbsG = carbsG,
                    fatG = fatG,
                    ingredients = ingredients,
                    aiGenerated = false
                )
            )
        }
    }
}
