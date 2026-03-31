package com.gbc.dormio_mobile_app.viewmodel.profile

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gbc.dormio_mobile_app.data.model.profile.ProfileUiState
import com.gbc.dormio_mobile_app.data.repository.ProfileRepository
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

}