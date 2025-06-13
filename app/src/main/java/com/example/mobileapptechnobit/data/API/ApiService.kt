package com.example.mobileapptechnobit.data.API

import com.example.mobileapptechnobit.data.remote.AlternatePermissionResponse
import com.example.mobileapptechnobit.data.remote.ClockOutRequest
import com.example.mobileapptechnobit.data.remote.CompanyProfileResponse
import com.example.mobileapptechnobit.data.remote.EmployeeResponse
import com.example.mobileapptechnobit.data.remote.HistoryPatroliResponse
import com.example.mobileapptechnobit.data.remote.Presensi
import com.example.mobileapptechnobit.data.remote.PresensiResponse
import com.example.mobileapptechnobit.data.remote.Permission
import com.example.mobileapptechnobit.data.remote.HistoryPresensiResponseItem
import com.example.mobileapptechnobit.data.remote.PatrolScheduleWrapper
import com.example.mobileapptechnobit.data.remote.PatroliRequest
import com.example.mobileapptechnobit.data.remote.PermissionResponse
import com.example.mobileapptechnobit.data.remote.SalaryDetailResponse
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.HTTP
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Query

interface ApiService{

    // API autentikasi
    @Headers("Accept: application/json", "Content-Type: application/json")
    @POST("v1/login")
    suspend fun login(@Body request: HashMap<String, String>): Response<ResponseBody>

    @POST("v1/android/request-otp")
    suspend fun requestOtp(@Body email: HashMap<String, String>): Response<ResponseBody>

    @POST("v1/android/reset-password")
    suspend fun resetPassword(@Body request: HashMap<String, String>): Response<ResponseBody>

    @POST("v1/android/verify-otp")
    suspend fun verifyOtp(@Body request: HashMap<String, String>): Response<ResponseBody>

    @PUT("v1/android/change-password")
    suspend fun changePassword(@Header("Authorization") authorization: String, @Body request: HashMap<String, String>): Response<ResponseBody>


    // API user & employee
    @GET("v1/profile/employee")
    suspend fun fetchEmployeeProfile(@Header("Authorization") token: String): Response<EmployeeResponse>

    @PUT("v1/profile/employee")
    suspend fun updateEmployeeProfile(@Header("Authorization") authorization: String, @Body request: Map<String, String>): Response<Unit>

    @GET("v1/profile/user")
    suspend fun getUserProfile(@Header("Authorization") token: String): Response<UserProfileResponse>


    // API Presensi
    @POST("v1/android/presensi")
    suspend fun sendPresensi(@Header("Authorization") token: String, @Body requestBody: Presensi): Response<PresensiResponse> // Untuk Clock In

    @POST("v1/android/presensi")
    suspend fun sendClockOutPresensi(@Header("Authorization") token: String, @Body requestBody: ClockOutRequest): Response<PresensiResponse>


    // API Perizinan
    @POST("v1/android/perizinan")
    suspend fun sendPermission(@Header("Authorization") authorization: String, @Body request: HashMap<String, String>): Response<ResponseBody>

    @HTTP(method = "DELETE", path = "v1/android/perizinan", hasBody = true)
    suspend fun deletePermission(@Header("Authorization") authorization: String, @Body request: HashMap<String, Int>): Response<ResponseBody>

    @GET("v1/android/permits")
    suspend fun fetchPermission(@Header("Authorization") token: String): Response<PermissionResponse>

    @GET("v1/android/permits/alternate")
    suspend fun fetchAlternatePermission(@Header("Authorization") token: String): Response<AlternatePermissionResponse>

    @PUT("v1/android/permits/confirm")
    suspend fun sendApproval(@Header("Authorization") token: String, @Body request: HashMap<String, Any>): Response<ResponseBody>

    @PUT("v1/android/permits/alternate/approved")
    suspend fun sendAlternateApproval(@Header("Authorization") token: String, @Body request: HashMap<String, Any>): Response<ResponseBody>



    // API histori
    @GET("v1/android/history-presensi")
    suspend fun getHistoryPresensi(
        @Header("Authorization") token: String
    ): Response<List<HistoryPresensiResponseItem>> // Get Data by Gson Array

    @GET("v1/android/histori-patroli")
    suspend fun getHistoryPatroli(
        @Header("Authorization") token: String
    ): HistoryPatroliResponse // Get Data by Gson Object contains Array



    @GET("v1/android/company-profile")
    suspend fun getCompanyProfile(@Header("Authorization") token: String): Response<CompanyProfileResponse>

    @GET("v1/android/schedules")
    suspend fun getPatrolSchedules(
        @Header("Authorization") token: String,
        @Query("date") date: String? = null,
        @Query("company_id") companyId: Int? = null
    ): Response<PatrolScheduleWrapper>

    @POST("v1/android/patroli")
    suspend fun submitPatroli(@Header("Authorization") token: String, @Body request: PatroliRequest): Response<ResponseBody>

    @GET("v1/android/salary-detail")
    suspend fun getSalaryDetail(
        @Header("Authorization") token: String
    ): Response<SalaryDetailResponse>

}