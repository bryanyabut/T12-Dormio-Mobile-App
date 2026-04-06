package com.gbc.dormio_mobile_app.utils

import com.gbc.dormio_mobile_app.network.ApiResponse
import retrofit2.HttpException
import retrofit2.Response

// This function is a generic wrapper for making safe API calls.
// It handles exceptions and checks the response status.
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

// This function is used to handle the ApiResponse wrapper that the server uses for all responses.
// It checks the success field and either returns the data or an error message.
fun <T : Any> handleApiResponse(
    result: NetworkResult<ApiResponse<T>>
): NetworkResult<T> {
    return when (result) {
        is NetworkResult.Success -> {
            if (result.data.success) {
                NetworkResult.Success(result.data.data)
            } else {
                NetworkResult.Error(ApiError(result.data.message ?: "Server logic error"))
            }
        }
        is NetworkResult.Error -> result
        is NetworkResult.Loading -> result
    }
}