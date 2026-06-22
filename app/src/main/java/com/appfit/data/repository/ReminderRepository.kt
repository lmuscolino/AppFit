package com.appfit.data.repository

import com.appfit.data.local.dao.ReminderDao
import com.appfit.data.model.Reminder
import com.appfit.data.model.ReminderCategory
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ReminderRepository @Inject constructor(
    private val dao: ReminderDao
) {
    fun getAllReminders(): Flow<List<Reminder>> = dao.getAllReminders()
    fun getRemindersForDate(date: LocalDate): Flow<List<Reminder>> = dao.getRemindersForDate(date)
    fun getDatesWithReminders(start: LocalDate, end: LocalDate): Flow<List<LocalDate>> = dao.getDatesWithReminders(start, end)
    fun getPendingReminders(): Flow<List<Reminder>> = dao.getPendingReminders()
    fun getRemindersByCategory(category: ReminderCategory) = dao.getRemindersByCategory(category)
    fun getUpcomingReminders(from: LocalDate, to: LocalDate) = dao.getUpcomingReminders(from, to)
    suspend fun getReminderById(id: Long): Reminder? = dao.getReminderById(id)
    suspend fun insertReminder(reminder: Reminder): Long = dao.insertReminder(reminder)
    suspend fun updateReminder(reminder: Reminder) = dao.updateReminder(reminder)
    suspend fun setCompleted(id: Long, completed: Boolean) = dao.setCompleted(id, completed)
    suspend fun deleteReminder(id: Long) = dao.deleteReminder(id)
}
