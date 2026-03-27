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

data class MealPlanUiState(
    val isLoading: Boolean = false,
    val mealPlans: List<MealPlanType> = emptyList(),
    val weeklyPlan: List<DayGroup> = emptyList(),
    val dailyIngredients: List<MealWithIngredients> = emptyList(),
    val errorMessage: String? = null
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