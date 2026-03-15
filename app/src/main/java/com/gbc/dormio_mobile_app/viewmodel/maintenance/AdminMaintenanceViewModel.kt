package com.gbc.dormio_mobile_app.viewmodel.maintenance

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gbc.dormio_mobile_app.data.model.MaintenanceAllReqUiState
import com.gbc.dormio_mobile_app.data.model.MaintenanceDetailUiState
import com.gbc.dormio_mobile_app.data.model.MaintenanceFormUiState
import com.gbc.dormio_mobile_app.data.model.MaintenanceQuery
import com.gbc.dormio_mobile_app.data.model.UpdateMaintenanceRequestDto
import com.gbc.dormio_mobile_app.data.repository.MaintenanceRepository
import com.gbc.dormio_mobile_app.utils.NetworkResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AdminMaintenanceViewModel @Inject constructor(
    private val repository: MaintenanceRepository
): ViewModel() {

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
            Log.d("StudentMaintenanceViewModel", "Fetching requests with status: ${query.status} and urgency: ${query.urgency}")

            val result = repository.getAllRequests(query)

            if (result is NetworkResult.Success) {
                Log.d("StudentMaintenanceViewModel", "Fetched ${result.data.data.size} requests")
                val newItems = result.data.data

                // If this is page 1, replace; otherwise append
                val updatedList = if (query.page <= 1) {
                    newItems
                } else {
                    _allReqState.value.maintenanceRequests + newItems
                }

                val hasMore = newItems.size >= query.limit

                _allReqState.value = _allReqState.value.copy(
                    maintenanceRequests = updatedList,
                    isLoading = false,
                    filterStatus = query.status,
                    filterUrgency = query.urgency,
                    currentPage = query.page,
                    hasMorePages = hasMore
                )
            }
            else if (result is NetworkResult.Error) {
                Log.e("StudentMaintenanceViewModel", "Error fetching requests: ${result.apiError.message}")
                val apiMsg = result.apiError.message ?: "An error occurred"

                if (apiMsg.contains("no maintenance requests", ignoreCase = true)) {
                    if (query.page > 1) {
                        _allReqState.value = _allReqState.value.copy(
                            isLoading = false,
                            hasMorePages = false,
                            errorMessage = null
                        )
                    } else {
                        _allReqState.value = _allReqState.value.copy(
                            maintenanceRequests = emptyList(),
                            isLoading = false,
                            errorMessage = null,
                            currentPage = 1,
                            hasMorePages = false
                        )
                    }
                } else {
                    _allReqState.value = _allReqState.value.copy(
                        errorMessage = apiMsg,
                        isLoading = false
                    )
                }
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
                    refresh()
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
            refresh()

            val result = repository.updateRequestStatus(requestId, status)
            when (result) {
                is NetworkResult.Success -> {
                    Log.d(TAG, "Request status updated successfully with ID: ${result.data.data.id}")
                    _formState.value = _formState.value.copy(
                        successMessage = "Request status updated successfully",
                        isLoading = false
                    )
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