package com.appfit.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NotificationChannels @Inject constructor(
    @ApplicationContext private val context: Context
) {
    companion object {
        const val CHANNEL_ACTIVITY_REMINDERS = "activity_reminders"
        const val CHANNEL_DAILY_SUMMARY = "daily_summary"
        const val CHANNEL_PROFILE_UPDATE = "profile_update"
    }

    fun createChannels() {
        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val activityChannel = NotificationChannel(
            CHANNEL_ACTIVITY_REMINDERS,
            "Promemoria attività",
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            description = "Notifiche per ricordare le attività fisiche programmate"
            enableVibration(true)
        }

        val summaryChannel = NotificationChannel(
            CHANNEL_DAILY_SUMMARY,
            "Riepilogo giornaliero",
            NotificationManager.IMPORTANCE_DEFAULT
        ).apply {
            description = "Riepilogo mattutino delle attività del giorno"
        }

        val profileUpdateChannel = NotificationChannel(
            CHANNEL_PROFILE_UPDATE,
            "Aggiornamento profilo",
            NotificationManager.IMPORTANCE_DEFAULT
        ).apply {
            description = "Promemoria mensile per aggiornare le caratteristiche fisiche"
        }

        manager.createNotificationChannels(listOf(activityChannel, summaryChannel, profileUpdateChannel))
    }
}
