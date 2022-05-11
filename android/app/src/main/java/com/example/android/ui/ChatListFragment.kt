package com.example.android.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.android.R
import com.example.android.adapters.ChatAdapter
import com.example.android.databinding.FragmentChatListBinding
import com.example.android.globals.AppPreferences
import com.example.android.models.Chat
import com.example.android.view_model.ChatViewModel
import com.example.android.view_model.DBViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class ChatListFragment : Fragment() {
    private lateinit var binding: FragmentChatListBinding
    private val vm: ChatViewModel by viewModel<ChatViewModel>()
    private val db: DBViewModel by viewModel<DBViewModel>()

    private lateinit var layoutManager: RecyclerView.LayoutManager
    private lateinit var adapter: ChatAdapter


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentChatListBinding.inflate(layoutInflater)
        layoutManager = LinearLayoutManager(activity)

        configureAdapter()

        vm.getUserChats()

        vm.userChatsResponse.observe(viewLifecycleOwner){ response->
            if (response.isSuccessful){
                val chats = response.body()
                adapter.submitList(chats)
            }else {
                Toast.makeText(context, response.errorBody().toString(), Toast.LENGTH_SHORT).show()
            }
        }
        binding.logoutBtn.setOnClickListener { view: View->
            AppPreferences.accessToken = null
            db.delete()
            findNavController().navigate(R.id.action_chat_to_login)
        }
        return binding.root
    }

    private fun configureAdapter() {
        binding.chatsRv.layoutManager = layoutManager
        adapter = ChatAdapter(){toChat(it)}
        binding.chatsRv.adapter = adapter
    }

    private fun toChat(chat: Chat) {
        val action = ChatListFragmentDirections.actionChatToDetail(chat.id)
        findNavController().navigate(action)
    }

}