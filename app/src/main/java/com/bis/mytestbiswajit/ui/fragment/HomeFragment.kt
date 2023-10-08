package com.bis.mytestbiswajit.ui.fragment

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.bis.mytestbiswajit.R
import com.bis.mytestbiswajit.databinding.FragmentHomeBinding
import com.bis.mytestbiswajit.ui.activity.MainActivity
import com.bis.mytestbiswajit.ui.base.BaseFragment
import com.bis.mytestbiswajit.utils.MyConstants.STATIC_OBJ.isVideo
import com.bis.mytestbiswajit.network.viewModel.MainViewModel


class HomeFragment : BaseFragment() {
    lateinit var binding: FragmentHomeBinding
    private val mainViewModel: MainViewModel by activityViewModels()


    @RequiresApi(Build.VERSION_CODES.P)
    val pickMedia = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->

        if (uri != null) {
            mainViewModel.filePath.value = uri
            findNavController().navigate(R.id.action_homeFragment_to_previewFragment)
        } else {
            Log.d("PhotoPicker", "No media selected")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false)

        return binding.root
    }


    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
        onViewClick()
    }

    fun init() {


    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    fun onViewClick() {
        binding.btnGalary.setOnClickListener {
            isVideo = false

           pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))

        }

        binding.btnCaptureImage.setOnClickListener {
            isVideo = false
            checkPermission()
        }
        binding.btnBlueTooth.setOnClickListener {

            (activity as? MainActivity)?.goToBluetooth(null)
        }


        binding.btnVideo.setOnClickListener {
            isVideo = true

            checkPermission()
        }

    }
    fun checkPermission(){
        if (Build.VERSION.SDK_INT >= 30 && !Environment.isExternalStorageManager()) {
            /*val getpermission = Intent()
            getpermission.action = Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION
            startActivity(getpermission)*/
            (activity as? MainActivity)?.dialogAllFileAccessPermissionAbove30()
        } else {
            if (ActivityCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ) != PackageManager.PERMISSION_GRANTED
            ) {

                ActivityCompat.requestPermissions(
                    requireActivity(),
                    arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                    3
                )
            }
            findNavController().navigate(R.id.action_homeFragment_to_cameraFragment)
        }
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



}