package com.gbc.dormio_mobile_app.data.repository


import com.gbc.dormio_mobile_app.data.model.CreateMaintenanceRequestDto
import com.gbc.dormio_mobile_app.data.model.MaintenanceListResponse
import com.gbc.dormio_mobile_app.data.model.MaintenanceResponse
import com.gbc.dormio_mobile_app.data.model.UpdateMaintenanceRequestDto
import com.gbc.dormio_mobile_app.data.model.UpdateMaintenanceStatusDto
import com.gbc.dormio_mobile_app.network.MaintenanceApiService
import com.gbc.dormio_mobile_app.utils.NetworkResult
import com.gbc.dormio_mobile_app.utils.safeApiCall

class MaintenanceRepository (private val apiService: MaintenanceApiService) {

    suspend fun createRequest(request: CreateMaintenanceRequestDto): NetworkResult<MaintenanceResponse> {
        return safeApiCall { apiService.createRequest(request) }
    }

    suspend fun getMyRequests(status: String? = null, urgency: String? = null): NetworkResult<MaintenanceListResponse> {
        return safeApiCall { apiService.getMyRequests(status, urgency) }
    }

    suspend fun updateRequestStudent(requestId: String, request: UpdateMaintenanceRequestDto): NetworkResult<MaintenanceResponse> {
        return safeApiCall { apiService.updateRequestStudent(requestId, request) }
    }

    suspend fun getRequestDetailStudent(requestId: String): NetworkResult<MaintenanceResponse> {
        return safeApiCall { apiService.getRequestDetailStudent(requestId) }
    }

    // Admin functions
    suspend fun getAllRequests(status: String? = null, urgency: String? = null): NetworkResult<MaintenanceListResponse> {
        return safeApiCall { apiService.getAllRequests(status, urgency) }
    }

    suspend fun getRequestDetailAdmin(requestId: String): NetworkResult<MaintenanceResponse> {
        return safeApiCall { apiService.getRequestDetailAdmin(requestId) }
    }

    suspend fun updateRequestStatus(requestId: String, newStatus: String): NetworkResult<MaintenanceResponse> {
        val request = UpdateMaintenanceStatusDto(status = newStatus)
        return safeApiCall { apiService.updateRequestStatus(requestId, request) }
    }

    suspend fun deleteRequest(requestId: String): NetworkResult<MaintenanceResponse> {
        return safeApiCall { apiService.deleteRequest(requestId) }
    }
}