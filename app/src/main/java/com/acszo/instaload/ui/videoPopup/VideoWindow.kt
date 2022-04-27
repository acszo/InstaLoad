package com.acszo.instaload.ui.videoPopup

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager2.widget.ViewPager2
import com.acszo.instaload.R
import com.acszo.instaload.data.db.VideoInfo
import com.acszo.instaload.data.db.VideoInfoViewModel
import com.acszo.instaload.presentation.SharedViewModel
import com.bumptech.glide.Glide
import kotlinx.coroutines.*

class VideoWindow(var postData: String, var previewData: ArrayList<String>, var videoData: ArrayList<String>, var usernameData: String, var pfpData: String): DialogFragment() {

    private lateinit var viewPager: ViewPager2
    private lateinit var videoAdapter: VideoAdapter
    private lateinit var pfpView: ImageView
    private lateinit var usernameView: TextView
    lateinit var downloadElement: Button
    lateinit var videoInfoViewModel: VideoInfoViewModel
    lateinit var sharedViewModel: SharedViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.custom_popup, container, false)
        dialog!!.window?.setBackgroundDrawableResource(R.drawable.popup_bg)
        dialog!!.window?.attributes?.windowAnimations = R.style.PopupAnimation

        sharedViewModel = ViewModelProvider(this, defaultViewModelProviderFactory)[SharedViewModel::class.java]
        videoInfoViewModel = ViewModelProvider(this, defaultViewModelProviderFactory)[VideoInfoViewModel::class.java]
        downloadElement = view.findViewById(R.id.btn_download_video)
        pfpView = view.findViewById(R.id.popup_pfp_placeholder)
        usernameView = view.findViewById(R.id.popup_username)

        Glide.with(pfpView).load(pfpData).into(pfpView)
        usernameView.text = usernameData

        viewPager = view.findViewById(R.id.viewpager)
        videoAdapter = VideoAdapter(videoData)
        viewPager.adapter = videoAdapter

        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                val width = (resources.displayMetrics.widthPixels * 0.95).toInt()
                dialog!!.window?.setLayout(width, ViewGroup.LayoutParams.WRAP_CONTENT)
                val selectedVideo = videoData[position]
                val selectedVideoPreview = previewData[position]
                downloadElement.setOnClickListener {
                    val pathName = "InstaLoad_${System.currentTimeMillis()}.mp4"
                    CoroutineScope(Dispatchers.IO).launch {
                        sharedViewModel.downloadVideo(selectedVideo, pathName)
                    }
                    val durationVideo = sharedViewModel.getDuration(videoData[position])
                    val date = sharedViewModel.getDate()
                    val videoInfo = VideoInfo(0, postData, selectedVideoPreview, selectedVideo, pathName, durationVideo, date, usernameData, pfpData)
                    videoInfoViewModel.addVideoInfo(videoInfo)
                }
                super.onPageSelected(position)
            }
        })

        return view
    }

    override fun onStart() {
        super.onStart()
        val width = (resources.displayMetrics.widthPixels * 0.95).toInt()
        val height = resources.displayMetrics.widthPixels
        dialog!!.window?.setLayout(width, height)
    }
}