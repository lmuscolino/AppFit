package com.appfit.notification

import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.appfit.MainActivity
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class ActivityReminderWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        val activityId = inputData.getLong("activity_id", -1L)
        val activityTitle = inputData.getString("activity_title") ?: "Attività"
        val durationMinutes = inputData.getInt("duration_minutes", 30)

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
            .setContentTitle("Attività tra 30 minuti")
            .setContentText("$activityTitle - $durationMinutes min")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        val manager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.notify(activityId.toInt(), notification)

        return Result.success()
    }
}
