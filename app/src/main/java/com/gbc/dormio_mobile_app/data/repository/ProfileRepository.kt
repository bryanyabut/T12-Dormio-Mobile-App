package com.gbc.dormio_mobile_app.data.repository

import com.gbc.dormio_mobile_app.data.model.profile.ProfileData
import com.gbc.dormio_mobile_app.network.ApiServices.ProfileApiService
import com.gbc.dormio_mobile_app.utils.NetworkResult
import com.gbc.dormio_mobile_app.utils.handleApiResponse
import com.gbc.dormio_mobile_app.utils.safeApiCall
import javax.inject.Inject

class ProfileRepository @Inject constructor(
    private val apiService: ProfileApiService
) {

    suspend fun getProfile(): NetworkResult<ProfileData>{
        val networkResult = safeApiCall { apiService.getProfile() }
        return handleApiResponse(networkResult)
    }
}