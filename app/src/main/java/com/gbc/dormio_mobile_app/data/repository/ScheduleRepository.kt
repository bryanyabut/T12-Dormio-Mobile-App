package com.gbc.dormio_mobile_app.data.repository

import com.gbc.dormio_mobile_app.data.model.schedule.CalendarDeleteResponse
import com.gbc.dormio_mobile_app.data.model.schedule.CalendarItemResponse
import com.gbc.dormio_mobile_app.data.model.schedule.CalendarListResponse
import com.gbc.dormio_mobile_app.data.model.schedule.CreateScheduleRequest
import com.gbc.dormio_mobile_app.network.ApiServices.ScheduleApiService
import com.gbc.dormio_mobile_app.utils.NetworkResult
import com.gbc.dormio_mobile_app.utils.safeApiCall
import javax.inject.Inject

class ScheduleRepository @Inject constructor(
    private val apiService: ScheduleApiService
) {

    suspend fun getSchedules(
        startDate: String? = null,
        endDate: String? = null
    ): NetworkResult<CalendarListResponse> {
        return safeApiCall { apiService.getSchedules(startDate, endDate) }
    }

    suspend fun createSchedule(
        request: CreateScheduleRequest
    ): NetworkResult<CalendarItemResponse> {
        return safeApiCall { apiService.createSchedule(request) }
    }

    suspend fun deleteSchedule(id: Int): NetworkResult<CalendarDeleteResponse> {
        return safeApiCall { apiService.deleteSchedule(id) }
    }
}
