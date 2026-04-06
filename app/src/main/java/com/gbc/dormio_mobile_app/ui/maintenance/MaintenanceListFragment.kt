package com.gbc.dormio_mobile_app.ui.maintenance

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.gbc.dormio_mobile_app.R
import com.gbc.dormio_mobile_app.databinding.FragmentMaintenanceListBinding
import com.gbc.dormio_mobile_app.viewmodel.maintenance.MaintenanceListViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MaintenanceListFragment : Fragment(R.layout.fragment_maintenance_list) {

    private var _binding: FragmentMaintenanceListBinding? = null
    private val binding get() = _binding!!
    private val viewModel: MaintenanceListViewModel by viewModels()
    private lateinit var adapter: MaintenanceAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentMaintenanceListBinding.bind(view)

        setupRecyclerView()
        setupObservers()

        binding.swipeRefresh.setOnRefreshListener {
            viewModel.fetchRequests(isRefresh = true)
        }

        binding.fabCreateRequest.setOnClickListener {
            findNavController().navigate(R.id.action_maintenanceListFragment_to_createMaintenanceFragment)
        }
    }

    private fun setupRecyclerView() {
        adapter = MaintenanceAdapter { requestId ->
            val action = MaintenanceListFragmentDirections
                .actionMaintenanceListFragmentToMaintenanceDetailFragment(requestId)
            findNavController().navigate(action)
        }
        binding.rvRequests.adapter = adapter
        binding.rvRequests.layoutManager = LinearLayoutManager(requireContext())
    }

    private fun setupObservers() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->
                    binding.swipeRefresh.isRefreshing = state.isLoading
                    adapter.submitList(state.maintenanceRequests)

                    val isEmpty = state.maintenanceRequests.isEmpty() && !state.isLoading
                    binding.llEmptyState.visibility = if (isEmpty) View.VISIBLE else View.GONE
                    binding.rvRequests.visibility = if (isEmpty) View.GONE else View.VISIBLE

                    state.errorMessage?.let {
                        Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.fetchRequests()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}