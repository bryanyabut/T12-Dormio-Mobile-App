package com.gbc.dormio_mobile_app.ui

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.gbc.dormio_mobile_app.R
import com.gbc.dormio_mobile_app.data.model.RequestStatus
import com.gbc.dormio_mobile_app.data.model.UrgencyLevel
import com.gbc.dormio_mobile_app.data.repository.MaintenanceRepository
import com.gbc.dormio_mobile_app.network.RetrofitClient.mainteannceApiService
import com.gbc.dormio_mobile_app.viewmodel.maintenance.StudentMaintenanceViewModel

class MaintenanceTestActivity : AppCompatActivity() {

    private lateinit var repository: MaintenanceRepository
    private lateinit var viewModel: StudentMaintenanceViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        repository = MaintenanceRepository(mainteannceApiService)
        viewModel = StudentMaintenanceViewModel(repository)

        enableEdgeToEdge()
        setContentView(R.layout.activity_maintenance_test)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val status = RequestStatus.PENDING.value
        val urgency = UrgencyLevel.HIGH.value

        viewModel.fetchMyRequests()
        viewModel.createRequest("BATHROOM tiles", "Loose tile in the bathroom", urgency)
        viewModel.updateRequest("1", "BATHROOM tiles", "Loose tile in the bathroom - updated", urgency)
        viewModel.requestDetail("1")
    }
}