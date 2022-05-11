package com.example.android.dao

import android.content.Context
import androidx.room.Room

object RefreshDB {
    fun create(context: Context): RefreshDao {
        return Room.databaseBuilder(context, AppDatabase::class.java, "refresh_db")
            .fallbackToDestructiveMigration()
            .allowMainThreadQueries()
            .build().refreshDao()
    }
}