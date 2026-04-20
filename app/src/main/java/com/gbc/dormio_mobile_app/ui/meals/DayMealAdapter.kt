package com.gbc.dormio_mobile_app.ui.meals

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.gbc.dormio_mobile_app.R
import com.gbc.dormio_mobile_app.data.model.mealplan.MealWithIngredients

class DayMealAdapter : ListAdapter<MealWithIngredients, DayMealAdapter.ViewHolder>(DiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_day_meal_card, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val meal = getItem(position)
        holder.bind(meal)
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val type = view.findViewById<TextView>(R.id.tvMealType)
        private val name = view.findViewById<TextView>(R.id.tvMealName)
        private val ingredients = view.findViewById<TextView>(R.id.tvIngredients)

        fun bind(meal: MealWithIngredients) {
            type.text = meal.mealType.name
            name.text = meal.name

            val ingredientsList = meal.ingredients.joinToString(", ") { it.name }
            ingredients.text = "Ingredients: $ingredientsList"
        }
    }

    companion object {
        private val DiffCallback = object : DiffUtil.ItemCallback<MealWithIngredients>() {
            override fun areItemsTheSame(old: MealWithIngredients, new: MealWithIngredients) = old.id == new.id
            override fun areContentsTheSame(old: MealWithIngredients, new: MealWithIngredients) = old == new
        }
    }
}