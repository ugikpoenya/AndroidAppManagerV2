package com.ugikpoenya.appmanager

import android.content.Context
import android.content.SharedPreferences

class Prefs(context: Context) {
    val prefs: SharedPreferences = context.getSharedPreferences(context.packageName, 0)
    var privacy_policy: Boolean
        get() = prefs.getBoolean("privacy_policy", false)
        set(value) = prefs.edit().putBoolean("privacy_policy", value).apply()
}
