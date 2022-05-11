package com.example.android.repository

import com.example.android.api.ChatService
import com.example.android.models.Chat
import com.example.android.models.Message
import retrofit2.Response

class ChatRepository(private val service: ChatService) {

    suspend fun getUserChats(): Response<List<Chat>> {
        return service.getUserChats()
    }

    suspend fun getChatById(id: Int): Response<Chat> {
        return service.getChatById(id)
    }

    suspend fun getChatMessages(id: Int): Response<List<Message>>{
        return service.getChatMessages(id)
    }
}