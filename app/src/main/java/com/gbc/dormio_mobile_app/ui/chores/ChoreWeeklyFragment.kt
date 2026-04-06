package com.gbc.dormio_mobile_app.ui.chores

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.gbc.dormio_mobile_app.R
import com.google.android.material.card.MaterialCardView

class ChoreWeeklyFragment : Fragment(R.layout.fragment_chore_weekly) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val btnReturn = view.findViewById<View>(R.id.btnReturnFromWeekly)
        val btnAddChore = view.findViewById<View>(R.id.btnWeeklyAddChore)
        val taskCard1 = view.findViewById<MaterialCardView>(R.id.taskCard1)
        val taskCard2 = view.findViewById<MaterialCardView>(R.id.taskCard2)

        btnReturn.setOnClickListener {
            findNavController().navigate(R.id.action_choreWeeklyFragment_to_choresFragment)
        }

        btnAddChore.setOnClickListener {
            findNavController().navigate(R.id.action_choreWeeklyFragment_to_choreCompleteFragment)
        }

        taskCard1.setOnClickListener {
            findNavController().navigate(R.id.action_choreWeeklyFragment_to_choreCompleteFragment)
        }

        taskCard2.setOnClickListener {
            findNavController().navigate(R.id.action_choreWeeklyFragment_to_choreCompleteFragment)
        }
    }
}
