package com.gbc.dormio_mobile_app.viewmodel.mealplan

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gbc.dormio_mobile_app.data.model.mealplan.MealPlanUiState
import com.gbc.dormio_mobile_app.data.repository.MealPlanRepository
import com.gbc.dormio_mobile_app.utils.NetworkResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MealPlanViewModel @Inject constructor(
    private val repository: MealPlanRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(MealPlanUiState())
    val uiState: StateFlow<MealPlanUiState> = _uiState.asStateFlow()

    init {
        fetchMealPlans()
    }

    //functions to fetch meal plan
    fun fetchMealPlans(){
        _uiState.update {
            it.copy(
                isLoading = true,
                errorMessage = null
            )
        }

        viewModelScope.launch{
            when (val result = repository.getAllMealPlans()) {
                is NetworkResult.Success -> {
                    _uiState.update {
                        it.copy(
                            mealPlans = result.data,
                            isLoading = false
                        ) }
                }

                is NetworkResult.Error -> {
                    _uiState.update { it.copy(
                        isLoading = false,
                        errorMessage = result.apiError.message
                    )}
                }

                is NetworkResult.Loading -> {
                    _uiState.update { it.copy(
                        isLoading = true
                    )}
                }
            }
        }
    }

    // function to fetch meal plan details
    fun fetchWeeklyPlan(id: Int){
        _uiState.update {
            it.copy(
                isLoading = true,
                errorMessage = null
            )
        }

        viewModelScope.launch{
            when (val result = repository.getWeeklyPlan(id)) {
                is NetworkResult.Success -> {
                    _uiState.update {
                        it.copy(
                            weeklyPlan = result.data,
                            isLoading = false
                        ) }
                }

                is NetworkResult.Error -> {
                    _uiState.update { it.copy(
                        isLoading = false,
                        errorMessage = result.apiError.message
                    )}
                }

                is NetworkResult.Loading -> {
                    _uiState.update { it.copy(
                        isLoading = true
                    )}
                }
            }
        }
    }
}