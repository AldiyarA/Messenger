package com.example.android.models

data class User(
    var id: Int,
    var profile: Profile?
){
    fun fullName(): String{
        if (profile == null || profile!!.first_name + profile!!.last_name == ""){
            return "User $id"
        }
        return profile!!.first_name + " " + profile!!.last_name
    }
    fun name(): String{
        if (profile == null || profile!!.first_name == ""){
            return "User $id"
        }
        return profile!!.first_name
    }
}
