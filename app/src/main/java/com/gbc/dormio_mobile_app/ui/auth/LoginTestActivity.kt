package com.gbc.dormio_mobile_app.ui.auth

import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.gbc.dormio_mobile_app.MainActivity
import com.gbc.dormio_mobile_app.R
import com.gbc.dormio_mobile_app.ui.maintenance.MaintenanceActivity
import com.gbc.dormio_mobile_app.viewmodel.auth.AuthViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class LoginTestActivity : AppCompatActivity() {

    private val viewModel: AuthViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requestPermissions(
                arrayOf(android.Manifest.permission.POST_NOTIFICATIONS),
                1
            )
        }

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
                    val intent = Intent(this@LoginTestActivity, MainActivity::class.java)
                    intent.putExtra("user_role", role)
                    startActivity(intent)
                    finish()
                }
            }
        }

        viewModel.login(
            email = "john.doe@example.com",
            password = "pass123"
//            email = "admin@dormio.com",
//            password = "adminpass"
        )
    }
}