package com.gbc.dormio_mobile_app.viewmodel.maintenance

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gbc.dormio_mobile_app.data.model.CreateMaintenanceRequestDto
import com.gbc.dormio_mobile_app.data.model.MaintenanceAllReqUiState
import com.gbc.dormio_mobile_app.data.model.MaintenanceDetailUiState
import com.gbc.dormio_mobile_app.data.model.MaintenanceFormUiState
import com.gbc.dormio_mobile_app.data.model.UpdateMaintenanceRequestDto
import com.gbc.dormio_mobile_app.data.repository.MaintenanceRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import com.gbc.dormio_mobile_app.utils.NetworkResult

class StudentMaintenanceViewModel(private val repository: MaintenanceRepository): ViewModel() {

    private val _allReqState = MutableStateFlow(MaintenanceAllReqUiState())
    val allReqUiState: StateFlow<MaintenanceAllReqUiState> = _allReqState.asStateFlow()

    private val _detailReqState = MutableStateFlow(MaintenanceDetailUiState())
    val detailReqUiState: StateFlow<MaintenanceDetailUiState> = _detailReqState.asStateFlow()

    private val _formState = MutableStateFlow(MaintenanceFormUiState())
    val formUiState: StateFlow<MaintenanceFormUiState> = _formState.asStateFlow()

    fun fetchMyRequests(status: String? = null, urgency: String? = null) {
        viewModelScope.launch {
            _allReqState.value = _allReqState.value.copy(isLoading = true)
            Log.d("StudentMaintenanceViewModel", "Fetching requests with status: $status and urgency: $urgency")

            when (val result = repository.getMyRequests(status, urgency)) {
                is NetworkResult.Success -> {
                    Log.d("StudentMaintenanceViewModel", "Fetched ${result.data.data.size} requests")
                    _allReqState.value = _allReqState.value.copy(
                        maintenanceRequests = result.data.data,
                        isLoading = false
                    )
                }

                is NetworkResult.Error -> {
                        Log.e("StudentMaintenanceViewModel", "Error fetching requests: ${result.apiError.message}")
                    _allReqState.value = _allReqState.value.copy(
                        errorMessage = result.apiError.message ?: "An error occurred",
                        isLoading = false
                    )
                }

                is NetworkResult.Loading -> {
                    _allReqState.value = _allReqState.value.copy(
                        isLoading = true
                    )
                }
            }
        }
    }

    fun requestDetail(requestId: String) {
        viewModelScope.launch {
            _detailReqState.value = _detailReqState.value.copy(isLoading = true)

            when (val result = repository.getRequestDetailStudent(requestId)) {
                is NetworkResult.Success -> {
                    Log.d("StudentMaintenanceViewModel", "Fetched details for request ID: $requestId")
                    _detailReqState.value = _detailReqState.value.copy(
                        requestDetail = result.data.data,
                        isLoading = false
                    )
                }

                is NetworkResult.Error -> {
                    Log.e("StudentMaintenanceViewModel", "Error fetching request details: ${result.apiError.message}")
                    _detailReqState.value = _detailReqState.value.copy(
                        errorMessage = result.apiError.message ?: "An error occurred",
                        isLoading = false
                    )
                }

                is NetworkResult.Loading -> {
                    _detailReqState.value = _detailReqState.value.copy(
                        isLoading = true
                    )
                }
            }
        }
    }

    fun createRequest(title: String, description: String, urgency: String) {
        viewModelScope.launch {
            _formState.value = _formState.value.copy(isLoading = true)
            Log.d("StudentVM", "Creating request: $title, $description, $urgency")

            val request = CreateMaintenanceRequestDto(title, description, urgency)
            when (val result = repository.createRequest(request)) {
                is NetworkResult.Success -> {
                    Log.d("StudentVM", "Request created successfully with ID: ${result.data.data.id}")
                    _formState.value = _formState.value.copy(
                        successMessage = "Request created successfully",
                        isLoading = false
                    )
                }

                is NetworkResult.Error -> {
                    Log.e("StudentVM", "Error creating request: ${result.apiError.message}")
                    _formState.value = _formState.value.copy(
                        errorMessage = result.apiError.message ?: "An error occurred",
                        isLoading = false
                    )
                }

                is NetworkResult.Loading -> {
                    _formState.value = _formState.value.copy(
                        isLoading = true
                    )
                }
            }
        }
    }

    fun updateRequest(
        requestId: String,
        title: String? = null,
        description: String? = null,
        urgency: String? = null
    ) {
        viewModelScope.launch {
            _formState.value = _formState.value.copy(isLoading = true)
            Log.d("StudentVM", "Updating request ID: $requestId with title: $title, description: $description, urgency: $urgency")

            val request = UpdateMaintenanceRequestDto(title, description, urgency)
            when (val result = repository.updateRequestStudent(requestId, request)) {
                is NetworkResult.Success -> {
                    Log.d("StudentVM", "Request updated successfully with ID: ${result.data.data.id}")
                    _formState.value = _formState.value.copy(
                        successMessage = "Request updated successfully",
                        isLoading = false
                    )
                }

                is NetworkResult.Error -> {
                    Log.e("StudentVM", "Error updating request: ${result.apiError.message}")
                    _formState.value = _formState.value.copy(
                        errorMessage = result.apiError.message ?: "An error occurred",
                        isLoading = false
                    )
                }

                is NetworkResult.Loading -> {
                    _formState.value = _formState.value.copy(
                        isLoading = true
                    )
                }
            }
        }
    }
}