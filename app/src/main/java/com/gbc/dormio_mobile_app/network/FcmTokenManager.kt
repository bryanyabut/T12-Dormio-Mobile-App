package com.gbc.dormio_mobile_app.network

import android.content.Context
import android.util.Log
import com.gbc.dormio_mobile_app.data.model.DeviceTokenRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FcmTokenManager @Inject constructor(private val authApiService: AuthApiService) {

    private val prefsName = "dormio_prefs"
    private val keyToken = "fcm_token"
    private val keyLastSent = "last_sent_token"

    fun saveTokenLocally(context: Context, token: String) {
        val prefs = context.getSharedPreferences(prefsName, Context.MODE_PRIVATE)
        prefs.edit().putString(keyToken, token).apply()
    }

    fun getToken(context: Context): String? {
        val prefs = context.getSharedPreferences(prefsName, Context.MODE_PRIVATE)
        return prefs.getString(keyToken, null)
    }

    suspend fun resendTokenIfAvailable(context: Context) {
        val prefs = context.getSharedPreferences(prefsName, Context.MODE_PRIVATE)
        val token = getToken(context) ?: return
        val lastSent = prefs.getString(keyLastSent, null)

        if (token == lastSent) {
            Log.d("FCM", "Token already sent, skipping resend")
            return
        }

        // Retry up to 3 times
        repeat(3) { attempt ->
            try {
                sendTokenToServer(token)
                prefs.edit().putString(keyLastSent, token).apply()
                Log.d("FCM", "Token successfully sent to server")
                return
            } catch (e: Exception) {
                Log.e("FCM", "Attempt ${attempt + 1} failed: ${e.message}")
            }
        }
        Log.e("FCM", "Failed to send FCM token after 3 attempts")
    }

    private suspend fun sendTokenToServer(token: String) = withContext(Dispatchers.IO) {
        val request = DeviceTokenRequest(deviceToken = token)
        val response = authApiService.updateDeviceToken(request)
        if (!response.isSuccessful) {
            throw Exception("Failed to send device token: ${response.code()} ${response.message()}")
        }
    }
}