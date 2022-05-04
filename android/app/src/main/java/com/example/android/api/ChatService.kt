package com.example.android.api

import com.example.android.models.Chat
import com.example.android.models.Message
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface ChatService {
    @GET("chats/")
    suspend fun getChats(): Response<List<Chat>>

    @GET("user/chats/")
    suspend fun getUserChats(): Response<List<Chat>>

    @GET("chats/{id}/")
    suspend fun getChatById(@Path("id") chatId: Int): Response<Chat>

    @GET("chats/{id}/messages/")
    suspend fun getChatMessages(@Path("id") chatId: Int): Response<List<Message>>
}