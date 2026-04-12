package com.appfit.ui.workouts

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.appfit.data.model.Activity
import com.appfit.data.repository.ActivityRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class WorkoutsViewModel @Inject constructor(
    private val repository: ActivityRepository
) : ViewModel() {

    private val _weekStart = MutableStateFlow(
        LocalDate.now().with(DayOfWeek.MONDAY)
    )
    val weekStart: StateFlow<LocalDate> = _weekStart.asStateFlow()

    @OptIn(ExperimentalCoroutinesApi::class)
    val activities: StateFlow<List<Activity>> = _weekStart
        .flatMapLatest { start ->
            repository.getActivitiesForRange(start, start.plusDays(6))
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun prevWeek() {
        _weekStart.value = _weekStart.value.minusWeeks(1)
    }

    fun nextWeek() {
        _weekStart.value = _weekStart.value.plusWeeks(1)
    }

    fun updateActivity(activity: Activity) {
        viewModelScope.launch {
            repository.updateActivity(activity)
        }
    }

    fun deleteActivity(id: Long) {
        viewModelScope.launch {
            repository.deleteActivity(id)
        }
    }

    fun toggleCompleted(id: Long, current: Boolean) {
        viewModelScope.launch {
            repository.setCompleted(id, !current)
        }
    }
}
