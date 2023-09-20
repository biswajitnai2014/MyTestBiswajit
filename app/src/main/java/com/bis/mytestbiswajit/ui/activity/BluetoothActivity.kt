package com.bis.mytestbiswajit.ui.activity

import android.Manifest
import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.provider.MediaStore
import android.util.Log
import android.view.View
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.loader.content.CursorLoader
import com.bis.mytestbiswajit.R
import com.bis.mytestbiswajit.databinding.ActivityBluetoothBinding
import com.bis.mytestbiswajit.ui.adapter.DeviceListAdapter
import com.bis.mytestbiswajit.utils.BluetoothReceiveFile
import com.bis.mytestbiswajit.utils.BluetoothService
import com.bis.mytestbiswajit.utils.DeviceNameOnClickListner
import com.bis.mytestbiswajit.utils.MyConstants.STATIC_OBJ.FILE_NAME
import com.bis.mytestbiswajit.utils.MyConstants.STATIC_OBJ.STATE_CONNECTED
import com.bis.mytestbiswajit.utils.MyConstants.STATIC_OBJ.STATE_CONNECTING
import com.bis.mytestbiswajit.utils.MyConstants.STATIC_OBJ.STATE_CONNECTION_FAILED
import com.bis.mytestbiswajit.utils.MyConstants.STATIC_OBJ.STATE_LISTING
import com.bis.mytestbiswajit.utils.MyConstants.STATIC_OBJ.STATE_NESSAGE_RECEIVED
import java.io.File
import java.io.FileInputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Locale


class BluetoothActivity : AppCompatActivity() {
    lateinit var bluetoothManager: BluetoothManager
    lateinit var bluetoothAdapter: BluetoothAdapter
    lateinit var binding: ActivityBluetoothBinding
    var bluetoothService: BluetoothService? = null
    private val FILENAME_FORMAT = "yyyy-MM-dd-HH-mm-ss-SSS"
    var file: File? = null
    private val mHandlar: Handler = object : Handler(Looper.getMainLooper()) {
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            when (msg.what) {
                STATE_LISTING -> {
                    binding.tvStatus.setText("Listening")
                    binding.group.visibility = View.VISIBLE
                    binding.img.visibility = View.GONE

                    binding.tvShareImage.visibility = View.GONE
                }

                STATE_CONNECTING -> {
                    binding.tvStatus.setText("CONNECTING")
                    binding.group.visibility = View.VISIBLE
                    binding.img.visibility = View.GONE

                    binding.tvShareImage.visibility = View.GONE
                }

                STATE_CONNECTED -> {
                    binding.tvStatus.setText("CONNECTED")
                    binding.group.visibility = View.GONE
                    binding.img.visibility = View.VISIBLE

                    binding.tvShareImage.visibility = View.VISIBLE
                }

                STATE_CONNECTION_FAILED -> {
                    binding.tvStatus.setText("CONNECTION FAILED")
                    binding.group.visibility = View.VISIBLE
                    binding.img.visibility = View.GONE

                    binding.tvShareImage.visibility = View.GONE
                }

                STATE_NESSAGE_RECEIVED -> {


                    binding.img.visibility = View.VISIBLE

                    binding.tvShareImage.visibility = View.GONE
                    binding.tvStatus.setText("MESSAGE RECEIVED")

                }

            }
        }
    }


    @RequiresApi(Build.VERSION_CODES.R)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setContentView(R.layout.activity_bluetooth)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_bluetooth)
        binding.lifecycleOwner = this

        init()

    }

    val pickMedia = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->

        if (uri != null) {
            binding.img.visibility = View.VISIBLE
            val filePath = getFilePathFromContentUri(this, uri)
            if (filePath != null) {
                file = File(filePath)
                file?.let { f ->
                    if (f.exists()) {
                        setImage(f)

                        fileToByteArray(f)?.let { it1 ->
                            bluetoothService?.sendRecever?.write(
                                it1
                            )
                        }
                    }
                }

            }

        }
    }

    @SuppressLint("NewApi")
    @RequiresApi(Build.VERSION_CODES.R)
    fun init() {

        bluetoothManager = getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        bluetoothAdapter = bluetoothManager.adapter
        FILE_NAME = SimpleDateFormat(FILENAME_FORMAT, Locale.US)
            .format(System.currentTimeMillis())

        bluetoothService =
            BluetoothService(bluetoothAdapter, mHandlar, binding.tvScanDevice.context, object :
                BluetoothReceiveFile {
                override fun getFile(receivefile: File?) {
                    receivefile?.let {
                        if (receivefile?.exists() == true) {
                            binding.tvStatus.setText("FILE RECEIVED and file path : " + receivefile.absolutePath)


                            if (receivefile.exists()) {

                                val myBitmap = BitmapFactory.decodeFile(receivefile.absolutePath)
                                binding.img.setImageBitmap(myBitmap)
                            }
                        }
                    }
                }
            })
        bluetoothManager = getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        bluetoothAdapter = bluetoothManager.adapter
        enableDisable()
        enableDiscoverable()

        onViewClick()

    }


    fun getFilePathFromContentUri(context: Context, contentUri: Uri): String? {
        val projection = arrayOf(MediaStore.Images.Media.DATA)
        val cursorLoader = CursorLoader(context, contentUri, projection, null, null, null)
        val cursor = cursorLoader.loadInBackground()

        return if (cursor != null) {
            val columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
            cursor.moveToFirst()
            val filePath = cursor.getString(columnIndex)
            cursor.close()
            filePath
        } else {
            null
        }
    }

    @RequiresApi(Build.VERSION_CODES.S)
    fun enableDiscoverable() {
        when {
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.BLUETOOTH_ADVERTISE
            ) == PackageManager.PERMISSION_GRANTED -> {

            }

            shouldShowRequestPermissionRationale(android.Manifest.permission.BLUETOOTH_ADVERTISE) -> {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(android.Manifest.permission.BLUETOOTH_ADVERTISE),
                    101
                )
            }
        }
    }

    fun enableDisable() {
        when {
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.BLUETOOTH_CONNECT
            ) == PackageManager.PERMISSION_GRANTED -> {

            }

            shouldShowRequestPermissionRationale(android.Manifest.permission.BLUETOOTH_CONNECT) -> {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(android.Manifest.permission.BLUETOOTH_CONNECT),
                    101
                )
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.R)
    fun onViewClick() {
        binding.tvClose.setOnClickListener {
            bluetoothService = null
            finish()
        }
        binding.tvShareImage.setOnClickListener {
            binding.tvShareImage.visibility = View.GONE

            pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }
        binding.tvOnOff.setOnClickListener {
            if (!bluetoothAdapter.isEnabled) {
                if (ActivityCompat.checkSelfPermission(
                        this,
                        Manifest.permission.BLUETOOTH_CONNECT
                    ) != PackageManager.PERMISSION_GRANTED
                ) {

                }
                bluetoothAdapter.enable()
                val intent = (Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE))
                startActivity(intent)
            }
            if (bluetoothAdapter.isEnabled) {
                bluetoothAdapter.disable()
            }
        }
        binding.tvClientDevice.setOnClickListener {
            val arr = bluetoothAdapter.bondedDevices
            if (!arr.isNullOrEmpty()) {
                binding.recDeciceList.adapter =
                    DeviceListAdapter(binding.recDeciceList.context, arr.toList(), object :
                        DeviceNameOnClickListner {
                        override fun showDevice(device: BluetoothDevice) {
                            device?.let {

                                if (ActivityCompat.checkSelfPermission(
                                        binding.tvClientDevice.context,
                                        Manifest.permission.BLUETOOTH_CONNECT
                                    ) != PackageManager.PERMISSION_GRANTED
                                ) {

                                }
                                binding.tvStatus.setText("Connecting..." + it.name)
                                val clientClass = bluetoothService?.ClientClass(it)
                                clientClass?.start()
                            }

                        }

                    })
            }


        }
        binding.tvScanDevice.setOnClickListener {
            val serverClass = bluetoothService?.ServerClass()
            serverClass?.start()
        }


    }

    private fun setImage(imgFile: File) {
        val myBitmap = BitmapFactory.decodeFile(imgFile.absolutePath)
        binding.img.setImageBitmap(myBitmap)
    }

    fun fileToByteArray(file: File): ByteArray? {
        try {
            val fileInputStream = FileInputStream(file)
            val byteArray = ByteArray(file.length().toInt())
            val bytesRead = fileInputStream.read(byteArray)
            fileInputStream.close()

            if (bytesRead == byteArray.size) {
                return byteArray
            }
        } catch (e: IOException) {
            e.printStackTrace()
            Log.e("TAG_errorf", "fileToByteArray: " + e.message)
        } catch (e: Exception) {
            Log.e("TAG_errorf", "fileToByteArray: " + e.message)
        }
        return null
    }


}



