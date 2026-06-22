package com.appfit.ui.activity

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.appfit.ai.GoogleCalendarService
import com.appfit.data.model.Activity
import com.appfit.data.model.ActivityType
import com.appfit.data.repository.ActivityRepository
import com.appfit.notification.NotificationScheduler
import com.appfit.ui.common.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.LocalTime
import javax.inject.Inject

@HiltViewModel
class ActivityDetailViewModel @Inject constructor(
    private val repository: ActivityRepository,
    private val googleCalendarService: GoogleCalendarService,
    private val notificationScheduler: NotificationScheduler,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val activityId: Long = checkNotNull(savedStateHandle["activityId"])

    private val _uiState = MutableStateFlow<UiState<Activity>>(UiState.Loading)
    val uiState: StateFlow<UiState<Activity>> = _uiState.asStateFlow()

    init {
        loadActivity()
    }

    private fun loadActivity() {
        viewModelScope.launch {
            val activity = repository.getActivityById(activityId)
            _uiState.value = if (activity != null) {
                UiState.Success(activity)
            } else {
                UiState.Error("Attività non trovata")
            }
        }
    }

    fun toggleCompleted() {
        val current = (_uiState.value as? UiState.Success)?.data ?: return
        viewModelScope.launch {
            repository.setCompleted(current.id, !current.isCompleted)
            loadActivity()
        }
    }

    fun updateActivity(
        title: String,
        type: ActivityType,
        durationMinutes: Int,
        scheduledTime: LocalTime?,
        caloriesBurned: Int,
        description: String
    ) {
        val current = (_uiState.value as? UiState.Success)?.data ?: return
        val updated = current.copy(
            title = title,
            type = type,
            durationMinutes = durationMinutes,
            scheduledTime = scheduledTime,
            caloriesBurned = caloriesBurned,
            description = description
        )
        viewModelScope.launch {
            repository.updateActivity(updated)
            notificationScheduler.cancelReminder(updated.id)
            if (scheduledTime != null) {
                notificationScheduler.scheduleReminder(
                    activityId = updated.id,
                    title = updated.title,
                    durationMinutes = updated.durationMinutes,
                    reminderAt = LocalDateTime.of(updated.scheduledDate, scheduledTime)
                        .minusMinutes(NotificationScheduler.REMINDER_ADVANCE_MINUTES)
                )
            }
            googleCalendarService.syncActivity(updated)
            loadActivity()
        }
    }

    fun deleteActivity(onDeleted: () -> Unit) {
        viewModelScope.launch {
            googleCalendarService.deleteActivityEvent(activityId)
            repository.deleteActivity(activityId)
            onDeleted()
        }
    }
}
