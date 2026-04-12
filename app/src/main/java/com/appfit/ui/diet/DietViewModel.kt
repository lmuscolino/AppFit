package com.appfit.ui.diet

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.appfit.data.model.DailyPlan
import com.appfit.domain.usecase.GetDailyPlanUseCase
import com.appfit.ui.common.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class DietViewModel @Inject constructor(
    private val getDailyPlanUseCase: GetDailyPlanUseCase
) : ViewModel() {

    val todayPlan: StateFlow<UiState<DailyPlan>> =
        getDailyPlanUseCase(LocalDate.now())
            .map { UiState.Success(it) as UiState<DailyPlan> }
            .catch { emit(UiState.Error(it.message ?: "Errore")) }
            .stateIn(
                viewModelScope,
                SharingStarted.WhileSubscribed(5000),
                UiState.Loading
            )
}
