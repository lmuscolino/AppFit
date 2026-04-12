package com.appfit.notification

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.appfit.MainActivity
import com.appfit.ai.UserProfileProvider
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class MonthlyUpdateWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val userProfileProvider: UserProfileProvider
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        if (!userProfileProvider.isMonthlyUpdateEnabled()) return Result.success()

        val intent = Intent(applicationContext, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(
            applicationContext,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(
            applicationContext,
            NotificationChannels.CHANNEL_PROFILE_UPDATE
        )
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle("Aggiorna il tuo profilo")
            .setContentText("È passato un mese — aggiorna peso e statistiche per piani AI più accurati")
            .setStyle(NotificationCompat.BigTextStyle()
                .bigText("È passato un mese — aggiorna il tuo peso e le statistiche fisiche per ricevere piani di allenamento e dieta più accurati dall'AI"))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        val manager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.notify(NOTIFICATION_ID, notification)

        return Result.success()
    }

    companion object {
        const val WORK_NAME = "monthly_profile_update"
        private const val NOTIFICATION_ID = 1001
    }
}
