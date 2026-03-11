package com.gbc.dormio_mobile_app.viewmodel.maintenance

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gbc.dormio_mobile_app.data.model.MaintenanceAllReqUiState
import com.gbc.dormio_mobile_app.data.model.MaintenanceDetailUiState
import com.gbc.dormio_mobile_app.data.model.MaintenanceFormUiState
import com.gbc.dormio_mobile_app.data.model.MaintenanceQuery
import com.gbc.dormio_mobile_app.data.model.UpdateMaintenanceRequestDto
import com.gbc.dormio_mobile_app.data.model.UpdateMaintenanceStatusDto
import com.gbc.dormio_mobile_app.data.repository.MaintenanceRepository
import com.gbc.dormio_mobile_app.utils.NetworkResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AdminMaintenanceViewModel(private val repository: MaintenanceRepository): ViewModel() {

    var TAG = "AdminMaintenanceViewModel"
    private val _allReqState = MutableStateFlow(MaintenanceAllReqUiState())
    val allReqUiState: StateFlow<MaintenanceAllReqUiState> = _allReqState.asStateFlow()

    private val _detailReqState = MutableStateFlow(MaintenanceDetailUiState())
    val detailReqUiState: StateFlow<MaintenanceDetailUiState> = _detailReqState.asStateFlow()

    private val _formState = MutableStateFlow(MaintenanceFormUiState())
    val formUiState: StateFlow<MaintenanceFormUiState> = _formState.asStateFlow()

    private var currentQuery = MaintenanceQuery()


    fun fetchAllRequests(query: MaintenanceQuery = currentQuery) {
        currentQuery = query

        viewModelScope.launch {
            _allReqState.value = _allReqState.value.copy(
                isLoading = true,
                errorMessage = null
            )
            Log.d(TAG, "Fetching requests with status: ${query.status} and urgency: ${query.urgency}")

            val result = repository.getAllRequests(query)

            if (result is NetworkResult.Success) {
                Log.d(TAG, "Fetched ${result.data.data.size} requests")
                _allReqState.value = _allReqState.value.copy(
                    maintenanceRequests = result.data.data,
                    isLoading = false,
                    filterStatus = query.status,
                    filterUrgency = query.urgency
                )
            }
            else if (result is NetworkResult.Error) {
                Log.e(TAG, "Error fetching requests: ${result.apiError.message}")
                _allReqState.value = _allReqState.value.copy(
                    errorMessage = result.apiError.message ?: "An error occurred",
                    isLoading = false
                )
            }
        }
    }

    fun updateSearch(search: String){
        val updatedQuery = currentQuery.copy(search = search, page = 1)
        fetchAllRequests(updatedQuery)
    }

    fun updateStatusFilter(status: String?){
        val updatedQuery = currentQuery.copy(status = status, page = 1)
        fetchAllRequests(updatedQuery)
    }

    fun updateUrgencyFilter(urgency: String?){
        val updatedQuery = currentQuery.copy(urgency = urgency, page = 1)
        fetchAllRequests(updatedQuery)
    }

    fun loadNextPage() {
        if (_allReqState.value.isLoading) return

        val nextPage = currentQuery.page + 1
        val updatedQuery = currentQuery.copy(page = nextPage)
        fetchAllRequests(updatedQuery)
    }

    fun refresh() {
        val refreshedQuery = currentQuery.copy(page = 1)
        fetchAllRequests(refreshedQuery)
    }

    fun requestDetailAdmin(requestId: String) {
        viewModelScope.launch {
            _detailReqState.value = _detailReqState.value.copy(isLoading = true)

            when (val result = repository.getRequestDetailAdmin(requestId)) {
                is NetworkResult.Success -> {
                    Log.d(TAG, "Fetched details for request ID: $requestId")
                    _detailReqState.value = _detailReqState.value.copy(
                        requestDetail = result.data.data,
                        isLoading = false
                    )
                }

                is NetworkResult.Error -> {
                    Log.e(TAG, "Error fetching request details: ${result.apiError.message}")
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

    fun updateRequestAdmin(
        requestId: String,
        title: String? = null,
        description: String? = null,
        urgency: String? = null
    ) {
        viewModelScope.launch {
            _formState.value = _formState.value.copy(isLoading = true)
            Log.d(TAG, "Updating request ID: $requestId with title: $title, description: $description, urgency: $urgency")

            val request = UpdateMaintenanceRequestDto(title, description, urgency)
            when (val result = repository.updateRequestStudent(requestId, request)) {
                is NetworkResult.Success -> {
                    Log.d(TAG, "Request updated successfully with ID: ${result.data.data.id}")
                    _formState.value = _formState.value.copy(
                        successMessage = "Request updated successfully",
                        isLoading = false
                    )

                    refresh()
                }

                is NetworkResult.Error -> {
                    Log.e(TAG, "Error updating request: ${result.apiError.message}")
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

    fun updateRequestStatus(requestId: String, status: String) {
        viewModelScope.launch {
            _formState.value = _formState.value.copy(isLoading = true)
            Log.d(TAG, "Updating request ID: $requestId to status: $status")

            val result = repository.updateRequestStatus(requestId, status)
            when (result) {
                is NetworkResult.Success -> {
                    Log.d(TAG, "Request status updated successfully with ID: ${result.data.data.id}")
                    _formState.value = _formState.value.copy(
                        successMessage = "Request status updated successfully",
                        isLoading = false
                    )

                    refresh()
                }

                is NetworkResult.Error -> {
                    Log.e(TAG, "Error updating request status: ${result.apiError.message}")
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

    fun deleteRequests(requestId: String) {
        viewModelScope.launch {
            _formState.value = _formState.value.copy(isLoading = true)
            Log.d(TAG, "Deleting request ID: $requestId")

            when (val result = repository.deleteRequest(requestId)) {
                is NetworkResult.Success -> {
                    Log.d(TAG, "Request deleted successfully with ID: $requestId")
                    _formState.value = _formState.value.copy(
                        successMessage = "Request deleted successfully",
                        isLoading = false
                    )

                    refresh()
                }

                is NetworkResult.Error -> {
                    Log.e(TAG, "Error deleting request: ${result.apiError.message}")
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