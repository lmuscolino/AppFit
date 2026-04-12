package com.appfit.data.repository

import com.appfit.data.local.dao.ChatMessageDao
import com.appfit.data.model.ChatMessage
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ChatRepository @Inject constructor(
    private val dao: ChatMessageDao
) {
    fun getAllMessages(): Flow<List<ChatMessage>> = dao.getAllMessages()

    suspend fun getLastNMessages(n: Int): List<ChatMessage> = dao.getLastNMessages(n)

    suspend fun insertMessage(message: ChatMessage) = dao.insertMessage(message)

    suspend fun clearHistory() = dao.clearHistory()
}
