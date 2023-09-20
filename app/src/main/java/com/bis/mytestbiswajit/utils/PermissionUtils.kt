package com.bis.mytestbiswajit.utils

import android.Manifest
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Build
import android.widget.Button
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener

object PermissionUtils {
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private val CAMERA_PERMISSIONS:MutableList<String> =

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            mutableListOf(
                Manifest.permission.READ_MEDIA_IMAGES,
                Manifest.permission.READ_MEDIA_VIDEO,
                Manifest.permission.READ_MEDIA_AUDIO,
                Manifest.permission.CAMERA,
                Manifest.permission.RECORD_AUDIO,

                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ).apply {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    add(Manifest.permission.ACCESS_MEDIA_LOCATION)
                }
            }

        }
    else {
            mutableListOf(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.CAMERA,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION,

                Manifest.permission.RECORD_AUDIO,
            ).apply {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    add(Manifest.permission.ACCESS_MEDIA_LOCATION)
                }
            }
        }

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private val BLUETOOTH_PERMISSIONS:MutableList<String> =
            mutableListOf(

                Manifest.permission.BLUETOOTH_CONNECT,
                Manifest.permission.BLUETOOTH_ADVERTISE,
                Manifest.permission.BLUETOOTH_SCAN,

            )








    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    fun requestVideoRecordingPermission(context: Context, callback: PermissionsCallback) {

        Dexter.withContext(context)
            .withPermissions(CAMERA_PERMISSIONS)
            .withListener(object : MultiplePermissionsListener {
                override fun onPermissionsChecked(report: MultiplePermissionsReport?) {
                    // Check if user has granted all
                    if (report?.areAllPermissionsGranted() == true) {
                        callback.onPermissionRequest(true)
                    } else {
                        callback.onPermissionRequest(false)
                    }
                }

                override fun onPermissionRationaleShouldBeShown(
                    permissions: MutableList<PermissionRequest>?,
                    token: PermissionToken?
                ) {
                    // User has denied a permission, proceed and ask them again
                    token?.continuePermissionRequest()
                }
            }).check()
    }


    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    fun requestBluetoothPermission(context: Context, callback: PermissionsCallback) {

        Dexter.withContext(context)
            .withPermissions(BLUETOOTH_PERMISSIONS)
            .withListener(object : MultiplePermissionsListener {
                override fun onPermissionsChecked(report: MultiplePermissionsReport?) {
                    // Check if user has granted all
                    if (report?.areAllPermissionsGranted() == true) {
                        callback.onPermissionRequest(true)
                    } else {
                        callback.onPermissionRequest(false)
                    }
                }

                override fun onPermissionRationaleShouldBeShown(
                    permissions: MutableList<PermissionRequest>?,
                    token: PermissionToken?
                ) {
                    // User has denied a permission, proceed and ask them again
                    token?.continuePermissionRequest()
                }
            }).check()
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    fun hasVideoRecordingPermissions(context: Context): Boolean =
       // CAMERA_PERMISSIONS.all {
        CAMERA_PERMISSIONS.all {
            ActivityCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED
        }
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    fun hasBluetoothPermissions(context: Context): Boolean =

        BLUETOOTH_PERMISSIONS.all {
            ActivityCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED
        }


    fun createAlertDialog(
        context: Context,
        title: String,
        msg: String,
        positiveButtonText: String,
        negativeButtonText: String,
        listener: (Boolean) -> Unit
    ) {
        val builder = AlertDialog.Builder(context)
        builder.setTitle(title)
        builder.setMessage(msg)
        builder.setPositiveButton(
            positiveButtonText
        ) { dialog, which ->
            dialog.cancel()
            listener(true)
        }
        builder.setNegativeButton(
            negativeButtonText
        ) { dialog, which ->
            dialog.cancel()
            listener(false)
        }

        var alertDialog: AlertDialog = builder.create()
        // Set other dialog properties
        alertDialog.setCancelable(false)
        alertDialog.show()

        val buttonbackground: Button = alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE)
        buttonbackground.setTextColor(Color.BLACK)

        val buttonbackground1: Button = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE)
        buttonbackground1.setTextColor(Color.BLACK)
    }
}
