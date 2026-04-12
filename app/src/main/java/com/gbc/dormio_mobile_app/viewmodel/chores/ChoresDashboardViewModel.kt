package com.gbc.dormio_mobile_app.viewmodel.chores

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gbc.dormio_mobile_app.data.model.chores.DashboardData
import com.gbc.dormio_mobile_app.data.repository.ChoresRepository
import com.gbc.dormio_mobile_app.utils.NetworkResult
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChoresDashboardViewModel @Inject constructor(
    private val choresRepository: ChoresRepository
) : ViewModel() {

    private val _dashboardState = MutableStateFlow<NetworkResult<DashboardData>>(NetworkResult.Loading)
    val dashboardState: StateFlow<NetworkResult<DashboardData>> = _dashboardState.asStateFlow()

    init {
        getDashboard()
    }

    fun getDashboard() {
        viewModelScope.launch {
            _dashboardState.value = NetworkResult.Loading

            val result = choresRepository.getChoreDashboard()

            _dashboardState.value = result
        }
    }
}