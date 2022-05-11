package com.example.android.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity
data class RefreshToken (
    @PrimaryKey(autoGenerate = true)
    @SerializedName("id")
    var id: Long? = null,
    @ColumnInfo(name = "refresh")
    @SerializedName("refresh")
    var refresh: String? = null
)