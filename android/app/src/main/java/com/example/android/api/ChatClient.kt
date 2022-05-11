package com.example.android.api

import android.content.SharedPreferences
import com.example.android.interceptors.AuthInterceptor
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ChatClient {
    fun create(): ChatService {
        val okHttpClient = getOkHttpClient()
        val retrofit = Retrofit.Builder()
            .baseUrl(url+"core/")
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        return retrofit.create(ChatService::class.java)
    }

    private fun getOkHttpClient(): OkHttpClient {
        val authInterceptor = AuthInterceptor()
        return OkHttpClient.Builder()
            .addInterceptor(authInterceptor)
            .build()
    }
}