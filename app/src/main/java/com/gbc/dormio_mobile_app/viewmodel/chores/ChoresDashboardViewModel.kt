package com.gbc.dormio_mobile_app.viewmodel.chores

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gbc.dormio_mobile_app.data.model.chores.ChoresDashboardUiState
import com.gbc.dormio_mobile_app.data.model.chores.DashboardData
import com.gbc.dormio_mobile_app.data.repository.ChoresRepository
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
class ChoresDashboardViewModel @Inject constructor(
    private val choresRepository: ChoresRepository
) : ViewModel() {

    private val _dashboardState = MutableStateFlow(ChoresDashboardUiState(isLoading = true))
    val dashboardState: StateFlow<ChoresDashboardUiState> = _dashboardState.asStateFlow()

    init {
        getDashboard()
    }

    fun getDashboard() {
        viewModelScope.launch {
            _dashboardState.update { it.copy(isLoading = true) }

            when (val result = choresRepository.getChoreDashboard()) {
                is NetworkResult.Success -> {
                    _dashboardState.update { it.copy(
                        isLoading = false,
                        data = result.data,
                        errorMessage = null
                    )}
                }
                is NetworkResult.Error -> {
                    _dashboardState.update { it.copy(
                        isLoading = false,
                        errorMessage = result.apiError.message
                    )}
                }
                else -> {}
            }
        }
    }

    fun refreshDashboard() {
        getDashboard()
    }

    fun markChoreComplete(choreId: Int) {
        _dashboardState.update { it.copy(actionLoading = true, errorMessage = null) }

        viewModelScope.launch {
            val result = choresRepository.markChoreComplete(choreId)

            when (result) {
                is NetworkResult.Success -> {
                    _dashboardState.update { it.copy(
                        actionLoading = false,
                        successMessage = "Chore marked as completed!"
                    )}
                    getDashboard()
                }
                is NetworkResult.Error -> {
                    _dashboardState.update { it.copy(
                        actionLoading = false,
                        errorMessage = result.apiError.message
                    )}
                }
                else -> {}
            }
        }
    }

    fun clearMessages() {
        _dashboardState.update { it.copy(
            successMessage = null,
            errorMessage = null
        )}
    }


}