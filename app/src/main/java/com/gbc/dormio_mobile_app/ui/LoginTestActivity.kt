package com.gbc.dormio_mobile_app.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.gbc.dormio_mobile_app.R
import com.gbc.dormio_mobile_app.data.repository.AuthRepository
import com.gbc.dormio_mobile_app.network.RetrofitClient
import com.gbc.dormio_mobile_app.viewmodel.auth.AuthViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class LoginTestActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        RetrofitClient.initialize(this)

        val repository = AuthRepository(RetrofitClient.authApiService)
        val viewModel = AuthViewModel(repository, this)

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
                    // Token is saved, start maintenance test activity
                    startActivity(
                        Intent(
                            this@LoginTestActivity,
                            MaintenanceTestActivity::class.java
                        )
                    )
                }
            }
        }

        viewModel.login(
            email = "john.doe@example.com",
            password = "pass123"
        )
    }
}