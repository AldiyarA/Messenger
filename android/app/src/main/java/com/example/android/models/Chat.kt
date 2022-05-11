package com.example.android.models

import com.google.gson.annotations.SerializedName

data class Chat (
    @SerializedName("id")
    val id: Int,
    @SerializedName("name")
    val name: String,
    @SerializedName("description")
    val description: String,
    @SerializedName("owner")
    val owner: Int,
    @SerializedName("message")
    val message: Message?
)