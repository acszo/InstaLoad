package com.acszo.instaload.ui.recyclerView

import androidx.recyclerview.widget.DiffUtil
import com.acszo.instaload.data.db.VideoInfo

class ListDiffUtil(
    private val oldList: List<VideoInfo>,
    private val newList: List<VideoInfo>
): DiffUtil.Callback() {

    override fun getOldListSize(): Int {
        return oldList.size
    }

    override fun getNewListSize(): Int {
        return newList.size
    }

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition].id == newList[newItemPosition].id
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return when {
            oldList[oldItemPosition].id != newList[newItemPosition].id -> false
            oldList[oldItemPosition].url != newList[newItemPosition].url -> false
            else -> true
        }
    }
}