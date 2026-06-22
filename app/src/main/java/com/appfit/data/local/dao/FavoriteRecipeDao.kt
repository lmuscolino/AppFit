package com.appfit.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.appfit.data.model.FavoriteRecipe
import kotlinx.coroutines.flow.Flow

@Dao
interface FavoriteRecipeDao {

    @Query("SELECT * FROM favorite_recipes ORDER BY addedAt DESC")
    fun getAllFavorites(): Flow<List<FavoriteRecipe>>

    @Query("SELECT * FROM favorite_recipes ORDER BY addedAt DESC")
    suspend fun getAllFavoritesOnce(): List<FavoriteRecipe>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(recipe: FavoriteRecipe): Long

    @Query("DELETE FROM favorite_recipes WHERE id = :id")
    suspend fun deleteById(id: Long)
}
