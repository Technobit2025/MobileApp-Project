package com.example.mobileapptechnobit.data.repository

import android.util.Log
import com.example.mobileapptechnobit.data.API.ApiClient
import com.example.mobileapptechnobit.data.remote.CheckNfcRequest
import com.example.mobileapptechnobit.data.remote.CheckPatrolSpotResponse
import com.example.mobileapptechnobit.data.remote.Resource
import org.json.JSONObject

class CheckPatrolSpotRepository {
    suspend fun checkPatrolSpotByNfcUid(
        token: String,
        nfcTagUid: String
    ): Resource<CheckPatrolSpotResponse> {
        return try {
            val response = ApiClient.apiService.checkPatrolSpot(
                token = "Bearer $token",
                nfcTagUid = CheckNfcRequest(nfcTagUid = nfcTagUid)
            )
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null) {
                    Resource.Success(body)
                } else {
                    Resource.Error("Empty response body")
                }
            } else {
                val errorJson = response.errorBody()?.string()
                val errorMessage = try {
                    val jsonObj = JSONObject(errorJson!!)
                    jsonObj.getString("message")
                } catch (e: Exception) {
                    "Unknown error"
                }
                Log.d("errorMessageNotFound",errorMessage)
                Resource.Error(errorMessage)
            }
        } catch (e: Exception) {
            Resource.Error(e.localizedMessage ?: "Unexpected error")
        }
    }

}
