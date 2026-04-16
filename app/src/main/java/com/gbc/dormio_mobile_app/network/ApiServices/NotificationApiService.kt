package com.gbc.dormio_mobile_app.network.ApiServices

import com.gbc.dormio_mobile_app.data.model.notification.NotificationRemoteModel
import com.gbc.dormio_mobile_app.network.ApiResponse
import com.gbc.dormio_mobile_app.utils.Constants
import retrofit2.Response
import retrofit2.http.GET

interface NotificationApiService {
    @GET(Constants.API_NOTIFICATIONS)
    suspend fun getMyNotifications(): Response<ApiResponse<List<NotificationRemoteModel>>>
}