package com.bis.mytestbiswajit.ui.activity

import android.app.AlertDialog
import android.bluetooth.BluetoothAdapter
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import android.widget.Button
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.bis.mytestbiswajit.R
import com.bis.mytestbiswajit.databinding.ActivityMainBinding
import com.bis.mytestbiswajit.network.ApiInterface
import com.bis.mytestbiswajit.network.ApiUtility
import com.bis.mytestbiswajit.network.repository.MainRepository
import com.bis.mytestbiswajit.ui.base.BaseActivity
import com.bis.mytestbiswajit.utils.PermissionUtils
import com.bis.mytestbiswajit.utils.PermissionsCallback
import com.bis.mytestbiswajit.network.viewModel.MainViewModel
import com.bis.mytestbiswajit.network.viewModel.ViewModelFactory


class MainActivity : BaseActivity() {
    lateinit var binding: ActivityMainBinding
    lateinit var mainViewModel: MainViewModel
    private var navController: NavController? = null

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        //mainViewModel = ViewModelProvider(this)[MainViewModel::class.java]
        binding.lifecycleOwner = this
        init()
        binding.executePendingBindings()
    }


    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    fun init() {
        val apiInterface= ApiUtility.getInstance().create(ApiInterface::class.java)
        val mainRepository= MainRepository(apiInterface)

       //mainViewModel=ViewModelProvider(this@MainActivity,ViewModelFactory(mainRepository)).get()
        mainViewModel = ViewModelProvider(this@MainActivity, ViewModelFactory(mainRepository)).get(MainViewModel::class.java)
        val navHostFragment: NavHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController
        checkPermission()

    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    fun checkPermission() {
        if (!PermissionUtils.hasVideoRecordingPermissions(this@MainActivity)) {


            PermissionUtils.requestVideoRecordingPermission(this, object : PermissionsCallback {
                override fun onPermissionRequest(granted: Boolean) {
                    if (!granted) {
                        dialogRecordingPermission()

                    } else {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                            if (!Environment.isExternalStorageManager()) {
                                dialogAllFileAccessPermissionAbove30()
                            }

                        }

                    }

                }

            })

        }
    }

    private fun dialogRecordingPermission() {
        createAlertDialog(
            this@MainActivity,
            "Permission Denied!",
            "Go to setting and enable recording permission",
            "OK", ""
        ) { value ->
            if (value) {
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                val uri = Uri.fromParts("package", packageName, null)
                intent.data = uri
                startActivity(intent)
            }
        }
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
        alertDialog.setCancelable(false)
        alertDialog.show()

        val buttonbackground: Button = alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE)
        buttonbackground.setTextColor(Color.BLACK)

        val buttonbackground1: Button = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE)
        buttonbackground1.setTextColor(Color.BLACK)
    }

    fun dialogAllFileAccessPermissionAbove30() {
        createAlertDialog(
            this@MainActivity,
            "All file permissions",
            "Go to setting and enable all files permission",
            "OK", ""
        ) { value ->
            if (value) {
                val getpermission = Intent()
                getpermission.action = Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION
                startActivity(getpermission)
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    fun goToBluetooth(uri:Uri?){
        PermissionUtils.requestBluetoothPermission(this,object:PermissionsCallback{
                override fun onPermissionRequest(granted: Boolean) {
                    val intent=Intent(this@MainActivity,BluetoothActivity::class.java)
                    intent.putExtra("path",uri.toString())
                   if (BluetoothAdapter.getDefaultAdapter()!=null){
                       startActivity(intent)
                   } else {

                       Toast.makeText(binding.root.context, "This device does not support bluetooth", Toast.LENGTH_SHORT).show()
                   }

                }
            })


    }
}