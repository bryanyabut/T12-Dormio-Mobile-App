package com.gbc.dormio_mobile_app.data.model
import com.google.gson.annotations.SerializedName

data class LoginRequest(
    val email: String,
    val password: String
)

data class RegisterRequest(
    val email: String,
    val password: String,
    @SerializedName("firstName") val firstName: String,
    @SerializedName("lastName") val lastName: String,
    val role: String? = null
)

data class DeviceTokenRequest(
    @SerializedName("deviceToken") val deviceToken: String
)

data class LoginResponse(
    val message: String,
    val token: String,
    val user: UserDto
)

data class RegisterResponse(
    val message: String,
    val user: UserDto
)

data class DeviceTokenResponse(
    val message: String,
    val user: DeviceTokenUserDto
)

data class ErrorResponse(
    val error: String
)

data class UserDto(
    val id: String,
    val email: String,
    @SerializedName("firstName") val firstName: String,
    @SerializedName("lastName") val lastName: String,
    val role: String
)

data class DeviceTokenUserDto(
    val id: String,
    val email: String,
    @SerializedName("deviceToken") val deviceToken: String?
)

data class AuthUiState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val user: UserDto? = null,
    val token: String? = null
)
