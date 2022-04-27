package com.acszo.instaload.data.db

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface VideoInfoDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addVideoInfo(videoInfo: VideoInfo)

    @Query("SELECT * FROM videoInfo_table ORDER BY id DESC")
    fun readAllData(): LiveData<List<VideoInfo>>

    @Delete
    suspend fun deleteVideoInfo(videoInfo: VideoInfo)

    @Query("DELETE FROM videoInfo_table")
    suspend fun deleteAll()
}