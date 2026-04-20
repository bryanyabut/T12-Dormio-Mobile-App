package com.gbc.dormio_mobile_app.data.repository

import com.gbc.dormio_mobile_app.data.model.chores.AddChoreRequest
import com.gbc.dormio_mobile_app.data.model.chores.Chore
import com.gbc.dormio_mobile_app.data.model.chores.DashboardData
import com.gbc.dormio_mobile_app.data.model.chores.Housemate
import com.gbc.dormio_mobile_app.data.model.chores.UpdateChoreRequest
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

    suspend fun getHousemates(): NetworkResult<List<Housemate>> {
        val networkResult = safeApiCall { apiService.getHousemates() }
        return handleApiResponse(networkResult)
    }

    suspend fun createChore(request: AddChoreRequest): NetworkResult<Chore> {
        val networkResult = safeApiCall { apiService.createChore(request) }
        return handleApiResponse(networkResult)
    }

    suspend fun markChoreComplete(choreId: Int): NetworkResult<Chore> {
        val networkResult = safeApiCall { apiService.markChoreComplete(choreId) }
        return handleApiResponse(networkResult)
    }

    suspend fun getChoreById(choreId: Int): NetworkResult<Chore> {
        val networkResult = safeApiCall { apiService.getChoreById(choreId) }
        return handleApiResponse(networkResult)
    }

    suspend fun updateChore(choreId: Int, request: UpdateChoreRequest): NetworkResult<Chore> {
        val networkResult = safeApiCall { apiService.updateChore(choreId, request) }
        return handleApiResponse(networkResult)
    }



}