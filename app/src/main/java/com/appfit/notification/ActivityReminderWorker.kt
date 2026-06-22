package com.appfit.notification

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.hilt.work.HiltWorker
import androidx.work.*
import com.appfit.MainActivity
import com.appfit.data.repository.ActivityRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import java.util.concurrent.TimeUnit

@HiltWorker
class ActivityReminderWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val activityRepository: ActivityRepository
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        val activityId = inputData.getLong("activity_id", -1L)
        val activityTitle = inputData.getString("activity_title") ?: "Attività"
        val durationMinutes = inputData.getInt("duration_minutes", 30)
        val isImportant = inputData.getBoolean("is_important", false)

        val activity = activityRepository.getActivityById(activityId)
        if (activity?.isCompleted == true) return Result.success()

        val intent = Intent(applicationContext, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra("navigate_to_activity", activityId)
        }
        val pendingIntent = PendingIntent.getActivity(
            applicationContext,
            activityId.toInt(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(
            applicationContext,
            NotificationChannels.CHANNEL_ACTIVITY_REMINDERS
        )
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle(if (isImportant) "⚠️ Attività importante tra 30 minuti" else "Attività tra 30 minuti")
            .setContentText("$activityTitle - $durationMinutes min")
            .setPriority(if (isImportant) NotificationCompat.PRIORITY_MAX else NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        val manager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.notify(activityId.toInt(), notification)

        // Attività importante non completata → ri-notifica tra 1 ora
        if (isImportant && activity?.isCompleted == false) {
            val reschedule = OneTimeWorkRequestBuilder<ActivityReminderWorker>()
                .setInitialDelay(60, TimeUnit.MINUTES)
                .setInputData(inputData)
                .addTag("reminder_$activityId")
                .build()
            WorkManager.getInstance(applicationContext).enqueueUniqueWork(
                "reminder_$activityId",
                ExistingWorkPolicy.REPLACE,
                reschedule
            )
        }

        return Result.success()
    }
}
