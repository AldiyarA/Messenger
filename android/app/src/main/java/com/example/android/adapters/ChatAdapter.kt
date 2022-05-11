package com.example.android.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.android.R
import com.example.android.models.Chat

class ChatAdapter(
    private val clickHandler: (Chat) -> Unit
): ListAdapter<Chat, ChatAdapter.ViewHolder>(DIFF_CONFIG){

    companion object {
        val DIFF_CONFIG = object : DiffUtil.ItemCallback<Chat>() {
            override fun areContentsTheSame(
                oldItem: Chat,
                newItem: Chat
            ): Boolean {
                return oldItem == newItem
            }

            override fun areItemsTheSame(oldItem: Chat, newItem: Chat): Boolean {
                return oldItem === newItem
            }
        }
    }

    inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        var chatName: TextView = itemView.findViewById(R.id.chat_name)
        var message: TextView = itemView.findViewById(R.id.chat_message)
        var time: TextView = itemView.findViewById(R.id.message_time)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatAdapter.ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.chat_layout, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ChatAdapter.ViewHolder, position: Int) {
        val chat = getItem(position)
        holder.chatName.text = chat.name
        if (chat.message != null){
            val message = chat.message
            holder.message.text = message.getOwnerContent()
            holder.time.text = message.getTime()
        }
        holder.itemView.setOnClickListener {
            clickHandler(chat)
        }
    }


}