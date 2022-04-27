package com.acszo.instaload.data.repository

import androidx.lifecycle.LiveData
import com.acszo.instaload.data.db.VideoInfo
import com.acszo.instaload.data.db.VideoInfoDAO

class VideoInfoRepository(private val videoInfoDao: VideoInfoDAO) {

    val readAllData: LiveData<List<VideoInfo>> = videoInfoDao.readAllData()

    suspend fun addVideoInfo(videoInfo: VideoInfo) {
        videoInfoDao.addVideoInfo(videoInfo)
    }

    suspend fun deleteVideoInfo(videoInfo: VideoInfo) {
        videoInfoDao.deleteVideoInfo(videoInfo)
    }

    suspend fun deleteAll() {
        videoInfoDao.deleteAll()
    }
}