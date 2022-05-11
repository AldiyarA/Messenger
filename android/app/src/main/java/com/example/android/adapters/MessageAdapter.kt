package com.example.android.adapters

import android.annotation.SuppressLint
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.android.R
import com.example.android.models.Message
import com.example.android.ui.ChatDetailFragment

class MessageAdapter(
    private var adapterActions: ChatDetailFragment.AdapterActions,
    private var dataSet: ArrayList<Message> = ArrayList<Message>()
): RecyclerView.Adapter<MessageAdapter.ViewHolder>(){


    inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
//        var chatName: TextView = itemView.findViewById(R.id.chat_name)
        var initials: TextView = itemView.findViewById(R.id.initials)
        var image: ImageView = itemView.findViewById(R.id.initialsImage)

        var messageBackground: ConstraintLayout = itemView.findViewById(R.id.message_background)
        var messageSender: TextView = itemView.findViewById(R.id.sender)
        var messageOwner: TextView = itemView.findViewById(R.id.owner)
        var messageContent: TextView = itemView.findViewById(R.id.content)
        var messageTime: TextView = itemView.findViewById(R.id.time)

        var resendButton: ImageView = itemView.findViewById(R.id.resend_button)
        var editButton: ImageView = itemView.findViewById(R.id.edit_button)
        var deleteButton: ImageView = itemView.findViewById(R.id.delete_button)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.message_layout, parent, false)
        return ViewHolder(v)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val message = dataSet[position]

        holder.initials.text = message.getInitials()
        holder.messageContent.text = message.content

        if (message.sender.profile!!.image != null){
            holder.initials.visibility = View.GONE
            holder.image.visibility = View.VISIBLE
            adapterActions.loadImage(message.sender.profile!!.image!!, holder.image)
        }else{
            holder.initials.visibility = View.VISIBLE
            holder.image.visibility = View.GONE
        }

        if (message.is_sender){
            Log.e("Message", "$position, ${message.is_sender}")
            holder.messageSender.visibility = View.GONE
            holder.messageBackground.setBackgroundColor(Color.parseColor("#aaffaa"))
            holder.initials.setBackgroundResource(R.drawable.green_circle_textview)
            if (message.message_type != 2){
                holder.editButton.visibility = View.VISIBLE
            }
            holder.deleteButton.visibility = View.VISIBLE
        }else{
            holder.messageSender.visibility = View.VISIBLE
            holder.messageSender.text = message.sender.fullName()
            holder.messageBackground.setBackgroundColor(Color.parseColor("#eeeeee"))
            holder.initials.setBackgroundResource(R.drawable.red_circle_textview)

            holder.editButton.visibility = View.GONE
            holder.deleteButton.visibility = View.GONE
        }
        var time = ""
        if (message.message_type == 2){
            holder.messageOwner.text = "Forwarded from ${message.creator.name()}"
            holder.messageOwner.visibility = View.VISIBLE
        }else{
            holder.messageOwner.visibility = View.GONE
            if(message.message_type == 3){
                time += "edited  "
            }
        }
        time += message.date.subSequence(11, 16)
        holder.messageTime.text = time

        holder.editButton.setOnClickListener { adapterActions.editMessage(message) }
        holder.deleteButton.setOnClickListener { adapterActions.deleteMessage(message.id) }
        holder.resendButton.setOnClickListener { adapterActions.resendMessage(message.id) }
    }

    fun submitList(newDataSet: List<Message>){
        dataSet.clear()
        dataSet.addAll(newDataSet)
    }

    fun addMessage(message: Message){
        dataSet.add(message)
        notifyItemInserted(itemCount-1)
    }

    private fun findByMessageId(id: Int): Int{
        for (i in 0..dataSet.size){
            if (dataSet[i].id == id) return i
        }
        return -1
    }

    fun editMessage(message: Message) {
        val position = findByMessageId(message.id)
        dataSet[position].content = message.content
        dataSet[position].message_type = 3
        notifyItemChanged(position)
    }
    fun deleteMessage(message: Message){
        val pos = findByMessageId(message.id)
        dataSet.removeAt(pos)
        notifyItemRemoved(pos)
    }
    override fun getItemCount(): Int = dataSet.size

}