package com.bis.mytestbiswajit.network

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiUtility {
    val baseUrl="http://192.168.0.190/test/api/"

    fun getInstance() : Retrofit{
        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(OkHttpClient.Builder().also { client ->
                //if (BuildConfig.DEBUG) {
                    val logging = HttpLoggingInterceptor()
                    logging.setLevel(HttpLoggingInterceptor.Level.BODY)
                    client.addInterceptor(logging)

               // }
            }.build())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
}