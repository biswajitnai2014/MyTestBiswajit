package com.bis.mytestbiswajit.network.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.bis.mytestbiswajit.network.repository.BaseRepository
import com.bis.mytestbiswajit.network.repository.MainRepository

class ViewModelFactory(private val repository: BaseRepository):ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {

        return when {
            modelClass.isAssignableFrom(MainViewModel::class.java) -> MainViewModel(
                repository as MainRepository
            ) as T

            else -> throw IllegalArgumentException("ViewModelClass Not found")
        }



    }
}