package com.gbc.dormio_mobile_app.network.ApiServices

import com.gbc.dormio_mobile_app.data.model.chores.AddChoreRequest
import com.gbc.dormio_mobile_app.data.model.chores.Chore
import com.gbc.dormio_mobile_app.data.model.chores.DashboardData
import com.gbc.dormio_mobile_app.data.model.chores.Housemate
import com.gbc.dormio_mobile_app.data.model.chores.UpdateChoreRequest
import com.gbc.dormio_mobile_app.network.ApiResponse
import com.gbc.dormio_mobile_app.utils.Constants
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface ChoresApiService {

    @GET(Constants.API_CHORES_DASHBOARD)
    suspend fun getDashboard(): Response<ApiResponse<DashboardData>>

    @GET(Constants.API_CHORES_HOUSEMATES)
    suspend fun getHousemates(): Response<ApiResponse<List<Housemate>>>

    @POST(Constants.API_CHORES_CREATE)
    suspend fun createChore(
        @Body request: AddChoreRequest
    ) : Response<ApiResponse<Chore>>

    @PATCH(Constants.API_CHORES_COMPLETE)
    suspend fun markChoreComplete(
        @Path("id") choreId: Int
    ): Response<ApiResponse<Chore>>

    @PUT(Constants.API_CHORES_UPDATE)
    suspend fun updateChore(
        @Path("id") choreId: Int,
        @Body request: UpdateChoreRequest
    ): Response<ApiResponse<Chore>>

    @GET(Constants.API_CHORES_BY_ID)
    suspend fun getChoreById(
        @Path("id") choreId: Int
    ): Response<ApiResponse<Chore>>
}