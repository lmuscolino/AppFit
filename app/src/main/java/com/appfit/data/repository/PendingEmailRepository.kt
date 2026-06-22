package com.appfit.data.repository

import com.appfit.data.local.dao.PendingEmailItemDao
import com.appfit.data.model.PendingEmailItem
import com.appfit.data.model.PendingItemStatus
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PendingEmailRepository @Inject constructor(
    private val dao: PendingEmailItemDao
) {
    fun getPendingItems(): Flow<List<PendingEmailItem>> = dao.getPendingItems()
    fun getAllItems(): Flow<List<PendingEmailItem>> = dao.getAllItems()
    fun getPendingCount(): Flow<Int> = dao.getPendingCount()
    suspend fun getAllSourceEmailIds(): List<String> = dao.getAllSourceEmailIds()
    suspend fun insertItem(item: PendingEmailItem): Long = dao.insertItem(item)
    suspend fun approve(id: Long) = dao.updateStatus(id, PendingItemStatus.APPROVED)
    suspend fun reject(id: Long) = dao.updateStatus(id, PendingItemStatus.REJECTED)
    suspend fun deleteProcessed() = dao.deleteProcessed()
}
