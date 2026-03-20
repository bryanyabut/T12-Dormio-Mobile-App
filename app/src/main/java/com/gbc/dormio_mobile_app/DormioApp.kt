package com.gbc.dormio_mobile_app

import android.app.Application
import android.util.Log
import com.gbc.dormio_mobile_app.network.FcmTokenManager
import com.google.firebase.FirebaseApp
import com.google.firebase.messaging.FirebaseMessaging
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltAndroidApp
class DormioApp : Application() {

    @Inject lateinit var fcmTokenManager: FcmTokenManager

    override fun onCreate() {
        super.onCreate()
        FirebaseApp.initializeApp(this)

        //fetch token and save locally and send to server
        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.w("FCM", "Fetching FCM token failed", task.exception)
                return@addOnCompleteListener
            }

            val token = task.result ?: return@addOnCompleteListener
            Log.d("FCM", "Fetched FCM token: $token")

            fcmTokenManager.saveTokenLocally(applicationContext, token)

            CoroutineScope(Dispatchers.IO).launch {
                try {
                    fcmTokenManager.resendTokenIfAvailable(applicationContext)
                } catch (e: Exception) {
                    Log.e("FCM", "Failed to send FCM token: ${e.message}")
                }
            }
        }
    }
}