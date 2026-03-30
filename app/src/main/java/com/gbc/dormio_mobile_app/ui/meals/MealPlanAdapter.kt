package com.gbc.dormio_mobile_app.ui.meals

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.gbc.dormio_mobile_app.R
import com.gbc.dormio_mobile_app.data.model.mealplan.MealPlanType
import com.google.android.material.card.MaterialCardView

class MealPlanAdapter (
    private var mealPlans: List<MealPlanType>,
    private val onItemClick: (MealPlanType) -> Unit
) : RecyclerView.Adapter<MealPlanAdapter.MealViewHolder>(){

    private var activePlanId: Int? = null

    class MealViewHolder(view: View) : RecyclerView.ViewHolder(view){
        val tvName: TextView = view.findViewById(R.id.tvPlanName)
        val tvDesc: TextView = view.findViewById(R.id.tvPlanDescription)
        val tvActiveBadge: TextView = view.findViewById(R.id.tvActiveBadge)
        val cardView: MaterialCardView = view.findViewById(R.id.mealCard)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) : MealViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_meal_plan, parent, false)
        return MealViewHolder(view)
    }

    override fun onBindViewHolder(holder: MealViewHolder, position: Int) {
        val mealPlan = mealPlans[position]
        holder.tvName.text = mealPlan.name
        holder.tvDesc.text = mealPlan.description ?: "No description available"

        val isSubscribed = mealPlan.id == activePlanId

        android.util.Log.d("MealDebug", "Drawing: ${mealPlan.name}")
        android.util.Log.d("MealDebug", "Plan ID: ${mealPlan.id} | Active ID: $activePlanId | Match: $isSubscribed")

        holder.tvActiveBadge.isVisible = isSubscribed

        if (isSubscribed) {
            holder.cardView.strokeColor = android.graphics.Color.parseColor("#4CAF50") // Material Green
            holder.cardView.strokeWidth = 6
        } else {
            holder.cardView.strokeColor = android.graphics.Color.parseColor("#E0E0E0") // Light Grey
            holder.cardView.strokeWidth = 2
        }

        holder.itemView.setOnClickListener {
            onItemClick(mealPlan)
        }
    }

    override fun getItemCount(): Int = mealPlans.size

    fun updateData(newMealPlans: List<MealPlanType>, newActiveId: Int? = null) {
        this.mealPlans = newMealPlans
        this.activePlanId = newActiveId
        notifyDataSetChanged()
    }

}