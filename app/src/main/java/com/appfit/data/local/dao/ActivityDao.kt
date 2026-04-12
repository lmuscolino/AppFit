package com.appfit.data.local.dao

import androidx.room.*
import com.appfit.data.model.Activity
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

@Dao
interface ActivityDao {

    @Query("SELECT * FROM activities WHERE scheduledDate = :date ORDER BY scheduledTime ASC")
    fun getActivitiesForDate(date: LocalDate): Flow<List<Activity>>

    @Query("SELECT * FROM activities WHERE scheduledDate BETWEEN :start AND :end ORDER BY scheduledDate ASC, scheduledTime ASC")
    fun getActivitiesForRange(start: LocalDate, end: LocalDate): Flow<List<Activity>>

    @Query("SELECT * FROM activities WHERE id = :id")
    suspend fun getActivityById(id: Long): Activity?

    @Query("SELECT * FROM activities WHERE scheduledDate >= :from AND scheduledTime IS NOT NULL AND isCompleted = 0 ORDER BY scheduledDate ASC, scheduledTime ASC")
    fun getUpcomingActivitiesWithTime(from: LocalDate): Flow<List<Activity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertActivity(activity: Activity): Long

    @Update
    suspend fun updateActivity(activity: Activity)

    @Query("UPDATE activities SET isCompleted = :completed WHERE id = :id")
    suspend fun setCompleted(id: Long, completed: Boolean)

    @Query("DELETE FROM activities WHERE id = :id")
    suspend fun deleteActivity(id: Long)

    @Query("SELECT DISTINCT scheduledDate FROM activities WHERE scheduledDate BETWEEN :start AND :end")
    fun getDatesWithActivities(start: LocalDate, end: LocalDate): Flow<List<LocalDate>>
}
