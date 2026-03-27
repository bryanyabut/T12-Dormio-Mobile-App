package com.gbc.dormio_mobile_app.ui.meals

import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import androidx.fragment.app.Fragment
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
import kotlin.collections.emptyList

@AndroidEntryPoint
class MealsFragment : Fragment(R.layout.fragment_meals){

    private val viewModel: MealPlanViewModel by viewModels()
    private lateinit var mealPlanAdapter: MealPlanAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val recyclerView = view.findViewById<RecyclerView>(R.id.mealsRecyclerView)
        val progressBar = view.findViewById<ProgressBar>(R.id.progressBar)

        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        mealPlanAdapter = MealPlanAdapter(emptyList()){ selectedPlan ->
            Toast.makeText(context, "Selected: ${selectedPlan.name}", Toast.LENGTH_SHORT).show()
        }

        recyclerView.apply{
            layoutManager = LinearLayoutManager(requireContext())
            adapter = mealPlanAdapter
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->
                    progressBar.visibility = if (state.isLoading) View.VISIBLE else View.GONE

                    mealPlanAdapter.updateData(state.mealPlans)

                    state.errorMessage?.let { errorMsg ->
                        Toast.makeText(context, "Error: $errorMsg", Toast.LENGTH_LONG).show()
                    }
                }
            }
        }

        viewModel.fetchMealPlans()

    }

}
