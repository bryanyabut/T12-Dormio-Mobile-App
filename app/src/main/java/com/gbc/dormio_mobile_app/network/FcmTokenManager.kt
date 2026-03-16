package com.gbc.dormio_mobile_app.network

import android.content.Context
import com.gbc.dormio_mobile_app.data.model.DeviceTokenRequest
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FcmTokenManager @Inject constructor(private val authApiService: AuthApiService) {
    fun saveTokenLocally(context: Context, token: String){
        val prefs = context.getSharedPreferences("dormio_prefs", Context.MODE_PRIVATE)
        prefs.edit().putString("fcm_token", token).apply()
    }
    fun getToken(context: Context): String? {
        val prefs = context.getSharedPreferences("dormio_prefs", Context.MODE_PRIVATE)
        return prefs.getString("fcm_token", null)
    }

    suspend fun sendTokenToServer(token: String) {
        val request = DeviceTokenRequest(deviceToken = token)
        val response = authApiService.updateDeviceToken(request)
        if (!response.isSuccessful) {
            throw Exception("Failed to update device token: ${response.code()} ${response.message()}")
        }
    }
}