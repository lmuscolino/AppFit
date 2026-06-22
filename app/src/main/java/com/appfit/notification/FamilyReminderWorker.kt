package com.appfit.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.hilt.work.HiltWorker
import androidx.work.*
import com.appfit.MainActivity
import com.appfit.data.repository.FamilyRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale
import java.util.concurrent.TimeUnit

@HiltWorker
class FamilyReminderWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val familyRepository: FamilyRepository
) : CoroutineWorker(appContext, workerParams) {

    companion object {
        private const val CHANNEL_FAMILY = "family_todos"
        private const val WORK_NAME = "family_reminder_check"

        fun schedule(context: Context) {
            val request = PeriodicWorkRequestBuilder<FamilyReminderWorker>(60, TimeUnit.MINUTES)
                .setConstraints(
                    Constraints.Builder()
                        .setRequiredNetworkType(NetworkType.CONNECTED)
                        .build()
                )
                .addTag(WORK_NAME)
                .build()
            WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                WORK_NAME,
                ExistingPeriodicWorkPolicy.KEEP,
                request
            )
        }

        fun cancel(context: Context) {
            WorkManager.getInstance(context).cancelUniqueWork(WORK_NAME)
        }
    }

    override suspend fun doWork(): Result {
        val uid = familyRepository.currentUser?.uid ?: return Result.success()
        val familyId = runCatching { familyRepository.getUserFamilyId() }.getOrNull()
            ?: return Result.success()

        val importantTodos = runCatching {
            familyRepository.getImportantPendingTodosForUser(familyId, uid)
        }.getOrElse { emptyList() }

        val pendingProposals = runCatching {
            familyRepository.getPendingProposalsForUser(familyId, uid)
        }.getOrElse { emptyList() }

        if (importantTodos.isEmpty() && pendingProposals.isEmpty()) return Result.success()

        ensureChannel()

        val manager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val intent = Intent(applicationContext, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra("navigate_to", "family")
        }

        importantTodos.forEach { todo ->
            val pendingIntent = PendingIntent.getActivity(
                applicationContext,
                todo.id.hashCode(),
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            val notification = NotificationCompat.Builder(applicationContext, CHANNEL_FAMILY)
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .setContentTitle("⚠️ TODO importante in attesa")
                .setContentText(todo.title)
                .setStyle(NotificationCompat.BigTextStyle().bigText(
                    "${todo.title}${if (todo.description.isNotBlank()) "\n${todo.description}" else ""}"
                ))
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .build()
            manager.notify(todo.id.hashCode(), notification)
        }

        val dateFormatter = DateTimeFormatter.ofPattern("EEE d MMM", Locale.ITALIAN)
        pendingProposals.forEach { proposal ->
            val pendingIntent = PendingIntent.getActivity(
                applicationContext,
                "proposal_${proposal.id}".hashCode(),
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            val localDate = proposal.scheduledDate.toDate().toInstant()
                .atZone(ZoneId.systemDefault()).toLocalDate()
            val dateStr = localDate.format(dateFormatter)
            val timeStr = if (proposal.scheduledTime.isNotBlank()) " · ${proposal.scheduledTime}" else ""
            val notification = NotificationCompat.Builder(applicationContext, CHANNEL_FAMILY)
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .setContentTitle("Nuova proposta da ${proposal.proposedByName.substringBefore(" ")}")
                .setContentText("${proposal.title} · $dateStr")
                .setStyle(NotificationCompat.BigTextStyle().bigText(
                    "${proposal.title}\n$dateStr$timeStr · ${proposal.durationMinutes} min\nApri l'app per approvare o rifiutare"
                ))
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .build()
            manager.notify("proposal_${proposal.id}".hashCode(), notification)
        }

        return Result.success()
    }

    private fun ensureChannel() {
        val manager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (manager.getNotificationChannel(CHANNEL_FAMILY) != null) return
        val channel = NotificationChannel(
            CHANNEL_FAMILY,
            "TODO Famiglia",
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            description = "Promemoria per TODO importanti del gruppo famiglia"
            enableVibration(true)
        }
        manager.createNotificationChannel(channel)
    }
}
