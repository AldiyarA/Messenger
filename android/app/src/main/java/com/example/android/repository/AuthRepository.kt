package com.example.android.repository

import com.example.android.api.AuthService
import com.example.android.models.*
import retrofit2.Response

class AuthRepository(private val service: AuthService) {
    suspend fun signup(userForm: UserForm): Response<Void> {
        return service.signup(userForm=userForm)
    }

    suspend fun login(credentials: Credentials): Response<AuthToken> {
        return service.login(credentials=credentials)
    }

    suspend fun refresh(refreshToken: RefreshToken): Response<AuthToken>{
        return service.refresh(refreshToken)
    }
}