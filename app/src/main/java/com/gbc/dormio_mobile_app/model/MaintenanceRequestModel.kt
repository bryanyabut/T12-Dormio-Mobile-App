package com.gbc.dormio_mobile_app.model

import com.google.gson.annotations.SerializedName

data class MaintenanceRequestDto(
    val id: String,
    val title: String,
    val description: String,
    val urgency: String,
    val status: String,
    val createdAt: String,
    val updatedAt: String,
    val resolvedAt: String?,
    val user: UserDto
)

data class CreateMaintenanceRequestDto(
    val title: String,
    val description: String,
    val urgency: String
)

data class UpdateMaintenanceRequestDto(
    val title: String? = null,
    val description: String? = null,
    val urgency: String? = null
)

data class UpdateMaintenanceStatusDto(
    val status: String
)

data class MaintenanceResponse(
    val success: Boolean,
    val data: MaintenanceRequestDto,
)

data class MaintenanceListResponse(
    val success: Boolean,
    val data: List<MaintenanceRequestDto>,
)

enum class UrgencyLevel(val value: String) {
    LOW("Low"),
    MEDIUM("Medium"),
    HIGH("High")
}

enum class RequestStatus(val value: String) {
    OPEN("Open"),
    IN_PROGRESS("In Progress"),
    RESOLVED("Resolved")
}