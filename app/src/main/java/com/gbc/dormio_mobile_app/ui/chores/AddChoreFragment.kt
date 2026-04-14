package com.gbc.dormio_mobile_app.ui.chores

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.gbc.dormio_mobile_app.R
import com.gbc.dormio_mobile_app.databinding.FragmentAddChoreBinding
import com.gbc.dormio_mobile_app.viewmodel.chores.AddChoreViewModel
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

@AndroidEntryPoint
class AddChoreFragment : Fragment(R.layout.fragment_add_chore) {
    private var _binding: FragmentAddChoreBinding? = null
    private val binding get() = _binding!!

    private val viewModel: AddChoreViewModel by viewModels()
    private lateinit var housemateAdapter: HousemateAdapter
    private var selectedDate: String = ""

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentAddChoreBinding.bind(view)

        setupRecyclerView()
        setupDatePicker()
        observeState()

        val choreId = arguments?.getInt("choreId", -1)?.takeIf { it != -1 }

        binding.btnSaveChore.setOnClickListener {
            viewModel.saveChore(
                choreId = choreId,
                name = binding.etChoreTitle.text.toString(),
                description = binding.etChoreNotes.text.toString(),
                dueDate = selectedDate
            )
        }

        binding.btnCancelAddChore.setOnClickListener {
            findNavController().popBackStack()
        }

        if (choreId != null) {
            binding.tvAddChoreScreenTitle.text = "Edit Chore"
            binding.btnSaveChore.text = "Update Chore"

            viewModel.loadChoreForEditing(choreId)
        }
    }

    private fun observeState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.uiState.collect { state ->
                housemateAdapter.submitList(state.housemates)

                if (state.initialChoreLoaded) {
                    binding.etChoreTitle.setText(state.choreName)
                    binding.etChoreNotes.setText(state.description)
                    viewModel.consumeInitialData()
                }

                if (state.isSuccess) {
                    Toast.makeText(requireContext(), state.successMessage, Toast.LENGTH_SHORT).show()
                    findNavController().popBackStack()
                }

                state.errorMessage?.let {
                    Snackbar.make(binding.root, it, Snackbar.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun setupRecyclerView() {
        housemateAdapter = HousemateAdapter { userId -> viewModel.toggleUserSelection(userId) }
        binding.rvAssignPeople.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = housemateAdapter
        }
    }

    private fun setupDatePicker() {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        selectedDate = sdf.format(Date(binding.calendarViewDueDate.date))

        binding.calendarViewDueDate.setOnDateChangeListener { _, year, month, dayOfMonth ->
            val calendar = Calendar.getInstance()
            calendar.set(year, month, dayOfMonth)
            selectedDate = sdf.format(calendar.time)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
