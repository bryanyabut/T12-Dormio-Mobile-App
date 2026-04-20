package com.gbc.dormio_mobile_app.viewmodel.schedule

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gbc.dormio_mobile_app.data.model.schedule.CreateScheduleRequest
import com.gbc.dormio_mobile_app.data.model.schedule.ScheduleUiState
import com.gbc.dormio_mobile_app.data.repository.ScheduleRepository
import com.gbc.dormio_mobile_app.utils.NetworkResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ScheduleViewModel @Inject constructor(
    private val repository: ScheduleRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ScheduleUiState())
    val uiState: StateFlow<ScheduleUiState> = _uiState.asStateFlow()

    fun fetchSchedules() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            when (val result = repository.getSchedules()) {
                is NetworkResult.Success -> {
                    _uiState.update { it.copy(isLoading = false, schedules = result.data.data) }
                }
                is NetworkResult.Error -> {
                    _uiState.update { it.copy(isLoading = false, errorMessage = result.apiError.message ?: "Failed to load schedules") }
                }
                else -> {}
            }
        }
    }

    fun createSchedule(
        title: String,
        type: String,
        startTime: String,
        endTime: String,
        description: String? = null,
        location: String? = null
    ) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null, successMessage = null) }
            val request = CreateScheduleRequest(title, type, startTime, endTime, description, location)
            when (val result = repository.createSchedule(request)) {
                is NetworkResult.Success -> {
                    _uiState.update { it.copy(isLoading = false, successMessage = "Event saved!") }
                    fetchSchedules()
                }
                is NetworkResult.Error -> {
                    _uiState.update { it.copy(isLoading = false, errorMessage = result.apiError.message ?: "Failed to save event") }
                }
                else -> {}
            }
        }
    }

    fun deleteSchedule(id: Int) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            when (val result = repository.deleteSchedule(id)) {
                is NetworkResult.Success -> {
                    _uiState.update { it.copy(isLoading = false, successMessage = "Event deleted.") }
                    fetchSchedules()
                }
                is NetworkResult.Error -> {
                    _uiState.update { it.copy(isLoading = false, errorMessage = result.apiError.message ?: "Failed to delete event") }
                }
                else -> {}
            }
        }
    }

    fun clearMessages() {
        _uiState.update { it.copy(errorMessage = null, successMessage = null) }
    }
}
