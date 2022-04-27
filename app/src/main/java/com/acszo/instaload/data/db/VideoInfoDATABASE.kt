package com.acszo.instaload.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [VideoInfo::class], version = 1, exportSchema = false)
abstract class VideoInfoDATABASE: RoomDatabase() {

    abstract fun videoInfoDAO(): VideoInfoDAO

    companion object {
        @Volatile
        private var INSTANCE: VideoInfoDATABASE? = null

        fun getDatabase(context: Context): VideoInfoDATABASE {
            val tempInstance = INSTANCE
            if(tempInstance != null){
                return tempInstance
            }
            synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    VideoInfoDATABASE::class.java,
                    "videoInfo_database"
                ).build()
                INSTANCE = instance
                return instance
            }
        }
    }

}