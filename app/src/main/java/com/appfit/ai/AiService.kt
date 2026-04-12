package com.appfit.ai

import com.appfit.data.model.ChatMessage
import com.appfit.data.model.DailyPlan

interface AiService {
    suspend fun sendMessage(
        userMessage: String,
        conversationHistory: List<ChatMessage>,
        currentPlan: DailyPlan,
        toolExecutor: ClaudeToolExecutor
    ): AiResponse
}
