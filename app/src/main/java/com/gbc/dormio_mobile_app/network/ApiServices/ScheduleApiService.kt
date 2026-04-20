package com.gbc.dormio_mobile_app.network.ApiServices

import com.gbc.dormio_mobile_app.data.model.schedule.CalendarDeleteResponse
import com.gbc.dormio_mobile_app.data.model.schedule.CalendarItemResponse
import com.gbc.dormio_mobile_app.data.model.schedule.CalendarListResponse
import com.gbc.dormio_mobile_app.data.model.schedule.CreateScheduleRequest
import com.gbc.dormio_mobile_app.utils.Constants
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface ScheduleApiService {

    @GET(Constants.API_CALENDAR)
    suspend fun getSchedules(
        @Query("startDate") startDate: String? = null,
        @Query("endDate") endDate: String? = null
    ): Response<CalendarListResponse>

    @POST(Constants.API_CALENDAR)
    suspend fun createSchedule(
        @Body request: CreateScheduleRequest
    ): Response<CalendarItemResponse>

    @DELETE(Constants.API_CALENDAR_DETAIL)
    suspend fun deleteSchedule(
        @Path("id") id: Int
    ): Response<CalendarDeleteResponse>
}
