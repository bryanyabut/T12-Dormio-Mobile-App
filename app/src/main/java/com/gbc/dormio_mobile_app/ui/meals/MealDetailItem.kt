package com.gbc.dormio_mobile_app.ui.meals

import com.gbc.dormio_mobile_app.data.model.mealplan.MealSummary

sealed class MealDetailItem {
    data class Header(val dayName: String) : MealDetailItem()
    data class Meal(val summary: MealSummary) : MealDetailItem()
}