package com.example.android.models

data class Message(
    val id: Int,
    var content: String,
    val date: String,
    val creator: User,
    val sender: User,
    val chat: Int,
    var message_type: Int,
    val is_sender: Boolean
){
    fun getInitials(): String{
        if (sender.profile == null){
            return "U" + sender.id
        }
        var initials = ""

        if (sender.profile!!.first_name != ""){
            initials += sender.profile!!.first_name[0]
        }
        if (sender.profile!!.last_name != ""){
            initials += sender.profile!!.last_name[0]
        }
        if (initials == ""){
            return "U" + sender.id
        }
        return initials
    }
}
