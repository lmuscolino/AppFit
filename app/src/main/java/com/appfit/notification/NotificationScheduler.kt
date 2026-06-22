package com.appfit.notification

import androidx.work.*
import com.appfit.data.repository.ActivityRepository
import kotlinx.coroutines.flow.first
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NotificationScheduler @Inject constructor(
    private val workManager: WorkManager,
    private val activityRepository: ActivityRepository
) {
    companion object {
        const val REMINDER_ADVANCE_MINUTES = 30L
    }

    suspend fun rescheduleAll() {
        val today = LocalDate.now()
        val nextWeek = today.plusDays(7)
        val upcoming = activityRepository.getUpcomingActivitiesWithTime(today).first()

        upcoming.filter { it.scheduledDate.isBefore(nextWeek) }.forEach { activity ->
            if (activity.scheduledTime != null) {
                scheduleReminder(
                    activityId = activity.id,
                    title = activity.title,
                    durationMinutes = activity.durationMinutes,
                    reminderAt = LocalDateTime.of(activity.scheduledDate, activity.scheduledTime)
                        .minusMinutes(REMINDER_ADVANCE_MINUTES),
                    isImportant = activity.isImportant
                )
            }
        }
    }

    fun scheduleReminder(
        activityId: Long,
        title: String,
        durationMinutes: Int,
        reminderAt: LocalDateTime,
        isImportant: Boolean = false
    ) {
        val activityStartMs = reminderAt
            .plusMinutes(REMINDER_ADVANCE_MINUTES)
            .atZone(ZoneId.systemDefault())
            .toInstant()
            .toEpochMilli()

        // Attività già iniziata o terminata → nessuna notifica
        if (activityStartMs <= System.currentTimeMillis()) return

        val delayMs = (reminderAt
            .atZone(ZoneId.systemDefault())
            .toInstant()
            .toEpochMilli() - System.currentTimeMillis())
            .coerceAtLeast(0L)   // se la finestra dei 30min è già passata, manda subito

        val inputData = workDataOf(
            "activity_id" to activityId,
            "activity_title" to title,
            "duration_minutes" to durationMinutes,
            "is_important" to isImportant
        )

        val request = OneTimeWorkRequestBuilder<ActivityReminderWorker>()
            .setInitialDelay(delayMs, TimeUnit.MILLISECONDS)
            .setInputData(inputData)
            .addTag("reminder_$activityId")
            .build()

        workManager.enqueueUniqueWork(
            "reminder_$activityId",
            ExistingWorkPolicy.REPLACE,
            request
        )
    }

    fun cancelReminder(activityId: Long) {
        workManager.cancelUniqueWork("reminder_$activityId")
    }
}
