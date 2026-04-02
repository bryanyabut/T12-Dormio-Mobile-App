package com.gbc.dormio_mobile_app.ui.chores

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.gbc.dormio_mobile_app.R

class ChoreCompleteFragment : Fragment(R.layout.fragment_chore_complete) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val btnReturn = view.findViewById<View>(R.id.btnReturnFromComplete)
        val btnCompleted = view.findViewById<View>(R.id.btnCompletedChore)

        btnReturn.setOnClickListener {
            findNavController().navigate(R.id.action_choreCompleteFragment_to_choresFragment)
        }

        btnCompleted.setOnClickListener {
            findNavController().navigate(R.id.action_choreCompleteFragment_to_choresFragment)
        }
    }
}
