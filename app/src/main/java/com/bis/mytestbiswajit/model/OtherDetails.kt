package com.bis.mytestbiswajit.model

import com.google.gson.annotations.SerializedName

data class OtherDetails(
    @SerializedName("name") var name: String? = null,
    @SerializedName("email") var email: String? = null,
    @SerializedName("dataValue") var dataValue: ArrayList<DataValue> = arrayListOf()
    )

data class DataValue(@SerializedName("title") var title: String? = null)