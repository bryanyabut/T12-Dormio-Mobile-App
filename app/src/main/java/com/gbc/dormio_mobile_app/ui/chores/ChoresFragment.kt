package com.gbc.dormio_mobile_app.ui.chores

import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.gbc.dormio_mobile_app.R
import com.google.android.material.card.MaterialCardView

class ChoresFragment : Fragment(R.layout.fragment_chores) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val weeklyCard = view.findViewById<MaterialCardView>(R.id.weeklyScheduleCard)
        val btnViewSchedule = view.findViewById<View>(R.id.btnViewFullSchedule)
        val btnCompleteChore1 = view.findViewById<View>(R.id.btnCompleteChore1)
        val btnEditChore1 = view.findViewById<View>(R.id.btnEditChore1)
        val btnCompleteChore2 = view.findViewById<View>(R.id.btnCompleteChore2)
        val btnEditChore2 = view.findViewById<View>(R.id.btnEditChore2)
        val btnAddChore = view.findViewById<View>(R.id.btnAddChore)

        weeklyCard.setOnClickListener {
            findNavController().navigate(R.id.action_choresFragment_to_choreWeeklyFragment)
        }

        btnViewSchedule.setOnClickListener {
            findNavController().navigate(R.id.action_choresFragment_to_choreWeeklyFragment)
        }

        btnCompleteChore1.setOnClickListener {
            findNavController().navigate(R.id.action_choresFragment_to_choreCompleteFragment)
        }

        btnEditChore1.setOnClickListener {
            findNavController().navigate(
                R.id.action_choresFragment_to_addChoreFragment,
                bundleOf("choreId" to 1)
            )
        }

        btnCompleteChore2.setOnClickListener {
            findNavController().navigate(R.id.action_choresFragment_to_choreCompleteFragment)
        }

        btnEditChore2.setOnClickListener {
            findNavController().navigate(
                R.id.action_choresFragment_to_addChoreFragment,
                bundleOf("choreId" to 2)
            )
        }

        btnAddChore.setOnClickListener {
            findNavController().navigate(R.id.action_choresFragment_to_addChoreFragment)
        }
    }
}
