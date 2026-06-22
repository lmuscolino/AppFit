package com.appfit.ai

import android.accounts.Account
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.google.android.gms.auth.GoogleAuthUtil
import com.google.android.gms.auth.UserRecoverableAuthException
import com.google.android.gms.auth.api.signin.GoogleSignIn
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GoogleCalendarProvider @Inject constructor(
    @ApplicationContext private val context: Context,
    private val dataStore: DataStore<Preferences>
) {
    companion object {
        val SYNC_ENABLED = booleanPreferencesKey("gcal_sync_enabled")
        val ACCOUNT_EMAIL = stringPreferencesKey("gcal_account_email")
        // JSON map activityId -> gcalEventId stored as serialized string
        val EVENT_MAP = stringPreferencesKey("gcal_event_map")
        // JSON map reminderId -> gcalEventId
        val REMINDER_EVENT_MAP = stringPreferencesKey("gcal_reminder_event_map")
        const val CALENDAR_SCOPE = "oauth2:https://www.googleapis.com/auth/calendar.events"
    }

    val syncEnabledFlow: Flow<Boolean> = dataStore.data.map { it[SYNC_ENABLED] ?: false }
    val accountEmailFlow: Flow<String?> = dataStore.data.map { it[ACCOUNT_EMAIL] }

    private val _pendingConsentIntent = MutableSharedFlow<Intent>(extraBufferCapacity = 1)
    val pendingConsentIntent: SharedFlow<Intent> = _pendingConsentIntent

    suspend fun isSyncEnabled(): Boolean = dataStore.data.first()[SYNC_ENABLED] ?: false

    suspend fun setSyncEnabled(enabled: Boolean) {
        dataStore.edit { prefs -> prefs[SYNC_ENABLED] = enabled }
    }

    suspend fun saveAccount(email: String) {
        dataStore.edit { prefs ->
            prefs[ACCOUNT_EMAIL] = email
            prefs[SYNC_ENABLED] = true
        }
    }

    suspend fun setEmail(email: String) {
        dataStore.edit { prefs -> prefs[ACCOUNT_EMAIL] = email }
    }

    suspend fun clearAccount() {
        dataStore.edit { prefs ->
            prefs.remove(ACCOUNT_EMAIL)
            prefs[SYNC_ENABLED] = false
        }
    }

    suspend fun getAccessToken(): String? {
        val email = dataStore.data.first()[ACCOUNT_EMAIL] ?: return null
        val lastSignedIn = GoogleSignIn.getLastSignedInAccount(context)
        Log.d("GCalSync", "getLastSignedInAccount: email=${lastSignedIn?.email} (stored=$email)")
        val account = lastSignedIn?.account ?: Account(email, "com.google")
        return withContext(Dispatchers.IO) {
            try {
                GoogleAuthUtil.getToken(context, account, CALENDAR_SCOPE)
            } catch (e: UserRecoverableAuthException) {
                Log.w("GCalSync", "UserRecoverableAuthException — need consent: ${e.message}")
                e.intent?.let { _pendingConsentIntent.tryEmit(it) }
                null
            } catch (e: Exception) {
                Log.e("GCalSync", "getToken error: ${e::class.simpleName}: ${e.message}")
                null
            }
        }
    }

    suspend fun saveEventId(activityId: Long, eventId: String) {
        dataStore.edit { prefs ->
            val current = prefs[EVENT_MAP] ?: "{}"
            val map = parseEventMap(current).toMutableMap()
            map[activityId.toString()] = eventId
            prefs[EVENT_MAP] = serializeEventMap(map)
        }
    }

    suspend fun getEventId(activityId: Long): String? {
        val raw = dataStore.data.first()[EVENT_MAP] ?: return null
        return parseEventMap(raw)[activityId.toString()]
    }

    suspend fun saveReminderEventId(reminderId: Long, eventId: String) {
        dataStore.edit { prefs ->
            val map = parseEventMap(prefs[REMINDER_EVENT_MAP] ?: "{}").toMutableMap()
            map[reminderId.toString()] = eventId
            prefs[REMINDER_EVENT_MAP] = serializeEventMap(map)
        }
    }

    suspend fun getReminderEventId(reminderId: Long): String? {
        val raw = dataStore.data.first()[REMINDER_EVENT_MAP] ?: return null
        return parseEventMap(raw)[reminderId.toString()]
    }

    suspend fun removeReminderEventId(reminderId: Long) {
        dataStore.edit { prefs ->
            val map = parseEventMap(prefs[REMINDER_EVENT_MAP] ?: "{}").toMutableMap()
            map.remove(reminderId.toString())
            prefs[REMINDER_EVENT_MAP] = serializeEventMap(map)
        }
    }

    suspend fun removeEventId(activityId: Long) {
        dataStore.edit { prefs ->
            val current = prefs[EVENT_MAP] ?: return@edit
            val map = parseEventMap(current).toMutableMap()
            map.remove(activityId.toString())
            prefs[EVENT_MAP] = serializeEventMap(map)
        }
    }

    private fun parseEventMap(json: String): Map<String, String> {
        return try {
            val result = mutableMapOf<String, String>()
            val trimmed = json.trim().removePrefix("{").removeSuffix("}")
            if (trimmed.isBlank()) return result
            trimmed.split(",").forEach { entry ->
                val parts = entry.split(":")
                if (parts.size == 2) {
                    result[parts[0].trim().removeSurrounding("\"")] =
                        parts[1].trim().removeSurrounding("\"")
                }
            }
            result
        } catch (e: Exception) {
            emptyMap()
        }
    }

    private fun serializeEventMap(map: Map<String, String>): String {
        return "{" + map.entries.joinToString(",") { (k, v) -> "\"$k\":\"$v\"" } + "}"
    }
}
