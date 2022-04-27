package com.acszo.instaload.ui

import android.animation.Animator
import android.animation.AnimatorInflater
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.view.forEach
import androidx.lifecycle.ViewModelProvider
import com.acszo.instaload.data.DownloadVideoClass
import com.acszo.instaload.R
import com.acszo.instaload.Resource
import com.acszo.instaload.data.db.VideoInfo
import com.acszo.instaload.data.db.VideoInfoViewModel
import com.acszo.instaload.presentation.SharedViewModel
import com.acszo.instaload.ui.videoPopup.VideoWindow
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.*

class DownloadActivity : AppCompatActivity() {

    lateinit var editText: EditText
    private lateinit var storyRing: ImageView
    private val storagePermissionCode: Int = 1000
    private lateinit var fadeIn: Animation
    private lateinit var bottomNavigationView: BottomNavigationView
    private lateinit var videoInfoViewModel: VideoInfoViewModel
    private lateinit var downloadClass: DownloadVideoClass
    private lateinit var pfpImage: ImageView
    private lateinit var usernameTextView: TextView
    private lateinit var sharedViewModel: SharedViewModel
    private lateinit var animator: Animator

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        storyRing = findViewById(R.id.storie_ring)
        storyRing.visibility = View.INVISIBLE
        downloadClass = DownloadVideoClass(this)
        val btnDownload: Button = findViewById(R.id.btn_download)
        val clear: Button = findViewById(R.id.btn_cancel)
        clear.visibility = View.INVISIBLE
        pfpImage = findViewById(R.id.pfp_placeholder)
        usernameTextView = findViewById(R.id.username_placeholder)
        sharedViewModel = ViewModelProvider(this, defaultViewModelProviderFactory)[SharedViewModel::class.java]

        fadeIn = AnimationUtils.loadAnimation(this, R.anim.fade_in)
        animator = AnimatorInflater.loadAnimator(this, R.animator.rotation_loop)
        animator.setTarget(storyRing)

        editText = findViewById(R.id.insta_URL)
        editText.addTextChangedListener(object: TextWatcher {
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if(editText.text.isNotEmpty()){
                    clear.visibility = View.VISIBLE
                } else clear.visibility = View.INVISIBLE
            }
            override fun afterTextChanged(s: Editable?) {}
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
        })

        bottomNavigationView = findViewById(R.id.navigation_bar)
        bottomNavigationView.selectedItemId = R.id.home_nav
        bottomNavigationView.itemRippleColor = ColorStateList.valueOf(Color.parseColor("#00FFFFFF"))
        val homeMenu = bottomNavigationView.menu.findItem(R.id.home_nav)
        homeMenu.setIcon(R.drawable.home_logo_filled)
        bottomNavigationView.setOnItemSelectedListener { item ->
            if(item.itemId == R.id.history_nav) {
                val intent = Intent(this, HistoryActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
                startActivity(intent)
                onNewIntent(intent)
            }
            true
        }

        //hide tooltip
        bottomNavigationView.menu.forEach {
            val view = bottomNavigationView.findViewById<View>(it.itemId)
            view.setOnLongClickListener {
                true
            }
        }

        hasWriteExternalStoragePermission()

        btnDownload.setOnClickListener {
            val inputText: String = editText.text.toString()
            //CoroutineScope(Dispatchers.IO).launch {
                getViewModelData(inputText)
            //}
            it.hideKeyboard()
        }

        clear.setOnClickListener {
            editText.text.clear()
            usernameTextView.text = null
            pfpImage.setImageDrawable(null)
            storyRing.visibility = View.INVISIBLE
            animator.end()
        }

    }

    override fun finish() {
        super.finish()
        overridePendingTransition(0, 0)
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        overridePendingTransition(0, 0)
    }

    override fun onPause() {
        super.onPause()
        animator.pause()
    }

    override fun onResume() {
        super.onResume()
        animator.resume()
    }

    private fun getViewModelData(inputText: String) {
        sharedViewModel.getResponse(inputText).observe(this) {
            val pfp = it.data?.graphql?.shortcode_media?.owner?.profile_pic_url
            val username = it.data?.graphql?.shortcode_media?.owner?.username
            val preview = it.data?.graphql?.shortcode_media?.thumbnail_src
            val videoUrl = it.data?.graphql?.shortcode_media?.video_url

            when (it) {
                is Resource.Success -> {
                    if(it.data?.graphql?.shortcode_media?.edge_sidecar_to_children?.edges == null) {
                        Glide.with(this).load(pfp)
                            .listener(object : RequestListener<Drawable> {
                                override fun onLoadFailed(p0: GlideException?, p1: Any?, p2: Target<Drawable>?, p3: Boolean): Boolean {
                                    return false
                                }
                                override fun onResourceReady(p0: Drawable?, p1: Any?, p2: Target<Drawable>?, p3: DataSource?, p4: Boolean): Boolean {
                                    storyRing.startAnimation(fadeIn)
                                    storyRing.visibility = View.VISIBLE
                                    animator.start()
                                    usernameTextView.text = username
                                    usernameTextView.startAnimation(fadeIn)
                                    return false
                                }
                            }).into(pfpImage)
                        pfpImage.startAnimation(fadeIn)
                        val videoName = "InstaLoad_${System.currentTimeMillis()}.mp4"
                        if (preview != null && videoUrl != null && username != null && pfp != null) {
                            insertData(inputText, preview, videoUrl, videoName, username, pfp)
                            CoroutineScope(Dispatchers.IO).launch {
                                sharedViewModel.downloadVideo(videoUrl, videoName)
                            }
                        }
                    } else {
                        val size: Int = it.data.graphql.shortcode_media.edge_sidecar_to_children.edges.size
                        val listVideo = ArrayList<String>()
                        val listPreview = ArrayList<String>()
                        for(i in 0 until size) {
                            listPreview.add(it.data.graphql.shortcode_media.edge_sidecar_to_children.edges[i].node.display_url)
                            listVideo.add(it.data.graphql.shortcode_media.edge_sidecar_to_children.edges[i].node.video_url)
                        }
                        VideoWindow(inputText, listPreview, listVideo, it.data.graphql.shortcode_media.owner.username, it.data.graphql.shortcode_media.owner.profile_pic_url)
                            .show(supportFragmentManager, "videoWindow")
                    }
                }
                is Resource.Error -> {
                    if (inputText.trim().isEmpty()) {
                        Toast.makeText(this, R.string.emptyField, Toast.LENGTH_SHORT).show()
                    } else Toast.makeText(this, it.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun insertData(postUrl: String, parsedPreview: String, parsedVideo: String, videoName: String, parsedUsername: String, parsedPFP: String) {
        videoInfoViewModel = ViewModelProvider(this, defaultViewModelProviderFactory)[VideoInfoViewModel::class.java]
        val duration = sharedViewModel.getDuration(parsedVideo)
        val date = sharedViewModel.getDate()
        val videoInfo = VideoInfo(0, postUrl, parsedPreview, parsedVideo, videoName, duration, date, parsedUsername, parsedPFP)
        videoInfoViewModel.addVideoInfo(videoInfo)
    }

    private fun View.hideKeyboard() {
        val inputManager = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputManager.hideSoftInputFromWindow(windowToken, 0)
    }

    private fun hasWriteExternalStoragePermission() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED){
            requestPermissions(arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE), storagePermissionCode)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

}