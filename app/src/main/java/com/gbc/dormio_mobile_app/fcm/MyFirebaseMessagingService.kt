package com.gbc.dormio_mobile_app.fcm

import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.gbc.dormio_mobile_app.network.AuthApiService
import com.gbc.dormio_mobile_app.network.FcmTokenManager
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MyFirebaseMessagingService : FirebaseMessagingService() {

    @Inject
    lateinit var fcmTokenManager: FcmTokenManager

    override fun onMessageReceived(remoteMessage: RemoteMessage){
        remoteMessage.notification?.let{notification ->
            sendNotification(
                notification.title ?: "Dormio Notification",
                notification.body ?: "")
        }
    }

    private fun sendNotification(title: String?, message: String?) {
        val channelId = "maintenance_channel"

        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            val channel = NotificationChannel(
                channelId,
                "Maintenance Notifications",
                NotificationManager.IMPORTANCE_HIGH
            )
            notificationManager.createNotificationChannel(channel)
        }

        val notification = NotificationCompat.Builder(this, channelId)
            .setContentTitle(title)
            .setContentText(message)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .build()
        notificationManager.notify(System.currentTimeMillis().toInt(), notification)
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)

        fcmTokenManager.saveTokenLocally(applicationContext, token)

        CoroutineScope(Dispatchers.IO).launch {
            try {
                fcmTokenManager.sendTokenToServer(token)
            }catch (e: Exception) {
                Log.e("FCM", "Failed to send token to server: ${e.message}")
            }
        }

    }

}