package com.ugikpoenya.appmanager.tools

fun formatFileSize(bytes: Long): String {
    if (bytes < 1024) return "$bytes B"
    val units = arrayOf("B", "KB", "MB", "GB", "TB", "PB", "EB", "ZB", "YB")
    val exp = (Math.log(bytes.toDouble()) / Math.log(1024.0)).toInt()
    return String.format("%.2f %s", bytes / Math.pow(1024.0, exp.toDouble()), units[exp])
}