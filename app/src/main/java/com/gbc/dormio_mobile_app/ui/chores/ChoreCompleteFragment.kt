package com.gbc.dormio_mobile_app.ui.chores

import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.gbc.dormio_mobile_app.R
import com.gbc.dormio_mobile_app.databinding.FragmentChoreCompleteBinding
import com.gbc.dormio_mobile_app.viewmodel.chores.ChoresDashboardViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ChoreCompleteFragment : Fragment(R.layout.fragment_chore_complete) {

    private val viewModel: ChoresDashboardViewModel by viewModels()
    private var choreId: Int = -1
    private var _binding: FragmentChoreCompleteBinding? = null
    private val binding get() = _binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentChoreCompleteBinding.bind(view)

        choreId = arguments?.getInt("choreId") ?: -1
        val choreTitle = arguments?.getString("choreTitle") ?: "Unknown Chore"

        binding.tvChoreTitle.text = choreTitle

        binding.btnReturnFromComplete.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.btnCompletedChore.setOnClickListener {
            if (choreId != -1) {
                viewModel.markChoreComplete(choreId)
            }
        }

        observeViewModel()
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.dashboardState.collect { state ->
                    state.successMessage?.let { message ->
                        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
                        viewModel.clearMessages()
                        findNavController().popBackStack()
                    }

                    state.errorMessage?.let { error ->
                        Toast.makeText(requireContext(), error, Toast.LENGTH_SHORT).show()
                        viewModel.clearMessages()
                    }

                    binding.btnCompletedChore.isEnabled = !state.actionLoading
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}