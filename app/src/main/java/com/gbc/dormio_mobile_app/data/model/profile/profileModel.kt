package com.gbc.dormio_mobile_app.data.model.profile

data class ProfileData(
    val id: Int,
    val studentId: String?,
    val roomNumber: String?,
    val avatarUrl: String?,
    val userId: Int,
    val user: UserProfileDetails
)

//user table nested inside profile model
data class UserProfileDetails(
    val email: String,
    val firstName: String?,
    val lastName: String?
)

//UI state for profile screen
data class ProfileUiState(
    val isLoading: Boolean = false,
    val profile: ProfileData? = null,
    val errorMessage: String? = null,
    val userRole: String? = null
)