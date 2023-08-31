package com.ugikpoenya.appmanager

import android.content.Context
import android.util.Log
import com.google.gson.Gson
import com.ugikpoenya.appmanager.model.PostModel
import com.ugikpoenya.appmanager.model.PostResponse
import java.io.InputStream

class LocalManager {
    fun getPosts(context: Context): ArrayList<PostModel> {
        val list: Array<String>? = context.assets.list("json")
        if (list.isNullOrEmpty()) {
            return ArrayList()
        } else {
            val gson = Gson()
            val postModelArrayList = ArrayList<PostModel>()
            for (file in list) {
                var json: String?
                try {
                    val inputStream: InputStream = context.assets.open("json/$file")
                    json = inputStream.bufferedReader().use { it.readText() }
                    val postResponse = gson.fromJson(json, PostResponse::class.java)
                    postResponse?.data?.forEach {
                        it.post_image = "file:///android_asset/" + it.post_image
                        postModelArrayList.add(it)
                    }
                } catch (ex: Exception) {
                    Log.d("LOG", "Failed read  : $file")
                    ex.printStackTrace()
                }
            }
            Log.d("LOG", "getPosts Local:  " + postModelArrayList.size)
            return postModelArrayList
        }
    }
}