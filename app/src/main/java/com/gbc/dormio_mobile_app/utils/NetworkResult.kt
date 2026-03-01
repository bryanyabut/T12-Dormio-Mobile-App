package com.gbc.dormio_mobile_app.utils

sealed class NetworkResult<out T : Any> {
    data class Success<out T : Any>(val data: T) : NetworkResult<T>()
    data class Error(val apiError: ApiError) : NetworkResult<Nothing>()
    object Loading : NetworkResult<Nothing>()
}

data class ApiError(
    val message: String? = null,
    val code: Int? = null
)