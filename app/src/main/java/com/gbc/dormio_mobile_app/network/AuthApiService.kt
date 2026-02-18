package com.gbc.dormio_mobile_app.network

import com.gbc.dormio_mobile_app.model.*
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

    @POST("auth/login")
    suspend fun login(
        @Body request: LoginRequest
    ): Response<LoginResponse>

    @POST("auth/register")
    suspend fun register(
        @Body request: RegisterRequest
    ): Response<RegisterResponse>

    @POST("auth/device-token")
    suspend fun updateDeviceToken(
        @Body request: DeviceTokenRequest
    ): Response<DeviceTokenResponse>



}