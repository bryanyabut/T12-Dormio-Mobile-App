package com.gbc.dormio_mobile_app.ui.meals

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.gbc.dormio_mobile_app.R
import com.gbc.dormio_mobile_app.data.model.mealplan.MealSummary

class WeeklyDetailAdapter(
    private val onMealClick: (String) -> Unit
) : ListAdapter<MealDetailItem, RecyclerView.ViewHolder>(DiffCallback) {

    companion object {
        private const val TYPE_HEADER = 0
        private const val TYPE_MEAL = 1
    }

    override fun getItemViewType(position: Int): Int = when (getItem(position)) {
        is MealDetailItem.Header -> TYPE_HEADER
        is MealDetailItem.Meal -> TYPE_MEAL
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            TYPE_HEADER -> HeaderViewHolder(inflater.inflate(R.layout.meal_day_header, parent, false))
            else -> MealViewHolder(inflater.inflate(R.layout.item_meal_weekly_card, parent, false))
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = getItem(position)
        when (holder) {
            is HeaderViewHolder -> {
                val headerItem = item as MealDetailItem.Header
                holder.bind(headerItem.dayName)

                holder.itemView.setOnClickListener {
                    onMealClick(headerItem.dayName)
                }
            }
            is MealViewHolder -> {
                val mealItem = item as MealDetailItem.Meal
                holder.bind(mealItem.summary)

                holder.itemView.setOnClickListener {
                    onMealClick(mealItem.dayName)
                }
            }
        }
    }

    class HeaderViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val headerText: TextView = view.findViewById(R.id.headerText)
        fun bind(day: String) {
            headerText.text = day
        }
    }

    class MealViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val nameTxt: TextView = view.findViewById(R.id.mealName)
        private val descTxt: TextView = view.findViewById(R.id.mealDescription)

        fun bind(summary: MealSummary) {
            val mealName = if (!summary.name.isNullOrBlank()) summary.name else "TBD"
            nameTxt.text = "${summary.mealType}: $mealName"
            descTxt.text = summary.description ?: "No description provided."
        }
    }

    object DiffCallback : DiffUtil.ItemCallback<MealDetailItem>() {
        override fun areItemsTheSame(old: MealDetailItem, new: MealDetailItem) = old == new
        override fun areContentsTheSame(old: MealDetailItem, new: MealDetailItem) = old == new
    }
}