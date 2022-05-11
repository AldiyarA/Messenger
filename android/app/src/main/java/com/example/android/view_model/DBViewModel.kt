package com.example.android.view_model

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.android.dao.RefreshDao
import com.example.android.models.RefreshToken
import kotlinx.coroutines.launch

class DBViewModel(private val refreshDao: RefreshDao) : ViewModel(){
    val refreshToken: MutableLiveData<List<RefreshToken>> = MutableLiveData()

    fun get(){
        Log.e("DBViewModel", "GET refresh")
        viewModelScope.launch {
            refreshToken.value = refreshDao.get()
        }
    }

    fun insert(refresh: String){
        refreshDao.insert(RefreshToken(refresh = refresh))
    }

    fun delete(){
        refreshDao.delete()
    }

}