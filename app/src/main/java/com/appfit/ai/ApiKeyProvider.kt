package com.appfit.ai

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

enum class AiProvider(val id: String, val displayName: String) {
    ANTHROPIC("anthropic", "Claude (Anthropic)"),
    GEMINI("gemini", "Gemini (Google)")
}

@Singleton
class ApiKeyProvider @Inject constructor(
    private val dataStore: DataStore<Preferences>
) {
    companion object {
        private val ANTHROPIC_KEY  = stringPreferencesKey("anthropic_api_key")
        private val GEMINI_KEY     = stringPreferencesKey("gemini_api_key")
        private val AI_PROVIDER    = stringPreferencesKey("ai_provider")
        private val SELECTED_MODEL = stringPreferencesKey("selected_ai_model")

        val CLAUDE_MODELS = listOf("claude-sonnet-4-6", "claude-opus-4-6", "claude-haiku-4-5-20251001")
        val GEMINI_MODELS = listOf("gemini-2.5-pro", "gemini-2.5-flash", "gemini-2.5-flash-lite", "gemini-3.1-flash", "gemini-3.1-flash-lite")
    }

    // ── Provider ─────────────────────────────────────────────────────────────
    val providerFlow: Flow<AiProvider> = dataStore.data.map { prefs ->
        if (prefs[AI_PROVIDER] == AiProvider.GEMINI.id) AiProvider.GEMINI else AiProvider.ANTHROPIC
    }

    suspend fun getProvider(): AiProvider =
        if (dataStore.data.first()[AI_PROVIDER] == AiProvider.GEMINI.id) AiProvider.GEMINI
        else AiProvider.ANTHROPIC

    suspend fun saveProvider(provider: AiProvider) {
        dataStore.edit { it[AI_PROVIDER] = provider.id }
    }

    // ── Model ─────────────────────────────────────────────────────────────────
    val selectedModelFlow: Flow<String> = dataStore.data.map { prefs ->
        val saved = prefs[SELECTED_MODEL]
        val provider = if (prefs[AI_PROVIDER] == AiProvider.GEMINI.id) AiProvider.GEMINI else AiProvider.ANTHROPIC
        val validModels = if (provider == AiProvider.GEMINI) GEMINI_MODELS else CLAUDE_MODELS
        if (saved != null && saved in validModels) saved else validModels.first()
    }

    suspend fun getSelectedModel(): String {
        val prefs = dataStore.data.first()
        val saved = prefs[SELECTED_MODEL]
        val provider = if (prefs[AI_PROVIDER] == AiProvider.GEMINI.id) AiProvider.GEMINI else AiProvider.ANTHROPIC
        val validModels = if (provider == AiProvider.GEMINI) GEMINI_MODELS else CLAUDE_MODELS
        return if (saved != null && saved in validModels) saved else validModels.first()
    }

    suspend fun saveSelectedModel(model: String) {
        dataStore.edit { it[SELECTED_MODEL] = model }
    }

    // ── Anthropic key ─────────────────────────────────────────────────────────
    val apiKeyFlow: Flow<String?> = dataStore.data.map { it[ANTHROPIC_KEY] }

    suspend fun getApiKey(): String? = dataStore.data.first()[ANTHROPIC_KEY]

    suspend fun saveApiKey(key: String) {
        dataStore.edit { it[ANTHROPIC_KEY] = key }
    }

    // ── Gemini key ────────────────────────────────────────────────────────────
    val geminiKeyFlow: Flow<String?> = dataStore.data.map { it[GEMINI_KEY] }

    suspend fun getGeminiApiKey(): String? = dataStore.data.first()[GEMINI_KEY]

    suspend fun saveGeminiApiKey(key: String) {
        dataStore.edit { it[GEMINI_KEY] = key }
    }

    // ── Is any key set (based on current provider) ────────────────────────────
    val apiKeyFlow_any: Flow<Boolean> = combine(providerFlow, apiKeyFlow, geminiKeyFlow) { provider, ant, gem ->
        when (provider) {
            AiProvider.GEMINI -> !gem.isNullOrBlank()
            AiProvider.ANTHROPIC -> !ant.isNullOrBlank()
        }
    }

    suspend fun isApiKeySet(): Boolean {
        val prefs = dataStore.data.first()
        return when (getProvider()) {
            AiProvider.GEMINI    -> !prefs[GEMINI_KEY].isNullOrBlank()
            AiProvider.ANTHROPIC -> !prefs[ANTHROPIC_KEY].isNullOrBlank()
        }
    }
}
