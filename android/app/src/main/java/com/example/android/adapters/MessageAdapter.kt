package com.example.android.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.android.R
import com.example.android.models.Message
import com.example.android.ui.ChatDetailFragment

class MessageAdapter(
    private val context: Context,
    private var adapterActions: ChatDetailFragment.AdapterActions,
    private var dataSet: ArrayList<Message> = ArrayList<Message>()
): RecyclerView.Adapter<MessageAdapter.ViewHolder>(){


    inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
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

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val message = dataSet[position]

        holder.initials.text = message.getInitials()
        holder.messageContent.text = message.content
        holder.messageTime.text = message.getTime(true)

        if (message.sender.profile!!.image != null){
            holder.initials.visibility = View.GONE
            holder.image.visibility = View.VISIBLE
            adapterActions.loadImage(message.sender.profile!!.image!!, holder.image)
        }else{
            holder.initials.visibility = View.VISIBLE
            holder.image.visibility = View.GONE
        }

        if (message.isSender){
            holder.messageSender.visibility = View.GONE
            holder.messageBackground.setBackgroundColor(ContextCompat.getColor(context, R.color.green_afa))
            holder.initials.setBackgroundResource(R.drawable.green_circle_textview)
            if (message.messageType != 2){
                holder.editButton.visibility = View.VISIBLE
            }
            holder.deleteButton.visibility = View.VISIBLE
        }else{
            holder.messageSender.visibility = View.VISIBLE
            holder.messageSender.text = message.sender.fullName()
            holder.messageBackground.setBackgroundColor(ContextCompat.getColor(context, R.color.white_e))
            holder.initials.setBackgroundResource(R.drawable.red_circle_textview)
            holder.editButton.visibility = View.GONE
            holder.deleteButton.visibility = View.GONE
        }

        if (message.messageType == 2){
            holder.messageOwner.text = message.getForwardedBannerText()
            holder.messageOwner.visibility = View.VISIBLE
        }else{
            holder.messageOwner.visibility = View.GONE
        }

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
        if (position == -1) return
        dataSet[position].content = message.content
        dataSet[position].messageType = 3
        notifyItemChanged(position)
    }
    fun deleteMessage(message: Message){
        val position = findByMessageId(message.id)
        if (position == -1) return
        dataSet.removeAt(position)
        notifyItemRemoved(position)
    }
    override fun getItemCount(): Int = dataSet.size

}