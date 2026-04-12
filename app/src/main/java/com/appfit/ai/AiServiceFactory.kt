package com.appfit.ai

import com.appfit.data.model.ChatMessage
import com.appfit.data.model.DailyPlan
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Delegates to AnthropicService or GeminiService based on the currently selected provider.
 */
@Singleton
class AiServiceFactory @Inject constructor(
    private val apiKeyProvider: ApiKeyProvider,
    private val anthropicService: AnthropicService,
    private val geminiService: GeminiService
) : AiService {

    override suspend fun sendMessage(
        userMessage: String,
        conversationHistory: List<ChatMessage>,
        currentPlan: DailyPlan,
        toolExecutor: ClaudeToolExecutor
    ): AiResponse {
        return when (apiKeyProvider.getProvider()) {
            AiProvider.GEMINI -> geminiService.sendMessage(userMessage, conversationHistory, currentPlan, toolExecutor)
            AiProvider.ANTHROPIC -> anthropicService.sendMessage(userMessage, conversationHistory, currentPlan, toolExecutor)
        }
    }
}
