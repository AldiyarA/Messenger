package com.example.android.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.example.android.models.RefreshToken

@Dao
interface RefreshDao {
    @Query("SELECT * FROM RefreshToken")
    fun get(): List<RefreshToken>
    @Insert()
    fun insert(refresh: RefreshToken): Long
    @Query("DELETE FROM RefreshToken")
    fun delete()
}