package com.ugikpoenya.appmanager

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.ugikpoenya.appmanager.model.ItemModel
import com.ugikpoenya.appmanager.model.PostModel

class Prefs(context: Context) {
    val prefs: SharedPreferences = context.getSharedPreferences(context.packageName, 0)
    var BASE_URL: String
        get() = prefs.getString("BASE_URL", "").toString()
        set(value) = prefs.edit().putString("BASE_URL", value).apply()

    var API_KEY: String
        get() = prefs.getString("API_KEY", "").toString()
        set(value) = prefs.edit().putString("API_KEY", value).apply()

    var FIREBASE_URL: String
        get() = prefs.getString("FIREBASE_URL", "").toString()
        set(value) = prefs.edit().putString("FIREBASE_URL", value).apply()

    var FIREBASE_KEY: String
        get() = prefs.getString("FIREBASE_KEY", "").toString()
        set(value) = prefs.edit().putString("FIREBASE_KEY", value).apply()

    var ITEM_RESPONSE: String
        get() = prefs.getString("ITEM_RESPONSE", "").toString()
        set(value) = prefs.edit().putString("ITEM_RESPONSE", value).apply()

    var ITEM_MODEL: ItemModel
        get() {
            return try {
                val json = prefs.getString("ITEM_MODEL", "")
                Gson().fromJson(json, ItemModel::class.java)

            } catch (e: Exception) {
                Log.d("LOG", "Error : " + e.message)
                ItemModel()
            }

        }
        set(value) {
            try {
                prefs.edit().putString("ITEM_MODEL", Gson().toJson(value)).apply()
            } catch (e: Exception) {
                Log.d("LOG", "Error : " + e.message)
            }

        }

    var post_model_array_list: List<PostModel>
        get() {
            return try {
                val json = prefs.getString("post_model_array_list", "")
                val gsonBuilder = GsonBuilder().serializeNulls()
                val gson = gsonBuilder.create()
                gson.fromJson(json, Array<PostModel>::class.java).toList()
            } catch (e: Exception) {
                Log.d("LOG", "Error : " + e.message)
                ArrayList()
            }
        }
        set(value) {
            try {
                prefs.edit().putString("post_model_array_list", Gson().toJson(value)).apply()
            } catch (e: Exception) {
                Log.d("LOG", "Error : " + e.message)
            }

        }

    var serach_array_list: List<String>
        get() {
            return try {
                val json = prefs.getString("serach_array_list", "")
                val gsonBuilder = GsonBuilder().serializeNulls()
                val gson = gsonBuilder.create()
                gson.fromJson(json, Array<String>::class.java).toList()
            } catch (e: Exception) {
                Log.d("LOG", "Error : " + e.message)
                ArrayList()
            }
        }
        set(value) {
            try {
                prefs.edit().putString("serach_array_list", Gson().toJson(value)).apply()
            } catch (e: Exception) {
                Log.d("LOG", "Error : " + e.message)
            }

        }

    var version_code: Int
        get() = prefs.getInt("version_code", 0)
        set(value) = prefs.edit().putInt("version_code", value).apply()

    var privacy_policy: Boolean
        get() = prefs.getBoolean("privacy_policy", false)
        set(value) = prefs.edit().putBoolean("privacy_policy", value).apply()
}
