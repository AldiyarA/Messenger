package com.example.android.app

import android.app.Application
import com.example.android.di.databaseModule
import com.example.android.di.networkModule
import com.example.android.di.repositoryModule
import com.example.android.di.viewModelModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class App: Application() {

    override fun onCreate(){
        super.onCreate()

        startKoin {
            androidContext(this@App)
            modules(listOf(viewModelModule, repositoryModule, networkModule, databaseModule))
        }
    }
}