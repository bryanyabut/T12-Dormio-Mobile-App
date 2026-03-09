package com.gbc.dormio_mobile_app.data.model

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

data class MaintenanceAllReqUiState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val maintenanceRequests: List<MaintenanceRequestDto> = emptyList(),
    val filterStatus: String? = null,
    val filterUrgency: String? = null
)

data class MaintenanceDetailUiState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val requestDetail: MaintenanceRequestDto? = null,
)

data class MaintenanceFormUiState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val successMessage: String? = null
)

enum class UrgencyLevel(val value: String) {
    LOW("LOW"),
    MEDIUM("MEDIUM"),
    HIGH("HIGH")
}

enum class RequestStatus(val value: String) {
    PENDING("PENDING"),
    IN_PROGRESS("IN_PROGRESS"),
    RESOLVED("RESOLVED")
}