package com.bis.mytestbiswajit.viewModel

import android.net.Uri
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel



class MainViewModel :ViewModel(){

    var filePath: MutableLiveData<Uri> = MutableLiveData()
}