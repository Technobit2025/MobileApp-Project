package com.example.mobileapptechnobit.ViewModel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mobileapptechnobit.data.remote.CheckPatrolSpotResponse
import com.example.mobileapptechnobit.data.remote.Resource
import com.example.mobileapptechnobit.data.repository.CheckPatrolSpotRepository
import com.example.mobileapptechnobit.data.repository.PermissionRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class PatroliNfcViewModel(private val repository: CheckPatrolSpotRepository) :
    ViewModel() {
    private val _checkPatrolSpotResponse = MutableStateFlow<Resource<CheckPatrolSpotResponse>>(Resource.Idle())
    val checkPatrolSpotResponse= _checkPatrolSpotResponse

    fun getPatrolSpotDataByNfcUid(token: String, nfcTagUid: String) {
        Log.d("ViewModelGetPatrolSpot", nfcTagUid)
        viewModelScope.launch(Dispatchers.IO) {
            _checkPatrolSpotResponse.value = Resource.Loading()
            _checkPatrolSpotResponse.value = repository.checkPatrolSpotByNfcUid(
                token = token,
                nfcTagUid = nfcTagUid
            )
        }
    }
}