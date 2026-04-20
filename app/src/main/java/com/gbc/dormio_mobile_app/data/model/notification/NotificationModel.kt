package com.gbc.dormio_mobile_app.data.model.notification

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

data class NotificationModel(
    val id: Int? = null,
    val title: String,
    val message: String,
    val timestamp: Long,
    val type: NotificationType = NotificationType.GENERAL,
    val typeString: String? = null
)

data class NotificationRemoteModel(
    val id: Int,
    val title: String,
    val message: String,
    val type: String,
    val createdAt: String
)

data class NotificationUiState(
    val isLoading: Boolean = false,
    val notifications: List<NotificationModel> = emptyList(),
    val errorMessage: String? = null
)

enum class NotificationType {
    CHORE, MAINTENANCE, BILL, GENERAL;

    companion object {
        fun fromString(type: String?): NotificationType {
            return when (type?.uppercase()) {
                "CHORE_ASSIGNMENT", "CHORE_UPDATE" -> CHORE
                "MAINTENANCE_REQUEST", "MAINTENANCE_UPDATE" -> MAINTENANCE
                "BILL_SPLIT", "BILL_PAYMENT" -> BILL
                else -> GENERAL
            }
        }
    }
}

fun NotificationRemoteModel.toDomainModel(): NotificationModel {
    val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US)
    sdf.timeZone = TimeZone.getTimeZone("UTC")
    val timeAsLong = sdf.parse(this.createdAt)?.time ?: System.currentTimeMillis()

    return NotificationModel(
        id = this.id,
        title = this.title,
        message = this.message,
        timestamp = timeAsLong,
        type = NotificationType.fromString(this.type),
        typeString = this.type
    )
}

fun NotificationModel.getFormattedTime(): String {
    val date = Date(this.timestamp)
    val sdf = SimpleDateFormat("MMM dd, h:mm a", Locale.getDefault())
    return sdf.format(date)
}