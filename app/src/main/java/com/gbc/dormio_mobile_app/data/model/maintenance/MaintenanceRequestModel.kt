package com.gbc.dormio_mobile_app.data.model.maintenance

import com.gbc.dormio_mobile_app.data.model.UserDto
import com.google.gson.annotations.SerializedName

data class MaintenanceRequestDto(
    val id: Int,
    val title: String,
    val description: String,
    val urgency: UrgencyLevel,
    val status: RequestStatus,
    val imageUrl: String?,
    val adminComment: String?,
    val createdAt: String,
    val updatedAt: String,
    val resolvedAt: String?,
    val user: UserDto?
)

data class CreateMaintenanceRequestDto(
    val title: String,
    val description: String,
    val urgency: String
)

data class UpdateMaintenanceRequestDto(
    val title: String? = null,
    val description: String? = null,
    val urgency: String? = null,
)

data class UpdateMaintenanceStatusDto(
    val status: String,
    val adminComment: String? = null
)

data class MaintenanceResponse(
    val success: Boolean,
    val data: MaintenanceRequestDto
)

data class MaintenanceListResponse(
    val success: Boolean,
    val data: List<MaintenanceRequestDto>,
    val page: Int,
    val totalPages: Int,
    val totalItems: Int
)

data class MaintenanceAllReqUiState(
    val isLoading: Boolean = false,
    val userRole: String = UserRole.STUDENT.value,
    val errorMessage: String? = null,
    val maintenanceRequests: List<MaintenanceRequestDto> = emptyList(),
    val filterStatus: String? = null,
    val filterUrgency: String? = null,
    val currentPage: Int = 1,
    val hasMorePages: Boolean = true
)

data class MaintenanceDetailUiState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val requestDetail: MaintenanceRequestDto? = null,
)

data class MaintenanceFormUiState(
    val isLoading: Boolean = false,
    val userRole: String = UserRole.STUDENT.value,
    val errorMessage: String? = null,
    val successMessage: String? = null
)

enum class UrgencyLevel {
    @SerializedName("LOW") LOW,
    @SerializedName("MEDIUM") MEDIUM,
    @SerializedName("HIGH") HIGH
}

enum class RequestStatus {
    @SerializedName("PENDING") PENDING,
    @SerializedName("IN_PROGRESS") IN_PROGRESS,
    @SerializedName("RESOLVED") RESOLVED
}
enum class UserRole(val value: String) {
    STUDENT("STUDENT"),
    ADMIN("ADMIN")
}