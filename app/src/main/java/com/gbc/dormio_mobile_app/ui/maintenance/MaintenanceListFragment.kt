package com.gbc.dormio_mobile_app.ui.maintenance

import android.app.AlertDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
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

        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                viewModel.onSearchQueryChanged(query)
                binding.searchView.clearFocus()
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if (newText.isNullOrEmpty()) {
                    viewModel.onSearchQueryChanged(null)
                }
                return true
            }
        })

        binding.btnSort.setOnClickListener {
            showSortDialog()
        }
    }

    private fun showSortDialog() {
        val options = arrayOf("Newest First", "Oldest First", "Priority (High-Low)")
        AlertDialog.Builder(requireContext())
            .setTitle("Sort By")
            .setItems(options) { _, which ->
                val sortVal = when(which) {
                    0 -> "createdAt:desc"
                    1 -> "createdAt:asc"
                    else -> "urgency:desc"
                }
                viewModel.onSortChanged(sortVal)
            }.show()
    }

    private fun setupRecyclerView() {
        adapter = MaintenanceAdapter { requestId ->
            val action = MaintenanceListFragmentDirections
                .actionMaintenanceListFragmentToMaintenanceDetailFragment(requestId)
            findNavController().navigate(action)
        }
        binding.rvRequests.adapter = adapter
        binding.rvRequests.layoutManager = LinearLayoutManager(requireContext())

        val swipeHandler = object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {

            override fun onChildDraw(
                c: android.graphics.Canvas,
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                dX: Float,
                dY: Float,
                actionState: Int,
                isCurrentlyActive: Boolean
            ) {
                val itemView = viewHolder.itemView
                val paint = android.graphics.Paint()

                if (dX < 0) {
                    paint.color = android.graphics.Color.RED
                    c.drawRect(
                        itemView.right.toFloat() + dX,
                        itemView.top.toFloat(),
                        itemView.right.toFloat(),
                        itemView.bottom.toFloat(),
                        paint
                    )

                    val icon = ContextCompat.getDrawable(requireContext(),
                        android.R.drawable.ic_menu_delete)
                    icon?.let {
                        val iconMargin = (itemView.height - it.intrinsicHeight) / 2
                        val iconTop = itemView.top + iconMargin
                        val iconBottom = iconTop + it.intrinsicHeight
                        val iconLeft = itemView.right - iconMargin - it.intrinsicWidth
                        val iconRight = itemView.right - iconMargin
                        it.setBounds(iconLeft, iconTop, iconRight, iconBottom)
                        it.draw(c)
                    }
                }

                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
            }

            override fun onMove(rv: RecyclerView, vh: RecyclerView.ViewHolder, t: RecyclerView.ViewHolder) = false

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                val request = adapter.currentList[position]

                AlertDialog.Builder(requireContext())
                    .setTitle("Delete Request")
                    .setMessage("Are you sure?")
                    .setPositiveButton("Delete") { _, _ -> viewModel.deleteRequest(request.id) }
                    .setNegativeButton("Cancel") { _, _ -> adapter.notifyItemChanged(position) }
                    .show()
            }
        }
        ItemTouchHelper(swipeHandler).attachToRecyclerView(binding.rvRequests)

        binding.rvRequests.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                val layoutManager = binding.rvRequests.layoutManager as LinearLayoutManager
                val lastVisibleItem = layoutManager.findLastVisibleItemPosition()
                val totalItems = adapter.itemCount

                if (!viewModel.uiState.value.isLoading &&
                    viewModel.uiState.value.hasMorePages &&
                    lastVisibleItem >= totalItems - 3) {
                    viewModel.fetchRequests()
                }
            }
        })
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
                        viewModel.resetMessages()
                    }

                    state.successMessage?.let {
                        Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
                        viewModel.resetMessages()
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