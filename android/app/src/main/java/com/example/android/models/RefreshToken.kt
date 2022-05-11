package com.example.android.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class RefreshToken (
    @PrimaryKey(autoGenerate = true) var id: Long? = null,
    @ColumnInfo(name = "refresh") var refresh: String? = null
)