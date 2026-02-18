package com.gbc.dormio_mobile_app.repository

import android.net.Network
import com.gbc.dormio_mobile_app.model.*
import com.gbc.dormio_mobile_app.network.AuthApiService
import com.gbc.dormio_mobile_app.utils.ApiError
import com.gbc.dormio_mobile_app.utils.NetworkResult
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException

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

private suspend fun <T : Any> safeApiCall(apiCall: suspend () -> Response<T>): NetworkResult<T> {
    return try {
        val response = apiCall()
        if (response.isSuccessful) {
            val body = response.body()
            if (body != null) {
                NetworkResult.Success(body)
            } else {
                NetworkResult.Error(ApiError("Empty response from server", response.code()))
            }
        } else {
            val errorMessage = response.errorBody()?.string()
            NetworkResult.Error(ApiError(errorMessage, response.code()))
        }
    } catch (e: HttpException) {
        NetworkResult.Error(ApiError("Network error: ${e.message()}", e.code()))
    } catch (e: Exception) {
        NetworkResult.Error(ApiError("Unknown error: ${e.localizedMessage}"))
    }
}