package com.example.android.view_model

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.android.models.AuthToken
import com.example.android.models.Credentials
import com.example.android.models.UserForm
import com.example.android.repository.AuthRepository
import kotlinx.coroutines.launch
import retrofit2.Response

class AuthViewModel(private val repository: AuthRepository): ViewModel() {
    val loginResponse: MutableLiveData<Response<AuthToken>> = MutableLiveData()
    val signupResponse: MutableLiveData<Response<Void>> = MutableLiveData()

    fun login(credentials: Credentials) {
        viewModelScope.launch {
            val response = repository.login(credentials=credentials)
            loginResponse.value = response
        }
    }

    fun signup(userForm: UserForm) {
        viewModelScope.launch {
            val response = repository.signup(userForm=userForm)
            signupResponse.value = response
        }
    }
}