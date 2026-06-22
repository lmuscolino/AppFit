package com.appfit.ai

import android.util.Log
import com.appfit.data.model.Activity
import com.appfit.data.model.Reminder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GoogleCalendarService @Inject constructor(
    private val googleCalendarProvider: GoogleCalendarProvider
) {
    companion object {
        private const val BASE_URL = "https://www.googleapis.com/calendar/v3/calendars/primary/events"
        private val ISO_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")
        private val ZONE = ZoneId.of("Europe/Rome")
    }

    /**
     * Creates or updates a Google Calendar event for the given activity.
     * If the activity was already synced (event ID known), does a PATCH; otherwise a POST.
     * Returns the event ID on success, null on failure or if sync is disabled.
     */
    suspend fun syncActivity(activity: Activity): String? = withContext(Dispatchers.IO) {
        val syncEnabled = googleCalendarProvider.isSyncEnabled()
        Log.d("GCalSync", "syncActivity called — id=${activity.id} title='${activity.title}' syncEnabled=$syncEnabled")
        if (!syncEnabled) return@withContext null

        val token = googleCalendarProvider.getAccessToken()
        Log.d("GCalSync", "getAccessToken → ${if (token != null) "OK (${token.take(10)}...)" else "NULL"}")
        if (token == null) return@withContext null

        try {
            val startDateTime = activity.scheduledDate.atTime(
                activity.scheduledTime ?: java.time.LocalTime.of(8, 0)
            )
            val endDateTime = startDateTime.plusMinutes(activity.durationMinutes.toLong())

            val body = JSONObject().apply {
                put("summary", activity.title)
                put("description", buildDescription(activity))
                put("start", JSONObject().apply {
                    put("dateTime", ISO_FORMATTER.format(startDateTime))
                    put("timeZone", ZONE.id)
                })
                put("end", JSONObject().apply {
                    put("dateTime", ISO_FORMATTER.format(endDateTime))
                    put("timeZone", ZONE.id)
                })
                put("source", JSONObject().apply {
                    put("title", "AppFit")
                    put("url", "https://appfit.app")
                })
            }

            val existingEventId = googleCalendarProvider.getEventId(activity.id)
            val (url, method) = if (existingEventId != null) {
                URL("$BASE_URL/$existingEventId") to "PATCH"
            } else {
                URL(BASE_URL) to "POST"
            }
            Log.d("GCalSync", "$method ${url} existingEventId=$existingEventId")

            val conn = (url.openConnection() as HttpURLConnection).apply {
                requestMethod = method
                setRequestProperty("Authorization", "Bearer $token")
                setRequestProperty("Content-Type", "application/json")
                doOutput = true
                connectTimeout = 15_000
                readTimeout = 15_000
            }

            OutputStreamWriter(conn.outputStream).use { it.write(body.toString()) }

            val responseCode = conn.responseCode
            Log.d("GCalSync", "responseCode=$responseCode")
            if (responseCode in 200..201) {
                val response = conn.inputStream.bufferedReader().readText()
                val eventId = JSONObject(response).optString("id")
                Log.d("GCalSync", "success — eventId=$eventId")
                if (eventId.isNotBlank()) {
                    googleCalendarProvider.saveEventId(activity.id, eventId)
                }
                eventId.ifBlank { null }
            } else {
                val errorBody = runCatching { conn.errorStream?.bufferedReader()?.readText() }.getOrNull()
                Log.e("GCalSync", "error $responseCode — $errorBody")
                null
            }
        } catch (e: Exception) {
            Log.e("GCalSync", "exception: ${e.message}", e)
            null
        }
    }

    /**
     * Creates or updates a Google Calendar all-day event for the given reminder.
     * Skipped if dueDate is null or sync is disabled.
     */
    suspend fun syncReminder(reminder: Reminder): String? = withContext(Dispatchers.IO) {
        if (!googleCalendarProvider.isSyncEnabled()) return@withContext null
        if (reminder.dueDate == null) return@withContext null
        val token = googleCalendarProvider.getAccessToken()
        Log.d("GCalSync", "syncReminder id=${reminder.id} '${reminder.title}' dueDate=${reminder.dueDate} token=${if (token != null) "OK" else "NULL"}")
        if (token == null) return@withContext null

        try {
            val body = JSONObject().apply {
                put("summary", "${reminder.category.emoji()} ${reminder.title}")
                if (reminder.description.isNotBlank()) put("description", reminder.description)
                put("start", JSONObject().apply { put("date", reminder.dueDate.toString()) })
                put("end", JSONObject().apply { put("date", reminder.dueDate.plusDays(1).toString()) })
                put("source", JSONObject().apply {
                    put("title", "AppFit")
                    put("url", "https://appfit.app")
                })
            }

            val existingEventId = googleCalendarProvider.getReminderEventId(reminder.id)
            val (url, method) = if (existingEventId != null)
                URL("$BASE_URL/$existingEventId") to "PATCH"
            else
                URL(BASE_URL) to "POST"

            val conn = (url.openConnection() as HttpURLConnection).apply {
                requestMethod = method
                setRequestProperty("Authorization", "Bearer $token")
                setRequestProperty("Content-Type", "application/json")
                doOutput = true
                connectTimeout = 15_000
                readTimeout = 15_000
            }
            OutputStreamWriter(conn.outputStream).use { it.write(body.toString()) }

            val responseCode = conn.responseCode
            Log.d("GCalSync", "syncReminder responseCode=$responseCode")
            if (responseCode in 200..201) {
                val response = conn.inputStream.bufferedReader().readText()
                val eventId = JSONObject(response).optString("id")
                if (eventId.isNotBlank()) googleCalendarProvider.saveReminderEventId(reminder.id, eventId)
                eventId.ifBlank { null }
            } else {
                val errorBody = runCatching { conn.errorStream?.bufferedReader()?.readText() }.getOrNull()
                Log.e("GCalSync", "syncReminder error $responseCode — $errorBody")
                null
            }
        } catch (e: Exception) {
            Log.e("GCalSync", "syncReminder exception: ${e.message}", e)
            null
        }
    }

    /**
     * Deletes the Google Calendar event associated with the given activity ID.
     */
    suspend fun deleteActivityEvent(activityId: Long) = withContext(Dispatchers.IO) {
        if (!googleCalendarProvider.isSyncEnabled()) return@withContext
        val token = googleCalendarProvider.getAccessToken() ?: return@withContext
        val eventId = googleCalendarProvider.getEventId(activityId) ?: return@withContext

        try {
            val url = URL("$BASE_URL/$eventId")
            val conn = (url.openConnection() as HttpURLConnection).apply {
                requestMethod = "DELETE"
                setRequestProperty("Authorization", "Bearer $token")
                connectTimeout = 15_000
                readTimeout = 15_000
            }
            conn.responseCode // trigger the request
            googleCalendarProvider.removeEventId(activityId)
        } catch (e: Exception) {
            // Silent failure — event may already be deleted
        }
    }

    private fun buildDescription(activity: Activity): String {
        val sb = StringBuilder()
        if (activity.description.isNotBlank()) sb.appendLine(activity.description)
        sb.appendLine()
        sb.append("📱 Aggiunto da AppFit")
        sb.append(" · #${activity.type.name}")
        if (activity.caloriesBurned > 0) sb.append(" · ${activity.caloriesBurned} kcal stimate")
        return sb.toString().trim()
    }
}
