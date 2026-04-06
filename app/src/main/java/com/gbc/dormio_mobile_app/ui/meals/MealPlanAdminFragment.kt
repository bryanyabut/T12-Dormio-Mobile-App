package com.gbc.dormio_mobile_app.ui.meals

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.gbc.dormio_mobile_app.R
import com.gbc.dormio_mobile_app.data.model.mealplan.AdminTemplateRequest
import com.gbc.dormio_mobile_app.data.model.mealplan.MealItem
import com.gbc.dormio_mobile_app.data.model.mealplan.MealType
import com.gbc.dormio_mobile_app.data.model.mealplan.Weekday
import com.gbc.dormio_mobile_app.databinding.FragmentMealPlanAdminBinding
import com.gbc.dormio_mobile_app.viewmodel.mealplan.MealPlanViewModel
import com.google.android.material.chip.Chip
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch


@AndroidEntryPoint
class MealPlanAdminFragment : Fragment(R.layout.fragment_meal_plan_admin) {

    private val viewModel: MealPlanViewModel by viewModels()
    private var _binding: FragmentMealPlanAdminBinding? = null
    private val binding get() = _binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentMealPlanAdminBinding.bind(view)

        viewModel.fetchAdminMealItems()

        binding.btnSaveUpdate.setOnClickListener {
            submitUpsert()
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->
                    binding.progressBar.isVisible = state.isLoading

                    binding.btnSaveUpdate.isEnabled = !state.isLoading

                    if (state.availableMeals.isNotEmpty() && binding.mealSpinner.adapter == null) {
                        setupMealSpinner(state.availableMeals)
                    }

                    state.subscriptionSuccessMessage?.let {
                        android.widget.Toast.makeText(requireContext(), it, android.widget.Toast.LENGTH_SHORT).show()
                        viewModel.resetSubscriptionStatus()
                    }

                    state.errorMessage?.let {
                        android.widget.Toast.makeText(requireContext(), it, android.widget.Toast.LENGTH_LONG).show()
                    }
                }
            }
        }
    }

    private fun setupMealSpinner(meals: List<MealItem>) {

        if(binding.mealSpinner.adapter != null && binding.mealSpinner.adapter.count == meals.size) {
            return
        }

        val adapter = android.widget.ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            meals.map { it.name }
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.mealSpinner.adapter = adapter
    }

    private fun submitUpsert() {
        val checkedDayId = binding.dayChipGroup.checkedChipId
        val checkedMealTypeId = binding.mealTypeChipGroup.checkedChipId
        val selectedPosition = binding.mealSpinner.selectedItemPosition

        if (checkedDayId == View.NO_ID ||
            checkedMealTypeId == View.NO_ID ||
            selectedPosition == android.widget.AdapterView.INVALID_POSITION) {

            android.widget.Toast.makeText(
                requireContext(),
                "Please select a Day, Meal Time, and Dish",
                android.widget.Toast.LENGTH_SHORT
            ).show()
            return
        }

        val mealItemId = viewModel.uiState.value.availableMeals[selectedPosition].id
        val day = getSelectedWeekday()
        val mealType = getSelectedMealType()
        val planId = arguments?.getInt("mealPlanTypeId") ?: 1

        val request = AdminTemplateRequest(
            mealPlanTypeId = planId,
            dayOfWeek = day,
            mealType = mealType,
            mealItemId = mealItemId
        )

        androidx.appcompat.app.AlertDialog.Builder(requireContext())
            .setTitle("Confirm Update")
            .setMessage("Change ${day} ${mealType} to ${viewModel.uiState.value.availableMeals[selectedPosition].name}?")
            .setPositiveButton("Update") { _, _ ->
                viewModel.updateMealTemplate(request)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
    private fun getSelectedWeekday(): Weekday {
        val checkedId = binding.dayChipGroup.checkedChipId
        val chip = binding.dayChipGroup.findViewById<Chip>(checkedId)
        return when (chip?.text.toString()) {
            "Monday" -> Weekday.MON
            "Tuesday" -> Weekday.TUE
            "Wednesday" -> Weekday.WED
            "Thursday" -> Weekday.THU
            "Friday" -> Weekday.FRI
            "Saturday" -> Weekday.SAT
            "Sunday" -> Weekday.SUN
            else -> Weekday.MON
        }
    }

    private fun getSelectedMealType(): MealType {
        val checkedId = binding.mealTypeChipGroup.checkedChipId
        val chip = binding.mealTypeChipGroup.findViewById<Chip>(checkedId)
        return when (chip?.text.toString()) {
            "Breakfast" -> MealType.BREAKFAST
            "Lunch" -> MealType.LUNCH
            "Dinner" -> MealType.DINNER
            else -> MealType.LUNCH
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}