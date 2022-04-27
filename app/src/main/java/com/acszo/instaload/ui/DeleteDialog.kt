package com.acszo.instaload.ui

import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import com.acszo.instaload.R
import com.acszo.instaload.data.db.VideoInfoViewModel

class DeleteDialog: DialogFragment() {

    lateinit var videoInfoViewModel: VideoInfoViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.custom_dialog, container, false)
        val btnCancel: Button? = view?.findViewById(R.id.dialog_btn_cancel)
        val btnDelete: Button? = view?.findViewById(R.id.dialog_btn_delete)
        dialog!!.window?.setBackgroundDrawableResource(R.drawable.dialog_bg)
        dialog!!.window?.attributes?.windowAnimations = R.style.DialogAnimation
        btnCancel?.setOnClickListener {
            dialog!!.dismiss()
        }
        btnDelete?.setOnClickListener {
            videoInfoViewModel = ViewModelProvider(this, defaultViewModelProviderFactory).get(
                VideoInfoViewModel::class.java)
            videoInfoViewModel.deleteAll()
            dialog!!.dismiss()
        }
        return view
    }

    override fun onStart() {
        super.onStart()
        val width = (resources.displayMetrics.widthPixels * 0.95).toInt()
        val height = (resources.displayMetrics.heightPixels * 0.17).toInt()
        dialog!!.window?.setLayout(width, height)
        dialog!!.window?.setGravity(Gravity.BOTTOM)
    }

}