package com.gbc.dormio_mobile_app.ui.meals

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.gbc.dormio_mobile_app.R
import com.gbc.dormio_mobile_app.databinding.FragmentDayMealBinding
import com.gbc.dormio_mobile_app.viewmodel.mealplan.MealPlanViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class DayMealFragment : Fragment() {

    private var _binding: FragmentDayMealBinding? = null
    private val binding get() = _binding!!

    private val viewModel: MealPlanViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDayMealBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val adapter = DayMealAdapter()
        binding.rvDayMeals.adapter = adapter

        val planId = arguments?.getInt("mealPlanTypeId") ?: -1
        val day = arguments?.getString("dayOfWeek") ?: ""

        if (planId != -1 && day.isNotEmpty()) {
            viewModel.fetchDayMeals(planId, day)
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->
                    binding.progressBar.isVisible = state.isLoading
                    adapter.submitList(state.dailyIngredients)
                    binding.tvEmptyMessage.isVisible = !state.isLoading && state.dailyIngredients.isEmpty()

                    state.errorMessage?.let {
                        Log.d("DayMealFragment", "Error fetching day meals: $it")
                        binding.tvEmptyMessage.text = "Error: $it"
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}