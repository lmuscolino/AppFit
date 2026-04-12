package com.appfit.domain.usecase

import com.appfit.ai.AiService
import com.appfit.ai.ClaudeToolExecutor
import com.appfit.data.model.ChatMessage
import com.appfit.data.model.ChatRole
import com.appfit.data.model.DailyPlan
import com.appfit.data.repository.ChatRepository
import com.appfit.notification.NotificationScheduler
import javax.inject.Inject

class SendChatMessageUseCase @Inject constructor(
    private val chatRepository: ChatRepository,
    private val aiService: AiService,
    private val toolExecutor: ClaudeToolExecutor,
    private val notificationScheduler: NotificationScheduler
) {
    // Returns true if the AI modified the plan
    suspend operator fun invoke(userText: String, currentPlan: DailyPlan): Boolean {
        // Save user message
        val userMessage = ChatMessage(role = ChatRole.USER, content = userText)
        chatRepository.insertMessage(userMessage)

        // Get conversation history
        val history = chatRepository.getLastNMessages(20).reversed()

        // Send to AI (provider chosen at runtime by AiServiceFactory)
        val response = aiService.sendMessage(
            userMessage = userText,
            conversationHistory = history.dropLast(1), // exclude the message we just inserted
            currentPlan = currentPlan,
            toolExecutor = toolExecutor
        )

        // Save assistant response
        val assistantMessage = ChatMessage(
            role = ChatRole.ASSISTANT,
            content = response.text,
            planModified = response.planModified
        )
        chatRepository.insertMessage(assistantMessage)

        // If plan was modified, reschedule notifications
        if (response.planModified) {
            notificationScheduler.rescheduleAll()
        }

        return response.planModified
    }
}
