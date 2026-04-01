package com.gbc.dormio_mobile_app.data.repository

import android.content.Context
import android.net.Uri
import com.gbc.dormio_mobile_app.data.model.profile.ProfileData
import com.gbc.dormio_mobile_app.data.model.profile.ProfileUpdateRequest
import com.gbc.dormio_mobile_app.network.ApiServices.ProfileApiService
import com.gbc.dormio_mobile_app.utils.ApiError
import com.gbc.dormio_mobile_app.utils.FileHandle
import com.gbc.dormio_mobile_app.utils.NetworkResult
import com.gbc.dormio_mobile_app.utils.handleApiResponse
import com.gbc.dormio_mobile_app.utils.safeApiCall
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class ProfileRepository @Inject constructor(
    private val apiService: ProfileApiService
) {

    suspend fun getProfile(): NetworkResult<ProfileData>{
        val networkResult = safeApiCall { apiService.getProfile() }
        return handleApiResponse(networkResult)
    }

    suspend fun updateProfile(updateRequest: ProfileUpdateRequest): NetworkResult<ProfileData>{
        val networkResult = safeApiCall { apiService.updateProfile(updateRequest) }
        return handleApiResponse(networkResult)
    }

    suspend fun uploadAvatar(context: Context, imageUri: Uri): NetworkResult<String> {
        return withContext(Dispatchers.IO) {
            val preparedImage = FileHandle.prepareImage(context, "profileImage", imageUri)
                ?: return@withContext NetworkResult.Error(ApiError("Could not process image file"))

            try {
                val networkResult = safeApiCall { apiService.uploadAvatar(preparedImage.part) }
                handleApiResponse(networkResult)

            } catch (e: Exception) {
                NetworkResult.Error(ApiError(e.localizedMessage ?: "Unknown upload error"))

            } finally {
                if (preparedImage.file.exists()) {
                    preparedImage.file.delete()
                }
            }
        }
    }

}