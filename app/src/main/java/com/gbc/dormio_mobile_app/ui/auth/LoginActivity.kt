package com.gbc.dormio_mobile_app.ui.auth

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.gbc.dormio_mobile_app.MainActivity
import com.gbc.dormio_mobile_app.R
import com.gbc.dormio_mobile_app.network.TokenManager
import com.gbc.dormio_mobile_app.viewmodel.auth.AuthViewModel
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class LoginActivity : AppCompatActivity() {

    private val viewModel: AuthViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val token = TokenManager.getToken(this)
        if (token != null) {
            if (!TokenManager.isTokenExpired(this)) {
                navigateToMain()
                return
            }
            TokenManager.clearToken(this)
        }

        setContentView(R.layout.activity_login)

        val emailEditText = findViewById<TextInputEditText>(R.id.emailEditText)
        val passwordEditText = findViewById<TextInputEditText>(R.id.passwordEditText)
        val emailInputLayout = findViewById<TextInputLayout>(R.id.emailInputLayout)
        val passwordInputLayout = findViewById<TextInputLayout>(R.id.passwordInputLayout)
        val loginButton = findViewById<Button>(R.id.loginButton)
        val signUpText = findViewById<TextView>(R.id.signUpText)

        lifecycleScope.launch {
            viewModel.uiState.collectLatest { state ->
                loginButton.isEnabled = !state.isLoading

                if (state.token != null) {
                    navigateToMain()
                    return@collectLatest
                }

                if (state.errorMessage != null) {
                    passwordInputLayout.error = state.errorMessage
                }
            }
        }

        loginButton.setOnClickListener {
            val email = emailEditText.text?.toString()?.trim() ?: ""
            val password = passwordEditText.text?.toString() ?: ""

            emailInputLayout.error = null
            passwordInputLayout.error = null

            if (email.isEmpty()) {
                emailInputLayout.error = "Email is required"
                return@setOnClickListener
            }
            if (password.isEmpty()) {
                passwordInputLayout.error = "Password is required"
                return@setOnClickListener
            }

            viewModel.login(email, password)
        }

        signUpText.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }

    private fun navigateToMain() {
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }
}
