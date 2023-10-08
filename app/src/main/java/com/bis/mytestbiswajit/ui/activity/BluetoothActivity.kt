package com.bis.mytestbiswajit.ui.activity

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.database.Cursor
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.provider.MediaStore
import android.provider.Settings
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.loader.content.CursorLoader
import androidx.navigation.fragment.findNavController
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
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.text.SimpleDateFormat
import java.util.Locale


class BluetoothActivity : AppCompatActivity() {
    lateinit var bluetoothManager: BluetoothManager
    lateinit var bluetoothAdapter: BluetoothAdapter
    lateinit var binding: ActivityBluetoothBinding
    var bluetoothService: BluetoothService? = null
    var deviceListAdapter: DeviceListAdapter? = null
    var bluetoothDeviceList: ArrayList<BluetoothDevice>? = null
    var alertDialog: AlertDialog? = null
    var isClient=false
    private var imageCapture: ImageCapture? = null
    private val FILENAME_FORMAT = "yyyy-MM-dd-HH-mm-ss-SSS"
    var file: File? = null



    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (!isGranted) {
                openAppSettings()
            }
        }

    private fun openAppSettings() {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        val uri = Uri.fromParts("package", packageName, null)
        intent.data = uri
        startActivity(intent)
    }

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
                    if (alertDialog?.isShowing == true) {
                        alertDialog?.dismiss()
                    }

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
    val MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE=102
    val MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 103
    @RequiresApi(Build.VERSION_CODES.R)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setContentView(R.layout.activity_bluetooth)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_bluetooth)
        binding.lifecycleOwner = this

        init()

    }

    @RequiresApi(Build.VERSION_CODES.P)
    val pickMedia = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->

        if (uri != null) {
            binding.img.visibility = View.VISIBLE
            val filePath = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q){
                getFilePathFromContentUriQ(this, uri)
            }
            else{
                getFilePathFromContentUri(this, uri)
            }


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
        setDialog()
        callDeviceList(ArrayList())
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

        val filter = IntentFilter(BluetoothDevice.ACTION_FOUND)
        registerReceiver(receiver, filter)
    }

    private fun setDialog() {
        val dialog = AlertDialog.Builder(this@BluetoothActivity)
        dialog.setMessage(R.string.dialogMessage)
        dialog.setIcon(android.R.drawable.ic_dialog_alert)
        dialog.setIcon(android.R.drawable.ic_dialog_alert)
        alertDialog = dialog.create()
        dialog.setCancelable(false)
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
            /*val filter = IntentFilter(BluetoothDevice.ACTION_FOUND)
            registerReceiver(receiver, filter)*/
            bluetoothService = null
            finish()
        }
        binding.tvShareImage.setOnClickListener {

        binding.tvShareImage.visibility = View.GONE

       if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.Q) {
                    pickMediaFromDCIM()
                }
                else{
                    pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                }
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
            isClient=true
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {

                startClientDevice()
            }
            else{
                checkWriteFilePermission()
            }

        }
        binding.tvScanDevice.setOnClickListener {
            isClient=false
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {

                startClientService()
            }
            else{
                checkWriteFilePermission()
            }


        }

      //  binding.tvShareImage.performClick()
    }

    @SuppressLint("MissingPermission")
    private fun startClientDevice() {
        discoverDevices()
        val arr = bluetoothAdapter.bondedDevices
        if (!arr.isNullOrEmpty()) {
            arr?.let {
                val deviceList = ArrayList(it)
                callDeviceList(deviceList)
            }
        }
    }

    private fun startClientService() {
        val serverClass = bluetoothService?.ServerClass()
        serverClass?.start()
        alertDialog?.show()
    }

    private fun callDeviceList(deviceList: ArrayList<BluetoothDevice>) {


        deviceListAdapter = DeviceListAdapter(binding.recDeciceList.context, deviceList, object :
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
        binding.recDeciceList.adapter = deviceListAdapter

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

    private fun discoverDevices() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.BLUETOOTH_SCAN
            ) != PackageManager.PERMISSION_GRANTED
        ) {

        }
        if (bluetoothAdapter.isDiscovering) {
            bluetoothAdapter.cancelDiscovery()
        }
        val bool = bluetoothAdapter.startDiscovery()
        Log.i("", bool.toString())
        val filter = IntentFilter(BluetoothDevice.ACTION_FOUND)
        registerReceiver(mReceiver, filter)


    }

    private val mReceiver = object : BroadcastReceiver() {
        @SuppressLint("MissingPermission")
        override fun onReceive(context: Context, intent: Intent) {
            val action = intent.action
            if (BluetoothDevice.ACTION_FOUND == action) {
                // A Bluetooth device was found
                // Getting device information from the intent
                val device =
                    intent.getParcelableExtra<BluetoothDevice>(BluetoothDevice.EXTRA_DEVICE)
                Log.e("TAG_error", "onReceive: " + device?.name)
                deviceListAdapter?.let { adapter ->
                    device?.let { device ->
                        adapter.bluetoothDeviceList.add(device)
                        adapter.bluetoothDeviceList = adapter.bluetoothDeviceList
                    }
                    adapter.notifyDataSetChanged()
                }
            }
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        try {
            unregisterReceiver(mReceiver)
            unregisterReceiver(receiver)

        }catch (e:Exception){}
    }


    // Create a BroadcastReceiver for ACTION_FOUND.
    private val receiver = object : BroadcastReceiver() {

        override fun onReceive(context: Context, intent: Intent) {
            val action: String? = intent.action
            Log.e("TAG_dd_1", "onReceive: SSS " + action)
            when (action) {
                BluetoothDevice.ACTION_FOUND -> {
                    // Discovery has found a device. Get the BluetoothDevice
                    // object and its info from the Intent.
                    val device: BluetoothDevice? =
                        intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE)
                    if (ActivityCompat.checkSelfPermission(
                            this@BluetoothActivity,
                            Manifest.permission.BLUETOOTH_CONNECT
                        ) != PackageManager.PERMISSION_GRANTED
                    ) {
                        // TODO: Consider calling
                        //    ActivityCompat#requestPermissions
                        // here to request the missing permissions, and then overriding
                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                        //                                          int[] grantResults)
                        // to handle the case where the user grants the permission. See the documentation
                        // for ActivityCompat#requestPermissions for more details.

                    }

                    deviceListAdapter?.let { adapter ->
                        device?.let { device ->
                            bluetoothDeviceList?.add(device)
                            bluetoothDeviceList?.let { callDeviceList(it) }
                            adapter.notifyDataSetChanged()
                        }
                    }
                    val deviceName = device?.name
                    val deviceHardwareAddress = device?.address // MAC address
                    Log.e("TAG_dd_1", "onReceive: " + deviceName)
                }
            }
        }
    }

    fun checkWriteFilePermission(){
        // Check if the app has permission to write to external storage
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
            != PackageManager.PERMISSION_GRANTED) {
            // If not, request the permission
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE
            )
        } else {
            // Permission already granted, proceed with file writing
            if(isClient) {
                startClientDevice()
            }
            else{
                startClientService()
            }
        }


    }

    // Handle the result of the permission request
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, proceed with file writing
                if(isClient) {
                    startClientDevice()
                }
                else{
                    startClientService()
                }
            } else {
                Toast.makeText(this, "Permission Required", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun takePhotoAndroidQ() {
        val outputDirectory = File(binding.root.context.getExternalFilesDir(Environment.DIRECTORY_PICTURES), "YourAppDirectoryName")
        outputDirectory.mkdirs()

        val photoFile = File(outputDirectory, "photo.jpg")

        val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()

        imageCapture?.takePicture(outputOptions, ContextCompat.getMainExecutor(binding.root.context), object : ImageCapture.OnImageSavedCallback {
            override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {

                file = File(outputFileResults.savedUri.toString())
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

            override fun onError(exception: ImageCaptureException) {
                Toast.makeText(binding.root.context, ""+exception.message, Toast.LENGTH_SHORT).show()
                // Handle error here
                //Log.e(TAG, "Photo capture failed: ${exception.message}", exception)
            }
        })
    }

    private fun getFilePathFromContentUriQ(context: Context, contentUri: Uri): String? {
        val cursor: Cursor? = context.contentResolver.query(contentUri, null, null, null, null)
        return cursor?.use {
            it.moveToFirst()
            val columnIndex = it.getColumnIndex(MediaStore.Images.Media.DATA)
            if (columnIndex != -1) {
                it.getString(columnIndex)
            } else {
                null
            }
        }
    }

    fun pickMediaFromDCIM() {

      //  val pickIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
      //  pickIntent.type = "image/*"  // You can set the MIME type for the type of media you want to pick
        val folderName = "DCIM"
        val folderUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI.buildUpon().appendPath(folderName).build()
        val pickIntent = Intent(Intent.ACTION_PICK, folderUri)
        pickIntent.type = "image/*"
        startActivityForResult(pickIntent, 400)
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 400 && resultCode == Activity.RESULT_OK) {
            data?.data?.let { selectedMediaUri ->
                val file: File? = uriToFile(binding.root.context, selectedMediaUri)


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

    @RequiresApi(Build.VERSION_CODES.Q)
    fun uriToFile(context: Context, uri: Uri): File? {
        val contentResolver = context.contentResolver
        val outputFile = createTempFilePath(context)
        try {
            contentResolver.openInputStream(uri)?.use { inputStream ->
                FileOutputStream(outputFile).use { outputStream ->
                    val buffer = ByteArray(4 * 1024) // 4K buffer size
                    var read: Int
                    while (inputStream.read(buffer).also { read = it } != -1) {
                        outputStream.write(buffer, 0, read)
                    }
                    outputStream.flush()
                    return outputFile
                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return null
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun createTempFilePath(context: Context): File {
        val fileName = "temp_file_" + System.currentTimeMillis()
        val storageDir = context.cacheDir
        val filePath = File(storageDir, fileName)
        return filePath
    }
}



