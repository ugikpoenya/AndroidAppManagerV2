package com.ugikpoenya.appmanager

import android.content.Context
import android.util.Log

class SearchManager {
    fun set(context: Context, lists: List<String>) {
        Prefs(context).serach_array_list = lists
    }

    fun get(context: Context): List<String> {
        return Prefs(context).serach_array_list
    }

    fun get(context: Context, filter: String): List<String> {
        return Prefs(context).serach_array_list
            .filter { it -> (it.lowercase().contains(filter.lowercase())) }
    }

    fun add(context: Context, value: String) {
        val lists = ArrayList<String>()
        lists.addAll(Prefs(context).serach_array_list)

        val found = lists.find { it.lowercase().trim() == value.lowercase().trim() }
        if (found !== null) {
            lists.remove(found)
        }
        lists.add(0, value)
        Prefs(context).serach_array_list = lists
    }

    fun delete(context: Context, value: String): Boolean {
        val lists = ArrayList<String>()
        lists.addAll(Prefs(context).serach_array_list)
        val found = lists.find { it.lowercase().trim() == value.lowercase().trim() }
        if (found == null) {
            return false
        } else {
            Log.d("", "Delete : " + found)
            lists.remove(found)
            Prefs(context).serach_array_list = lists
            return true
        }
    }
}