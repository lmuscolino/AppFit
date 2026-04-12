package com.appfit.ui.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.appfit.ai.AiProvider
import com.appfit.ai.ApiKeyProvider
import com.appfit.ai.UserProfile
import com.appfit.ai.UserProfileProvider
import com.appfit.notification.MonthlyUpdateWorker
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val userProfileProvider: UserProfileProvider,
    private val apiKeyProvider: ApiKeyProvider,
    private val workManager: WorkManager
) : ViewModel() {

    val profile: StateFlow<UserProfile> = userProfileProvider.profileFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), UserProfile(null, null, null, false))

    val currentProvider: StateFlow<AiProvider> = apiKeyProvider.providerFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), AiProvider.ANTHROPIC)

    val currentModel: StateFlow<String> = apiKeyProvider.selectedModelFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), ApiKeyProvider.CLAUDE_MODELS.first())

    private val _aiSaveResult = MutableStateFlow<AiSaveResult>(AiSaveResult.Idle)
    val aiSaveResult: StateFlow<AiSaveResult> = _aiSaveResult.asStateFlow()

    fun saveProfile(
        weightKg: Float?,
        heightCm: Int?,
        age: Int?,
        monthlyUpdateEnabled: Boolean
    ) {
        viewModelScope.launch {
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
        fitnessGoal: String?
    ) {
        viewModelScope.launch {
            userProfileProvider.savePreferences(workoutTypes, dietaryRestrictions, fitnessGoal)
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

    sealed class AiSaveResult {
        object Idle : AiSaveResult()
        object Saved : AiSaveResult()
        data class Error(val message: String) : AiSaveResult()
    }
}
