package com.ugikpoenya.appmanager

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.Log
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.ugikpoenya.appmanager.model.Category
import com.ugikpoenya.appmanager.model.FileMetadataModel
import com.ugikpoenya.appmanager.model.FileModel
import com.ugikpoenya.appmanager.model.ItemModel
import com.ugikpoenya.appmanager.model.ItemResponse
import com.ugikpoenya.appmanager.model.PostModel
import com.ugikpoenya.appmanager.model.PostResponse
import com.ugikpoenya.appmanager.model.StorageModel
import org.json.JSONException
import org.json.JSONObject


class FirebaseManager {

    fun setBaseUrl(context: Context, firebase_url: String) {
        Prefs(context).FIREBASE_URL = firebase_url
    }

    fun setApiKey(context: Context, firebase_key: String) {
        Prefs(context).FIREBASE_KEY = firebase_key
    }

    fun getItemResponse(context: Context, function: (response: String?) -> (Unit)) {
        if (Prefs(context).FIREBASE_URL.isEmpty()) {
            Log.d("LOG", "Firebase url not found")
            function(null)
        } else {
            val queue = Volley.newRequestQueue(context)
            val stringRequest = StringRequest(
                Request.Method.GET,
                Prefs(context).FIREBASE_URL + "/apps/" + Prefs(context).FIREBASE_KEY + ".json",
                { response ->
                    try {
                        Log.d("LOG", "getItem successfully")
                        val itemResponse = Gson().fromJson(response, ItemResponse::class.java)
                        Prefs(context).ITEM_RESPONSE = response
                        Prefs(context).ITEM_MODEL = itemResponse.item
                        function(response)
                    } catch (e: Exception) {
                        Log.d("LOG", "Error : " + e.message)
                        function(null)
                    }
                }, {
                    Log.d("LOG", "Error : " + it.message.toString())
                    function(null)
                })
            queue.add(stringRequest)
        }
    }

    fun getItem(context: Context, key: String): String {
        return try {
            val jsonObject = JSONObject(Prefs(context).ITEM_RESPONSE)
            val item = jsonObject.getJSONObject("item")
            item.getString(key.trim())
        } catch (e: Exception) {
            Log.d("LOG", "Error : " + e.message)
            ""
        }
    }

    fun getItemModel(context: Context): ItemModel {
        Log.d("LOG", "getItemModel")
        return Prefs(context).ITEM_MODEL
    }

    fun getItemDelay(context: Context, ms: Long = 0, function: () -> (Unit)) {
        Log.d("LOG", "getItemDelay: " + ms + " ms")
        var timerResponse = false
        var serverResponse = false
        Handler(Looper.getMainLooper()).postDelayed({
            timerResponse = true
            if (serverResponse && timerResponse) {
                AdsManager().initAds(context, function)
            }
        }, ms)

        getItemResponse(context) { response: String? ->
            serverResponse = true
            if (serverResponse && timerResponse) {
                AdsManager().initAds(context, function)
            }
        }
    }

    fun getPosts(context: Context, url: String, function: (response: ArrayList<PostModel>?) -> (Unit)) {
        if (url.isEmpty()) {
            Log.d("LOG", "Firebase url not found")
        } else {
            val queue = Volley.newRequestQueue(context)
            val stringRequest = StringRequest(
                Request.Method.GET, url, { response ->
                    try {
                        Log.d("LOG", "getCategories successfully")
                        val postModelArrayList: ArrayList<PostModel> = ArrayList()
                        val json = JSONObject(response)
                        val posts = json.getJSONObject("posts")
                        val iter = posts.keys()
                        while (iter.hasNext()) {
                            val key = iter.next()
                            try {
                                val value = posts.getJSONObject(key)
                                val postModel = PostModel()
                                postModel.key = key
                                if (value.has("post_title")) postModel.post_title = value.getString("post_title")
                                if (value.has("post_content")) postModel.post_content = value.getString("post_content")
                                if (value.has("post_video")) postModel.post_video = value.getString("post_video")
                                if (value.has("post_audio")) postModel.post_audio = value.getString("post_audio")
                                if (value.has("post_asset")) postModel.post_asset = value.getString("post_asset")
                                postModelArrayList.add(postModel)
                            } catch (e: JSONException) {
                                Log.d("LOG", e.message.toString())
                            }
                        }
                        function(postModelArrayList)
                    } catch (e: Exception) {
                        Log.d("LOG", "Error : " + e.message)
                        function(null)
                    }
                }, {
                    Log.d("LOG", "Error : " + it.message.toString())
                    function(null)
                })
            queue.add(stringRequest)
        }
    }


    fun getStorage(context: Context, url: String, function: (response: StorageModel?) -> (Unit)) {
        if (url.isEmpty()) {
            Log.d("LOG", "Firebase url not found")
        } else {
            val queue = Volley.newRequestQueue(context)
            val stringRequest = StringRequest(
                Request.Method.GET, url, { response ->
                    try {
                        Log.d("LOG", "getStorage successfully")
                        val json = JSONObject(response)
                        function(getStorageModel(json, null))
                    } catch (e: Exception) {
                        Log.d("LOG", "Error : " + e.message)
                        function(null)
                    }
                }, {
                    Log.d("LOG", "Error : " + it.message.toString())
                    function(null)
                })
            queue.add(stringRequest)
        }
    }

    fun getStorageModel(json: JSONObject, folderKey: String?): StorageModel {
        val storageModel = StorageModel()
        storageModel.key = folderKey
        storageModel.files = ArrayList()
        storageModel.folder = ArrayList()
        try {
            val iter = json.keys()
            while (iter.hasNext()) {
                val key = iter.next()

                val value = json.getJSONObject(key)
                val fileModel = FileModel()
                if (value.has("name") && value.has("size") && value.has("path") && value.has("url")) {
                    fileModel.key = key
                    fileModel.name = value.getString("name")
                    fileModel.size = value.getString("size")
                    fileModel.path = value.getString("path")
                    fileModel.url = value.getString("url")
                    if (value.has("metadata")) {
                        val meta = FileMetadataModel()
                        val metadata = value.getJSONObject("metadata")
                        meta.id = if (metadata.has("id")) metadata.getString("id") else null
                        meta.title = if (metadata.has("title")) metadata.getString("title") else null
                        meta.category = if (metadata.has("category")) metadata.getString("category") else null
                        meta.content = if (metadata.has("content")) metadata.getString("content") else null
                        fileModel.metadata = meta
                    }

                    storageModel.files?.add(fileModel)
                } else {
                    storageModel.folder?.add(getStorageModel(value, key))
                }
            }
        } catch (e: Exception) {
            Log.d("LOG", "Error : " + e.message)
        }
        return storageModel
    }

}