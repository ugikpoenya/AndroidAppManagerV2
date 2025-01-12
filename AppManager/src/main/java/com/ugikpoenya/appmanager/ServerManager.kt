package com.ugikpoenya.appmanager

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.Log
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.gson.Gson
import com.ugikpoenya.appmanager.model.Category
import com.ugikpoenya.appmanager.model.ItemModel
import com.ugikpoenya.appmanager.model.ItemResponse
import com.ugikpoenya.appmanager.model.PostModel
import com.ugikpoenya.appmanager.model.PostResponse
import com.ugikpoenya.appmanager.model.StorageModel
import org.json.JSONObject

val DEFAULT_NATIVE_START = 2
val DEFAULT_NATIVE_INTERVAL = 8
val DEFAULT_INTERSTITIAL_INTERVAL = 0
var DEFAULT_PRIORITY = "0,1,2,3"

class ServerManager {
    fun setBaseUrl(context: Context, base_url: String) {
        Prefs(context).BASE_URL = base_url
    }

    fun setApiKey(context: Context, api_key: String) {
        Prefs(context).API_KEY = api_key
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

    fun getCategories(context: Context): ArrayList<Category> ?{
        return try {
            val itemResponse = Gson().fromJson(Prefs(context).ITEM_RESPONSE, ItemResponse::class.java)
            itemResponse.categories
        } catch (e: Exception) {
            Log.d("LOG", "Error : " + e.message)
            null
        }
    }

    fun getItemResponse(context: Context, function: (response: String?) -> (Unit)) {
        if (Prefs(context).BASE_URL.isEmpty()) {
            Log.d("LOG", "Base url not found")
            function(null)
        } else {
            val queue = Volley.newRequestQueue(context)
            val stringRequest = object : StringRequest(Method.GET, Prefs(context).BASE_URL, com.android.volley.Response.Listener { response ->
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
            }, com.android.volley.Response.ErrorListener {
                Log.d("LOG", "Error : " + it.message.toString())
                function(null)
            }) {
                override fun getHeaders(): MutableMap<String, String> {
                    val headers = HashMap<String, String>()
                    headers["package_name"] = context.packageName
                    headers["api_key"] = Prefs(context).API_KEY
                    return headers
                }
            }
            queue.add(stringRequest)
        }
    }


    fun getPostResponse(context: Context, url: String, function: (postResponse: PostResponse?) -> (Unit)) {
        val queue = Volley.newRequestQueue(context)
        val stringRequest = object : StringRequest(Method.GET, Prefs(context).BASE_URL + url, com.android.volley.Response.Listener { response ->
            try {
                val postResponse = Gson().fromJson(response, PostResponse::class.java)
                function(postResponse)
            } catch (e: Exception) {
                Log.d("LOG", "Error : " + e.message)
                function(null)
            }
        }, com.android.volley.Response.ErrorListener {
            Log.d("LOG", "Error : " + it.message)
            function(null)
        }) {
            override fun getHeaders(): MutableMap<String, String> {
                val headers = HashMap<String, String>()
                headers["package_name"] = context.packageName
                headers["api_key"] = Prefs(context).API_KEY
                return headers
            }
        }
        queue.add(stringRequest)
    }

    fun getPosts(context: Context, function: (posts: ArrayList<PostModel>?) -> (Unit)) {
        val localPost = LocalManager().getPosts(context)
        if (localPost.isEmpty()) {
            getPostResponse(context, "posts") {
                Log.d("LOG", "getPosts :  " + it?.data?.size)
                function(it?.data)
            }
        } else {
            function(localPost)
        }
    }

    fun getAssetFiles(context: Context, function: (files: ArrayList<String>?) -> (Unit)) {
        getPostResponse(context, "assets") {
            Log.d("LOG", "getAssetFiles :  " + it?.files?.size)
            function(it?.files)
        }
    }

    fun getAssetFiles(context: Context, folder: String, function: (files: ArrayList<String>?) -> (Unit)) {
        getPostResponse(context, "assets/$folder") {
            Log.d("LOG", "getAssetFiles $folder:  " + it?.files?.size)
            function(it?.files)
        }
    }

    fun getAssetFolders(context: Context, function: (folders: Map<String, ArrayList<String>>?) -> (Unit)) {
        getPostResponse(context, "assets") {
            Log.d("LOG", "getAssetFolders :  " + it?.folders?.size)
            function(it?.folders)
        }
    }

    fun getAssetFolders(context: Context, folder: String, function: (folders: Map<String, ArrayList<String>>?) -> (Unit)) {
        getPostResponse(context, "assets/$folder") {
            Log.d("LOG", "getAssetFolders $folder:  " + it?.folders?.size)
            function(it?.folders)
        }
    }

    fun getAssets(context: Context, function: (files: ArrayList<String>?, folders: Map<String, ArrayList<String>>?) -> (Unit)) {
        getPostResponse(context, "assets") {
            Log.d("LOG", "getAssets :  " + it?.files?.size + "/" + it?.folders?.size)
            function(it?.files, it?.folders)
        }
    }

    fun getAssets(context: Context, folder: String, function: (files: ArrayList<String>?, folders: Map<String, ArrayList<String>>?) -> (Unit)) {
        getPostResponse(context, "assets/$folder") {
            Log.d("LOG", "getAssets $folder:  " + it?.files?.size + "/" + it?.folders?.size)
            function(it?.files, it?.folders)
        }
    }

    fun getFolder(context: Context, folder: String, function: (files: ArrayList<String>?, folders: Map<String, ArrayList<String>>?) -> (Unit)) {
        getPostResponse(context, "folder/$folder") {
            Log.d("LOG", "getFolder $folder:  " + it?.files?.size + "/" + it?.folders?.size)
            function(it?.files, it?.folders)
        }
    }

    fun getStorage(context: Context, function: (storageModel: StorageModel?) -> (Unit)) {
        Log.d("LOG", "getStorage "+Prefs(context).BASE_URL + "storage")
        val queue = Volley.newRequestQueue(context)
        val stringRequest = object : StringRequest(Method.GET, Prefs(context).BASE_URL + "storage", com.android.volley.Response.Listener { response ->
            try {
                val storageModel = Gson().fromJson(response, StorageModel::class.java)
                Log.d("LOG", "getStorage files "+storageModel.files?.size.toString())
                Log.d("LOG", "getStorage folder "+storageModel.folders?.size.toString())
                function(storageModel)
            } catch (e: Exception) {
                Log.d("LOG", "Error : " + e.message)
                function(null)
            }
        }, com.android.volley.Response.ErrorListener {
            Log.d("LOG", "Error : " + it.message)
            function(null)
        }) {
            override fun getHeaders(): MutableMap<String, String> {
                val headers = HashMap<String, String>()
                headers["package_name"] = context.packageName
                headers["api_key"] = Prefs(context).API_KEY
                return headers
            }
        }
        queue.add(stringRequest)
    }
}