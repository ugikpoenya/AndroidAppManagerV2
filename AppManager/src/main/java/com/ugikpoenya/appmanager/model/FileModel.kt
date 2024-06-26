package com.ugikpoenya.appmanager.model

import java.io.Serializable
import java.net.URLDecoder
import java.net.URLEncoder

class FileModel : Serializable {
    var key: String? = null
    var parent: String? = null
    var name: String? = null
    var url: String? = null
    var thumb: String? = null
    var size: String? = null
    var path: String? = null
    var metadata: FileMetadataModel? = null

    fun getThumbUrl(size: String? = null): String? {
        if (!this.url.isNullOrEmpty()) {
            var fileName = url?.substringAfterLast("/")?.substringBefore("?")
            val pathBeforeFileName = url?.substringBeforeLast("/")
            fileName = URLDecoder.decode(fileName, "UTF-8")
            val pathFolder = fileName.substringBeforeLast("/")
            val rawName = fileName.substringAfterLast("/").substringBeforeLast(".")
            val extension = fileName.substringAfterLast(".")

            fileName = if (size.isNullOrEmpty()) rawName + "_200x200." + extension
            else rawName + "_" + size + "." + extension

            return pathBeforeFileName + "/" + URLEncoder.encode("$pathFolder/thumbnails/$fileName", "UTF-8") + "?alt=media"
        } else {
            return null
        }
    }
}