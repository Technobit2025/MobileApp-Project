package com.example.sisteminformasimenejemensatpam.ViewModel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sisteminformasimenejemensatpam.data.repository.AuthRepository
import kotlinx.coroutines.launch

class AuthViewModel(private val repository: AuthRepository): ViewModel(){

    private val _verifyOtpMessage = MutableLiveData<String>()
    val verifyOtpMessage: LiveData<String> get() = _verifyOtpMessage

    private val _requestOtpMessage = MutableLiveData<String>()
    val requestOtpMessage: LiveData<String> get() = _requestOtpMessage

    private val _resetPasswordMessage = MutableLiveData<String>()
    val resetPasswordMessage: LiveData<String> get() = _resetPasswordMessage

    private val _isOtpVerified = MutableLiveData<Boolean>()
    val isOtpVerified: LiveData<Boolean> get() = _isOtpVerified

    fun requestOtp(email: String) {
        viewModelScope.launch {
            try {
                val response = repository.requestOtp(email)
                if (response.isSuccessful) {
                    val responseBody = response.body()?.string() ?: "Empty response"
                    Log.d("ForgotPassword", "Response: $responseBody")
                    _requestOtpMessage.value = "Link telah dikirim. Silakan cek email anda!"
                } else {
                    val errorBody = response.errorBody()?.string() ?: "Unknown error"
                    Log.e("ForgotPassword", "Failed: ${response.code()}, Error: $errorBody")
                    _requestOtpMessage.value = "Link gagal terkirim"
                }
            } catch (e: Exception) {
                Log.e("ForgotPassword", "Error: ${e.message}")
                _requestOtpMessage.postValue("Error: ${e.message}")

            }
        }
    }

    fun verifyOtp(email: String, otp: String){
        viewModelScope.launch {
            try {
                val response = repository.verifyOtp(email, otp)
                if(response.isSuccessful){
                    val responseBody = response.body()?.string() ?: "Empty response"
                    Log.d("VerifyOtp", "Response: $responseBody")
                    _verifyOtpMessage.value = "Verifikasi berhasil!"
                    _isOtpVerified.value = true
                } else{
                    val errorBody = response.errorBody()?.string() ?: "Unknown error"
                    Log.e("ForgotPassword", "Failed: ${response.code()}, Error: $errorBody")
                    _verifyOtpMessage.value = "Verifikasi gagal!"
                }
            } catch (e: Exception){
                Log.e("ForgotPassword", "Error: ${e.message}")
                _verifyOtpMessage.postValue("Error: ${e.message}")
            }
        }
    }

    fun resetPassword(email: String, newPassword: String) {
        viewModelScope.launch {
            try {
                val response = repository.resetPassword(email, newPassword)
                if (response.isSuccessful) {
                    val responseBody = response.body()?.string() ?: "Empty response"
                    Log.d("ResetPassword", "Response: $responseBody")
                    _resetPasswordMessage.value = ("Password berhasil diperbarui!")
                } else {
                    val errorBody = response.errorBody()?.string() ?: "Unknown error"
                    Log.e("ResetPassword", "Failed: ${response.code()}, Error: $errorBody")
                    _resetPasswordMessage.value = ("Gagal: $errorBody")
                }
            } catch (e: Exception) {
                Log.e("ResetPassword", "Exception: ${e.message}")
                _resetPasswordMessage.postValue("Error: ${e.message}")
            }
        }
    }
}