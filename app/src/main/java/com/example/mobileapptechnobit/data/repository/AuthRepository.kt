package com.example.sisteminformasimenejemensatpam.data.repository

import com.example.sisteminformasimenejemensatpam.data.API.ApiClient
import okhttp3.ResponseBody
import retrofit2.Response

class AuthRepository {
    private val api = ApiClient.apiService

    suspend fun requestOtp(email: String): Response<ResponseBody> {
        val emailMap = hashMapOf("email" to email)
        return api.requestOtp(emailMap)
    }

    suspend fun verifyOtp(email: String, otp: String): Response<ResponseBody>{
        val map = hashMapOf("email" to email, "otp" to otp)
        return api.verifyOtp(map)
    }

    suspend fun resetPassword(email: String, newPassword: String): Response<ResponseBody> {
        val request = hashMapOf(
            "email" to email,
            "password" to newPassword,
            "password_confirmation" to newPassword
        )
        return api.resetPassword(request)
    }
}