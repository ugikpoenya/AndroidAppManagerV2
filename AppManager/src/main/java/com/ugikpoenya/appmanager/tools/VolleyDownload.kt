package com.ugikpoenya.masterguidev4

import android.content.Context
import android.util.Log
import com.android.volley.Request
import com.android.volley.toolbox.Volley
import java.io.BufferedInputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL

fun VolleyDownload(context: Context, fileUrl: String, filePath: String, callback: DownloadCallback) {
    Log.d("LOG", "Download " + fileUrl)
    Log.d("LOG", "Download Path " + filePath)
    val request = InputStreamVolleyRequest(
        method = Request.Method.GET,
        url = fileUrl,
        listener = { response ->
            try {
                val file = File(filePath)
                val outputStream = FileOutputStream(file)
                outputStream.write(response)
                outputStream.close()
            } catch (e: IOException) {
                e.printStackTrace()
                callback.onDownloadFailed(e.message ?: "Unknown error")
            }
        },
        errorListener = { error ->
            callback.onDownloadFailed(error.message ?: "Unknown error")
        }
    )
    val queue = Volley.newRequestQueue(context)
    queue.add(request)
    trackDownloadProgress(request, filePath, callback)
}

private fun trackDownloadProgress(request: InputStreamVolleyRequest, filePath: String, callback: DownloadCallback) {
    val downloadThread = Thread {
        try {
            val urlConnection = URL(request.url).openConnection() as HttpURLConnection
            urlConnection.connect()

            val fileLength = urlConnection.contentLength
            val input: InputStream = BufferedInputStream(urlConnection.inputStream)
            val data = ByteArray(1024)
            var total: Long = 0
            var count: Int
            val output = FileOutputStream(File(filePath))

            while (input.read(data).also { count = it } != -1) {
                total += count.toLong()
                output.write(data, 0, count)

                // Update progress bar
                val progress = (total * 100 / fileLength).toInt()
                callback.onProgressUpdate(progress)
            }
            output.flush()
            output.close()
            input.close()
            callback.onDownloadComplete(filePath)
        } catch (e: Exception) {
            e.printStackTrace()
            callback.onDownloadFailed(e.message ?: "Unknown error")
        }
    }
    downloadThread.start()
}
