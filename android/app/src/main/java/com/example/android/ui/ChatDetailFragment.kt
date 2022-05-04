package com.example.android.ui

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.android.adapters.ChatAdapter
import com.example.android.adapters.MessageAdapter
import com.example.android.api.ChatService
import com.example.android.api.createChatService
import com.example.android.databinding.FragmentChatDetailBinding
import com.example.android.models.Chat
import com.example.android.models.Message
import com.example.android.repository.ChatRepository
import com.example.android.view_model.ChatViewModel
import com.example.android.view_model.ChatViewModelFactory
import com.example.android.websocket.MessageWebSocket


class ChatDetailFragment : Fragment() {
    private val args: ChatDetailFragmentArgs by navArgs();
    private lateinit var binding: FragmentChatDetailBinding
    private lateinit var repository: ChatRepository
    private lateinit var viewModel: ChatViewModel
    private lateinit var service: ChatService
    private lateinit var viewModelFactory: ChatViewModelFactory

    private lateinit var layoutManager: RecyclerView.LayoutManager
    private lateinit var layoutManager2: RecyclerView.LayoutManager
    private lateinit var adapter: MessageAdapter
    private lateinit var adapter2: ChatAdapter
    private lateinit var webSocket: MessageWebSocket
    private lateinit var adapterActions: AdapterActions
    private var actionMessageId = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentChatDetailBinding.inflate(layoutInflater)
        layoutManager = LinearLayoutManager(activity)
        layoutManager2 = LinearLayoutManager(activity)

        configureViewModel()
        configureAdapter()

        val chatId = args.chatId
        viewModel.getChatById(chatId)
        viewModel.getMessages(chatId)

        observeResponse()
        setOnClickListeners()
        return binding.root
    }
    private fun configureViewModel(){
        service = createChatService()
        repository = ChatRepository(service)
        viewModelFactory = ChatViewModelFactory(repository=repository)
        viewModel = ViewModelProvider(this, viewModelFactory)[ChatViewModel::class.java]
    }

    private fun configureAdapter() {
        binding.messagesRV.layoutManager = layoutManager
        binding.resendChatsRV.layoutManager = layoutManager2
        adapterActions = AdapterActions()
        adapter = MessageAdapter(adapterActions)
        binding.messagesRV.adapter = adapter

        adapter2 = ChatAdapter { resendMessage(it) }
        binding.resendChatsRV.adapter = adapter2
    }

    private fun resendMessage(chat: Chat){
        webSocket.resendMessage(actionMessageId, chat.id)
        toSendMode()
    }

    private fun connectWebSocket(id: Int){
        webSocket = MessageWebSocket(id)
        webSocket.initWebSocket()
        webSocket.socketMessage.observe(viewLifecycleOwner) { message->
            Log.e("On chat detail", message.content)
            if (message.message_type == 3){
                adapter.editMessage(message)
                return@observe
            }else if(message.message_type == 4){
                adapter.deleteMessage(message)
                return@observe
            }
            adapter.addMessage(message)
            binding.messagesRV.scrollToPosition(adapter.itemCount-1)

        }
    }

    private fun observeResponse(){
        viewModel.chatById.observe(viewLifecycleOwner) {response->
            if (response.isSuccessful){
                val chat = response.body()
                binding.chatName.text = chat?.name
                if (chat != null) {
                    connectWebSocket(chat.id)
                }
            }else {
                Toast.makeText(context, response.errorBody().toString(), Toast.LENGTH_SHORT).show()
            }
        }
        viewModel.chatMessages.observe(viewLifecycleOwner) {response ->
            if (response.isSuccessful){
                val messages = response.body()
                adapter.submitList(messages!!)
                binding.messagesRV.scrollToPosition(adapter.itemCount-1)
            }else{
                Toast.makeText(context, response.errorBody().toString(), Toast.LENGTH_SHORT).show()
            }
        }
        viewModel.userChatsResponse.observe(viewLifecycleOwner) {response ->
            if (response.isSuccessful){
                adapter2.submitList(response.body())
            }else {
                Toast.makeText(context, response.errorBody().toString(), Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setOnClickListeners(){
        binding.sendMessage.setOnClickListener{ view: View->
            Log.e("Button", "Send message")
            val content = binding.messageContent.text.toString()
            if (content.isNotEmpty()){
                webSocket.sendMessage(content)
            }
            binding.messageContent.text.clear()
        }
        binding.sendNewMessage.setOnClickListener { view: View->
            Log.e("Button", "Edit message")
            val content = binding.messageNewContent.text.toString()
            if (content.isNotEmpty()){
                webSocket.editMessage(content, actionMessageId)
            }else{
                webSocket.deleteMessage(actionMessageId)
            }
            binding.messageContent.text.clear()
            toSendMode()
        }
        binding.cancelNewMessage.setOnClickListener { toSendMode() }
    }

    private fun toSendMode(){
        actionMessageId = 0
        binding.sendMessageBlock.visibility = View.VISIBLE
        binding.editMessageBlock.visibility = View.GONE
        binding.resendChatsBlock.visibility = View.GONE
    }

    inner class AdapterActions {
        fun resendMessage(message: Int){
            actionMessageId = message
            binding.resendChatsBlock.visibility = View.VISIBLE
            viewModel.getUserChats()
        }

        fun editMessage(message: Message){
            actionMessageId = message.id
            binding.editMessageBlock.visibility = View.VISIBLE
            binding.sendMessageBlock.visibility = View.GONE
            binding.oldContent.text = message.content
            binding.messageNewContent.text.clear()
            binding.messageNewContent.text.append(message.content)
        }
        fun deleteMessage(id: Int){
            webSocket.deleteMessage(id)
        }
    }
}

