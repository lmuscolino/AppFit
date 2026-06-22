package com.appfit.data.repository

import com.appfit.data.local.dao.FavoriteRecipeDao
import com.appfit.data.model.FavoriteRecipe
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FavoriteRecipeRepository @Inject constructor(
    private val dao: FavoriteRecipeDao
) {
    fun getAllFavorites(): Flow<List<FavoriteRecipe>> = dao.getAllFavorites()

    suspend fun getAllFavoritesOnce(): List<FavoriteRecipe> = dao.getAllFavoritesOnce()

    suspend fun insert(recipe: FavoriteRecipe): Long = dao.insert(recipe)

    suspend fun delete(id: Long) = dao.deleteById(id)
}
