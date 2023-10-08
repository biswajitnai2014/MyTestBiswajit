package com.bis.mytestbiswajit.ui.fragment

import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.net.toUri
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import com.bis.mytestbiswajit.R
import com.bis.mytestbiswajit.databinding.FragmentUplodeFragmentBinding
import com.bis.mytestbiswajit.network.viewModel.MainViewModel
import com.bis.mytestbiswajit.ui.base.BaseFragment
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream


class UplodeFragmentFragment : BaseFragment() {
    lateinit var binding: FragmentUplodeFragmentBinding
    private val mainViewModel: MainViewModel by activityViewModels()
    var path: Uri?=null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_uplode_fragment, container, false)
        initFun()
        observer()
        viewOnClick()
        return binding.root
    }

    private fun viewOnClick() {
        binding.linearFileUplode.setOnClickListener {
            if (path!=null){
                try {
                    /*createFileFromUri(requireContext(), path!!)*/
                    val file = createFileFromUri(requireContext(), path!!)
                    if (file?.exists() == true){
                        binding.src.setImageURI(file.toUri())
                        val requestFile = RequestBody.create("multipart/form-data".toMediaTypeOrNull(), file)
                        val body = MultipartBody.Part.createFormData("file", file.name, requestFile)
                        val description = RequestBody.create("multipart/form-data".toMediaType(), "File description")

                        mainViewModel.upload(body,description)

                       // mainViewModel.uploadbase64String()
                    }
                    else{
                        Toast.makeText(requireActivity(), "tttt", Toast.LENGTH_SHORT).show()
                    }

                } catch (e: FileNotFoundException) {
                    // Handle the FileNotFoundException here
                    e.printStackTrace()
                    Toast.makeText(requireActivity(), ""+e.message, Toast.LENGTH_SHORT).show()
                }
                catch (e:Exception){
                    Toast.makeText(requireActivity(), ""+e.message, Toast.LENGTH_SHORT).show()

                }

            }else{
                Toast.makeText(requireActivity(), "It is null", Toast.LENGTH_SHORT).show()
            }



        }
    }

    private fun initFun() {

       // mainViewModel.getOther()
    }
    private fun observer() {
        mainViewModel.filePath.observe(viewLifecycleOwner) {
            path = it

        }
        mainViewModel.otherExpenceList.observe(viewLifecycleOwner){
            Toast.makeText(requireActivity(), ""+it.name, Toast.LENGTH_SHORT).show()
           /* binding.regExp.adapter= ExpenceAdapter(it.otherDataValue)
            binding.regExp.isFocusable = false*/
        }

        mainViewModel.uploadResponse.observe(viewLifecycleOwner){
            Toast.makeText(requireActivity(), "File "+it.name, Toast.LENGTH_SHORT).show()
            /* binding.regExp.adapter= ExpenceAdapter(it.otherDataValue)
             binding.regExp.isFocusable = false*/
        }
        mainViewModel.uploadbase64StringResponse.observe(viewLifecycleOwner){
            Toast.makeText(requireActivity(), "File "+it.name, Toast.LENGTH_SHORT).show()
            /* binding.regExp.adapter= ExpenceAdapter(it.otherDataValue)
             binding.regExp.isFocusable = false*/
        }

    }

    fun createFileFromUri(context: Context, uri: Uri): File? {
        try {
            val contentResolver: ContentResolver = context.contentResolver
            val inputStream: InputStream? = contentResolver.openInputStream(uri)
            if (inputStream != null) {
                val outputFile: File
                val fileDescriptor = contentResolver.openFileDescriptor(uri, "r")
                if (fileDescriptor != null) {
                    // Create a file with a unique name in the app's cache directory
                    outputFile = File(context.cacheDir, "temp_file")
                    val outputStream = FileOutputStream(outputFile)
                    val buffer = ByteArray(4 * 1024) // 4KB buffer size
                    var bytesRead: Int
                    while (inputStream.read(buffer).also { bytesRead = it } != -1) {
                        outputStream.write(buffer, 0, bytesRead)
                    }
                    outputStream.close()
                    inputStream.close()
                    return outputFile
                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return null
    }


}