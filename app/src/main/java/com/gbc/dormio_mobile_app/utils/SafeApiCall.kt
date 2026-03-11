package com.gbc.dormio_mobile_app.utils

import retrofit2.HttpException
import retrofit2.Response

suspend fun <T : Any> safeApiCall(apiCall: suspend () -> Response<T>): NetworkResult<T> {
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