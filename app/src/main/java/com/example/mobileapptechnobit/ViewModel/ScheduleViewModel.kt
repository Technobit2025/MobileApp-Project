package com.example.mobileapptechnobit.ViewModel

import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mobileapptechnobit.data.API.ApiClient
import com.example.mobileapptechnobit.data.remote.PatrolScheduleResponse
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException

class ScheduleViewModel : ViewModel() {

    var scheduleList by mutableStateOf<List<PatrolScheduleResponse>>(emptyList())
        private set

    var isLoading by mutableStateOf(false)
        private set

    var errorMessage by mutableStateOf<String?>(null)
        private set

    fun fetchSchedules(token: String, date: String? = null, companyId: Int? = null) {
        viewModelScope.launch {
            isLoading = true
            errorMessage = null

            try {
                val response = ApiClient.apiService.getPatrolSchedules(
                    token = "Bearer $token",
                    date = date,
                    companyId = companyId
                )
                if (response.isSuccessful) {
                    val body = response.body()
                    scheduleList = body?.data ?: emptyList()
                } else {
                    errorMessage = "Gagal mengambil data: ${response.code()}"
                }
            } catch (e: IOException) {
                errorMessage = "Network error: ${e.message}"
            } catch (e: HttpException) {
                errorMessage = "Server error: ${e.message}"
            } finally {
                isLoading = false
            }
        }
    }
}
