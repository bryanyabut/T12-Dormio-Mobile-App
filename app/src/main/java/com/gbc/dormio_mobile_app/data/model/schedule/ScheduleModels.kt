package com.gbc.dormio_mobile_app.data.model.schedule

import com.google.gson.annotations.SerializedName

data class CreateScheduleRequest(
    val title: String,
    val type: String,
    @SerializedName("startTime") val startTime: String,
    @SerializedName("endTime") val endTime: String,
    val description: String? = null,
    val location: String? = null,
    @SerializedName("courseCode") val courseCode: String? = null,
    val section: String? = null
)

data class ScheduleDto(
    val id: Int,
    val userId: Int,
    val title: String,
    val type: String,
    val description: String?,
    @SerializedName("startTime") val startTime: String,
    @SerializedName("endTime") val endTime: String,
    val location: String?,
    @SerializedName("courseCode") val courseCode: String?,
    val section: String?
)

data class CalendarListResponse(
    val success: Boolean,
    val count: Int,
    val data: List<ScheduleDto>
)

data class CalendarItemResponse(
    val success: Boolean,
    val data: ScheduleDto
)

data class CalendarDeleteResponse(
    val success: Boolean
)

data class ScheduleUiState(
    val isLoading: Boolean = false,
    val schedules: List<ScheduleDto> = emptyList(),
    val errorMessage: String? = null,
    val successMessage: String? = null
)
