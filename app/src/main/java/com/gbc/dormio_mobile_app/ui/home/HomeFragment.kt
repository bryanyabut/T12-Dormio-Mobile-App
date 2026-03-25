package com.gbc.dormio_mobile_app.ui.home

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.gbc.dormio_mobile_app.R
import com.google.android.material.card.MaterialCardView

class HomeFragment : Fragment(R.layout.activity_home) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

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
