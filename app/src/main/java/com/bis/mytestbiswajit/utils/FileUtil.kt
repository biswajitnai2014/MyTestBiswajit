package com.bis.mytestbiswajit.utils

import android.content.Context
import android.os.Environment
import android.webkit.MimeTypeMap
import java.io.File
import java.util.Locale


object FileUtil {
    fun isImage(filePath: String): Boolean {
        val mimeType = getMimeType(filePath)
        return mimeType?.startsWith("image/") ?: false
    }

    fun isVideo(filePath: String): Boolean {
        val mimeType = getMimeType(filePath)
        return mimeType?.startsWith("video/") ?: false
    }

    fun isDocument(filePath: String): Boolean {
        val documentExtensions: List<String> =
            mutableListOf("pdf", "doc", "docx", "ppt", "pptx", "xls", "xlsx")
        val extension = getFileExtension(filePath)
        return if (extension != null) {
            documentExtensions.contains(extension.lowercase(Locale.getDefault()))
        } else false
    }

    fun getMimeType(filePath: String): String? {
        val extension = getFileExtension(filePath)
        return if (extension != null) {
            MimeTypeMap.getSingleton()
                .getMimeTypeFromExtension(extension.lowercase(Locale.getDefault()))
        } else null
    }

    fun getFileExtension(filePath: String): String? {
        val lastIndex = filePath.lastIndexOf(".")
        return if (lastIndex != -1) {
            filePath.substring(lastIndex + 1)
        } else null
    }

    fun getDownloadFolder(context: Context): File? {
        val downloadFolder: File

        // Check if external storage is available and not read-only
        if (Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED) {
            // Get the external storage directory
            downloadFolder =
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
        } else {
            // External storage is not available, use internal storage or other options
            downloadFolder = File(context.getExternalFilesDir(null), "downloads")
        }
        return downloadFolder
    }
}