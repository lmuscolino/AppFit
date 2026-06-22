package com.appfit.data.local.dao

import androidx.room.*
import com.appfit.data.model.PendingEmailItem
import com.appfit.data.model.PendingItemStatus
import kotlinx.coroutines.flow.Flow

@Dao
interface PendingEmailItemDao {
    @Query("SELECT * FROM pending_email_items WHERE status = 'PENDING' ORDER BY createdAt DESC")
    fun getPendingItems(): Flow<List<PendingEmailItem>>

    @Query("SELECT * FROM pending_email_items ORDER BY createdAt DESC")
    fun getAllItems(): Flow<List<PendingEmailItem>>

    @Query("SELECT COUNT(*) FROM pending_email_items WHERE status = 'PENDING'")
    fun getPendingCount(): Flow<Int>

    @Query("SELECT sourceEmailId FROM pending_email_items")
    suspend fun getAllSourceEmailIds(): List<String>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertItem(item: PendingEmailItem): Long

    @Query("UPDATE pending_email_items SET status = :status WHERE id = :id")
    suspend fun updateStatus(id: Long, status: PendingItemStatus)

    @Query("DELETE FROM pending_email_items WHERE status != 'PENDING'")
    suspend fun deleteProcessed()
}
