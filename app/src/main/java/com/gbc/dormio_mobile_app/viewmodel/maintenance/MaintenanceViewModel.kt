package com.gbc.dormio_mobile_app.viewmodel.maintenance

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gbc.dormio_mobile_app.data.model.maintenance.MaintenanceFormUiState
import com.gbc.dormio_mobile_app.data.repository.MaintenanceRepository
import com.gbc.dormio_mobile_app.network.TokenManager
import com.gbc.dormio_mobile_app.utils.FileHandle
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
class MaintenanceViewModel @Inject constructor(
    private val repository: MaintenanceRepository,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _formState = MutableStateFlow(MaintenanceFormUiState())
    val formState: StateFlow<MaintenanceFormUiState> = _formState.asStateFlow()


    init {
        val role = TokenManager.getUserRole(context)
        _formState.update { it.copy(userRole = role) }
    }

    fun createMaintenanceRequest(
        title: String,
        description: String,
        urgency: String,
        imageUri: Uri?
    ) {
        _formState.update { it.copy(isLoading = true, errorMessage = null, successMessage = null) }

        viewModelScope.launch {
            val imageFile = imageUri?.let { uri ->
                FileHandle.prepareImage(context, "image", uri)?.file
            }

            when (val result = repository.createRequest(title, description, urgency, imageFile)) {
                is NetworkResult.Success -> {
                    _formState.update { it.copy(
                        isLoading = false,
                        successMessage = "Request created successfully!"
                    )}
                    imageFile?.delete()
                }

                is NetworkResult.Error -> {
                    _formState.update { it.copy(
                        isLoading = false,
                        errorMessage = result.apiError.message ?: "Failed to create request"
                    )}
                    imageFile?.delete()
                }

                NetworkResult.Loading -> {
                    _formState.update { it.copy(isLoading = true) }
                }
            }
        }
    }

    fun resetFormState() {
        val currentRole = _formState.value.userRole
        _formState.value = MaintenanceFormUiState(userRole = currentRole)
    }
}