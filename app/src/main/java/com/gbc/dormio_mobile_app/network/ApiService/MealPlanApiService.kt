package com.gbc.dormio_mobile_app.network.ApiService

import com.gbc.dormio_mobile_app.data.model.mealplan.DayGroup
import com.gbc.dormio_mobile_app.data.model.mealplan.MealPlanType
import com.gbc.dormio_mobile_app.data.model.mealplan.MealWithIngredients
import com.gbc.dormio_mobile_app.network.ApiResponse
import com.gbc.dormio_mobile_app.utils.Constants
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface MealPlanApiService {

    // Get meal plan premiums endpoints
    @GET(Constants.API_MEAL_PLANS)
    suspend fun getAllPlans(): Response<ApiResponse<List<MealPlanType>>>

    // Get meal plan details endpoints
    @GET(Constants.API_MEAL_PLAN_DETAIL_WEEK)
    suspend fun getWeeklyPlan(
        @Path("id") Id: Int
    ): Response<ApiResponse<List<DayGroup>>>

    // Get meals by day endpoints
    @GET(Constants.API_MEAL_PLAN_DAY)
    suspend fun getMealsByDay(
        @Path("id") Id: Int,
        @Path("dayOfWeek") day: String
    ): Response<ApiResponse<List<MealWithIngredients>>>
}