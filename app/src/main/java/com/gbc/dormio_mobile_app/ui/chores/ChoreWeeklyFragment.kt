package com.gbc.dormio_mobile_app.ui.chores

import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.gbc.dormio_mobile_app.R
import com.gbc.dormio_mobile_app.databinding.FragmentChoreWeeklyBinding
import com.gbc.dormio_mobile_app.viewmodel.chores.ChoresDashboardViewModel
import com.google.android.material.card.MaterialCardView
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ChoreWeeklyFragment : Fragment(R.layout.fragment_chore_weekly) {

    private val viewModel: ChoresDashboardViewModel by viewModels()
    private lateinit var choreAdapter: ChoreDashboardAdapter

    private var _binding: FragmentChoreWeeklyBinding? = null
    private val binding get() = _binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentChoreWeeklyBinding.bind(view)

        setupAdapter()
        setupNavigation()
        setupDatePicker()
        observeViewModel()
    }

    private fun setupAdapter() {
        choreAdapter = ChoreDashboardAdapter(
            onCompleteClick = { chore ->
                val bundle = bundleOf("choreId" to chore.id, "choreTitle" to chore.name)
                findNavController().navigate(R.id.action_choreWeeklyFragment_to_choreCompleteFragment, bundle)
            },
            onEditClick = { chore ->
                val bundle = bundleOf("choreId" to chore.id)
                findNavController().navigate(R.id.action_choreWeeklyFragment_to_addChoreFragment, bundle)
            }
        )
        binding.rvWeeklyChores.adapter = choreAdapter
    }

    private fun setupDatePicker() {
        binding.etDateInput.setOnClickListener {
            val calendar = java.util.Calendar.getInstance()
            val datePickerDialog = android.app.DatePickerDialog(
                requireContext(),
                { _, year, month, dayOfMonth ->
                    val selectedDate = String.format("%04d-%02d-%02d", year, month + 1, dayOfMonth)
                    binding.etDateInput.setText(selectedDate)
                    binding.tvSelectedDate.text = selectedDate

                    viewModel.filterChoresByDate(selectedDate)
                },
                calendar.get(java.util.Calendar.YEAR),
                calendar.get(java.util.Calendar.MONTH),
                calendar.get(java.util.Calendar.DAY_OF_MONTH)
            )
            datePickerDialog.show()
        }

        binding.btnDateOk.setOnClickListener {
            binding.etDateInput.clearFocus()
        }

        binding.btnDateCancel.setOnClickListener {
            binding.etDateInput.setText("")
            binding.tvSelectedDate.text = "Enter date"
            viewModel.filterChoresByDate("")
        }
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.filteredChores.collect { chores ->
                        choreAdapter.submitList(chores)
                    }
                }

                launch {
                    viewModel.dashboardState.collect { state ->
                        state.errorMessage?.let {
                            android.widget.Toast.makeText(requireContext(), it, android.widget.Toast.LENGTH_SHORT).show()
                            viewModel.clearMessages()
                        }
                    }
                }
            }
        }
    }

    private fun setupNavigation() {
        binding.btnReturnFromWeekly.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.btnWeeklyAddChore.setOnClickListener {
            findNavController().navigate(R.id.action_choreWeeklyFragment_to_addChoreFragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}