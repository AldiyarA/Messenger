package com.example.android.websocket

import androidx.lifecycle.MutableLiveData
import com.beust.klaxon.Klaxon
import com.example.android.globals.AppPreferences
import com.example.android.models.Message
import com.google.gson.Gson
import org.java_websocket.client.WebSocketClient
import org.java_websocket.handshake.ServerHandshake
import java.lang.Exception
import java.net.URI

class MessageWebSocket(private val chat: Int) {
    val socketMessage: MutableLiveData<Message> = MutableLiveData()

    val Base_url = "http://10.0.2.2:8000/ws/chat/"
    private lateinit var webSocketClient: WebSocketClient

    fun initWebSocket(){
        val token = AppPreferences.accessToken
        val url = Base_url + ""
        val coinbaseUri: URI? = URI("$url$chat?token=$token")
        createWebSocketClient(coinbaseUri)
    }

    private fun createWebSocketClient(coinbaseUri: URI?) {
        webSocketClient = object : WebSocketClient(coinbaseUri) {
            var cnt = 0
            var reconnectTime = 0
            var maxReconnectTime = 5
            override fun onOpen(handshakedata: ServerHandshake?) {
            }

            override fun onMessage(response: String?) {
                cnt++
                if (response != null) {
                    val data = Gson().fromJson(response, Message::class.java)
                    socketMessage.postValue(data)
                }
            }

            override fun onClose(code: Int, reason: String?, remote: Boolean) {
            }

            override fun onError(ex: Exception?) {
            }
        }
        webSocketClient.connect()
    }
    fun sendMessage(content: String){ // {"type": "send", "content": "Hello"}
        val body = mutableMapOf<String, Any>()
        body["type"] = "send"
        body["content"] = content
        this.webSocketClient.send(Klaxon().toJsonString(body))

    }
    fun resendMessage(message: Int, chat: Int){ // {"type": "resend", "message": 1, "chat": 2}
        val body = mutableMapOf<String, Any>()
        body["type"] = "resend"
        body["message"] = message
        body["chat"] = chat
        this.webSocketClient.send(Klaxon().toJsonString(body))
    }
    fun editMessage(content: String, message: Int){ // {"type": "edit", "message": 1, "content": "new content"}
        val body = mutableMapOf<String, Any>()
        body["type"] = "edit"
        body["message"] = message
        this.webSocketClient.send(Klaxon().toJsonString(body))

    }
    fun deleteMessage(message: Int){ // {"type": "delete", "message": 1}
        val body = mutableMapOf<String, Any>()
        body["type"] = "delete"
        body["message"] = message
        this.webSocketClient.send(Klaxon().toJsonString(body))
    }

    fun disconnect(){
        webSocketClient.connection.close()
    }
}