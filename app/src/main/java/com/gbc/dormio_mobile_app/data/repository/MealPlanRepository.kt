package com.gbc.dormio_mobile_app.data.repository

import com.gbc.dormio_mobile_app.data.model.mealplan.AdminTemplateRequest
import com.gbc.dormio_mobile_app.data.model.mealplan.DayGroup
import com.gbc.dormio_mobile_app.data.model.mealplan.MealItem
import com.gbc.dormio_mobile_app.data.model.mealplan.MealPlanType
import com.gbc.dormio_mobile_app.data.model.mealplan.MealWithIngredients
import com.gbc.dormio_mobile_app.data.model.mealplan.SubscribeRequest
import com.gbc.dormio_mobile_app.data.model.mealplan.UserMealPlan
import com.gbc.dormio_mobile_app.network.ApiService.MealPlanApiService
import com.gbc.dormio_mobile_app.utils.NetworkResult
import com.gbc.dormio_mobile_app.utils.handleApiResponse
import com.gbc.dormio_mobile_app.utils.safeApiCall
import javax.inject.Inject

class MealPlanRepository @Inject constructor(
    private val apiService: MealPlanApiService
) {

    // Get meal plan premiums
    suspend fun getAllMealPlans(): NetworkResult<List<MealPlanType>>{
        val networkResult = safeApiCall { apiService.getAllPlans() }
        return handleApiResponse(networkResult)
    }

    // Get meal plan details
    suspend fun getWeeklyPlan(id: Int): NetworkResult<List<DayGroup>>{
        val networkResult = safeApiCall { apiService.getWeeklyPlan(id) }
        return handleApiResponse(networkResult)
    }

    // Get meals by day
    suspend fun getMealsByDay(id: Int, day: String): NetworkResult<List<MealWithIngredients>>{
        val networkResult = safeApiCall { apiService.getMealsByDay(id, day) }
        return handleApiResponse(networkResult)
    }

    // Get all meal items
    suspend fun getAllMealItems(): NetworkResult<List<MealItem>>{
        val networkResult = safeApiCall { apiService.getAllMealItems() }
        return handleApiResponse(networkResult)
    }

    //POST subscribe to meal plan
    suspend fun subscribeToMealPlan(mealPlanTypeId: Int): NetworkResult<UserMealPlan>{
        val networkResult = safeApiCall {
            apiService.subscribeToMealPlan(SubscribeRequest(mealPlanTypeId))
        }
        return handleApiResponse(networkResult)
    }

    //GET user meal plan details
    suspend fun getActiveMealPlan(): NetworkResult<UserMealPlan>{
        val networkResult = safeApiCall { apiService.getActiveMealPlan() }
        return handleApiResponse(networkResult)
    }

    //POST ADMIN update/create meal plan
    suspend fun upsertMealTemplate(request: AdminTemplateRequest): NetworkResult<Any> {
        val networkResult = safeApiCall { apiService.upsertMealTemplate(request) }
        return handleApiResponse(networkResult)
    }
}