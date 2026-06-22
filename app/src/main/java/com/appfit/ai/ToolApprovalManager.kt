package com.appfit.ai

import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ToolApprovalManager @Inject constructor() {

    private val _pendingRequest = MutableStateFlow<ApprovalRequest?>(null)
    val pendingRequest: StateFlow<ApprovalRequest?> = _pendingRequest.asStateFlow()

    private var responseChannel: Channel<ApprovalResult>? = null

    @Volatile private var autoApproveNext = false

    fun setAutoApproveNext() {
        autoApproveNext = true
    }

    suspend fun requestApproval(request: ApprovalRequest): ApprovalResult {
        if (autoApproveNext) {
            autoApproveNext = false
            return ApprovalResult.Approved(request.items.map { it.copy(isSelected = true) })
        }
        val channel = Channel<ApprovalResult>(1)
        responseChannel = channel
        _pendingRequest.value = request
        return try {
            channel.receive()
        } finally {
            _pendingRequest.value = null
            responseChannel = null
        }
    }

    fun submit(result: ApprovalResult) {
        responseChannel?.trySend(result)
    }
}
