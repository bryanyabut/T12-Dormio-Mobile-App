package com.gbc.dormio_mobile_app.data.model.notification

data class NotificationModel(
    val title: String,
    val message: String,
    val timestamp: Long,
    val type: NotificationType = NotificationType.GENERAL
)

enum class NotificationType {
    CHORE, MAINTENANCE, BILL, GENERAL
}