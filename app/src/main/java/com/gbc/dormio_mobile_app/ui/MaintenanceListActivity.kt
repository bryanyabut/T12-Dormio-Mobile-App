package com.gbc.dormio_mobile_app.ui

import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.gbc.dormio_mobile_app.R
import com.gbc.dormio_mobile_app.data.model.RequestStatus
import com.gbc.dormio_mobile_app.data.model.UrgencyLevel
import com.gbc.dormio_mobile_app.data.model.MaintenanceQuery
import com.gbc.dormio_mobile_app.data.repository.MaintenanceRepository
import com.gbc.dormio_mobile_app.network.RetrofitClient.maintenanceApiService
import com.gbc.dormio_mobile_app.viewmodel.maintenance.StudentMaintenanceViewModel
import kotlinx.coroutines.launch

class MaintenanceListActivity : AppCompatActivity() {

    private lateinit var repository: MaintenanceRepository
    private lateinit var viewModel: StudentMaintenanceViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        repository = MaintenanceRepository(maintenanceApiService)
        viewModel = StudentMaintenanceViewModel(repository)

        enableEdgeToEdge()
        setContentView(R.layout.activity_maintenance_list)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        lifecycleScope.launch{
            viewModel.allReqUiState.collect { state ->
                if (state.isLoading){
                    Log.d("MaintenanceListActivity", "Loading maintenance requests...")
                }else if (state.errorMessage != null){
                    Log.e("MaintenanceListActivity", "Error: ${state.errorMessage}")
                }else {
                    Log.d(
                        "MaintenanceListActivity",
                        "Fetched ${state.maintenanceRequests.size} maintenance requests"
                    )
                    state.maintenanceRequests.forEach { request ->
                        Log.d(
                            "MaintenanceListActivity",
                            "Request ID: ${request.id}, Title: ${request.title}, Status: ${request.status}, Urgency: ${request.urgency}"
                        )
                    }
                }
            }
        }

        lifecycleScope.launch {
            viewModel.formUiState.collect { formState ->
                if (formState.isLoading) {
                    Log.d("MaintenanceListActivity", "Submitting maintenance request...")
                } else if (formState.errorMessage != null) {
                    Log.e("MaintenanceListActivity", "Error: ${formState.errorMessage}")
                } else if (formState.successMessage != null) {
                    Log.d("MaintenanceListActivity", "Success: ${formState.successMessage}")
                }
            }
        }

        lifecycleScope.launch {
            viewModel.detailReqUiState.collect { detailState ->
                if(detailState.isLoading){
                    Log.d("MaintenanceListActivity", "Loading maintenance request details...")
                }else if (detailState.errorMessage != null){
                    Log.e("MaintenanceListActivity", "Error: ${detailState.errorMessage}")
                }else if (detailState.requestDetail != null){
                    Log.d("MaintenanceTest", "Detail: ${detailState.requestDetail.title}")
                }
            }
        }

        val status = RequestStatus.PENDING.value
        val urgency = UrgencyLevel.HIGH.value

        val query = MaintenanceQuery(
            search = "floor",
            status = status,
            urgency = urgency
        )

        viewModel.fetchMyRequests(query)
        viewModel.createRequest("BATHROOM tiles", "Loose tile in the bathroom", urgency)
        viewModel.updateRequest("1", "BATHROOM tiles", "Loose tile in the bathroom updated", urgency)
        viewModel.requestDetail("1")

    }
}