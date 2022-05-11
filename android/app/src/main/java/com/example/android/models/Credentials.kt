package com.example.android.models

import com.google.gson.annotations.SerializedName

class Credentials (
    @SerializedName("email")
    var email: String,
    @SerializedName("password")
    var password: String
)