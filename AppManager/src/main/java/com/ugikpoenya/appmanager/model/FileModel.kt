package com.ugikpoenya.appmanager.model

import com.ugikpoenya.appmanager.tools.formatFileSize
import java.io.Serializable
import java.net.URLDecoder
import java.net.URLEncoder

class FileModel : Serializable {
    var key: String? = null
    var parent: String? = null
    var name: String? = null
    var url: String? = null
    var thumb: String? = null
    var ext: String? = null
    var size: String? = null
    var path: String? = null

    fun sizeFormat(): String {
        return if (size.isNullOrEmpty()) ""
        else {
            try {
                formatFileSize(size!!.toLong())
            } catch (e: Exception) {
                ""
            }
        }
    }

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