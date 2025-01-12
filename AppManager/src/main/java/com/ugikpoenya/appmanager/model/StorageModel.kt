package com.ugikpoenya.appmanager.model

import java.io.Serializable

class StorageModel : Serializable {
    var key: String? = null
    var folder_name: String? = null
    var files: ArrayList<FileModel>? = null
    var folder: ArrayList<StorageModel>? = null
    var folders: ArrayList<StorageModel>? = null
}