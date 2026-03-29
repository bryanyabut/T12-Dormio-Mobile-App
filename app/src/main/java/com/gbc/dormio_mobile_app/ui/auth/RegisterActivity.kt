package com.gbc.dormio_mobile_app.ui.auth

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.gbc.dormio_mobile_app.R
import com.gbc.dormio_mobile_app.viewmodel.auth.AuthViewModel
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class RegisterActivity : AppCompatActivity() {

    private val viewModel: AuthViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        val nameEditText = findViewById<TextInputEditText>(R.id.nameEditText)
        val emailEditText = findViewById<TextInputEditText>(R.id.emailEditText)
        val passwordEditText = findViewById<TextInputEditText>(R.id.passwordEditText)
        val nameInputLayout = findViewById<TextInputLayout>(R.id.nameInputLayout)
        val emailInputLayout = findViewById<TextInputLayout>(R.id.emailInputLayout)
        val passwordInputLayout = findViewById<TextInputLayout>(R.id.passwordInputLayout)
        val createAccountButton = findViewById<Button>(R.id.createAccountButton)
        val loginText = findViewById<TextView>(R.id.loginText)

        lifecycleScope.launch {
            viewModel.uiState.collectLatest { state ->
                createAccountButton.isEnabled = !state.isLoading

                if (state.user != null && !state.isLoading) {
                    val intent = Intent(this@RegisterActivity, LoginActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                    startActivity(intent)
                    finish()
                    return@collectLatest
                }

                if (state.errorMessage != null) {
                    emailInputLayout.error = state.errorMessage
                }
            }
        }

        createAccountButton.setOnClickListener {
            val fullName = nameEditText.text?.toString()?.trim() ?: ""
            val email = emailEditText.text?.toString()?.trim() ?: ""
            val password = passwordEditText.text?.toString() ?: ""

            nameInputLayout.error = null
            emailInputLayout.error = null
            passwordInputLayout.error = null

            if (fullName.isEmpty()) {
                nameInputLayout.error = "Name is required"
                return@setOnClickListener
            }
            if (email.isEmpty()) {
                emailInputLayout.error = "Email is required"
                return@setOnClickListener
            }
            if (password.isEmpty()) {
                passwordInputLayout.error = "Password is required"
                return@setOnClickListener
            }

            val parts = fullName.split(" ", limit = 2)
            val firstName = parts[0]
            val lastName = if (parts.size > 1) parts[1] else ""

            viewModel.register(email, password, firstName, lastName)
        }

        loginText.setOnClickListener {
            finish()
        }
    }
}
