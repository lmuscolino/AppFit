package com.appfit.ui.profile

import android.content.Intent
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.appfit.ai.AiProvider
import com.appfit.ai.ApiKeyProvider
import com.appfit.ai.GoogleCalendarProvider
import com.appfit.ai.GoogleMailProvider
import com.appfit.notification.GmailScanWorker
import com.appfit.ai.UserNote
import com.appfit.ai.UserProfile
import com.appfit.ai.UserProfileProvider
import com.appfit.ai.WorkoutScheduleSlot
import com.appfit.notification.MonthlyUpdateWorker
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val userProfileProvider: UserProfileProvider,
    private val apiKeyProvider: ApiKeyProvider,
    private val workManager: WorkManager,
    private val googleCalendarProvider: GoogleCalendarProvider,
    private val googleMailProvider: GoogleMailProvider
) : ViewModel() {

    val profile: StateFlow<UserProfile> = userProfileProvider.profileFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), UserProfile(sex = null, weightKg = null, heightCm = null, age = null, monthlyUpdateEnabled = false))

    val currentProvider: StateFlow<AiProvider> = apiKeyProvider.providerFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), AiProvider.ANTHROPIC)

    val currentModel: StateFlow<String> = apiKeyProvider.selectedModelFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), ApiKeyProvider.CLAUDE_MODELS.first())

    val googleCalendarSyncEnabled: StateFlow<Boolean> = googleCalendarProvider.syncEnabledFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    val googleCalendarAccount: StateFlow<String?> = googleCalendarProvider.accountEmailFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val gmailScanEnabled: StateFlow<Boolean> = googleMailProvider.isEnabledFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    val gmailAccount: StateFlow<String?> = googleMailProvider.accountEmailFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    // Account Google unificato: primo non-null tra Calendar e Gmail
    val googleAccount: StateFlow<String?> = combine(
        googleCalendarProvider.accountEmailFlow,
        googleMailProvider.accountEmailFlow
    ) { calEmail, gmailEmail -> calEmail ?: gmailEmail }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val anthropicKey: StateFlow<String> = apiKeyProvider.apiKeyFlow
        .map { it ?: "" }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "")

    val geminiKey: StateFlow<String> = apiKeyProvider.geminiKeyFlow
        .map { it ?: "" }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "")

    private val _aiSaveResult = MutableStateFlow<AiSaveResult>(AiSaveResult.Idle)
    val aiSaveResult: StateFlow<AiSaveResult> = _aiSaveResult.asStateFlow()

    private val _pendingConsentIntent = MutableStateFlow<Intent?>(null)
    val pendingConsentIntent: StateFlow<Intent?> = _pendingConsentIntent.asStateFlow()

    init {
        viewModelScope.launch {
            googleCalendarProvider.pendingConsentIntent.collect { _pendingConsentIntent.value = it }
        }
        viewModelScope.launch {
            googleMailProvider.pendingConsentIntent.collect { _pendingConsentIntent.value = it }
        }
    }

    fun clearPendingConsentIntent() {
        _pendingConsentIntent.value = null
    }

    fun saveProfile(
        sex: String?,
        weightKg: Float?,
        heightCm: Int?,
        age: Int?,
        monthlyUpdateEnabled: Boolean
    ) {
        viewModelScope.launch {
            userProfileProvider.savePhysicalProfile(sex = sex, weightKg = weightKg, heightCm = heightCm, age = age)
            userProfileProvider.saveProfile(weightKg, heightCm, age, monthlyUpdateEnabled)
            if (monthlyUpdateEnabled) {
                val request = PeriodicWorkRequestBuilder<MonthlyUpdateWorker>(30, TimeUnit.DAYS).build()
                workManager.enqueueUniquePeriodicWork(
                    MonthlyUpdateWorker.WORK_NAME,
                    ExistingPeriodicWorkPolicy.UPDATE,
                    request
                )
            } else {
                workManager.cancelUniqueWork(MonthlyUpdateWorker.WORK_NAME)
            }
        }
    }

    fun savePreferences(
        workoutTypes: List<String>?,
        dietaryRestrictions: List<String>?,
        fitnessGoal: String?,
        fitnessLevel: String?
    ) {
        viewModelScope.launch {
            userProfileProvider.savePreferences(workoutTypes, dietaryRestrictions, fitnessGoal, fitnessLevel)
        }
    }

    fun saveAiSettings(provider: AiProvider, apiKey: String, model: String) {
        val trimmed = apiKey.trim()
        when (provider) {
            AiProvider.ANTHROPIC -> {
                if (trimmed.isNotBlank() && !trimmed.startsWith("sk-ant-")) {
                    _aiSaveResult.value = AiSaveResult.Error("Chiave Claude non valida (deve iniziare con sk-ant-)")
                    return
                }
            }
            AiProvider.GEMINI -> {
                if (trimmed.isNotBlank() && trimmed.length < 10) {
                    _aiSaveResult.value = AiSaveResult.Error("Chiave Gemini non valida")
                    return
                }
            }
        }
        viewModelScope.launch {
            apiKeyProvider.saveProvider(provider)
            apiKeyProvider.saveSelectedModel(model)
            if (trimmed.isNotBlank()) {
                when (provider) {
                    AiProvider.ANTHROPIC -> apiKeyProvider.saveApiKey(trimmed)
                    AiProvider.GEMINI -> apiKeyProvider.saveGeminiApiKey(trimmed)
                }
            }
            _aiSaveResult.value = AiSaveResult.Saved
        }
    }

    fun resetAiSaveResult() {
        _aiSaveResult.value = AiSaveResult.Idle
    }

    fun addNote(content: String) {
        viewModelScope.launch { userProfileProvider.addNote(content) }
    }

    fun updateNote(id: String, content: String) {
        viewModelScope.launch { userProfileProvider.updateNote(id, content) }
    }

    fun deleteNote(id: String) {
        viewModelScope.launch { userProfileProvider.deleteNote(id) }
    }

    fun saveWorkoutSchedule(slots: List<WorkoutScheduleSlot>) {
        viewModelScope.launch { userProfileProvider.saveWorkoutSchedule(slots) }
    }

    fun connectGoogle(email: String) {
        viewModelScope.launch {
            googleCalendarProvider.setEmail(email)
            googleMailProvider.setEmail(email)
        }
    }

    fun disconnectGoogle() {
        viewModelScope.launch {
            googleCalendarProvider.clearAccount()
            googleMailProvider.clearAccount()
            GmailScanWorker.cancel(workManager)
        }
    }

    fun setCalendarSyncEnabled(enabled: Boolean) {
        viewModelScope.launch { googleCalendarProvider.setSyncEnabled(enabled) }
    }

    fun setGmailScanEnabled(enabled: Boolean) {
        viewModelScope.launch {
            googleMailProvider.setEnabled(enabled)
            if (enabled) GmailScanWorker.schedule(workManager)
            else GmailScanWorker.cancel(workManager)
        }
    }

    // Keep for backward compat
    fun onGoogleSignInSuccess(email: String) = connectGoogle(email)
    fun disconnectGoogleCalendar() = disconnectGoogle()
    fun onGmailSignInSuccess(email: String) = connectGoogle(email)
    fun disconnectGmail() = disconnectGoogle()

    sealed class AiSaveResult {
        object Idle : AiSaveResult()
        object Saved : AiSaveResult()
        data class Error(val message: String) : AiSaveResult()
    }
}
