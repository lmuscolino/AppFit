package com.appfit.ui.reminders

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.appfit.data.model.Reminder
import com.appfit.data.model.ReminderCategory
import com.appfit.data.repository.ReminderRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class ReminderViewModel @Inject constructor(
    private val repository: ReminderRepository
) : ViewModel() {

    val reminders: StateFlow<List<Reminder>> = repository.getPendingReminders()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val completedReminders: StateFlow<List<Reminder>> = repository.getAllReminders()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun toggleCompleted(reminder: Reminder) {
        viewModelScope.launch {
            repository.setCompleted(reminder.id, !reminder.isCompleted)
        }
    }

    fun delete(id: Long) {
        viewModelScope.launch { repository.deleteReminder(id) }
    }

    fun addReminder(
        title: String,
        description: String,
        category: ReminderCategory,
        dueDate: LocalDate?,
        amount: Float?,
        isImportant: Boolean
    ) {
        viewModelScope.launch {
            repository.insertReminder(
                Reminder(
                    title = title,
                    description = description,
                    category = category,
                    dueDate = dueDate,
                    amount = amount,
                    isImportant = isImportant
                )
            )
        }
    }
}
