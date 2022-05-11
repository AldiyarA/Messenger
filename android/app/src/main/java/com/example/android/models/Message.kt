package com.example.android.models

import android.util.Log
import com.google.gson.annotations.SerializedName
import java.text.SimpleDateFormat
import java.util.*

data class Message(
    @SerializedName("id")
    val id: Int,
    @SerializedName("content")
    var content: String,
    @SerializedName("date")
    val date: Date,
    @SerializedName("creator")
    val creator: User,
    @SerializedName("sender")
    val sender: User,
    @SerializedName("chat")
    val chat: Int,
    @SerializedName("message_type")
    var messageType: Int,
    @SerializedName("is_sender")
    val isSender: Boolean
){
    fun getInitials(): String{
        if (sender.profile == null){
            return "U${sender.id}"
        }
        var initials = ""
        if (sender.profile!!.firstName.isNotEmpty()){
            initials += sender.profile!!.firstName[0]
        }
        if (sender.profile!!.lastName.isNotEmpty()){
            initials += sender.profile!!.lastName[0]
        }
        if (initials.isEmpty()){
            return "U${sender.id}"
        }
        return initials
    }

    fun getOwnerContent(): String{
        if (sender.profile == null) return content
        return "${sender.profile!!.firstName}: $content"
    }
    fun getTime(isEdit: Boolean = false): String{
        var time = ""
        if (isEdit && messageType == 3){
            time += "edited "
        }
        return time + SimpleDateFormat("HH:mm", Locale.ROOT).format(date)
    }
    fun getForwardedBannerText(): String{
        return "Forwarded from ${creator.name()}"
    }
}
