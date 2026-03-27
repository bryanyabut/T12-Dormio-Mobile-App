package com.gbc.dormio_mobile_app.ui.meals

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.gbc.dormio_mobile_app.R
import com.gbc.dormio_mobile_app.data.model.mealplan.MealPlanType

class MealPlanAdapter (
    private var mealPlans: List<MealPlanType>,
    private val onItemClick: (MealPlanType) -> Unit
) : RecyclerView.Adapter<MealPlanAdapter.MealViewHolder>(){

    class MealViewHolder(view: View) : RecyclerView.ViewHolder(view){
        val tvName: TextView = view.findViewById(R.id.tvPlanName)
        val tvDesc: TextView = view.findViewById(R.id.tvPlanDescription)
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

        holder.itemView.setOnClickListener {
            onItemClick(mealPlan)
        }
    }

    override fun getItemCount(): Int = mealPlans.size

    fun updateData(newMealPlans: List<MealPlanType>) {
        this.mealPlans = newMealPlans
        notifyDataSetChanged()
    }

}