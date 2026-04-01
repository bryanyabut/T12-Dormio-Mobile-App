package com.gbc.dormio_mobile_app.network.ApiServices

import com.gbc.dormio_mobile_app.data.model.profile.ProfileData
import com.gbc.dormio_mobile_app.data.model.profile.ProfileUpdateRequest
import com.gbc.dormio_mobile_app.network.ApiResponse
import com.gbc.dormio_mobile_app.utils.Constants
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part

interface ProfileApiService {

    @GET(Constants.API_PROFILE)
    suspend fun getProfile(): Response<ApiResponse<ProfileData>>

    @PUT(Constants.API_PROFILE)
    suspend fun updateProfile(
        @Body updateRequest: ProfileUpdateRequest
    ): Response<ApiResponse<ProfileData>>

    @Multipart
    @POST(Constants.API_PROFILE_AVATAR)
    suspend fun uploadAvatar(
        @Part image: MultipartBody.Part
    ) : Response<ApiResponse<String>>


}