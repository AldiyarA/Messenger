package com.example.android.models

import com.google.gson.annotations.SerializedName

class AuthToken(
    @SerializedName("access")
    var access: String,
    @SerializedName("refresh")
    var refresh: String? = null,
)