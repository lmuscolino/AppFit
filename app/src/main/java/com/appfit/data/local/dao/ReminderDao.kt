package com.appfit.data.local.dao

import androidx.room.*
import com.appfit.data.model.Reminder
import com.appfit.data.model.ReminderCategory
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

@Dao
interface ReminderDao {

    @Query("SELECT * FROM reminders ORDER BY isImportant DESC, dueDate ASC, createdAt DESC")
    fun getAllReminders(): Flow<List<Reminder>>

    @Query("SELECT * FROM reminders WHERE isCompleted = 0 ORDER BY isImportant DESC, dueDate ASC")
    fun getPendingReminders(): Flow<List<Reminder>>

    @Query("SELECT * FROM reminders WHERE category = :category ORDER BY dueDate ASC")
    fun getRemindersByCategory(category: ReminderCategory): Flow<List<Reminder>>

    @Query("SELECT * FROM reminders WHERE dueDate BETWEEN :from AND :to AND isCompleted = 0")
    fun getUpcomingReminders(from: LocalDate, to: LocalDate): Flow<List<Reminder>>

    @Query("SELECT * FROM reminders WHERE dueDate = :date AND isCompleted = 0 ORDER BY isImportant DESC")
    fun getRemindersForDate(date: LocalDate): Flow<List<Reminder>>

    @Query("SELECT DISTINCT dueDate FROM reminders WHERE dueDate BETWEEN :start AND :end AND isCompleted = 0")
    fun getDatesWithReminders(start: LocalDate, end: LocalDate): Flow<List<LocalDate>>

    @Query("SELECT * FROM reminders WHERE id = :id")
    suspend fun getReminderById(id: Long): Reminder?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReminder(reminder: Reminder): Long

    @Update
    suspend fun updateReminder(reminder: Reminder)

    @Query("UPDATE reminders SET isCompleted = :completed WHERE id = :id")
    suspend fun setCompleted(id: Long, completed: Boolean)

    @Query("DELETE FROM reminders WHERE id = :id")
    suspend fun deleteReminder(id: Long)
}
