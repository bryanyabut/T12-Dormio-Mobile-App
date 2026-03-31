package com.gbc.dormio_mobile_app.ui.home

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.gbc.dormio_mobile_app.R
import com.gbc.dormio_mobile_app.viewmodel.profile.ProfileViewModel
import com.google.android.material.card.MaterialCardView
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class HomeFragment : Fragment(R.layout.activity_home) {

    private val viewModel: ProfileViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        observeViewModel(view)
        setupClickListeners(view)

    }

    private fun observeViewModel(view: View) {
        val userNameText = view.findViewById<TextView>(R.id.userNameText)

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.uiState.collect { state ->
                state.profile?.let { profile ->
                    val firstName = profile.user.firstName ?: "Resident"
                    val lastName = profile.user.lastName ?: ""
                    val fullName = "$firstName $lastName".trim()
                    userNameText.text = "$fullName"
                }

                if (state.isLoading) {
                    userNameText.text = "..."
                }
                if (state.errorMessage != null) {
                    userNameText.text = "User name unavailable"
                }
            }
        }
    }

    private fun setupClickListeners(view: View){
        view.findViewById<MaterialCardView>(R.id.maintenanceCard).setOnClickListener {
            findNavController().navigate(R.id.maintenanceFragment)
        }

        view.findViewById<MaterialCardView>(R.id.mealPlanCard).setOnClickListener {
            findNavController().navigate(R.id.mealsFragment)
        }

        view.findViewById<MaterialCardView>(R.id.budgetCard).setOnClickListener {
            findNavController().navigate(R.id.budgetFragment)
        }

        view.findViewById<MaterialCardView>(R.id.choresCard).setOnClickListener {
            findNavController().navigate(R.id.choresFragment)
        }
    }
}
