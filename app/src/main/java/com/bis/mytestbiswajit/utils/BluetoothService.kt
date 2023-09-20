package com.bis.mytestbiswajit.utils

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothServerSocket
import android.bluetooth.BluetoothSocket
import android.content.Context
import android.content.pm.PackageManager
import android.os.Handler
import android.os.Message
import android.util.Log
import androidx.core.app.ActivityCompat
import com.bis.mytestbiswajit.utils.FileUtil.getDownloadFolder
import com.bis.mytestbiswajit.utils.MyConstants.STATIC_OBJ.FILE_NAME
import com.bis.mytestbiswajit.utils.MyConstants.STATIC_OBJ.STATE_CONNECTED
import com.bis.mytestbiswajit.utils.MyConstants.STATIC_OBJ.STATE_CONNECTING
import com.bis.mytestbiswajit.utils.MyConstants.STATIC_OBJ.STATE_CONNECTION_FAILED
import com.bis.mytestbiswajit.utils.MyConstants.STATIC_OBJ.UUID_VALUE
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.util.UUID


class BluetoothService(
    val adapter: BluetoothAdapter,
    val handler: Handler,
    val ctx: Context,
    val file: BluetoothReceiveFile
):Thread() {
     var sendRecever: SendRecever?=null

    inner class ServerClass:Thread(){
       private var serverSocket:BluetoothServerSocket?=null
        override fun run() {
            super.run()
            var socket:BluetoothSocket?=null
            while (socket==null){
                try {
                    val message=Message.obtain()
                    message.what=STATE_CONNECTING
                    handler.sendMessage(message)
                    socket=serverSocket?.accept()
                }catch (io:IOException){
                    val message=Message.obtain()
                    message.what=STATE_CONNECTION_FAILED
                    handler.sendMessage(message)
                }
                catch (e:Exception){
                    val message=Message.obtain()
                    message.what=STATE_CONNECTION_FAILED
                    handler.sendMessage(message)
                }
                if(socket!=null){
                    val message=Message.obtain()
                    message.what= STATE_CONNECTED
                    handler.sendMessage(message)
                    sendRecever= SendRecever(socket)
                    sendRecever?.start()
                    break
                }
            }

        }
        init{
            try{
                if (ActivityCompat.checkSelfPermission(
                        ctx,
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
                serverSocket=adapter.listenUsingRfcommWithServiceRecord("Chat App", UUID.fromString(UUID_VALUE))
            }catch (io:IOException){

            }catch (e:Exception){

            }
        }




    }


    inner class ClientClass(private val bluetoothDevice: BluetoothDevice):Thread(){
        private var socket:BluetoothSocket?=null
        override fun run() {
            super.run()
            try {
                if (ActivityCompat.checkSelfPermission(
                        ctx,
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
                socket?.let {

                    it.connect()
                    val message = Message.obtain()
                    message.what = STATE_CONNECTED
                    handler.sendMessage(message)
                    sendRecever = SendRecever(it)
                    sendRecever?.start()
                }
            }catch (io:IOException){
                val message=Message.obtain()
                message.what= STATE_CONNECTION_FAILED
                handler.sendMessage(message)

            }
            catch (e:Exception){
                val message=Message.obtain()
                message.what= STATE_CONNECTION_FAILED
                handler.sendMessage(message)

            }
        }
        init {
            try {
                if (ActivityCompat.checkSelfPermission(
                        ctx,
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
                socket=bluetoothDevice.createRfcommSocketToServiceRecord(UUID.fromString(UUID_VALUE))
            }catch (io:IOException){}
            catch (e:Exception){}
        }


    }
    inner class SendRecever(private val bluetoothSocket: BluetoothSocket?):Thread(){
        private  var inputStream:InputStream?=null
        private  var outputStream:OutputStream?=null

        override fun run() {
            super.run()
            /*val buffer=ByteArray(1024)
            var bytes:Int?=null
            while (true){
                try {
                    bytes= inputStream?.read(buffer)
                    if (bytes != null) {
                        handler.obtainMessage(STATE_NESSAGE_RECEIVED,bytes,-1,buffer).sendToTarget()
                    }
                }catch (io:IOException){}
                catch (e:Exception){}*/


            val buffer = ByteArray(1024)
            var bytesRead: Int

            val folder = File(ctx.getExternalFilesDir(null), "bluetoothCheck")
            if (folder != null && !folder.exists()) folder.mkdirs()
            val receivedFile = File(folder, "received_file")
            try {
                while (true) {
                    bytesRead = inputStream?.read(buffer) ?: -1
                    if (bytesRead == -1) {
                        Log.d("TAG_folderkolkata", "run:complete 2")
                        break
                    }
                    Log.d("TAG_folderkolkata", "run:complete 1")
                    Log.d("TAG_folder", "run: "+receivedFile)
                    // Write the data to a file
                    writeDataToFile(buffer, bytesRead,receivedFile)
                    //handler.obtainMessage(STATE_NESSAGE_RECEIVED,0,-1,0).sendToTarget()
                }
            } catch (io: IOException) {
                Log.d("TAG_hh", "Error receiving file: ${io.message}")
            } catch (e: Exception) {
                Log.d("TAG_hh", "Error receiving file: ${e.message}")
            }


            }

        private fun writeDataToFile(data: ByteArray, length: Int, receivedFile: File) {
            try {
                // Create or open the file for writing



                val folder = getDownloadFolder(ctx)//File(ctx.getExternalFilesDir(null), "bluetoothCheck")
                //if (folder != null && !folder.exists()) folder.mkdirs()
                val receivedFile = File(folder, FILE_NAME)
                Log.d("TAG_file_path", "writeDataToFile: $receivedFile")
                val fileOutputStream = FileOutputStream(receivedFile, true)

                // Write the received data to the file
                fileOutputStream.write(data, 0, length)

                // Close the file output stream
                fileOutputStream.close()
                file.getFile(receivedFile)
            } catch (io: IOException) {
                Log.d("TAG_hh", "Error writing data to file: ${io.message}")
            } catch (e: Exception) {
                Log.d("TAG_hh", "Error writing data to file: ${e.message}")
            }
        }


        fun write(byte: ByteArray){
            try {
                outputStream?.write(byte)
            }catch (io:IOException){
                Log.d("TAG_hh", "write: A")
            }
            catch (e:Exception){
                Log.d("TAG_hh", "write: B")
            }
        }
        init {
            var tempIn:InputStream?=null
            var tempOut:OutputStream?=null
            try {
                bluetoothSocket?.let {
                    tempIn=it.inputStream
                    tempOut=it.outputStream
                }

            }   catch (io:IOException){}
            catch (e:Exception){}

            tempIn?.let{

            }
            tempIn?.let {
                inputStream =it
            }
            tempOut?.let {
                outputStream=it
            }

        }


    }
}