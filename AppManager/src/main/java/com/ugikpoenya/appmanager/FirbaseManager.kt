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


class FirbaseManager {

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

}