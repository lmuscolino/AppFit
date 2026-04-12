package com.appfit.ui.onboarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.appfit.ai.AiProvider
import com.appfit.ai.ApiKeyProvider
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
class OnboardingViewModel @Inject constructor(
    private val apiKeyProvider: ApiKeyProvider,
    private val userProfileProvider: UserProfileProvider,
    private val workManager: WorkManager
) : ViewModel() {

    val isApiKeySet: StateFlow<Boolean> = apiKeyProvider.apiKeyFlow_any
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    private val _selectedProvider = MutableStateFlow(AiProvider.ANTHROPIC)
    val selectedProvider: StateFlow<AiProvider> = _selectedProvider.asStateFlow()

    private val _selectedModel = MutableStateFlow(ApiKeyProvider.CLAUDE_MODELS.first())
    val selectedModel: StateFlow<String> = _selectedModel.asStateFlow()

    private val _saveResult = MutableStateFlow<SaveResult>(SaveResult.Idle)
    val saveResult: StateFlow<SaveResult> = _saveResult.asStateFlow()

    fun setProvider(provider: AiProvider) {
        _selectedProvider.value = provider
        // Reset model to first available for this provider
        _selectedModel.value = when (provider) {
            AiProvider.ANTHROPIC -> ApiKeyProvider.CLAUDE_MODELS.first()
            AiProvider.GEMINI -> ApiKeyProvider.GEMINI_MODELS.first()
        }
    }

    fun setModel(model: String) {
        _selectedModel.value = model
    }

    fun modelsForCurrentProvider(): List<String> = when (_selectedProvider.value) {
        AiProvider.ANTHROPIC -> ApiKeyProvider.CLAUDE_MODELS
        AiProvider.GEMINI -> ApiKeyProvider.GEMINI_MODELS
    }

    fun saveApiKey(key: String) {
        val trimmed = key.trim()
        when (_selectedProvider.value) {
            AiProvider.ANTHROPIC -> {
                if (trimmed.isBlank() || !trimmed.startsWith("sk-ant-")) {
                    _saveResult.value = SaveResult.Error("Chiave API non valida. Deve iniziare con sk-ant-")
                    return
                }
            }
            AiProvider.GEMINI -> {
                if (trimmed.isBlank()) {
                    _saveResult.value = SaveResult.Error("Inserisci la tua chiave API Gemini")
                    return
                }
            }
        }
        viewModelScope.launch {
            apiKeyProvider.saveProvider(_selectedProvider.value)
            apiKeyProvider.saveSelectedModel(_selectedModel.value)
            when (_selectedProvider.value) {
                AiProvider.ANTHROPIC -> apiKeyProvider.saveApiKey(trimmed)
                AiProvider.GEMINI -> apiKeyProvider.saveGeminiApiKey(trimmed)
            }
            _saveResult.value = SaveResult.ApiKeySaved
        }
    }

    fun saveProfile(
        weightKg: Float?,
        heightCm: Int?,
        age: Int?,
        monthlyUpdateEnabled: Boolean
    ) {
        viewModelScope.launch {
            userProfileProvider.saveProfile(weightKg, heightCm, age, monthlyUpdateEnabled)
            if (monthlyUpdateEnabled) {
                val request = PeriodicWorkRequestBuilder<MonthlyUpdateWorker>(30, TimeUnit.DAYS)
                    .build()
                workManager.enqueueUniquePeriodicWork(
                    MonthlyUpdateWorker.WORK_NAME,
                    ExistingPeriodicWorkPolicy.UPDATE,
                    request
                )
            } else {
                workManager.cancelUniqueWork(MonthlyUpdateWorker.WORK_NAME)
            }
            _saveResult.value = SaveResult.Success
        }
    }

    fun resetSaveResult() {
        _saveResult.value = SaveResult.Idle
    }

    sealed class SaveResult {
        object Idle : SaveResult()
        object ApiKeySaved : SaveResult()
        object Success : SaveResult()
        data class Error(val message: String) : SaveResult()
    }
}
