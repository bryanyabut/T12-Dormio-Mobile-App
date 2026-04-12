package com.gbc.dormio_mobile_app.network.ApiServices

import com.gbc.dormio_mobile_app.data.model.chores.DashboardData
import com.gbc.dormio_mobile_app.network.ApiResponse
import com.gbc.dormio_mobile_app.utils.Constants
import retrofit2.Response
import retrofit2.http.GET

interface ChoresApiService {

    @GET(Constants.API_CHORES_DASHBOARD)
    suspend fun getDashboard(): Response<ApiResponse<DashboardData>>

}