package com.bis.mytestbiswajit.network


import com.bis.Expence.data.model.MistoryDetails
import com.bis.mytestbiswajit.model.OtherDetails
import okhttp3.Call
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface ApiInterface {
    @FormUrlEncoded
    @POST("b.php")
    suspend fun getDataValue(@Field("action")  param1:String) : retrofit2.Response<OtherDetails>

    @Multipart
    @POST("upload.php")
    suspend fun upload2(@Part file: MultipartBody.Part) : retrofit2.Response<OtherDetails>

    @Multipart
    @POST("b.php")
    fun uploadFile(
        @Part file: MultipartBody.Part,
        @Part("description") description: RequestBody
    ): retrofit2.Response<OtherDetails>


    @Multipart
    @POST("upload.php")
    suspend fun upload3(@Body request: String) : retrofit2.Response<OtherDetails>


}