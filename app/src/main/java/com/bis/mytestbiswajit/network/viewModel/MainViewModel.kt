package com.bis.mytestbiswajit.network.viewModel

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bis.mytestbiswajit.model.OtherDetails
import com.bis.mytestbiswajit.network.repository.MainRepository
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import okhttp3.RequestBody


class MainViewModel(private val repository: MainRepository) : ViewModel(){

    var filePath: MutableLiveData<Uri> = MutableLiveData()

    fun getOther(){ viewModelScope.launch {repository.getOther() }}
    val otherExpenceList: LiveData<OtherDetails> get()=repository.otherDetails


    fun upload(body: MultipartBody.Part, description: RequestBody) { viewModelScope.launch {repository.upLoad(body,description)}}
    val uploadResponse: LiveData<OtherDetails> get()=repository.uploadDetails

    fun uploadbase64String(base64String: String) { viewModelScope.launch {repository.getBase64Details(base64String)}}
    val uploadbase64StringResponse: LiveData<OtherDetails> get()=repository.base64Details


}