package com.example.android.api

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object AuthClient {
    fun create(): AuthService{
        val okHttpClient = getOkHttpClient()
        val retrofit = Retrofit.Builder()
            .baseUrl(url)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        return retrofit.create(AuthService::class.java)
    }

    private fun getOkHttpClient(): OkHttpClient {
        return OkHttpClient.Builder().build()
    }
}