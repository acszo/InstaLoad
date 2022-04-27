package com.acszo.instaload.data.db

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.*

@Entity(tableName = "videoInfo_table", indices = [Index(value = ["url"], unique = true)])
data class VideoInfo(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val post: String,
    val preview: String,
    val url: String,
    val fileName: String,
    val duration: String,
    val date: String,
    val username: String,
    val pfp: String
)