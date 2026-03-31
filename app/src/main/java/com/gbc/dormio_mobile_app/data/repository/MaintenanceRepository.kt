package com.gbc.dormio_mobile_app.data.repository


import com.gbc.dormio_mobile_app.data.model.maintenance.CreateMaintenanceRequestDto
import com.gbc.dormio_mobile_app.data.model.maintenance.MaintenanceListResponse
import com.gbc.dormio_mobile_app.data.model.maintenance.MaintenanceResponse
import com.gbc.dormio_mobile_app.data.model.maintenance.UpdateMaintenanceRequestDto
import com.gbc.dormio_mobile_app.data.model.maintenance.UpdateMaintenanceStatusDto
import com.gbc.dormio_mobile_app.network.ApiServices.MaintenanceApiService
import com.gbc.dormio_mobile_app.utils.NetworkResult
import com.gbc.dormio_mobile_app.utils.safeApiCall
import com.gbc.dormio_mobile_app.data.model.maintenance.MaintenanceQuery
import javax.inject.Inject

class MaintenanceRepository @Inject constructor(
    private val apiService: MaintenanceApiService
) {

    suspend fun createRequest(request: CreateMaintenanceRequestDto): NetworkResult<MaintenanceResponse> {
        return safeApiCall { apiService.createRequest(request) }
    }

    suspend fun getMyRequests(query: MaintenanceQuery): NetworkResult<MaintenanceListResponse> {
        return safeApiCall {
            apiService.getMyRequests(
                search = query.search,
                status = query.status,
                urgency = query.urgency,
                page = query.page,
                limit = query.limit,
                sortBy = query.sort,
                sortOrder = query.order
            )
        }
    }

    suspend fun updateRequestStudent(requestId: String, request: UpdateMaintenanceRequestDto): NetworkResult<MaintenanceResponse> {
        return safeApiCall { apiService.updateRequestStudent(requestId, request) }
    }

    suspend fun getRequestDetailStudent(requestId: String): NetworkResult<MaintenanceResponse> {
        return safeApiCall { apiService.getRequestDetailStudent(requestId) }
    }

    // Admin functions
    suspend fun getAllRequests(query: MaintenanceQuery): NetworkResult<MaintenanceListResponse> {
        return safeApiCall {
            apiService.getAllRequests(
                search = query.search,
                status = query.status,
                urgency = query.urgency,
                page = query.page,
                limit = query.limit,
                sortBy = query.sort,
                sortOrder = query.order
            )
        }
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