package com.acszo.instaload.ui.recyclerView

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.acszo.instaload.R
import com.acszo.instaload.data.db.VideoInfo
import com.bumptech.glide.Glide

class ListAdapter: RecyclerView.Adapter<ListAdapter.MyViewHolder>(), Filterable {

    private var videoInfoList = emptyList<VideoInfo>()
    private var videoInfoListFull = emptyList<VideoInfo>()

    private lateinit var mListener: OnItemClickListener

    interface OnItemClickListener {
        fun downloadItemClickListener(position: Int)
        fun instagramItemClickListener(position: Int)
    }

    fun setOnItemClickListener(listener: OnItemClickListener) {
        mListener = listener
    }

    class MyViewHolder(itemView: View, listener: OnItemClickListener): RecyclerView.ViewHolder(itemView) {
        var itemPreview: ImageView = itemView.findViewById(R.id.preview)
        var itemPfp: ImageView = itemView.findViewById(R.id.pfp_placeholder)
        var itemUser: TextView = itemView.findViewById(R.id.username)
        private var itemDownload: ImageButton = itemView.findViewById(R.id.btn_download_element)
        private var itemInsta: ImageButton = itemView.findViewById(R.id.btn_instagram)
        var itemDuration: TextView = itemView.findViewById(R.id.duration)
        var itemDate: TextView = itemView.findViewById(R.id.date)

        init {
            itemDownload.setOnClickListener {
                listener.downloadItemClickListener(adapterPosition)
            }
            itemInsta.setOnClickListener {
                listener.instagramItemClickListener(adapterPosition)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.custom_row, parent, false), mListener)
    }

    override fun getItemCount(): Int {
        return videoInfoList.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val context = holder.itemView.context
        val currentItem = videoInfoList[position]
        holder.itemUser.text = currentItem.username
        Glide.with(holder.itemPreview).load(currentItem.preview).into(holder.itemPreview)
        Glide.with(holder.itemPfp).load(currentItem.pfp).into(holder.itemPfp)
        holder.itemDuration.text = context.getString(R.string.duration, currentItem.duration)
        holder.itemDate.text = context.getString(R.string.date, currentItem.date)
    }

    fun setData(newList: List<VideoInfo>) {
        val diffUtil = ListDiffUtil(videoInfoList, newList)
        val diffResult = DiffUtil.calculateDiff(diffUtil)
        videoInfoList = newList
        if (videoInfoListFull.isEmpty()) videoInfoListFull = videoInfoList
        diffResult.dispatchUpdatesTo(this)
    }

    fun getVideoInfo(position: Int): VideoInfo {
        return videoInfoList[position]
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            // background thread
            override fun performFiltering(p0: CharSequence?): FilterResults {
                val filteredList = ArrayList<VideoInfo>()
                if (p0.toString().isEmpty()){
                    filteredList.addAll(videoInfoListFull)
                } else {
                    videoInfoListFull.forEach {
                        if(it.username.lowercase().contains(p0.toString().lowercase()) || it.date.contains(p0.toString())) {
                            filteredList.add(it)
                        }
                    }
                }

                val filterResults = FilterResults()
                filterResults.values = filteredList
                return filterResults
            }
            // UI thread
            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                videoInfoList = results?.values as List<VideoInfo>
                notifyDataSetChanged()
            }
        }
    }

}