package com.example.android.di

import com.example.android.api.AuthClient
import com.example.android.api.ChatClient
import com.example.android.dao.RefreshDB
import com.example.android.repository.AuthRepository
import com.example.android.repository.ChatRepository
import com.example.android.view_model.AuthViewModel
import com.example.android.view_model.ChatViewModel
import com.example.android.view_model.DBViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {
    viewModel { AuthViewModel(get()) }
    viewModel { ChatViewModel(get()) }
    viewModel { DBViewModel(get()) }
}

val repositoryModule = module {
    single { AuthRepository(get()) }
    single { ChatRepository(get()) }
}

val networkModule = module {
    single { AuthClient.create() }
    single { ChatClient.create() }
}

val databaseModule = module {
    single { RefreshDB.create(get()) }
}