package com.gbc.dormio_mobile_app.viewmodel.auth

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gbc.dormio_mobile_app.model.*
import com.gbc.dormio_mobile_app.network.TokenManager
import com.gbc.dormio_mobile_app.utils.NetworkResult
import com.gbc.dormio_mobile_app.repository.AuthRepository
import kotlinx.coroutines.launch

class AuthViewModel(
    private val repository: AuthRepository,
    private val appContext: Context
): ViewModel(){
    private val _loginResult = MutableLiveData<NetworkResult<LoginResponse>>()
    val loginResult: LiveData<NetworkResult<LoginResponse>> = _loginResult

    private val _registerResult = MutableLiveData<NetworkResult<RegisterResponse>>()
    val registerResult: LiveData<NetworkResult<RegisterResponse>> = _registerResult

    fun login(email: String, password: String) {
        viewModelScope.launch {
            _loginResult.value = NetworkResult.Loading
            val result = repository.login(email, password)
            _loginResult.value = result

            if (result is NetworkResult.Success) {
                //save token
                val token = result.data.token
                TokenManager.saveToken(appContext, token)
            }

        }
    }

    fun register(email: String, password: String, firstName: String, lastName: String, role: String? = "STUDENT") {
        viewModelScope.launch {
            _registerResult.value = NetworkResult.Loading
            val result = repository.register(email, password, firstName, lastName, role)
            _registerResult.value = result
        }
    }
}