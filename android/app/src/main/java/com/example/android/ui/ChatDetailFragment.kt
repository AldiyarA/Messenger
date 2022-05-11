package com.example.android.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.android.R
import com.example.android.adapters.ChatAdapter
import com.example.android.adapters.MessageAdapter
import com.example.android.databinding.FragmentChatDetailBinding
import com.example.android.models.Chat
import com.example.android.models.Message
import com.example.android.view_model.ChatViewModel
import com.example.android.websocket.MessageWebSocket
import org.koin.androidx.viewmodel.ext.android.viewModel


class ChatDetailFragment : Fragment() {
    private val args: ChatDetailFragmentArgs by navArgs();
    private lateinit var binding: FragmentChatDetailBinding
    private val viewModel: ChatViewModel by viewModel<ChatViewModel>()

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

        configureAdapter()

        val chatId = args.chatId
        viewModel.getChatById(chatId)
        viewModel.getMessages(chatId)

        observeResponse()
        setOnClickListeners()
        return binding.root
    }

    private fun configureAdapter() {
        binding.messagesRV.layoutManager = layoutManager
        binding.resendChatsRV.layoutManager = layoutManager2
        adapterActions = AdapterActions()
        adapter = MessageAdapter(requireActivity(), adapterActions)
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
            if (message.messageType == 3){
                adapter.editMessage(message)
                return@observe
            }else if(message.messageType == 4){
                adapter.deleteMessage(message)
                return@observe
            }
            adapter.addMessage(message)
            binding.messagesRV.scrollToPosition(adapter.itemCount-1)

        }
    }

    private fun observeResponse(){
        viewModel.chatById.observe(viewLifecycleOwner) { response->
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
        viewModel.chatMessages.observe(viewLifecycleOwner) { response ->
            if (response.isSuccessful){
                val messages = response.body()
                adapter.submitList(messages!!)
                binding.messagesRV.scrollToPosition(adapter.itemCount-1)
            }else{
                Toast.makeText(context, response.errorBody().toString(), Toast.LENGTH_SHORT).show()
            }
        }
        viewModel.userChatsResponse.observe(viewLifecycleOwner) { response ->
            if (response.isSuccessful){
                adapter2.submitList(response.body())
            }else {
                Toast.makeText(context, response.errorBody().toString(), Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setOnClickListeners(){
        binding.sendMessage.setOnClickListener{ view: View->
            val content = binding.messageContent.text.toString()
            if (content.isNotEmpty()){
                webSocket.sendMessage(content)
            }
            binding.messageContent.text.clear()
        }
        binding.sendNewMessage.setOnClickListener { view: View->
            val content = binding.messageNewContent.text.toString()
            if (content.isNotEmpty()){
                webSocket.editMessage(content, actionMessageId)
            }else{
                webSocket.deleteMessage(actionMessageId)
            }
            binding.messageContent.text.clear()
            toSendMode()
        }
        binding.returnBtn.setOnClickListener {
            findNavController().navigate(R.id.action_detail_to_list)
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
        fun loadImage(url: String, imageView: ImageView){
            setImage(url, imageView)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        webSocket.disconnect()
    }

    fun setImage(url: String, imageView: ImageView){
        Glide.with(this).load("http://10.0.2.2:8000$url").into(imageView);
    }
}

