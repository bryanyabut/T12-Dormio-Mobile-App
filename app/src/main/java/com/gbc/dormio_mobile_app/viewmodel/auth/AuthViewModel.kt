package com.gbc.dormio_mobile_app.viewmodel.auth

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gbc.dormio_mobile_app.data.model.AuthUiState
import com.gbc.dormio_mobile_app.network.FcmTokenManager
import com.gbc.dormio_mobile_app.network.TokenManager
import com.gbc.dormio_mobile_app.utils.NetworkResult
import com.gbc.dormio_mobile_app.data.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val repository: AuthRepository,
    @param:ApplicationContext private val appContext: Context,
    private val fcmTokenManager: FcmTokenManager
): ViewModel(){
    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    fun login(email: String, password: String) {
        viewModelScope.launch {
            _uiState.value = AuthUiState(isLoading = true)

            when (val result = repository.login(email, password)) {
                is NetworkResult.Success -> {
                    val user = result.data.user
                    val jwt = result.data.token
                    val role = user.role ?: "STUDENT"
                    TokenManager.saveToken(appContext, jwt, role)

                    //immediately attempt to resend FCM token after login
                    viewModelScope.launch {
                        try {
                            fcmTokenManager.resendTokenIfAvailable(appContext)
                        } catch (e: Exception) {
                            Log.e("FCM", "Failed to resend FCM token after login: ${e.message}")
                        }
                    }

                    _uiState.value = AuthUiState(user = user, token = jwt, isLoading = false)
                }
                is NetworkResult.Error -> {
                    _uiState.value = AuthUiState(
                        errorMessage = result.apiError.message ?: "An error occurred",
                        isLoading = false
                    )
                }
                else -> {
                    _uiState.value = AuthUiState(
                        errorMessage = "An unexpected error occurred",
                        isLoading = false
                    )
                }
            }
        }
    }

    fun register(email: String, password: String, firstName: String, lastName: String, role: String? = "STUDENT") {
        viewModelScope.launch {
            _uiState.value = AuthUiState(isLoading = true)

            when(val result = repository.register(email, password, firstName, lastName, role)){
                is NetworkResult.Success -> {
                    val user = result.data.user
                    _uiState.value = AuthUiState(user = user, isLoading = false)
                }
                is NetworkResult.Error -> {
                    _uiState.value = AuthUiState(
                        errorMessage = result.apiError.message ?: "An error occurred",
                        isLoading = false
                    )
                }
                else -> {
                    _uiState.value = AuthUiState(
                        errorMessage = "An unexpected error occurred",
                        isLoading = false
                    )
                }
            }
        }
    }
}