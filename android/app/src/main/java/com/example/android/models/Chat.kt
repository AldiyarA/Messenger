package com.example.android.models

data class Chat (
    val id: Int,
    val name: String,
    val description: String,
    val owner: Int,
    val message: Message?
)