package com.appfit.ui.favorites

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.appfit.data.model.FavoriteRecipe
import com.appfit.data.model.Meal
import com.appfit.data.repository.DietRepository
import com.appfit.data.repository.FavoriteRecipeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class FavoritesViewModel @Inject constructor(
    private val favoriteRecipeRepository: FavoriteRecipeRepository,
    private val dietRepository: DietRepository
) : ViewModel() {

    val favorites: StateFlow<List<FavoriteRecipe>> = favoriteRecipeRepository.getAllFavorites()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun deleteFavorite(id: Long) {
        viewModelScope.launch { favoriteRecipeRepository.delete(id) }
    }

    fun addToMealPlan(recipe: FavoriteRecipe, date: LocalDate) {
        viewModelScope.launch {
            dietRepository.insertMeal(
                Meal(
                    name = recipe.name,
                    type = recipe.mealType,
                    scheduledDate = date,
                    ingredients = recipe.ingredients,
                    caloriesKcal = recipe.caloriesKcal,
                    proteinG = recipe.proteinG,
                    carbsG = recipe.carbsG,
                    fatG = recipe.fatG,
                    aiGenerated = false
                )
            )
        }
    }
}
