package com.gbc.dormio_mobile_app.viewmodel.profile

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gbc.dormio_mobile_app.data.model.profile.ProfileUiState
import com.gbc.dormio_mobile_app.data.model.profile.ProfileUpdateRequest
import com.gbc.dormio_mobile_app.data.repository.ProfileRepository
import com.gbc.dormio_mobile_app.network.FcmTokenManager
import com.gbc.dormio_mobile_app.network.TokenManager
import com.gbc.dormio_mobile_app.utils.NetworkResult
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val repository: ProfileRepository,
    private val fcmTokenManager: FcmTokenManager,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    init {
        val role = TokenManager.getUserRole(context)
        _uiState.update { it.copy(userRole = role) }
        fetchProfile()
    }

    fun fetchProfile() {
        _uiState.update {it.copy(
            isLoading = true,
            errorMessage = null
        )}

        viewModelScope.launch {
            when (val result = repository.getProfile()){
                is NetworkResult.Success -> {
                    _uiState.update { it.copy(
                        isLoading = false,
                        profile = result.data
                    ) }
                }

                is NetworkResult.Error -> {
                    _uiState.update { it.copy(
                        isLoading = false,
                        errorMessage = result.apiError.message
                    ) }
                }

                is NetworkResult.Loading -> {}
            }
        }
    }

    fun updateProfile(request: ProfileUpdateRequest){
        _uiState.update { it.copy(
            isLoading = true,
            errorMessage = null,
            isUpdateSuccessful = false
        )}

        viewModelScope.launch {
            when (val result = repository.updateProfile(request)) {
                is NetworkResult.Success -> {
                    _uiState.update { it.copy(
                        isLoading = false,
                        profile = result.data,
                        isUpdateSuccessful = true
                    )}
                }
                is NetworkResult.Error -> {
                    _uiState.update { it.copy(isLoading = false, errorMessage = result.apiError.message) }
                }
                is NetworkResult.Loading -> {}
            }
        }
    }

    fun uploadAvatar(uri: Uri) {
        _uiState.update { it.copy(
            isLoading = true,
            errorMessage = null
        ) }

        viewModelScope.launch {
            when (val result = repository.uploadAvatar(context, uri)) {
                is NetworkResult.Success -> {
                    _uiState.update { state ->
                        state.copy(
                            isLoading = false,
                            profile = state.profile?.copy(avatarUrl = result.data)
                        )
                    }
                }
                is NetworkResult.Error -> {
                    _uiState.update { it.copy(isLoading = false, errorMessage = result.apiError.message) }
                }
                is NetworkResult.Loading -> {}
            }
        }
    }

    fun resetUpdateStatus() {
        _uiState.update { it.copy(isUpdateSuccessful = false) }
    }

    fun logout(onComplete: () -> Unit) {
        viewModelScope.launch {
            try {
                fcmTokenManager.clearTokenOnServer(context)
                context.getSharedPreferences("dormio_prefs", Context.MODE_PRIVATE)
                    .edit()
                    .remove("last_sent_token")
                    .apply()
            } catch (e: Exception) {
                Log.e("ProfileViewModel", "FCM cleanup failed: ${e.message}")
            } finally {
                TokenManager.clearToken(context)
                onComplete()
            }
        }
    }

}