package com.gbc.dormio_mobile_app.ui.meals

import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.gbc.dormio_mobile_app.R
import com.gbc.dormio_mobile_app.viewmodel.mealplan.MealPlanViewModel
import com.google.android.material.floatingactionbutton.FloatingActionButton
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import kotlin.collections.emptyList

@AndroidEntryPoint
class MealsFragment : Fragment(R.layout.fragment_meals){

    private val viewModel: MealPlanViewModel by viewModels()
    private lateinit var mealPlanAdapter: MealPlanAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val userRole = activity?.intent?.getStringExtra("user_role") ?: "STUDENT"
        viewModel.setUserRole(userRole)

        val recyclerView = view.findViewById<RecyclerView>(R.id.mealsRecyclerView)
        val progressBar = view.findViewById<ProgressBar>(R.id.progressBar)
        val adminFab = view.findViewById<FloatingActionButton>(R.id.adminFab)

        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        mealPlanAdapter = MealPlanAdapter(emptyList()){ selectedPlan ->
            val bundle = Bundle().apply {
                putInt("mealPlanTypeId", selectedPlan.id)
            }
            findNavController().navigate(R.id.action_mealsFragment_to_weeklyDetailFragment, bundle)
        }

        recyclerView.apply{
            layoutManager = LinearLayoutManager(requireContext())
            adapter = mealPlanAdapter
        }

        adminFab.setOnClickListener {
            findNavController().navigate(R.id.action_mealsFragment_to_mealPlanAdminFragment)
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->
                    progressBar.visibility = if (state.isLoading) View.VISIBLE else View.GONE

                    adminFab.isVisible = (state.userRole == "ADMIN")

                    val activePlanId = state.userActivePlan?.mealPlanTypeId
                    mealPlanAdapter.updateData(state.mealPlans, activePlanId)

                    state.errorMessage?.let { errorMsg ->
                        Toast.makeText(context, "Error: $errorMsg", Toast.LENGTH_LONG).show()
                    }
                }
            }
        }

        viewModel.fetchMealPlans()

        viewModel.fetchUserActivePlan()

    }

}
