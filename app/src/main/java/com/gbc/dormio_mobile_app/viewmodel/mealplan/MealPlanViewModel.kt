package com.gbc.dormio_mobile_app.viewmodel.mealplan

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gbc.dormio_mobile_app.data.model.mealplan.AdminTemplateRequest
import com.gbc.dormio_mobile_app.data.model.mealplan.MealPlanUiState
import com.gbc.dormio_mobile_app.data.repository.MealPlanRepository
import com.gbc.dormio_mobile_app.network.TokenManager
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
class MealPlanViewModel @Inject constructor(
    private val repository: MealPlanRepository,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _uiState = MutableStateFlow(MealPlanUiState())
    val uiState: StateFlow<MealPlanUiState> = _uiState.asStateFlow()

    init {
        val role = TokenManager.getUserRole(context)
        _uiState.update { it.copy(userRole = role) }
        fetchMealPlans()
        fetchUserActivePlan()
    }

    //functions to fetch meal plan
    fun fetchMealPlans(){
        if (uiState.value.mealPlans.isNotEmpty()) return
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
                weeklyPlan = emptyList(),
                errorMessage = null
            )
        }

        viewModelScope.launch{
            launch { fetchUserActivePlan() }
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

    //function to fetch ingredients for meals by day
    fun fetchDayMeals(planId: Int, day: String) {
        _uiState.update {
            it.copy(
                isLoading = true,
                errorMessage = null,
                dailyIngredients = emptyList()
            )
        }

        viewModelScope.launch {
            when (val result = repository.getMealsByDay(planId, day)) {
                is NetworkResult.Success -> {
                    _uiState.update { it.copy(
                        isLoading = false,
                        dailyIngredients = result.data
                    )}
                }
                is NetworkResult.Error -> {
                    _uiState.update { it.copy(
                        isLoading = false,
                        errorMessage = result.apiError.message ?: "Could not load meals for $day"
                    )}
                }
                is NetworkResult.Loading -> {
                    _uiState.update { it.copy(
                        isLoading = true) }
                }
            }
        }
    }

    // fetch active user subscription to meal plan
    fun fetchUserActivePlan() {
        viewModelScope.launch {
            when (val result = repository.getActiveMealPlan()) {
                is NetworkResult.Success -> {
                    _uiState.update { it.copy(userActivePlan = result.data) }
                }
                is NetworkResult.Error -> {
                    _uiState.update { it.copy(userActivePlan = null) }
                }
                else -> {}
            }
        }
    }

    fun setUserRole(role: String) {
        _uiState.update { it.copy(userRole = role) }
        fetchUserActivePlan()
    }

    //function to subscribe to meal plan
    fun subscribeToMealPlan(mealPlanTypeId: Int, planName: String){
        _uiState.update{
            it.copy(
                isSubscribing = true,
                errorMessage = null,
                subscriptionSuccessMessage = null
            ) }

        viewModelScope.launch {
            when (val result = repository.subscribeToMealPlan(mealPlanTypeId)) {
                is NetworkResult.Success -> {
                    _uiState.update {
                        it.copy(
                            isSubscribing = false,
                            subscriptionSuccessMessage = "Successfully subscribed to $planName meal plan!"
                        )
                    }
                    fetchUserActivePlan()
                }

                is NetworkResult.Error -> {
                    _uiState.update {
                        it.copy(
                            isSubscribing = false,
                            errorMessage = result.apiError.message ?: "Subscription failed. Please try again."
                        )
                    }
                }

                is NetworkResult.Loading -> {
                    _uiState.update {
                        it.copy(
                            isSubscribing = true
                        )
                    }
                }
            }
        }
    }

    fun resetSubscriptionStatus(){
        _uiState.update {
            it.copy(
                subscriptionSuccessMessage = null,
                errorMessage = null
            )
        }
    }

    fun fetchAdminMealItems() {
        _uiState.update {
            it.copy(
                isLoading = true,
                errorMessage = null
            )
        }

        viewModelScope.launch {
            when (val result = repository.getAllMealItems()) {
                is NetworkResult.Success -> {
                    _uiState.update { it.copy(
                        isLoading = false,
                        availableMeals = result.data
                    ) }
                }
                is NetworkResult.Error -> {
                    _uiState.update { it.copy(
                        isLoading = false,
                        errorMessage = result.apiError.message
                    ) }
                }
                is NetworkResult.Loading -> {
                    _uiState.update { it.copy(isLoading = true) }
                }
            }
        }
    }

    //upsert meal template for admin
    fun updateMealTemplate(request: AdminTemplateRequest) {
        _uiState.update {
            it.copy(
                isLoading = true,
                errorMessage = null
            ) }

        viewModelScope.launch {
            when (val result = repository.upsertMealTemplate(request)) {
                is NetworkResult.Success -> {
                    _uiState.update { it.copy(
                        isLoading = false,
                        subscriptionSuccessMessage = "Meal plan updated successfully!"
                    ) }

                    fetchWeeklyPlan(request.mealPlanTypeId)
                }
                is NetworkResult.Error -> {
                    _uiState.update { it.copy(
                        isLoading = false,
                        errorMessage = result.apiError.message
                    ) }
                }
                is NetworkResult.Loading -> {
                    _uiState.update { it.copy(
                        isLoading = true
                    ) }
                }
            }
        }
    }



}