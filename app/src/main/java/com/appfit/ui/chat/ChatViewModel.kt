package com.appfit.ui.chat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.appfit.ai.AiDebugLogger
import com.appfit.ai.ApprovalItem
import com.appfit.ai.ApprovalRequest
import com.appfit.ai.ApprovalResult
import com.appfit.ai.ToolApprovalManager
import com.appfit.data.model.ChatMessage
import com.appfit.data.repository.ChatRepository
import com.appfit.domain.usecase.GetDailyPlanUseCase
import com.appfit.domain.usecase.SendChatMessageUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val chatRepository: ChatRepository,
    private val sendChatMessageUseCase: SendChatMessageUseCase,
    private val getDailyPlanUseCase: GetDailyPlanUseCase,
    private val aiDebugLogger: AiDebugLogger,
    private val toolApprovalManager: ToolApprovalManager
) : ViewModel() {

    val messages: StateFlow<List<ChatMessage>> = chatRepository.getAllMessages()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val debugLog: StateFlow<List<String>> = aiDebugLogger.entries

    val pendingApproval: StateFlow<ApprovalRequest?> = toolApprovalManager.pendingRequest

    private val _isThinking = MutableStateFlow(false)
    val isThinking: StateFlow<Boolean> = _isThinking.asStateFlow()

    private val _planModified = MutableStateFlow(false)
    val planModified: StateFlow<Boolean> = _planModified.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    private var pendingRegenerationMessage: String? = null
    private var activeJob: Job? = null

    fun sendMessage(text: String) {
        if (text.isBlank() || _isThinking.value) return
        activeJob = viewModelScope.launch {
            _isThinking.value = true
            _errorMessage.value = null
            try {
                val todayPlan = getDailyPlanUseCase(LocalDate.now()).first()
                val modified = sendChatMessageUseCase(text, todayPlan)
                if (modified) _planModified.value = true

                // Auto-send regeneration request if queued after plan approval
                val regenMsg = pendingRegenerationMessage
                pendingRegenerationMessage = null
                if (!regenMsg.isNullOrBlank()) {
                    // Auto-approve tools for regen: no second dialog shown to the user
                    toolApprovalManager.setAutoApproveNext()
                    val freshPlan = getDailyPlanUseCase(LocalDate.now()).first()
                    sendChatMessageUseCase(regenMsg, freshPlan)
                }
            } catch (e: Exception) {
                pendingRegenerationMessage = null
                if (e !is kotlinx.coroutines.CancellationException) {
                    _errorMessage.value = e.message ?: "Errore durante la comunicazione con l'AI"
                }
            } finally {
                _isThinking.value = false
                activeJob = null
            }
        }
    }

    fun cancelMessage() {
        activeJob?.cancel()
        activeJob = null
        pendingRegenerationMessage = null
        _isThinking.value = false
    }

    fun approveItems(items: List<ApprovalItem>, regenerationMessage: String? = null) {
        pendingRegenerationMessage = regenerationMessage?.takeIf { it.isNotBlank() }
        toolApprovalManager.submit(ApprovalResult.Approved(items))
    }

    fun rejectApproval() {
        toolApprovalManager.submit(ApprovalResult.Rejected)
    }

    fun clearPlanModifiedBanner() {
        _planModified.value = false
    }

    fun clearHistory() {
        viewModelScope.launch {
            chatRepository.clearHistory()
        }
    }

    fun dismissError() {
        _errorMessage.value = null
    }
}
