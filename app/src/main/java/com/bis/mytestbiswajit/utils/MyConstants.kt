package com.bis.mytestbiswajit.utils


import android.content.ContentValues
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import androidx.lifecycle.MutableLiveData
import java.io.File

class MyConstants{

    object STATIC_OBJ{
        var isVideo=false
        var contentValues: ContentValues? =null
        var STATE_LISTING=1
        var STATE_CONNECTING=2
        var STATE_CONNECTED=3
        var STATE_CONNECTION_FAILED=4
        var STATE_NESSAGE_RECEIVED=5
        var UUID_VALUE="00001101-0000-1000-8000-00805F9B34FB"
        var FILE_NAME = "k1"
        }

    companion object{
        fun getContentValues(name: String, mimeType: String): ContentValues? {
            ContentValues().apply {
                put(MediaStore.MediaColumns.DISPLAY_NAME, name)
                put(MediaStore.MediaColumns.MIME_TYPE, mimeType)

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    // For Android 10 and higher, use RELATIVE_PATH
                    put(MediaStore.Video.Media.RELATIVE_PATH, "Biswajit/image")
                } else {
                    // For versions prior to Android 10, manage the file operations manually
                    val directoryPath = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        // For Android Nougat and higher, use getExternalStoragePublicDirectory
                        Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                    } else {
                        // For versions prior to Nougat, use a hardcoded path
                        File(Environment.getExternalStorageDirectory(), "Biswajit/image")
                    }

                    // Ensure the directory exists, and create it if necessary
                    if (!directoryPath.exists()) {
                        directoryPath.mkdirs()
                    }

                    // Set the full file path
                    val filePath = File(directoryPath, name).absolutePath
                    put(MediaStore.MediaColumns.DATA, filePath)
                }
            }
            return null
        }
    }

}

