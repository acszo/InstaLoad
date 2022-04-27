package com.acszo.instaload.presentation

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.icu.text.SimpleDateFormat
import android.media.MediaPlayer
import android.net.ConnectivityManager
import android.net.Uri
import androidx.lifecycle.*
import com.acszo.instaload.R
import com.acszo.instaload.Resource
import com.acszo.instaload.data.DownloadVideoClass
import com.acszo.instaload.data.response.PostObject
import com.acszo.instaload.domain.DownloadActivityRepository
import java.util.*
import java.util.concurrent.TimeUnit

class SharedViewModel(application: Application): AndroidViewModel(application) {

    private var appContext: Application = application
    private val downloadActivityRepository: DownloadActivityRepository = DownloadActivityRepository()
    private val downloadVideoClass: DownloadVideoClass = DownloadVideoClass(appContext)

    fun getResponse(inputText: String): LiveData<Resource<PostObject>> {
        val response = MutableLiveData<Resource<PostObject>>()
        var instaURL = inputText

        if (inputText.length > 21) {
            instaURL = "${inputText.dropLast(21)}?__a=1"
        }

        return if(isConnected()) {
            downloadActivityRepository.getInstaPostJson(appContext, instaURL)
        } else {
            response.postValue(Resource.Error(appContext.resources.getString(R.string.notConnected)))
            response
        }
    }

    fun isConnected(): Boolean {
        val connectivityManager = appContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager?
        val activeNetworkInfo = connectivityManager!!.activeNetworkInfo
        return activeNetworkInfo != null && activeNetworkInfo.isConnected
    }

    fun downloadVideo(url: String, PathName: String): Long {
        return downloadVideoClass.download(url, PathName)
    }

    fun getDuration(url: String): String {
        var minutes: String
        var seconds: String
        MediaPlayer.create(appContext, Uri.parse(url)).also {
            val millis = it.duration.toLong()
            minutes = TimeUnit.MILLISECONDS.toMinutes(millis).toString()
            val convertSeconds = TimeUnit.MILLISECONDS.toSeconds(millis)
            seconds = if(convertSeconds < 10){ "0$convertSeconds" } else convertSeconds.toString()

            it.reset()
            it.release()
        }
        return "$minutes:$seconds"
    }

    @SuppressLint("SimpleDateFormat")
    fun getDate(): String {
        val sdf = SimpleDateFormat("dd/MM/yyyy")
        return sdf.format(Date())
    }

}