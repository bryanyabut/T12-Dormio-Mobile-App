package com.gbc.dormio_mobile_app.fcm

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.gbc.dormio_mobile_app.data.model.maintenance.RequestStatus
import com.gbc.dormio_mobile_app.data.model.UserDto
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

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        Log.d("FCM", "Received message from: ${remoteMessage.data}")
        remoteMessage.data.takeIf { it.isNotEmpty() }?.let { data ->

            when(data["type"]) {
                "maintenance_update" -> {
                    val requestId = data["request_id"] ?: return
                    val statusString = data["status"] ?: return
                    val status = try {
                        RequestStatus.valueOf(statusString.uppercase())
                    } catch (e: IllegalArgumentException) { return }

                    val user = UserDto(
                        id = data["user_id"]?.toInt() ?: 0,
                        firstName = data["user_firstName"] ?: "",
                        lastName = data["user_lastName"] ?: "",
                        email = data["user_email"] ?: "",
                        role = data["user_role"] ?: ""
                    )

                    MaintenanceUpdateBus.post(requestId, status, user)

                    //show notification to student
                    val title = data["title"] ?: "Maintenance Update"
                    val message = data["message"] ?: "Your maintenance request has been updated."
                    sendNotification(title, message, requestId)
                }

                "general_message" -> {
                    val title = data["title"] ?: "Notification"
                    val message = data["message"] ?: ""
                    sendNotification(title, message, null)
                }

                else -> {
                    Log.w("FCM", "Unknown message type: ${data["type"]}")
                }
            }
        }
    }

    private fun sendNotification(title: String, message: String, requestId: String?) {
        val channelId = "maintenance_channel"
        val manager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(channelId, "Maintenance Notifications", NotificationManager.IMPORTANCE_HIGH)
            manager.createNotificationChannel(channel)
        }

//        val intent = Intent(this, MaintenanceActivity::class.java).apply {
//            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
//            requestId?.let { putExtra("REQUEST_ID", it) }
//        }

//        val pendingIntent = PendingIntent.getActivity(
//            this,
//            System.currentTimeMillis().toInt(),
//            intent,
//            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
//        )

//        val notification = NotificationCompat.Builder(this, channelId)
//            .setContentTitle(title)
//            .setContentText(message)
//            .setSmallIcon(android.R.drawable.ic_dialog_info)
//            .setAutoCancel(true)
//            .setContentIntent(pendingIntent)
//            .build()

//        manager.notify(System.currentTimeMillis().toInt(), notification)
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d("FCM", "New FCM token: $token")
        fcmTokenManager.saveTokenLocally(applicationContext, token)

        CoroutineScope(Dispatchers.IO).launch {
            try {
                fcmTokenManager.resendTokenIfAvailable(applicationContext)
            } catch (e: Exception) {
                Log.e("FCM", "Failed to send FCM token to server: ${e.message}")
            }
        }
    }

}