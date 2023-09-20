package com.bis.mytestbiswajit.ui.adapter

import android.Manifest
import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice
import android.content.Context
import android.content.pm.PackageManager
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bis.mytestbiswajit.R
import com.bis.mytestbiswajit.databinding.LayoutdeviceListBinding
import com.bis.mytestbiswajit.utils.DeviceNameOnClickListner


class DeviceListAdapter(
    private val context: Context,
    private val bluetoothDeviceList: List<BluetoothDevice>,
    private val deviceNameOnClickListner: DeviceNameOnClickListner
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {


    inner class ViewHolder(val rowBinding: LayoutdeviceListBinding) :
        RecyclerView.ViewHolder(rowBinding.root) {
        @SuppressLint("SetTextI18n")
        fun bind(bluetoothDevice: BluetoothDevice) {
            rowBinding.apply {
                if (ActivityCompat.checkSelfPermission(
                        context,
                        Manifest.permission.BLUETOOTH_CONNECT
                    ) != PackageManager.PERMISSION_GRANTED
                ) {

                }
                tvDevice.apply {
                    text = bluetoothDevice.name.toString()
                    tvDevice.setOnClickListener {
                        deviceNameOnClickListner.showDevice(bluetoothDevice)
                    }
                }
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ViewHolder(
            DataBindingUtil.inflate(
                LayoutInflater.from(context),
                R.layout.layoutdevice_list,
                parent,
                false
            )
        )

    }

    override fun getItemCount(): Int {
        return bluetoothDeviceList.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as ViewHolder).bind(bluetoothDeviceList[position])
    }
}