package com.oneSaver.allStatus.extraUtils.firebaseMySaveUpdates


import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName = "app_updates")
data class AppUpdates (
    @PrimaryKey
    @ColumnInfo(defaultValue = "100")
    val id: Int,

    @SerializedName("new_app_name")
    val name: String,

    @SerializedName("new_app_url")
    val url: String,

    @SerializedName("new_app_version")
    val version: String,

    @SerializedName("new_app_updates")
    val updates: String
)

