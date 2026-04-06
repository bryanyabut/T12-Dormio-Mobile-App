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
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import javax.inject.Inject

class MaintenanceRepository @Inject constructor(
    private val apiService: MaintenanceApiService
) {

    //Student makes a request
    suspend fun createRequest(
        title: String,
        description: String,
        urgency: String,
        imageFile: File?
    ): NetworkResult<MaintenanceResponse> {
        return safeApiCall {
            val titlePart = title.toRequestBody("text/plain".toMediaTypeOrNull())
            val descriptionPart = description.toRequestBody("text/plain".toMediaTypeOrNull())
            val urgencyPart = urgency.toRequestBody("text/plain".toMediaTypeOrNull())

            val imagePart = imageFile?.let {
                val requestFile = it.asRequestBody("image/*".toMediaTypeOrNull())
                MultipartBody.Part.createFormData("image", it.name, requestFile)
            }

            apiService.createRequest(titlePart, descriptionPart, urgencyPart, imagePart)
        }
    }

    // Student requests
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

    //Student updates a request
    suspend fun updateRequestStudent(
        requestId: Int,
        title: String?,
        description: String?,
        urgency: String?,
        imageFile: File?
    ): NetworkResult<MaintenanceResponse> {
        return safeApiCall {
            val titlePart = title?.toRequestBody("text/plain".toMediaTypeOrNull())
            val descriptionPart = description?.toRequestBody("text/plain".toMediaTypeOrNull())
            val urgencyPart = urgency?.toRequestBody("text/plain".toMediaTypeOrNull())
            val imagePart = imageFile?.let {
                val requestFile = it.asRequestBody("image/*".toMediaTypeOrNull())
                MultipartBody.Part.createFormData("image", it.name, requestFile)
            }

            apiService.updateRequestStudent(requestId, titlePart, descriptionPart, urgencyPart, imagePart)
        }
    }

    suspend fun getRequestDetailStudent(requestId: Int): NetworkResult<MaintenanceResponse> {
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

    suspend fun getRequestDetailAdmin(requestId: Int): NetworkResult<MaintenanceResponse> {
        return safeApiCall { apiService.getRequestDetailAdmin(requestId) }
    }

    suspend fun updateRequestStatus(
        requestId: Int,
        newStatus: String,
        adminComment: String? = null
    ): NetworkResult<MaintenanceResponse> {
        val requestBody = UpdateMaintenanceStatusDto(
            status = newStatus,
            adminComment = adminComment
        )
        return safeApiCall {
            apiService.updateRequestStatus(requestId, requestBody)
        }
    }

    suspend fun deleteRequest(requestId: Int): NetworkResult<MaintenanceResponse> {
        return safeApiCall { apiService.deleteRequest(requestId) }
    }
}