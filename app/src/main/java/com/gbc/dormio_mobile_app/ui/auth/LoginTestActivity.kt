package com.gbc.dormio_mobile_app.ui.auth

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.gbc.dormio_mobile_app.R
import com.gbc.dormio_mobile_app.data.repository.AuthRepository
import com.gbc.dormio_mobile_app.network.RetrofitClient
import com.gbc.dormio_mobile_app.ui.maintenance.MaintenanceActivity
import com.gbc.dormio_mobile_app.viewmodel.auth.AuthViewModel
import com.gbc.dormio_mobile_app.viewmodel.auth.AuthViewModelFactory
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class LoginTestActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        RetrofitClient.initialize(this)

        val repository = AuthRepository(RetrofitClient.authApiService)
        val factory = AuthViewModelFactory(repository, this)
        val viewModel = androidx.lifecycle.ViewModelProvider(this, factory).get(AuthViewModel::class.java)

        enableEdgeToEdge()
        setContentView(R.layout.activity_login_test)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        lifecycleScope.launch {
            viewModel.uiState.collectLatest { state ->
                if (state.token != null) {
                    val role = state.user?.role ?: "STUDENT"
                    val intent = Intent(this@LoginTestActivity, MaintenanceActivity::class.java)
                    intent.putExtra("user_role", role)
                    startActivity(intent)
                }
            }
        }

        viewModel.login(
//            email = "john.doe@example.com",
//            password = "pass123"
            email = "admin@dormio.com",
            password = "adminpass"
        )
    }
}