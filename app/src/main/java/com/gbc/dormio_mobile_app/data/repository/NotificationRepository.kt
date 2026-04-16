package com.gbc.dormio_mobile_app.data.repository

import com.gbc.dormio_mobile_app.data.model.notification.NotificationModel
import com.gbc.dormio_mobile_app.data.model.notification.toDomainModel
import com.gbc.dormio_mobile_app.network.ApiServices.NotificationApiService
import com.gbc.dormio_mobile_app.utils.NetworkResult
import com.gbc.dormio_mobile_app.utils.handleApiResponse
import com.gbc.dormio_mobile_app.utils.safeApiCall
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NotificationRepository @Inject constructor(
    private val apiService: NotificationApiService
){
    suspend fun getNotifications(): NetworkResult<List<NotificationModel>> {
        val networkResult = safeApiCall { apiService.getMyNotifications() }

        val apiResponse = handleApiResponse(networkResult)

        return when (apiResponse) {
            is NetworkResult.Success -> {
                val domainList = apiResponse.data.map { it.toDomainModel() }
                NetworkResult.Success(domainList)
            }
            is NetworkResult.Error -> apiResponse
            is NetworkResult.Loading -> NetworkResult.Loading
        }
    }
}