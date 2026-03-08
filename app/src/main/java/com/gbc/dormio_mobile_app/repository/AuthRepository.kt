package com.gbc.dormio_mobile_app.repository

import com.gbc.dormio_mobile_app.model.*
import com.gbc.dormio_mobile_app.network.AuthApiService
import com.gbc.dormio_mobile_app.utils.NetworkResult
import com.gbc.dormio_mobile_app.utils.safeApiCall

class AuthRepository(private val authService: AuthApiService) {

    suspend fun login(email: String, password: String): NetworkResult<LoginResponse> {
        return safeApiCall { authService.login(LoginRequest(email, password)) }
    }

    suspend fun register(
        email: String,
        password: String,
        firstName: String,
        lastName: String,
        role: String? = "STUDENT"
    ): NetworkResult<RegisterResponse> {
        return safeApiCall { authService.register(RegisterRequest(email, password, firstName, lastName, role)) }
    }

}