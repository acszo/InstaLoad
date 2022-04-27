package com.acszo.instaload.ui.videoPopup

import android.media.MediaPlayer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.VideoView
import androidx.recyclerview.widget.RecyclerView
import com.acszo.instaload.R

class VideoAdapter(arrayVideo: ArrayList<String>) : RecyclerView.Adapter<VideoAdapter.VideoViewHolder>() {

    private var listVideo : ArrayList<String> = arrayVideo

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VideoViewHolder {
        return VideoViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.videoplayer, parent, false))
    }

    override fun onBindViewHolder(holder: VideoViewHolder, position: Int) {
        holder.setVideoData(listVideo[position])
    }

    override fun getItemCount(): Int {
        return listVideo.size
    }

    class VideoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var videoView: VideoView = itemView.findViewById(R.id.videoview)

        fun setVideoData(videoURL: String) {
            videoView.setVideoPath(videoURL)
            videoView.setOnPreparedListener { mp ->
                mp.start()
                mp.isLooping = true
            }
            videoView.setOnCompletionListener { MediaPlayer.OnCompletionListener { mp -> mp.start() } }
        }
    }

}