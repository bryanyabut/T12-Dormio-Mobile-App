package com.gbc.dormio_mobile_app.network

import com.gbc.dormio_mobile_app.data.model.DeviceTokenRequest
import com.gbc.dormio_mobile_app.data.model.DeviceTokenResponse
import com.gbc.dormio_mobile_app.data.model.LoginRequest
import com.gbc.dormio_mobile_app.data.model.LoginResponse
import com.gbc.dormio_mobile_app.data.model.RegisterRequest
import com.gbc.dormio_mobile_app.data.model.RegisterResponse
import com.gbc.dormio_mobile_app.data.model.UserDto
import com.gbc.dormio_mobile_app.utils.Constants
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface AuthApiService {

    @GET("auth/users")
    suspend fun getAllUsers(): Response<List<UserDto>>

    @GET("auth/profile")
    suspend fun getCurrentUser(): Response<UserDto>

    @GET("auth/users/{id}")
    suspend fun getUserById(
        @Path("id") userId: String
    ): Response<UserDto>

    @POST(Constants.API_AUTH_LOGIN)
    suspend fun login(
        @Body request: LoginRequest
    ): Response<LoginResponse>

    @POST(Constants.API_AUTH_REGISTER)
    suspend fun register(
        @Body request: RegisterRequest
    ): Response<RegisterResponse>

    @POST("auth/device-token")
    suspend fun updateDeviceToken(
        @Body request: DeviceTokenRequest
    ): Response<DeviceTokenResponse>



}