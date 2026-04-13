package com.gbc.dormio_mobile_app.viewmodel.chores

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gbc.dormio_mobile_app.data.model.chores.AddChoreRequest
import com.gbc.dormio_mobile_app.data.model.chores.ChoreFormUiState
import com.gbc.dormio_mobile_app.data.repository.ChoresRepository
import com.gbc.dormio_mobile_app.network.TokenManager // Import your TokenManager
import com.gbc.dormio_mobile_app.utils.NetworkResult
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddChoreViewModel @Inject constructor(
    private val repository: ChoresRepository,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _uiState = MutableStateFlow(ChoreFormUiState())
    val uiState: StateFlow<ChoreFormUiState> = _uiState.asStateFlow()

    init {
        fetchHousemates()
    }

    private fun fetchHousemates() {
        val currentUserId = TokenManager.getUserId(context)

        _uiState.update { it.copy(isLoading = true, errorMessage = null) }

        viewModelScope.launch {
            when (val result = repository.getHousemates()) {
                is NetworkResult.Success -> {
                    val processedHousemates = result.data?.map { housemate ->
                        housemate.copy(
                            isCurrentUser = housemate.id == currentUserId
                        )
                    } ?: emptyList()

                    _uiState.update { it.copy(
                        isLoading = false,
                        housemates = processedHousemates
                    )}
                }
                is NetworkResult.Error -> {
                    _uiState.update { it.copy(
                        isLoading = false,
                        errorMessage = result.apiError.message ?: "Failed to load housemates"
                    )}
                }
                NetworkResult.Loading -> {
                    _uiState.update { it.copy(isLoading = true) }
                }
            }
        }
    }

    fun toggleUserSelection(userId: Int) {
        _uiState.update { currentState ->
            val updatedHousemates = currentState.housemates.map { housemate ->
                if (housemate.id == userId) {
                    housemate.copy(isSelected = !housemate.isSelected)
                } else {
                    housemate
                }
            }

            val selectedIds = updatedHousemates.filter { it.isSelected }.map { it.id }

            currentState.copy(
                housemates = updatedHousemates,
                assignedUserIds = selectedIds
            )
        }
    }

    fun createChore(
        name: String,
        description: String?,
        dueDate: String
    ) {
        val assignedIds = _uiState.value.assignedUserIds

        if (name.isBlank() || assignedIds.isEmpty()) {
            _uiState.update { it.copy(errorMessage = "Name and at least one assignee required") }
            return
        }

        _uiState.update { it.copy(
            isLoading = true,
            errorMessage = null,
            successMessage = null,
            isSuccess = false
        )}

        viewModelScope.launch {
            val request = AddChoreRequest(name, description, dueDate, assignedIds)

            when (val result = repository.createChore(request)) {
                is NetworkResult.Success -> {
                    _uiState.update { it.copy(
                        isLoading = false,
                        isSuccess = true,
                        successMessage = "Chore created successfully!"
                    )}
                }
                is NetworkResult.Error -> {
                    _uiState.update { it.copy(
                        isLoading = false,
                        errorMessage = result.apiError.message ?: "Failed to create chore"
                    )}
                }
                else -> {}
            }
        }
    }

    fun resetState() {
        _uiState.value = ChoreFormUiState()
        fetchHousemates()
    }
}