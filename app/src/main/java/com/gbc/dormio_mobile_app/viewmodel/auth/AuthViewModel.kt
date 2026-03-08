package com.gbc.dormio_mobile_app.viewmodel.auth

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gbc.dormio_mobile_app.model.*
import com.gbc.dormio_mobile_app.network.TokenManager
import com.gbc.dormio_mobile_app.utils.NetworkResult
import com.gbc.dormio_mobile_app.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AuthViewModel(
    private val repository: AuthRepository,
    private val appContext: Context
): ViewModel(){
    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    fun login(email: String, password: String) {
        viewModelScope.launch {
            _uiState.value = AuthUiState(isLoading = true)

            when (val result = repository.login(email, password)) {
                is NetworkResult.Success -> {
                    val user = result.data.user
                    val token = result.data.token

                    TokenManager.saveToken(appContext, token)

                    _uiState.value = AuthUiState(user = user, token = token, isLoading = false)
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