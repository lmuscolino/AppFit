package com.appfit.notification

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.hilt.work.HiltWorker
import androidx.work.*
import com.appfit.MainActivity
import com.appfit.ai.ApiKeyProvider
import com.appfit.ai.GoogleMailProvider
import com.appfit.ai.gmail.GmailScanService
import com.appfit.ai.gmail.ScanResult
import com.appfit.data.repository.PendingEmailRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.concurrent.TimeUnit

@HiltWorker
class GmailScanWorker @AssistedInject constructor(
    @Assisted private val appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val googleMailProvider: GoogleMailProvider,
    private val pendingEmailRepository: PendingEmailRepository,
    private val apiKeyProvider: ApiKeyProvider,
    private val gmailScanService: GmailScanService
) : CoroutineWorker(appContext, workerParams) {

    companion object {
        const val WORK_NAME        = "gmail_scan_periodic"
        const val NOTIFICATION_ID  = 7001
        const val EXTRA_OPEN_INBOX = "open_pending_inbox"

        fun schedule(workManager: WorkManager) {
            val request = PeriodicWorkRequestBuilder<GmailScanWorker>(6, TimeUnit.HOURS)
                .setConstraints(Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build())
                .build()
            workManager.enqueueUniquePeriodicWork(WORK_NAME, ExistingPeriodicWorkPolicy.UPDATE, request)
        }

        fun cancel(workManager: WorkManager) = workManager.cancelUniqueWork(WORK_NAME)
    }

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        val tag = "GmailScan"
        Log.d(tag, "▶ doWork started")

        val enabled = googleMailProvider.isEnabled()
        Log.d(tag, "  gmailEnabled=$enabled")
        if (!enabled) return@withContext Result.success()

        val token = googleMailProvider.getAccessToken()
        Log.d(tag, "  token=${if (token != null) "OK (${token.take(10)}...)" else "NULL"}")
        if (token == null) return@withContext Result.retry()

        val lastScan = googleMailProvider.getLastScanTimestamp()
        Log.d(tag, "  lastScan=${if (lastScan == 0L) "mai" else java.util.Date(lastScan)}")

        val alreadyProcessed = pendingEmailRepository.getAllSourceEmailIds().toSet()
        Log.d(tag, "  alreadyProcessed=${alreadyProcessed.size}")

        val geminiKey = apiKeyProvider.getGeminiApiKey()

        val result = gmailScanService.scan(token, lastScan, alreadyProcessed, geminiKey)
        Log.d(tag, "  result=$result")

        when (result) {
            is ScanResult.ApiError -> {
                Log.w(tag, "  → API error, non salvo lastScan, retry")
                return@withContext Result.retry()
            }
            is ScanResult.AiUnavailable -> {
                Log.w(tag, "  → AI non disponibile")
                return@withContext Result.success()
            }
            is ScanResult.Done -> {
                Log.d(tag, "  scanned=${result.scannedCount}, nuovi=${result.newItems.size}")
                result.newItems.forEach { pendingEmailRepository.insertItem(it) }
                googleMailProvider.saveLastScanTimestamp(System.currentTimeMillis())
                if (result.newItems.isNotEmpty()) sendNotification(result.newItems.size)
                Log.d(tag, "✅ doWork completato")
                Result.success()
            }
        }
    }

    private fun sendNotification(count: Int) {
        val intent = Intent(appContext, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
            putExtra(EXTRA_OPEN_INBOX, true)
        }
        val pendingIntent = PendingIntent.getActivity(
            appContext, 0, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        val notification = NotificationCompat.Builder(appContext, NotificationChannels.CHANNEL_EMAIL_INBOX)
            .setSmallIcon(android.R.drawable.ic_dialog_email)
            .setContentTitle("AppFit ha trovato $count ${if (count == 1) "elemento" else "elementi"} nelle email")
            .setContentText("Tocca per rivedere e approvare")
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()
        (appContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager)
            .notify(NOTIFICATION_ID, notification)
    }
}
