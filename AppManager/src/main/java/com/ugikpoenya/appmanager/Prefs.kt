package com.ugikpoenya.appmanager

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.google.gson.Gson
import com.ugikpoenya.appmanager.model.ItemModel
import com.ugikpoenya.appmanager.model.ItemResponse
import java.lang.Exception

class Prefs(context: Context) {
    val prefs: SharedPreferences = context.getSharedPreferences(context.packageName, 0)
    var BASE_URL: String
        get() = prefs.getString("BASE_URL", "").toString()
        set(value) = prefs.edit().putString("BASE_URL", value).apply()

    var API_KEY: String
        get() = prefs.getString("API_KEY", "").toString()
        set(value) = prefs.edit().putString("API_KEY", value).apply()

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

    var privacy_policy: Boolean
        get() = prefs.getBoolean("privacy_policy", false)
        set(value) = prefs.edit().putBoolean("privacy_policy", value).apply()
}
