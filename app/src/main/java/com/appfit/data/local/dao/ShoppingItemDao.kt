package com.appfit.data.local.dao

import androidx.room.*
import com.appfit.data.model.ShoppingItem
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

@Dao
interface ShoppingItemDao {

    @Query("SELECT * FROM shopping_items WHERE weekStartDate = :weekStart ORDER BY category ASC, name ASC")
    fun getShoppingListForWeek(weekStart: LocalDate): Flow<List<ShoppingItem>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertItems(items: List<ShoppingItem>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertItem(item: ShoppingItem)

    @Update
    suspend fun updateItem(item: ShoppingItem)

    @Query("UPDATE shopping_items SET isChecked = :checked WHERE id = :id")
    suspend fun setChecked(id: Long, checked: Boolean)

    @Query("DELETE FROM shopping_items WHERE weekStartDate = :weekStart")
    suspend fun deleteItemsForWeek(weekStart: LocalDate)

    @Query("DELETE FROM shopping_items WHERE id = :id")
    suspend fun deleteItem(id: Long)

    @Transaction
    suspend fun replaceShoppingListForWeek(weekStart: LocalDate, items: List<ShoppingItem>) {
        deleteItemsForWeek(weekStart)
        insertItems(items)
    }
}
