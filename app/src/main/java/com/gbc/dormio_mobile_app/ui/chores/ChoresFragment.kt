package com.gbc.dormio_mobile_app.ui.chores

import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.gbc.dormio_mobile_app.R
import com.gbc.dormio_mobile_app.data.model.chores.Chore
import com.gbc.dormio_mobile_app.databinding.FragmentChoresBinding
import com.gbc.dormio_mobile_app.utils.NetworkResult
import com.gbc.dormio_mobile_app.viewmodel.chores.ChoresDashboardViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ChoresFragment : Fragment(R.layout.fragment_chores) {

    private val viewModel: ChoresDashboardViewModel by viewModels()
    private lateinit var choreAdapter: ChoreDashboardAdapter

    private var _binding: FragmentChoresBinding? = null
    private val binding get() = _binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentChoresBinding.bind(view)

        setupAdapter()
        setupNavigation()
        observeViewModel()
    }

    private fun setupAdapter() {
        choreAdapter = ChoreDashboardAdapter(
            onCompleteClick = { findNavController().navigate(R.id.action_choresFragment_to_choreCompleteFragment) },
            onEditClick = { chore ->
                findNavController().navigate(
                    R.id.action_choresFragment_to_addChoreFragment,
                    bundleOf("choreId" to chore.id)
                )
            }
        )
        binding.rvTodayChores.adapter = choreAdapter
    }

    private fun setupNavigation() {
        binding.weeklyScheduleCard.setOnClickListener {
            findNavController().navigate(R.id.action_choresFragment_to_choreWeeklyFragment)
        }
        binding.btnAddChore.setOnClickListener {
            findNavController().navigate(R.id.action_choresFragment_to_addChoreFragment)
        }
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.dashboardState.collect { result ->
                    binding.progressBar.visibility =
                        if (result is NetworkResult.Loading) View.VISIBLE else View.GONE

                    when (result) {
                        is NetworkResult.Success -> {
                            val data = result.data
                            binding.tvGreeting.text = data.greeting
                            binding.tvChoresLeftCount.text = "${data.stats.choresLeft} chores Left"
                            binding.btnProgress.text = "Progress  •  ${data.stats.progressMessage}"

                            choreAdapter.submitList(data.todayChores)
                        }

                        is NetworkResult.Error -> {
                            val errorMessage =
                                result.apiError.message ?: "An unknown error occurred"
                            Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_SHORT)
                                .show()
                        }

                        else -> {}
                    }
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.refreshDashboard()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}