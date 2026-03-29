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
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.gbc.dormio_mobile_app.R
import com.gbc.dormio_mobile_app.viewmodel.mealplan.MealPlanViewModel
import com.google.android.material.button.MaterialButton
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class WeeklyDetailFragment : Fragment(R.layout.fragment_weekly_detail) {

    private val viewModel: MealPlanViewModel by viewModels()
    private lateinit var weeklyAdapter: WeeklyDetailAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val mealPlanId = arguments?.getInt("mealPlanTypeId") ?: 1

        val recyclerView = view.findViewById<RecyclerView>(R.id.weeklyRecyclerView)
        val progressBar = view.findViewById<ProgressBar>(R.id.progressBarWeekly)
        val titleText = view.findViewById<TextView>(R.id.titleTextWeekly)
        val subscribeButton = view.findViewById<MaterialButton>(R.id.btnSubscribe)

        weeklyAdapter = WeeklyDetailAdapter { dayString ->
            val bundle = Bundle().apply {
                putInt("mealPlanTypeId", mealPlanId)
                putString("dayOfWeek", dayString)
            }

            findNavController().navigate(
                R.id.action_weeklyDetailFragment_to_dayMealFragment,
                bundle
            )
        }

        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = weeklyAdapter

        subscribeButton.setOnClickListener {
            val planName = titleText.text.toString()

            MaterialAlertDialogBuilder(requireContext())
                .setTitle("Confirm Subscription")
                .setMessage("Would you like to subscribe to the $planName?")
                .setNegativeButton("Cancel") {
                    dialog, _ -> dialog.dismiss() }
                .setPositiveButton("Subscribe") { _, _ ->
                    viewModel.subscribeToMealPlan(mealPlanId, planName)
                }
                .show()
        }

        //state flow
        viewLifecycleOwner.lifecycleScope.launch{
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED){
                viewModel.uiState.collect { state ->

                    progressBar.visibility = if (state.isLoading) View.VISIBLE else View.GONE

                    subscribeButton.isEnabled = !state.isSubscribing
                    if (state.isSubscribing){
                        subscribeButton.text = "Subscribing..."
                        subscribeButton.alpha = 0.6f
                    } else {
                        subscribeButton.text = "Subscribe to this plan"
                        subscribeButton.alpha = 1.0f
                    }

                    if (state.weeklyPlan.isNotEmpty()){
                        val displayList = state.weeklyPlan.flatMap { group ->
                            listOf(MealDetailItem.Header(group.day.name)) +
                                    group.meals.map { MealDetailItem.Meal(it, group.day.name) }
                        }
                        weeklyAdapter.submitList(displayList)
                    }

                    state.subscriptionSuccessMessage?.let { successMsg ->
                        Log.d("WeeklyDetailFragment", "Subscription successful: $successMsg")
                        Toast.makeText(requireContext(), successMsg, Toast.LENGTH_LONG).show()
                        viewModel.resetSubscriptionStatus()
                        findNavController().popBackStack()
                    }

                    state.errorMessage?.let { errorMsg ->
                        Log.d("WeeklyDetailFragment", "Error fetching weekly plan: $errorMsg")
                        Toast.makeText(requireContext(), "Error: $errorMsg", Toast.LENGTH_LONG).show()
                        viewModel.resetSubscriptionStatus()
                    }
                }
            }
        }

        titleText.text = when(mealPlanId){
            1 -> "Basic Plan"
            2 -> "Premium Plan"
            3 -> "Vegetarian Plan"
            else -> "Meal Plan Details"
        }

        viewModel.fetchWeeklyPlan(mealPlanId)

    }

}