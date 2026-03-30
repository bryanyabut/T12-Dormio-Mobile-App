package com.gbc.dormio_mobile_app.network.ApiService

import com.gbc.dormio_mobile_app.data.model.mealplan.AdminTemplateRequest
import com.gbc.dormio_mobile_app.data.model.mealplan.DayGroup
import com.gbc.dormio_mobile_app.data.model.mealplan.MealItem
import com.gbc.dormio_mobile_app.data.model.mealplan.MealPlanType
import com.gbc.dormio_mobile_app.data.model.mealplan.MealWithIngredients
import com.gbc.dormio_mobile_app.data.model.mealplan.SubscribeRequest
import com.gbc.dormio_mobile_app.data.model.mealplan.UserMealPlan
import com.gbc.dormio_mobile_app.network.ApiResponse
import com.gbc.dormio_mobile_app.utils.Constants
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface MealPlanApiService {

    // Get meal plan premiums endpoints
    @GET(Constants.API_MEAL_PLANS)
    suspend fun getAllPlans(): Response<ApiResponse<List<MealPlanType>>>

    // Get meal plan details endpoints
    @GET(Constants.API_MEAL_PLAN_DETAIL_WEEK)
    suspend fun getWeeklyPlan(
        @Path("mealPlanId") id: Int
    ): Response<ApiResponse<List<DayGroup>>>

    // Get meals by day endpoints
    @GET(Constants.API_MEAL_PLAN_DAY)
    suspend fun getMealsByDay(
        @Path("mealPlanTypeId") id: Int,
        @Path("dayOfWeek") day: String
    ): Response<ApiResponse<List<MealWithIngredients>>>

    @GET(Constants.API_MEAL_PLAN_MEAL_ITEMS)
    suspend fun getAllMealItems(): Response<ApiResponse<List<MealItem>>>

    //POST subscribe to meal plan
    @POST(Constants.API_MEAL_PLAN_SUBSCRIBE)
    suspend fun subscribeToMealPlan(
        @Body request: SubscribeRequest
    ): Response<ApiResponse<UserMealPlan>>

    //GET user meal plan details
    @GET(Constants.API_MEAL_PLAN_USER_DETAIL)
    suspend fun getActiveMealPlan(): Response<ApiResponse<UserMealPlan>>

    //POST ADMIN update/create meal plan
    @POST(Constants.API_MEAL_PLAN_UPSERT)
    suspend fun upsertMealTemplate(
        @Body request: AdminTemplateRequest
    ): Response<ApiResponse<Any>>


}