package com.example.android.api

import com.example.android.models.AuthToken
import com.example.android.models.Credentials
import com.example.android.models.RefreshToken
import com.example.android.models.UserForm
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthService {
    @POST("auth/")
    suspend fun login(@Body credentials: Credentials): Response<AuthToken>

    @POST("auth/refresh/")
    suspend fun refresh(@Body refreshToken: RefreshToken): Response<AuthToken>

    @POST("users/signup/")
    suspend fun signup(@Body userForm: UserForm): Response<Void>
}