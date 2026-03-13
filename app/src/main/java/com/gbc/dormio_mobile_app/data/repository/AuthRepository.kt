package com.gbc.dormio_mobile_app.data.repository

import com.gbc.dormio_mobile_app.data.model.LoginRequest
import com.gbc.dormio_mobile_app.data.model.LoginResponse
import com.gbc.dormio_mobile_app.data.model.RegisterRequest
import com.gbc.dormio_mobile_app.data.model.RegisterResponse
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
        return safeApiCall { authService.register(
            RegisterRequest(
                email,
                password,
                firstName,
                lastName,
                role
            )
        )}
    }
}