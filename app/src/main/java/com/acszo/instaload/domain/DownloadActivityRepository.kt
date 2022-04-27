package com.acszo.instaload.domain

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.acszo.instaload.R
import com.acszo.instaload.Resource
import com.acszo.instaload.data.response.*
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.lang.Exception

class DownloadActivityRepository {

    fun getInstaPostJson(context: Context, url: String): LiveData<Resource<PostObject>> {
        val dataResponse = MutableLiveData<Resource<PostObject>>()
        val gson = Gson()
        try {
            val queue = Volley.newRequestQueue(context)
            val jsonObjectRequest = JsonObjectRequest(
                Request.Method.GET, url, null,
                { response ->
                    val jsonResponse: PostObject = gson.fromJson(
                        response.toString(),
                        object : TypeToken<PostObject>() {}.type
                    )
                    dataResponse.postValue(Resource.Success(jsonResponse))
                    return@JsonObjectRequest
                }, {
                    dataResponse.postValue(Resource.Error(context.resources.getString(R.string.invalidLink)))
                    }
                )
            queue.add(jsonObjectRequest)
        } catch (e: Exception) {
            dataResponse.postValue(Resource.Error("Unknown error"))
        }

        return dataResponse
    }

}