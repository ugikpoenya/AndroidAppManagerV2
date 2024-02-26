package com.ugikpoenya.appmanager

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.Log
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.gson.Gson
import com.ugikpoenya.appmanager.model.ItemModel
import com.ugikpoenya.appmanager.model.ItemResponse
import org.json.JSONException
import org.json.JSONObject
import java.net.URLEncoder


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
                        val json = JSONObject(response)
                        if (json.has("item")) {
                            val itemObject = JSONObject()
                            val item = json.getJSONObject("item")
                            val iter = item.keys()
                            while (iter.hasNext()) {
                                val key = iter.next()
                                try {
                                    val value = item.getJSONObject(key)
                                    if (value.getString("is_active") == "1") {
                                        itemObject.put(key, value.getString("value"))
                                    }
                                } catch (e: JSONException) {
                                    Log.d("LOG", e.message.toString())
                                }
                            }
                            val itemModel: ItemModel = Gson().fromJson(itemObject.toString(), ItemModel::class.java)
                            Prefs(context).ITEM_MODEL = itemModel

                            val itemResponse = ItemResponse()
                            itemResponse.item = itemModel
                            Prefs(context).ITEM_RESPONSE = Gson().toJson(itemResponse)
                        }
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

    fun getAssetStorage(context: Context, function: (name: ArrayList<String>?) -> (Unit)) {
        getAssetStorage(context, getItemModel(context).asset_folder, function)
    }

    fun getAssetStorage(context: Context, parent: String, function: (name: ArrayList<String>?) -> (Unit)) {
        val itemModel = getItemModel(context)
        if (Prefs(context).FIREBASE_URL.isEmpty()) {
            Log.d("LOG", "Firebase url not found")
            function(null)
        } else if (itemModel.asset_storage.isEmpty()) {
            Log.d("LOG", "Asset storage not found")
            function(null)
        } else {
            val storageUrl = Prefs(context).FIREBASE_URL + "/storage/" + itemModel.asset_storage + ".json?orderBy=\"parent\"&equalTo=\"" + parent + "\""
            Log.d("LOG", storageUrl)
            val queue = Volley.newRequestQueue(context)
            val stringRequest = StringRequest(
                Request.Method.GET,
                storageUrl,
                { response ->
                    Log.d("LOG", "getAssetStorage successfully")
                    try {
                        var listFile: ArrayList<String> = ArrayList<String>()
                        val json = JSONObject(response)
                        val iter = json.keys()
                        while (iter.hasNext()) {
                            val key = iter.next()
                            try {
                                val value = json.getJSONObject(key)
                                var name = value.getString("name")
                                if (name.substringAfterLast("/").isEmpty()) {
                                    listFile.add(name.substringBeforeLast("/"))
                                } else {
                                    name = URLEncoder.encode(name, "UTF-8");
                                    val urlName = "https://firebasestorage.googleapis.com/v0/b/" + itemModel.asset_storage + ".appspot.com/o/" + name + "?alt=media"
                                    listFile.add(urlName)
                                }
                            } catch (e: JSONException) {
                                Log.d("LOG", e.message.toString())
                            }
                        }
                        function(listFile)
                    } catch (e: Exception) {
                        Log.d("LOG", "Error Storage: " + e.message)
                        function(null)
                    }
                }, {
                    Log.d("LOG", "Error Storage: " + it.message.toString())
                    function(null)
                })
            queue.add(stringRequest)
        }
    }
}