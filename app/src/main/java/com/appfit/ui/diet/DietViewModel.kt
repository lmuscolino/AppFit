package com.appfit.ui.diet

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.appfit.data.model.DailyPlan
import com.appfit.domain.usecase.GetDailyPlanUseCase
import com.appfit.ui.common.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class DietViewModel @Inject constructor(
    private val getDailyPlanUseCase: GetDailyPlanUseCase
) : ViewModel() {

    private val _selectedDate = MutableStateFlow(LocalDate.now())
    val selectedDate: StateFlow<LocalDate> = _selectedDate.asStateFlow()

    @OptIn(ExperimentalCoroutinesApi::class)
    val selectedPlan: StateFlow<UiState<DailyPlan>> =
        _selectedDate
            .flatMapLatest { date -> getDailyPlanUseCase(date) }
            .map { UiState.Success(it) as UiState<DailyPlan> }
            .catch { emit(UiState.Error(it.message ?: "Errore")) }
            .stateIn(
                viewModelScope,
                SharingStarted.WhileSubscribed(5000),
                UiState.Loading
            )

    fun prevDay() { _selectedDate.value = _selectedDate.value.minusDays(1) }
    fun nextDay() { _selectedDate.value = _selectedDate.value.plusDays(1) }
    fun goToToday() { _selectedDate.value = LocalDate.now() }
}
