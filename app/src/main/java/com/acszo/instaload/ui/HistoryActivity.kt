package com.acszo.instaload.ui

import android.app.DatePickerDialog
import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.res.ColorStateList
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.os.StrictMode
import android.os.StrictMode.VmPolicy
import android.util.DisplayMetrics
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.view.forEach
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSmoothScroller
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.AdapterDataObserver
import com.acszo.instaload.R
import com.acszo.instaload.Resource
import com.acszo.instaload.data.db.VideoInfoViewModel
import com.acszo.instaload.presentation.SharedViewModel
import com.acszo.instaload.ui.recyclerView.ListAdapter
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.snackbar.BaseTransientBottomBar.ANIMATION_MODE_SLIDE
import com.google.android.material.snackbar.Snackbar
import java.io.File
import java.util.*
import kotlin.math.abs

class HistoryActivity : AppCompatActivity() {

    lateinit var bottomNavigationView: BottomNavigationView
    lateinit var videoInfoViewModel: VideoInfoViewModel
    private lateinit var imgHistory: ImageView
    private lateinit var txtHistory: TextView
    private lateinit var viewArea: AppBarLayout
    private lateinit var toolbar: androidx.constraintlayout.widget.ConstraintLayout
    private lateinit var txtViewArea: TextView
    private lateinit var btnDeleteAll: ImageButton
    private lateinit var sharedViewModel: SharedViewModel
    private lateinit var searchView: SearchView
    private lateinit var btnSearch: ImageButton
    private lateinit var adapter: ListAdapter
    private lateinit var btnDatePicker: ImageButton
    private lateinit var btnCloseDate: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_history)

        viewArea = findViewById(R.id.app_bar_layout)
        toolbar = findViewById(R.id.include)
        txtViewArea = findViewById(R.id.tv_toolbar_top_title)
        imgHistory = findViewById(R.id.img_noHistory)
        txtHistory = findViewById(R.id.txt_noHistory)
        btnDeleteAll = findViewById(R.id.btn_delete_all)
        searchView = findViewById(R.id.search_view)
        btnSearch = findViewById(R.id.btn_search)
        btnDatePicker = findViewById(R.id.btn_date_picker)
        btnCloseDate = findViewById(R.id.btn_delete_date)

        imgHistory.visibility = View.INVISIBLE
        txtHistory.visibility = View.INVISIBLE
        btnCloseDate.visibility = View.INVISIBLE

        sharedViewModel = ViewModelProvider(this, defaultViewModelProviderFactory)[SharedViewModel::class.java]

        bottomNavigationView = findViewById(R.id.navigation_bar)
        bottomNavigationView.selectedItemId = R.id.history_nav
        bottomNavigationView.itemRippleColor = ColorStateList.valueOf(Color.parseColor("#00FFFFFF"))
        val homeMenu = bottomNavigationView.menu.findItem(R.id.home_nav)
        homeMenu.setIcon(R.drawable.home_logo)
        bottomNavigationView.setOnItemSelectedListener { item ->
            if(item.itemId == R.id.home_nav) {
                val intent = Intent(this, DownloadActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
                startActivity(intent)
                onNewIntent(intent)
            }
            true
        }

        val builder = VmPolicy.Builder()
        StrictMode.setVmPolicy(builder.build())

        //hide tooltip
        bottomNavigationView.menu.forEach {
            val view = bottomNavigationView.findViewById<View>(it.itemId)
            view.setOnLongClickListener {
                true
            }
        }

        btnSearch.setOnClickListener {
            adapter.filter.filter("")
            btnCloseDate.visibility = View.INVISIBLE
            btnDatePicker.visibility = View.VISIBLE
            searchView.visibility = View.VISIBLE
            btnSearch.visibility = View.INVISIBLE
            toolbar.visibility = View.INVISIBLE
            searchView.isIconified = false
        }

        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)
        val currentDate = calendar.timeInMillis

        btnDatePicker.setOnClickListener {
            searchView.setQuery("", false)
            searchView.clearFocus()
            val datePicker = DatePickerDialog(this, { _, year, month, day ->
                var fullMonth = "${month + 1}"
                var fullDay = "$day"
                if (month + 1 < 10) {
                    fullMonth = "0$fullMonth"
                }
                if (day < 10) {
                    fullDay = "0$fullDay"
                }
                val date = "$fullDay/$fullMonth/$year"
                adapter.filter.filter(date)
                viewArea.setExpanded(false)
                btnDatePicker.visibility = View.INVISIBLE
                btnCloseDate.text = "$fullDay/$fullMonth"
                btnCloseDate.visibility = View.VISIBLE
            }, year, month, day)

            calendar.set(Calendar.YEAR, 2022)
            calendar.set(Calendar.MONTH, 0)
            calendar.set(Calendar.DAY_OF_MONTH, 1)
            datePicker.datePicker.minDate = calendar.timeInMillis
            datePicker.datePicker.maxDate = currentDate
            datePicker.show()
        }

        btnCloseDate.setOnClickListener {
            adapter.filter.filter("")
            btnCloseDate.visibility = View.INVISIBLE
            btnDatePicker.visibility = View.VISIBLE
        }

        btnDeleteAll.setOnClickListener {
            DeleteDialog().show(supportFragmentManager, "MyCustomFragment")
        }

        adapter = ListAdapter()
        val recyclerView: RecyclerView = findViewById(R.id.recycleview)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener,
            android.widget.SearchView.OnQueryTextListener {

            override fun onQueryTextChange(qString: String): Boolean {
                viewArea.setExpanded(false)
                toolbar.visibility = View.INVISIBLE
                adapter.filter.filter(qString)
                return true
            }

            override fun onQueryTextSubmit(qString: String): Boolean {
                return true
            }
        })

        adapter.registerAdapterDataObserver(object : AdapterDataObserver() {
            override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
                super.onItemRangeInserted(positionStart, itemCount)
                if(positionStart == 0) scrollAt(recyclerView, 0)
            }

            override fun onChanged() {
                super.onChanged()
                isEmptyRecyclerView()
            }
        })

        adapter.setOnItemClickListener(object: ListAdapter.OnItemClickListener {
            override fun downloadItemClickListener(position: Int) {
                val postUrl = adapter.getVideoInfo(position).post
                downloadItem(postUrl)
            }

            override fun instagramItemClickListener(position: Int) {
                try {
                    val uri: Uri = Uri.parse(adapter.getVideoInfo(position).post)
                    val intent = Intent(Intent.ACTION_VIEW, uri)
                    intent.setPackage("com.instagram.android")
                          .flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    applicationContext.startActivity(intent)
                } catch (e: Exception) {
                    Toast.makeText(applicationContext, R.string.notDownloaded, Toast.LENGTH_SHORT).show()
                }
            }
        })

        videoInfoViewModel = ViewModelProvider(this)[VideoInfoViewModel::class.java]
        videoInfoViewModel.readAllData.observe(this) { videoInfo ->
            adapter.setData(videoInfo)
            isEmptyRecyclerView()
        }

        deleteGesture(adapter, recyclerView)
        viewAreaScroll()
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(0, 0)
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        overridePendingTransition(0, 0)
    }

    private fun isEmptyRecyclerView() {
        if (adapter.itemCount == 0) {
            viewArea.setExpanded(false)
            imgHistory.visibility = View.VISIBLE
            txtHistory.visibility = View.VISIBLE
            btnDeleteAll.isEnabled = false
            btnDatePicker.isEnabled = false
        } else {
            imgHistory.visibility = View.INVISIBLE
            txtHistory.visibility = View.INVISIBLE
            btnDeleteAll.isEnabled = true
            btnDatePicker.isEnabled = true
        }
    }

    private fun deleteGesture(adapter: ListAdapter, recyclerView: RecyclerView) {
        val deleteGesture = object : SwipeGesture(this) {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                when(direction) {
                    ItemTouchHelper.LEFT -> {
                        hideKeyboard()
                        val temp = adapter.getVideoInfo(viewHolder.adapterPosition)
                        val position = viewHolder.adapterPosition
                        videoInfoViewModel.deleteVideoInfo(adapter.getVideoInfo(viewHolder.adapterPosition))
                        val snackBar = Snackbar.make(findViewById(R.id.navigation_bar),
                            R.string.deleted, Snackbar.LENGTH_LONG)
                            .setAction(R.string.undo) {
                                videoInfoViewModel.addVideoInfo(temp)
                                if(position == adapter.itemCount) {
                                    scrollAt(recyclerView, position)
                                }
                            }
                            .setBackgroundTint(getColor(R.color.blue))
                            .setTextColor(getColor(R.color.white))
                            .setActionTextColor(getColor(R.color.white))
                            .setAnchorView(bottomNavigationView)
                            .setAnimationMode(ANIMATION_MODE_SLIDE)
                        val layout: Snackbar.SnackbarLayout = snackBar.view as Snackbar.SnackbarLayout
                        layout.elevation = 0F
                        snackBar.show()
                    }

                    ItemTouchHelper.RIGHT -> {
                        val videoPath = adapter.getVideoInfo(viewHolder.adapterPosition).fileName
                        val string = "sdcard/Download/$videoPath"
                        val file = File(string)
                        if (file.exists()) shareVideo(file)
                        else {
                            val videoUrlShare = adapter.getVideoInfo(viewHolder.adapterPosition).url
                            //val postUrlShare = adapter.getVideoInfo(viewHolder.adapterPosition).post
                            if (sharedViewModel.isConnected()) {
                                //CoroutineScope(Dispatchers.IO).launch {
                                    val id = /*downloadItem(postUrlShare)*/sharedViewModel.downloadVideo(videoUrlShare, videoPath)
                                    val onDownloadComplete: BroadcastReceiver = object : BroadcastReceiver() {
                                        override fun onReceive(context: Context, intent: Intent) {
                                            val downloadId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)
                                            if (id == downloadId) shareVideo(file)
                                        }
                                    }
                                    registerReceiver(onDownloadComplete, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))
                                //}
                            } else Toast.makeText(applicationContext, R.string.notConnected, Toast.LENGTH_SHORT).show()
                        }
                        adapter.notifyItemChanged(viewHolder.adapterPosition)
                    }
                }
            }
        }
        val itemTouchHelper = ItemTouchHelper(deleteGesture)
        itemTouchHelper.attachToRecyclerView(recyclerView)
    }

    private fun downloadItem(postUrl: String)/* : Long */ {
        //var id: Long = -1
        sharedViewModel.getResponse(postUrl).observe(this) {
            val videoUrl = it.data?.graphql?.shortcode_media?.video_url
            if (videoUrl != null) {
                when (it) {
                    is Resource.Success -> {
                        //CoroutineScope(Dispatchers.IO).launch {
                            /* id = */ sharedViewModel.downloadVideo(videoUrl, "")
                        //}
                    }
                    is Resource.Error -> {
                        Toast.makeText(this, it.message, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
        //return id
    }

    private fun viewAreaScroll() {
        //var scrollRange = -1
        viewArea.addOnOffsetChangedListener(AppBarLayout.OnOffsetChangedListener { appBarLayout, verticalOffset ->
            //if (scrollRange == -1) {
            val scrollRange = appBarLayout.totalScrollRange
            //}
            if (verticalOffset == 0){
                if (adapter.itemCount != 0) searchView.visibility = View.VISIBLE
                btnSearch.visibility = View.INVISIBLE
                toolbar.alpha = 0.0f
                txtViewArea.alpha = 1.0f
                if (searchView.hasFocus()) {
                    btnCloseDate.visibility = View.INVISIBLE
                    btnDatePicker.visibility = View.VISIBLE
                    adapter.filter.filter("")
                }
            } else {
                val alpha = abs(verticalOffset).toFloat() / scrollRange
                txtViewArea.alpha = 1 - alpha
                btnSearch.visibility = View.INVISIBLE
                if (!searchView.hasFocus() && searchView.query.isEmpty()) {
                    toolbar.alpha = alpha
                    searchView.visibility = View.INVISIBLE
                    btnSearch.visibility = View.INVISIBLE
                    toolbar.visibility = View.VISIBLE
                    searchView.setQuery("", false)
                    if (alpha == 1f && adapter.itemCount != 0) {
                        btnSearch.visibility = View.VISIBLE
                    }
                }
            }
        })
    }

    private fun shareVideo(file: File) {
        val shareIntent: Intent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file))
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            type = "video/mp4"
        }
        startActivity(Intent.createChooser(shareIntent, "share video"))
    }

    private fun scrollAt(recyclerView: RecyclerView, position: Int) {
        val linearSmoothScroller: LinearSmoothScroller =
            object : LinearSmoothScroller(recyclerView.context) {
                override fun calculateSpeedPerPixel(displayMetrics: DisplayMetrics): Float {
                    return 200f / displayMetrics.densityDpi
                }
            }
        linearSmoothScroller.targetPosition = position
        recyclerView.layoutManager?.startSmoothScroll(linearSmoothScroller)
    }

    private fun hideKeyboard() {
        val view: View? = currentFocus
        if (view != null) {
            val imm: InputMethodManager =
                getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }
}