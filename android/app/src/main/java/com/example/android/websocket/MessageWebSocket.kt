package com.example.android.websocket

import android.util.Log
import androidx.lifecycle.MutableLiveData
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
                Log.e("OPEN", "Finally did it")
                Log.e("RECONNECTED ATTEMPT", "" + reconnectTime)
            }

            override fun onMessage(response: String?) {
                cnt++
                if (response != null) {
                    Log.e("MESSAGE $cnt", response)
                    val data = Gson().fromJson(response, Message::class.java)
                    socketMessage.postValue(data)
                    Log.e("DATA $cnt", data.content)
                }else{
                    Log.e("MESSAGE", "null message")
                }
            }

            override fun onClose(code: Int, reason: String?, remote: Boolean) {
                Log.e("CLOSE", "onClose")
            }

            override fun onError(ex: Exception?) {
                Log.e("createWebSocketClient", "onError: ${ex?.message}")
            }
        }
        webSocketClient.connect()
    }
    fun sendMessage(content: String){ // {"type": "send", "content": "Hello"}
        this.webSocketClient.send("{"
                + "\"type\": \"send\","
                + "\"content\":\"$content\""
                + "}")
    }
    fun resendMessage(message: Int, chat: Int){ // {"type": "resend", "message": 1, "chat": 2}
        this.webSocketClient.send("{"
                + "\"type\": \"resend\","
                + "\"message\":$message,"
                + "\"chat\":$chat"
                + "}")
    }
    fun editMessage(content: String, message: Int){ // {"type": "edit", "message": 1, "content": "new content"}
        this.webSocketClient.send("{"
                + "\"type\": \"edit\","
                + "\"message\":$message,"
                + "\"content\":\"$content\""
                + "}")
    }
    fun deleteMessage(message: Int){ // {"type": "delete", "message": 1}
        this.webSocketClient.send("{"
                + "\"type\": \"delete\","
                + "\"message\":$message"
                + "}")
    }
}