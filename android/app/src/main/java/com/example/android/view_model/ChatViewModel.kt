package com.example.android.view_model

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.android.models.Chat
import com.example.android.models.Message
import com.example.android.repository.ChatRepository
import kotlinx.coroutines.launch
import retrofit2.Response

class ChatViewModel(private val repository: ChatRepository): ViewModel() {
    val chatsResponse: MutableLiveData<Response<List<Chat>>> = MutableLiveData()
    val userChatsResponse: MutableLiveData<Response<List<Chat>>> = MutableLiveData()
    val chatById: MutableLiveData<Response<Chat>> = MutableLiveData()
    val chatMessages: MutableLiveData<Response<List<Message>>> = MutableLiveData()

    fun getChats(){
        viewModelScope.launch {
            val response = repository.getChats()
            chatsResponse.value = response
        }
    }

    fun getUserChats(){
        viewModelScope.launch {
            val response = repository.getUserChats()
            userChatsResponse.value = response
        }
    }

    fun getChatById(id: Int){
        viewModelScope.launch {
            val response = repository.getChatById(id)
            chatById.value = response
        }
    }

    fun getMessages(id: Int){
        viewModelScope.launch {
            val response = repository.getChatMessages(id)
            chatMessages.value = response
        }
    }
}