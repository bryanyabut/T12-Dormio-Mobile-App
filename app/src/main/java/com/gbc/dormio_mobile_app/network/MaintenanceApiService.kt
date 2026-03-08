package com.gbc.dormio_mobile_app.network

import com.gbc.dormio_mobile_app.model.*
import retrofit2.Response
import com.gbc.dormio_mobile_app.utils.Constants
import retrofit2.http.*

interface MaintenanceApiService {

    //Student endpoints
    @GET(Constants.API_MAINTENANCE_REQUESTS_CREATE)
    suspend fun createRequest(@Body request: CreateMaintenanceRequestDto): Response<MaintenanceResponse>

    @GET(Constants.API_MAINTENANCE_MY_REQUESTS)
    suspend fun getMyRequests(
        @Query("status") status: String? = null,
        @Query("urgency") urgency: String? = null
    ): Response<MaintenanceListResponse>

    @GET(Constants.API_MAINTENANCE_REQUEST_UPDATE_STUDENT)
    suspend fun updateRequestStudent(
        @Path("id") requestId: String,
        @Body request: UpdateMaintenanceRequestDto
    ): Response<MaintenanceResponse>

    @GET(Constants.API_MAINTENANCE_REQUEST_DETAIL_STUDENT)
    suspend fun getRequestDetailStudent(@Path("id") requestId: String): Response<MaintenanceResponse>

    //Admin endpoints
    @GET(Constants.API_MAINTENANCE_REQUESTS_ALL)
    suspend fun getAllRequests(
        @Query("status") status: String? = null,
        @Query("urgency") urgency: String? = null
    ): Response<MaintenanceListResponse>

    @GET(Constants.API_MAINTENANCE_REQUEST_DETAIL_ADMIN)
    suspend fun getRequestDetailAdmin(@Path("id") requestId: String): Response<MaintenanceResponse>

    @PUT(Constants.API_MAINTENANCE_REQUEST_UPDATE_STATUS)
    suspend fun updateRequestStatus(
        @Path("id") requestId: String,
        @Body request: UpdateMaintenanceStatusDto
    ): Response<MaintenanceResponse>

    //Both user endpoints
    @DELETE(Constants.API_MAINTENANCE_REQUEST_DELETE)
    suspend fun deleteRequest(@Path("id") requestId: String): Response<MaintenanceResponse>

}