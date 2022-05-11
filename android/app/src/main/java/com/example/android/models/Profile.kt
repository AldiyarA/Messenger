package com.example.android.models

import com.google.gson.annotations.SerializedName


data class Profile(
    @SerializedName("first_name")
    var firstName: String,
    @SerializedName("last_name")
    var lastName: String,
    @SerializedName("bio")
    var bio: String? = null,
    @SerializedName("phone")
    var phone: String? = null,
    @SerializedName("user")
    var user: Int? = null,
    @SerializedName("image")
    var image: String? = null
)
