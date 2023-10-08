package com.bis.mytestbiswajit.network.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.bis.mytestbiswajit.model.OtherDetails
import com.bis.mytestbiswajit.network.ApiInterface
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody

class MainRepository(private val apiInterface: ApiInterface):BaseRepository() {


    private val _otherDetails=MutableLiveData<OtherDetails>()
    val otherDetails : LiveData<OtherDetails> get()=_otherDetails
    suspend fun getOther() {
        val result=apiInterface.getDataValue("A")
        if (result.body()!=null){
            _otherDetails.postValue(result.body())
        }
    }


    private val _uploadDetails=MutableLiveData<OtherDetails>()
    val uploadDetails : LiveData<OtherDetails> get()=_uploadDetails
    suspend fun upLoad(body: MultipartBody.Part, description: RequestBody) {

        val result=apiInterface.upload2(body)
        if (result.body()!=null){
            _uploadDetails.postValue(result.body())
        }
    }



    private val _base64Details=MutableLiveData<OtherDetails>()
    val base64Details : LiveData<OtherDetails> get()=_uploadDetails
    suspend fun getBase64Details(base64String: String) {

        val result=apiInterface.upload3(base64String)
        if (result.body()!=null){
            _base64Details.postValue(result.body())
        }
    }





}