package com.acszo.instaload.data

import android.app.DownloadManager
import android.content.Context
import android.net.Uri
import android.os.Environment

class DownloadVideoClass(val context: Context) {

    fun download(url: String, pathName: String): Long {
        val videoPath = if (pathName != "") {
            pathName
        } else "InstaLoad_${System.currentTimeMillis()}.mp4"

        val request = DownloadManager.Request(Uri.parse(url))
        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI or DownloadManager.Request.NETWORK_MOBILE)
        request.setTitle("InstaLoad")
        request.setDescription("Downloading...")

        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, videoPath)

        val manager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        return manager.enqueue(request)
    }

}