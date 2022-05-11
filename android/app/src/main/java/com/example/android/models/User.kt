package com.example.android.models

import com.google.gson.annotations.SerializedName

data class User(
    @SerializedName("id")
    var id: Int,
    @SerializedName("profile")
    var profile: Profile?
){
    fun fullName(): String{
        if (profile == null || profile!!.firstName + profile!!.lastName == ""){
            return "User $id"
        }
        return profile!!.firstName + " " + profile!!.lastName
    }
    fun name(): String{
        if (profile == null || profile!!.firstName == ""){
            return "User $id"
        }
        return profile!!.firstName
    }
}
