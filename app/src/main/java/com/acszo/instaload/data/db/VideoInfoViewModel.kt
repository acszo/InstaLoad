package com.acszo.instaload.data.db

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.acszo.instaload.data.repository.VideoInfoRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class VideoInfoViewModel(application: Application): AndroidViewModel(application) {

    val readAllData: LiveData<List<VideoInfo>>
    private val repository: VideoInfoRepository

    init {
        val videoInfoDAO = VideoInfoDATABASE.getDatabase(application).videoInfoDAO()
        repository = VideoInfoRepository(videoInfoDAO)
        readAllData = repository.readAllData
    }

    fun addVideoInfo(videoInfo: VideoInfo) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.addVideoInfo(videoInfo)
        }
    }

    fun deleteVideoInfo(videoInfo: VideoInfo) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteVideoInfo(videoInfo)
        }
    }

    fun deleteAll() {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteAll()
        }
    }
}