package com.appfit.data.repository

import com.appfit.data.local.dao.ActivityDao
import com.appfit.data.model.Activity
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ActivityRepository @Inject constructor(
    private val dao: ActivityDao
) {
    fun getActivitiesForDate(date: LocalDate): Flow<List<Activity>> =
        dao.getActivitiesForDate(date)

    fun getActivitiesForRange(start: LocalDate, end: LocalDate): Flow<List<Activity>> =
        dao.getActivitiesForRange(start, end)

    suspend fun getActivityById(id: Long): Activity? = dao.getActivityById(id)

    fun getUpcomingActivitiesWithTime(from: LocalDate): Flow<List<Activity>> =
        dao.getUpcomingActivitiesWithTime(from)

    suspend fun insertActivity(activity: Activity): Long = dao.insertActivity(activity)

    suspend fun updateActivity(activity: Activity) = dao.updateActivity(activity)

    suspend fun setCompleted(id: Long, completed: Boolean) = dao.setCompleted(id, completed)

    suspend fun deleteActivity(id: Long) = dao.deleteActivity(id)

    fun getDatesWithActivities(start: LocalDate, end: LocalDate): Flow<List<LocalDate>> =
        dao.getDatesWithActivities(start, end)
}
