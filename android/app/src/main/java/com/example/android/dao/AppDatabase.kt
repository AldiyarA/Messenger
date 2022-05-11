package com.example.android.dao

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.android.models.RefreshToken

@Database(
    entities = [RefreshToken::class], version = 1, exportSchema = false
)

abstract class AppDatabase: RoomDatabase() {
    abstract fun refreshDao(): RefreshDao
}