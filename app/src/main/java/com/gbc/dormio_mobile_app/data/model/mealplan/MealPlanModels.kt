package com.gbc.dormio_mobile_app.data.model.mealplan

import com.google.gson.annotations.SerializedName


//core meal plan models
data class MealPlanType(
    val id: Int,
    val name: String,
    val description: String?
)

data class MealItem(
    val id: Int,
    val name: String,
    val description: String?
)

data class Ingredient(
    val id: Int,
    val name: String,
    val description: String?
)

//get meal plan details
data class DayGroup(
    val day: Weekday,
    val meals: List<MealSummary>
)

data class MealSummary(
    val mealType: MealType,
    val id: Int,
    val name: String,
    val description: String?
)

//Get meals by day
data class MealWithIngredients(
    val mealType: MealType,
    val id: Int,
    val name: String,
    val description: String?,
    val ingredients: List<Ingredient>
)

//subscribe to meal plan request
data class SubscribeRequest(
    val mealPlanTypeId: Int
)

data class UserMealPlan(
    val id: Int,
    @SerializedName("user_id") val userId: Int,
    @SerializedName("meal_plan_type_id") val mealPlanTypeId: Int,
    @SerializedName("start_date") val startDate: String,
    @SerializedName("end_date") val endDate: String
)

data class MealPlanUiState(
    val isLoading: Boolean = false,
    val isSubscribing: Boolean = false,
    val mealPlans: List<MealPlanType> = emptyList(),
    val weeklyPlan: List<DayGroup> = emptyList(),
    val dailyIngredients: List<MealWithIngredients> = emptyList(),
    val errorMessage: String? = null,
    val subscriptionSuccessMessage: String? = null
)

enum class Weekday{
    @SerializedName("MON") MON,
    @SerializedName("TUE") TUE,
    @SerializedName("WED") WED,
    @SerializedName("THU") THU,
    @SerializedName("FRI") FRI,
    @SerializedName("SAT") SAT,
    @SerializedName("SUN") SUN
}

enum class MealType{
    @SerializedName("breakfast")BREAKFAST,
    @SerializedName("lunch")LUNCH,
    @SerializedName("dinner")DINNER
}