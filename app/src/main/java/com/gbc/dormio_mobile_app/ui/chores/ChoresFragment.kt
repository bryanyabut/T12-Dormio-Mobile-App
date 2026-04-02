package com.gbc.dormio_mobile_app.ui.chores

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.gbc.dormio_mobile_app.R
import com.google.android.material.card.MaterialCardView

class ChoresFragment : Fragment(R.layout.fragment_chores) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val weeklyCard = view.findViewById<MaterialCardView>(R.id.weeklyScheduleCard)
        val btnViewSchedule = view.findViewById<View>(R.id.btnViewFullSchedule)
        val choreItem1 = view.findViewById<View>(R.id.choreItem1)
        val choreItem2 = view.findViewById<View>(R.id.choreItem2)
        val btnAddChore = view.findViewById<View>(R.id.btnAddChore)

        weeklyCard.setOnClickListener {
            findNavController().navigate(R.id.action_choresFragment_to_choreWeeklyFragment)
        }

        btnViewSchedule.setOnClickListener {
            findNavController().navigate(R.id.action_choresFragment_to_choreWeeklyFragment)
        }

        choreItem1.setOnClickListener {
            findNavController().navigate(R.id.action_choresFragment_to_choreCompleteFragment)
        }

        choreItem2.setOnClickListener {
            findNavController().navigate(R.id.action_choresFragment_to_choreCompleteFragment)
        }

        btnAddChore.setOnClickListener {
            findNavController().navigate(R.id.action_choresFragment_to_choreCompleteFragment)
        }
    }
}
