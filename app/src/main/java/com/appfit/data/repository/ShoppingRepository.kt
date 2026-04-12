package com.appfit.data.repository

import com.appfit.data.local.dao.ShoppingItemDao
import com.appfit.data.model.ShoppingItem
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ShoppingRepository @Inject constructor(
    private val dao: ShoppingItemDao
) {
    fun getShoppingListForWeek(weekStart: LocalDate): Flow<List<ShoppingItem>> =
        dao.getShoppingListForWeek(weekStart)

    suspend fun replaceShoppingListForWeek(weekStart: LocalDate, items: List<ShoppingItem>) =
        dao.replaceShoppingListForWeek(weekStart, items)

    suspend fun toggleChecked(id: Long, currentState: Boolean) =
        dao.setChecked(id, !currentState)

    suspend fun deleteItem(id: Long) = dao.deleteItem(id)
}
