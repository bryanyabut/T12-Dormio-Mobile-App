package com.gbc.dormio_mobile_app.ui.meals

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.gbc.dormio_mobile_app.R
import com.gbc.dormio_mobile_app.viewmodel.mealplan.MealPlanViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class WeeklyDetailFragment : Fragment(R.layout.fragment_weekly_detail) {

    private val viewModel: MealPlanViewModel by viewModels()
    private lateinit var weeklyAdapter: WeeklyDetailAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val recyclerView = view.findViewById<RecyclerView>(R.id.weeklyRecyclerView)
        val progressBar = view.findViewById<ProgressBar>(R.id.progressBarWeekly)
        val titleText = view.findViewById<TextView>(R.id.titleTextWeekly)

        weeklyAdapter = WeeklyDetailAdapter()
        recyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = weeklyAdapter
        }

        //state flow
        viewLifecycleOwner.lifecycleScope.launch{
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED){
                viewModel.uiState.collect { state ->

                    progressBar.visibility = if (state.isLoading) View.VISIBLE else View.GONE

                    if (state.weeklyPlan.isNotEmpty()){
                        val displayList = state.weeklyPlan.flatMap { group ->
                            listOf(MealDetailItem.Header(group.day.name)) +
                                    group.meals.map { MealDetailItem.Meal(it) }
                        }
                        weeklyAdapter.submitList(displayList)
                    }

                    state.errorMessage?.let { errorMsg ->
                        Log.d("WeeklyDetailFragment", "Error fetching weekly plan: $errorMsg")
                        Toast.makeText(context, "Error: $errorMsg", Toast.LENGTH_LONG).show()
                    }
                }
            }
        }

        val mealPlanId = arguments?.getInt("mealPlanTypeId") ?: 1

        titleText.text = when(mealPlanId){
            1 -> "Basic Plan"
            2 -> "Premium Plan"
            3 -> "Vegetarian Plan"
            else -> "Meal Plan Details"
        }

        viewModel.fetchWeeklyPlan(mealPlanId)

    }

}