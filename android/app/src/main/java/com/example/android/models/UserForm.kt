package com.example.android.models

import com.google.gson.annotations.SerializedName

class UserForm (
    @SerializedName("user")
    var user: Credentials,
    @SerializedName("profile")
    var profile: Profile
)