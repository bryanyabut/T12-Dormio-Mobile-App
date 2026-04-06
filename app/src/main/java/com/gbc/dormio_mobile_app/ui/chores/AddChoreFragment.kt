package com.gbc.dormio_mobile_app.ui.chores

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.gbc.dormio_mobile_app.R
import com.google.android.material.button.MaterialButton

class AddChoreFragment : Fragment(R.layout.fragment_add_chore) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val choreId = arguments?.getInt("choreId", -1) ?: -1
        val isEditMode = choreId != -1

        val tvScreenTitle = view.findViewById<TextView>(R.id.tvAddChoreScreenTitle)
        val btnSave = view.findViewById<MaterialButton>(R.id.btnSaveChore)
        val btnCancel = view.findViewById<MaterialButton>(R.id.btnCancelAddChore)

        if (isEditMode) {
            tvScreenTitle.text = "Edit Chore"
            btnSave.text = "Update Chore"
        }

        btnSave.setOnClickListener {
            findNavController().navigate(R.id.action_addChoreFragment_to_choresFragment)
        }

        btnCancel.setOnClickListener {
            findNavController().navigate(R.id.action_addChoreFragment_to_choresFragment)
        }
    }
}
