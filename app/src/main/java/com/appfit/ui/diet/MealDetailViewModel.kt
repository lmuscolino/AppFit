package com.appfit.ui.diet

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.appfit.data.model.FavoriteRecipe
import com.appfit.data.model.Meal
import com.appfit.data.repository.DietRepository
import com.appfit.data.repository.FavoriteRecipeRepository
import com.appfit.ui.common.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MealDetailViewModel @Inject constructor(
    private val repository: DietRepository,
    private val favoriteRecipeRepository: FavoriteRecipeRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val mealId: Long = checkNotNull(savedStateHandle["mealId"])

    private val _uiState = MutableStateFlow<UiState<Meal>>(UiState.Loading)
    val uiState: StateFlow<UiState<Meal>> = _uiState.asStateFlow()

    private val _savedAsFavorite = MutableStateFlow(false)
    val savedAsFavorite: StateFlow<Boolean> = _savedAsFavorite.asStateFlow()

    init {
        loadMeal()
    }

    private fun loadMeal() {
        viewModelScope.launch {
            val meal = repository.getMealById(mealId)
            _uiState.value = if (meal != null) UiState.Success(meal)
                             else UiState.Error("Pasto non trovato")
        }
    }

    fun toggleConsumed() {
        val current = (_uiState.value as? UiState.Success)?.data ?: return
        viewModelScope.launch {
            repository.setConsumed(current.id, !current.isConsumed)
            loadMeal()
        }
    }

    fun saveAsFavorite() {
        val meal = (_uiState.value as? UiState.Success)?.data ?: return
        viewModelScope.launch {
            favoriteRecipeRepository.insert(
                FavoriteRecipe(
                    name = meal.name,
                    mealType = meal.type,
                    ingredients = meal.ingredients,
                    caloriesKcal = meal.caloriesKcal,
                    proteinG = meal.proteinG,
                    carbsG = meal.carbsG,
                    fatG = meal.fatG
                )
            )
            _savedAsFavorite.value = true
        }
    }
}
