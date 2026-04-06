package com.gbc.dormio_mobile_app.viewmodel.maintenance

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gbc.dormio_mobile_app.data.model.maintenance.*
import com.gbc.dormio_mobile_app.data.repository.MaintenanceRepository
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
class MaintenanceListViewModel @Inject constructor(
    private val repository: MaintenanceRepository,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _uiState = MutableStateFlow(MaintenanceAllReqUiState())
    val uiState: StateFlow<MaintenanceAllReqUiState> = _uiState.asStateFlow()

    init {
        val role = TokenManager.getUserRole(context) ?: UserRole.STUDENT.value
        _uiState.update { it.copy(userRole = role) }
        fetchRequests()
    }

    fun fetchRequests(isRefresh: Boolean = false) {
        if (isRefresh) {
            _uiState.update { it.copy(
                currentPage = 1,
                maintenanceRequests = emptyList()
            ) }
        }

        _uiState.update { it.copy(
            isLoading = true,
            errorMessage = null
        ) }

        viewModelScope.launch {
            val currentState = _uiState.value
            val query = MaintenanceQuery(
                status = currentState.filterStatus,
                urgency = currentState.filterUrgency,
                page = currentState.currentPage
            )

            val result = if (currentState.userRole == UserRole.ADMIN.value) {
                repository.getAllRequests(query)
            } else {
                repository.getMyRequests(query)
            }

            when (result) {
                is NetworkResult.Success -> {
                    val response = result.data
                    _uiState.update { it.copy(
                        isLoading = false,
                        maintenanceRequests = currentState.maintenanceRequests + response.data,
                        currentPage = response.page + 1,
                        hasMorePages = response.page < response.totalPages
                    )}
                }
                is NetworkResult.Error -> {
                    _uiState.update { it.copy(
                        isLoading = false,
                        errorMessage = result.apiError.message ?: "Error loading requests"
                    )}
                }
                is NetworkResult.Loading -> {
                    _uiState.update { it.copy(isLoading = true) }
                }
            }
        }
    }

    fun updateFilters(status: String?, urgency: String?) {
        _uiState.update { it.copy(filterStatus = status, filterUrgency = urgency) }
        fetchRequests(isRefresh = true)
    }
}