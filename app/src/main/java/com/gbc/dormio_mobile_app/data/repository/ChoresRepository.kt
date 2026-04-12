package com.gbc.dormio_mobile_app.data.repository

import com.gbc.dormio_mobile_app.data.model.chores.DashboardData
import com.gbc.dormio_mobile_app.network.ApiServices.ChoresApiService
import com.gbc.dormio_mobile_app.utils.NetworkResult
import com.gbc.dormio_mobile_app.utils.handleApiResponse
import com.gbc.dormio_mobile_app.utils.safeApiCall
import javax.inject.Inject

class ChoresRepository @Inject constructor(
    private val apiService: ChoresApiService
) {

    suspend fun getChoreDashboard(): NetworkResult<DashboardData> {
        val networkResult = safeApiCall { apiService.getDashboard() }
        return handleApiResponse(networkResult)
    }

}