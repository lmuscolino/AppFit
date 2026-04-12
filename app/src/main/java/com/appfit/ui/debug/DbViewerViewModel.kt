package com.appfit.ui.debug

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.appfit.ai.AiDebugLogger
import com.appfit.data.local.AppDatabase
import com.appfit.data.model.Activity
import com.appfit.data.model.ChatMessage
import com.appfit.data.model.Meal
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class DbViewerViewModel @Inject constructor(
    private val db: AppDatabase,
    private val aiDebugLogger: AiDebugLogger
) : ViewModel() {

    private val rangeStart = LocalDate.of(2020, 1, 1)
    private val rangeEnd = LocalDate.of(2030, 12, 31)

    val activities: StateFlow<List<Activity>> = db.activityDao()
        .getActivitiesForRange(rangeStart, rangeEnd)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val meals: StateFlow<List<Meal>> = db.mealDao()
        .getMealsForRange(rangeStart, rangeEnd)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val messages: StateFlow<List<ChatMessage>> = db.chatMessageDao()
        .getAllMessages()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val debugLog: StateFlow<List<String>> = aiDebugLogger.entries
}
