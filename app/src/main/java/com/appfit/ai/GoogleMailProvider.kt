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
class GoogleMailProvider @Inject constructor(
    @ApplicationContext private val context: Context,
    private val dataStore: DataStore<Preferences>
) {
    companion object {
        val GMAIL_ENABLED = booleanPreferencesKey("gmail_scan_enabled")
        val GMAIL_ACCOUNT = stringPreferencesKey("gmail_account_email")
        val LAST_SCAN_TS = androidx.datastore.preferences.core.longPreferencesKey("gmail_last_scan_epoch")
        const val GMAIL_SCOPE = "oauth2:https://www.googleapis.com/auth/gmail.readonly"
    }

    val isEnabledFlow: Flow<Boolean> = dataStore.data.map { it[GMAIL_ENABLED] ?: false }
    val accountEmailFlow: Flow<String?> = dataStore.data.map { it[GMAIL_ACCOUNT] }

    private val _pendingConsentIntent = MutableSharedFlow<Intent>(extraBufferCapacity = 1)
    val pendingConsentIntent: SharedFlow<Intent> = _pendingConsentIntent

    suspend fun isEnabled(): Boolean = dataStore.data.first()[GMAIL_ENABLED] ?: false

    suspend fun saveAccount(email: String) {
        dataStore.edit { prefs ->
            prefs[GMAIL_ACCOUNT] = email
            prefs[GMAIL_ENABLED] = true
        }
    }

    suspend fun setEmail(email: String) {
        dataStore.edit { prefs -> prefs[GMAIL_ACCOUNT] = email }
    }

    suspend fun setEnabled(enabled: Boolean) {
        dataStore.edit { prefs -> prefs[GMAIL_ENABLED] = enabled }
    }

    suspend fun getLastScanTimestamp(): Long = dataStore.data.first()[LAST_SCAN_TS] ?: 0L

    suspend fun saveLastScanTimestamp(epochMs: Long) {
        dataStore.edit { it[LAST_SCAN_TS] = epochMs }
    }

    suspend fun clearLastScanTimestamp() {
        dataStore.edit { it.remove(LAST_SCAN_TS) }
    }

    suspend fun clearAccount() {
        dataStore.edit { prefs ->
            prefs.remove(GMAIL_ACCOUNT)
            prefs[GMAIL_ENABLED] = false
        }
    }

    suspend fun getAccessToken(): String? {
        val email = dataStore.data.first()[GMAIL_ACCOUNT] ?: return null
        val lastSignedIn = GoogleSignIn.getLastSignedInAccount(context)
        val account = lastSignedIn?.account ?: Account(email, "com.google")
        return withContext(Dispatchers.IO) {
            try {
                GoogleAuthUtil.getToken(context, account, GMAIL_SCOPE)
            } catch (e: UserRecoverableAuthException) {
                Log.w("GmailAuth", "UserRecoverableAuthException — need consent: ${e.message}")
                e.intent?.let { _pendingConsentIntent.tryEmit(it) }
                null
            } catch (e: Exception) {
                Log.e("GmailAuth", "getToken error: ${e::class.simpleName}: ${e.message}")
                null
            }
        }
    }
}
