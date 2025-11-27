package com.example.mobileapptechnobit.ViewModel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.mobileapptechnobit.data.repository.CheckPatrolSpotRepository
import com.example.mobileapptechnobit.data.repository.LocationRepository



class PatroliNfcViewModelFactory(private val repository: CheckPatrolSpotRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PatroliNfcViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return PatroliNfcViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}