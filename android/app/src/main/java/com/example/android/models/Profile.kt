package com.example.android.models

data class Profile(
    var first_name: String,
    var last_name: String,
    var bio: String? = null,
    var phone: String? = null,
    var user: Int? = null,
    var image: String? = null
)
