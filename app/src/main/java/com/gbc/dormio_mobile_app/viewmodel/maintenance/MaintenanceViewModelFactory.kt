package com.gbc.dormio_mobile_app.viewmodel.maintenance

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.gbc.dormio_mobile_app.data.repository.MaintenanceRepository

class MaintenanceViewModelFactory(
    private val repository: MaintenanceRepository
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {

        if (modelClass.isAssignableFrom(StudentMaintenanceViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return StudentMaintenanceViewModel(repository) as T
        }

        if (modelClass.isAssignableFrom(AdminMaintenanceViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AdminMaintenanceViewModel(repository) as T
        }

        throw IllegalArgumentException("Unknown ViewModel class")
    }
}