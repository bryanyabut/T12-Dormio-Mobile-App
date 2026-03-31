package com.gbc.dormio_mobile_app.network.ApiServices

import com.gbc.dormio_mobile_app.data.model.profile.ProfileData
import com.gbc.dormio_mobile_app.network.ApiResponse
import com.gbc.dormio_mobile_app.utils.Constants
import retrofit2.Response
import retrofit2.http.GET

interface ProfileApiService {

    @GET(Constants.API_PROFILE)
    suspend fun getProfile(): Response<ApiResponse<ProfileData>>

}