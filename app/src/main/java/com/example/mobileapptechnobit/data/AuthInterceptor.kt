package com.example.sisteminformasimenejemensatpam.data

import android.content.Context
import android.util.Log
import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor(private val context: Context) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val sharedPref = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val token = sharedPref.getString("AUTH_TOKEN", null)

        Log.d("AuthInterceptor", "Token Retrieved: $token")

        val request = chain.request().newBuilder()
            .apply {
                if (token != null) {
                    addHeader("Authorization", "Bearer $token")
                }
            }
            .build()

        return chain.proceed(request)
    }
}