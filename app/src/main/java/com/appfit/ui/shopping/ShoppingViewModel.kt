package com.appfit.ui.shopping

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.appfit.data.model.ShoppingCategory
import com.appfit.data.model.ShoppingItem
import com.appfit.data.repository.ShoppingRepository
import com.appfit.domain.usecase.GenerateShoppingListUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.temporal.TemporalAdjusters
import javax.inject.Inject

@HiltViewModel
class ShoppingViewModel @Inject constructor(
    private val shoppingRepository: ShoppingRepository,
    private val generateShoppingListUseCase: GenerateShoppingListUseCase
) : ViewModel() {

    private val defaultStart: LocalDate
        get() = LocalDate.now().with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))

    private val _rangeStart = MutableStateFlow(defaultStart)
    private val _rangeEnd = MutableStateFlow(defaultStart.plusDays(6))

    val rangeStart: StateFlow<LocalDate> = _rangeStart.asStateFlow()
    val rangeEnd: StateFlow<LocalDate> = _rangeEnd.asStateFlow()

    @OptIn(ExperimentalCoroutinesApi::class)
    val groupedItems: StateFlow<Map<ShoppingCategory, List<ShoppingItem>>> =
        _rangeStart
            .flatMapLatest { start ->
                shoppingRepository.getShoppingListForWeek(start)
            }
            .map { items ->
                items.groupBy { it.category }
                    .toSortedMap(compareBy { it.order() })
            }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyMap())

    private val _isGenerating = MutableStateFlow(false)
    val isGenerating: StateFlow<Boolean> = _isGenerating.asStateFlow()

    fun prevWeek() {
        _rangeStart.value = _rangeStart.value.minusWeeks(1)
        _rangeEnd.value = _rangeEnd.value.minusWeeks(1)
    }

    fun nextWeek() {
        _rangeStart.value = _rangeStart.value.plusWeeks(1)
        _rangeEnd.value = _rangeEnd.value.plusWeeks(1)
    }

    fun setCustomRange(start: LocalDate, end: LocalDate) {
        _rangeStart.value = start
        _rangeEnd.value = if (end.isBefore(start)) start else end
    }

    fun regenerateFromMealPlan() {
        viewModelScope.launch {
            _isGenerating.value = true
            try {
                generateShoppingListUseCase(_rangeStart.value, _rangeEnd.value)
            } finally {
                _isGenerating.value = false
            }
        }
    }

    fun toggleItemChecked(item: ShoppingItem) {
        viewModelScope.launch {
            shoppingRepository.toggleChecked(item.id, item.isChecked)
        }
    }

    fun deleteItem(id: Long) {
        viewModelScope.launch {
            shoppingRepository.deleteItem(id)
        }
    }
}
