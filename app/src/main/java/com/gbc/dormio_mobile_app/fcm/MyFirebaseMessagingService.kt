package com.gbc.dormio_mobile_app.fcm

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.gbc.dormio_mobile_app.MainActivity
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
        Log.d("FCM", "Received message: ${remoteMessage.data}")

        val notificationTitle = remoteMessage.notification?.title
        val notificationBody = remoteMessage.notification?.body

        remoteMessage.data.takeIf { it.isNotEmpty() }?.let { data ->
            val type = data["type"]
            val title = data["title"] ?: notificationTitle ?: "Dormio Update"
            val message = data["message"] ?: notificationBody ?: ""

            when(type) {
                "maintenance_update", "maintenance_request" -> {
                    val requestId = data["REQUEST_ID"] ?: return

                    if (type == "maintenance_update") {
                        val statusString = data["status"] ?: ""
                        val status = try {
                            RequestStatus.valueOf(statusString.uppercase())
                        } catch (e: Exception) {
                            RequestStatus.PENDING
                        }

                        val user = UserDto(
                            id = data["user_id"]?.toInt() ?: 0,
                            firstName = data["user_firstName"] ?: "",
                            lastName = data["user_lastName"] ?: "",
                            email = data["user_email"] ?: "",
                            role = "STUDENT"
                        )

                        MaintenanceUpdateBus.post(requestId, status, user)
                    }

                    sendNotification(title, message, requestId, false)
                }

                "chore_assignment", "chore_update" -> {
                    val choreId = data["CHORE_ID"] ?: ""
                    val status = data["status"] ?: "PENDING"

                    CoroutineScope(Dispatchers.IO).launch {
                        ChoreUpdateBus.post(choreId, status)
                    }

                    sendNotification(title, message, choreId, true)
                }

                "general_message" -> {
                    sendNotification(title, message, null, false)
                }
            }
        }
    }

    private fun sendNotification(title: String, message: String, id: String?, isChore: Boolean) {
        val channelId = "maintenance_channel"
        val manager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Maintenance Notifications",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Updates for maintenance requests and chores"
            }
            manager.createNotificationChannel(channel)
        }

        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
            id?.let {
                if (isChore) {
                    putExtra("CHORE_ID", it)
                } else {
                    putExtra("REQUEST_ID", it)
                }
            }
        }

        val pendingIntent = PendingIntent.getActivity(
            this,
            System.currentTimeMillis().toInt(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(this, channelId)
            .setContentTitle(title)
            .setContentText(message)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .build()

        manager.notify(System.currentTimeMillis().toInt(), notification)
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d("FCM", "New Token generated: $token")
        fcmTokenManager.saveTokenLocally(applicationContext, token)

        CoroutineScope(Dispatchers.IO).launch {
            try {
                fcmTokenManager.resendTokenIfAvailable(applicationContext)
            } catch (e: Exception) {
                Log.e("FCM", "Failed to sync token: ${e.message}")
            }
        }
    }
}