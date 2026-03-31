package com.gbc.dormio_mobile_app.ui.maintenance

import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.gbc.dormio_mobile_app.R
import com.gbc.dormio_mobile_app.data.model.maintenance.MaintenanceQuery
import com.gbc.dormio_mobile_app.data.model.maintenance.RequestStatus
import com.gbc.dormio_mobile_app.fcm.MaintenanceUpdateBus
import com.gbc.dormio_mobile_app.viewmodel.maintenance.AdminMaintenanceViewModel
import com.gbc.dormio_mobile_app.viewmodel.maintenance.StudentMaintenanceViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MaintenanceListFragment : Fragment(R.layout.fragment_maintenance_list) {

    private val studentViewModel: StudentMaintenanceViewModel by viewModels()
    private val adminViewModel: AdminMaintenanceViewModel by viewModels()

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: MaintenanceAdapter

    private var isAdmin = false

    private lateinit var searchEditText: EditText
    private lateinit var statusSpinner: Spinner
    private lateinit var urgencySpinner: Spinner

    private lateinit var createButton: Button

    private var pendingRequestId: String? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        searchEditText = view.findViewById(R.id.searchEditText)
        statusSpinner = view.findViewById(R.id.statusSpinner)
        urgencySpinner = view.findViewById(R.id.urgencySpinner)
        createButton = view.findViewById(R.id.btnCreateRequest)

        recyclerView = view.findViewById(R.id.recyclerMaintenance)

        createButton.setOnClickListener {
            openCreateDialog()
        }

        // Setup status spinner
        val statusAdapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            listOf("ALL", "PENDING", "IN_PROGRESS", "RESOLVED")
        )
        statusAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        statusSpinner.adapter = statusAdapter

        // Setup urgency spinner
        val urgencyAdapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            listOf("ALL", "LOW", "MEDIUM", "HIGH")
        )
        urgencyAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        urgencySpinner.adapter = urgencyAdapter

        determineUserRole()

        createButton.visibility = if (!isAdmin) View.VISIBLE else View.GONE

        setupFilters()

        setupRecyclerView()

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(androidx.lifecycle.Lifecycle.State.STARTED) {
                // Listen for real-time updates from FCM
                MaintenanceUpdateBus.updatesFlow.collect { (requestId, status, user) ->
                    adapter.updateRequestStatus(requestId, status, user)
                }
            }
        }

        observeViewModel()

        createState()

        // Load initial data
        loadInitialData()

        // Check for pending REQUEST_ID from arguments (from notification tap)
        val intReqId = arguments?.getInt("REQUEST_ID", -1) ?: -1
        pendingRequestId = if (intReqId != -1) {
            intReqId.toString()
        } else {
            arguments?.getString("REQUEST_ID")
        }

    }

    private fun createState(){
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(androidx.lifecycle.Lifecycle.State.STARTED) {
                studentViewModel.formUiState.collect { state ->
                    if (state.isLoading) {
                        Toast.makeText(requireContext(), "is loading", Toast.LENGTH_SHORT).show()
                    }

                    // Handle success
                    state.successMessage?.let { message ->
                        // Show success message
                        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()

                        // Refresh the list
                        studentViewModel.refresh()

                        // Clear form state after handling success
                        studentViewModel.clearFormState()
                    }

                    // Handle error
                    state.errorMessage?.let { error ->
                        Toast.makeText(requireContext(), error, Toast.LENGTH_SHORT).show()
                        studentViewModel.clearFormState()
                    }
                }
            }
        }
    }

    private fun setupFilters() {

        // Helper to create query object
        fun buildQuery(): MaintenanceQuery {
            return MaintenanceQuery(
                search = searchEditText.text.toString().takeIf { it.isNotBlank() },
                status = statusSpinner.selectedItem.toString().takeIf { it != "ALL" },
                urgency = urgencySpinner.selectedItem.toString().takeIf { it != "ALL" }
            )
        }

        // Search text
        searchEditText.addTextChangedListener {
            val query = buildQuery()
            if (isAdmin) {
                adminViewModel.fetchAllRequests(query)
            } else {
                studentViewModel.fetchMyRequests(query)
            }
        }

        // Status spinner
        statusSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                val query = buildQuery()
                if (isAdmin) {
                    adminViewModel.fetchAllRequests(query)
                } else {
                    studentViewModel.fetchMyRequests(query)
                }
            }
            override fun onNothingSelected(parent: AdapterView<*>) {}
        }

        // Urgency spinner
        urgencySpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                val query = buildQuery()
                if (isAdmin) {
                    adminViewModel.fetchAllRequests(query)
                } else {
                    studentViewModel.fetchMyRequests(query)
                }
            }
            override fun onNothingSelected(parent: AdapterView<*>) {}
        }
    }

    private fun openRequestDetailDialog(requestId: String) {
        // Inflate dialog layout
        val dialogView = layoutInflater.inflate(R.layout.dialog_request_detail_maintenance, null)
        val titleInput = dialogView.findViewById<EditText>(R.id.etTitle)
        val descInput = dialogView.findViewById<EditText>(R.id.etDescription)
        val urgencySpinner = dialogView.findViewById<Spinner>(R.id.spinnerUrgency)
        val btnUpdate = dialogView.findViewById<Button>(R.id.btnUpdateRequest)
        val btnDelete = dialogView.findViewById<Button>(R.id.btnDeleteRequest)

        // Setup urgency spinner
        val urgencyAdapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            listOf("LOW", "MEDIUM", "HIGH")
        )
        urgencyAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        urgencySpinner.adapter = urgencyAdapter

        // Fetch request details first
        studentViewModel.requestDetail(requestId)

        // Collect detail state
        val job = viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(androidx.lifecycle.Lifecycle.State.STARTED) {
                studentViewModel.detailReqUiState.collectLatest { state ->
                    state.requestDetail?.let { request ->
                        titleInput.setText(request.title)
                        descInput.setText(request.description)
                        val pos = urgencyAdapter.getPosition(request.urgency.toString())
                        urgencySpinner.setSelection(pos)
                    }
                }
            }
        }

        val dialog = androidx.appcompat.app.AlertDialog.Builder(requireContext())
            .setTitle("Request Details")
            .setView(dialogView)
            .create()

        dialog.setOnDismissListener {
            job.cancel()
        }

        // Handle update
        btnUpdate.setOnClickListener {
            val title = titleInput.text.toString()
            val desc = descInput.text.toString()
            val urgency = urgencySpinner.selectedItem.toString()

            if (title.isNotBlank() && desc.isNotBlank()) {
                studentViewModel.updateRequest(requestId, title, desc, urgency)
            }

            dialog.dismiss()
        }

        // Handle delete
        btnDelete.setOnClickListener {
            studentViewModel.deleteRequest(requestId)
            dialog.dismiss()
        }

        // Disable editing if admin
        if (isAdmin) {
            titleInput.isEnabled = false
            descInput.isEnabled = false
            urgencySpinner.isEnabled = false
            btnUpdate.visibility = View.GONE
            btnDelete.visibility = View.GONE
        }

        dialog.show()
    }

    // Dialog to create new request
    private fun openCreateDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_create_request_maintenance, null)
        val titleInput = dialogView.findViewById<EditText>(R.id.etTitle)
        val descInput = dialogView.findViewById<EditText>(R.id.etDescription)
        val urgencySpinner = dialogView.findViewById<Spinner>(R.id.spinnerUrgency)

        // populate urgency
        val urgencyAdapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            listOf("LOW", "MEDIUM", "HIGH")
        )
        urgencyAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        urgencySpinner.adapter = urgencyAdapter

        androidx.appcompat.app.AlertDialog.Builder(requireContext())
            .setTitle("New Maintenance Request")
            .setView(dialogView)
            .setPositiveButton("Create") { dialog, _ ->
                val title = titleInput.text.toString()
                val desc = descInput.text.toString()
                val urgency = urgencySpinner.selectedItem.toString()

                if (title.isNotBlank() && desc.isNotBlank()) {
                    createMaintenanceRequest(title, desc, urgency)
                }

                dialog.dismiss()
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    // create request using ViewModel
    private fun createMaintenanceRequest(title: String, description: String, urgency: String) {
        studentViewModel.createRequest(title, description, urgency)
    }

    // determine user role
    private fun determineUserRole() {

        // TODO: replace real user session logic
        val role = arguments?.getString("user_role") ?: "STUDENT"
        isAdmin = role == "ADMIN"
    }

    // setup RecyclerView and adapter
    private fun setupRecyclerView() {

        adapter = MaintenanceAdapter(
            showAdminButtons = isAdmin,
            onAcceptStatus = { request ->
                adminViewModel.updateRequestStatus(
                    request.id,
                    RequestStatus.IN_PROGRESS.value
                )
            },
            onResolvedStatus = { request ->
                adminViewModel.updateRequestStatus(
                    request.id,
                    RequestStatus.RESOLVED.value
                )
            },
            onDetail = { requestId ->
                openRequestDetailDialog(requestId)
            },
            onDelete = { request ->
                adminViewModel.deleteRequests(request.id)
            }
        )

        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter

        setupPagination()
    }

    //observe ViewModel state and update UI
    private fun observeViewModel() {

        viewLifecycleOwner.lifecycleScope.launch {

            viewLifecycleOwner.repeatOnLifecycle(
                androidx.lifecycle.Lifecycle.State.STARTED
            ) {

                if (isAdmin) {

                    adminViewModel.allReqUiState.collect { state ->
                        // post the adapter update to avoid modifying adapter during layout/scroll
                        recyclerView.post { adapter.submitList(state.maintenanceRequests) }

                        pendingRequestId?.let { reqId ->
                            if (state.maintenanceRequests.any { it.id == reqId }) {
                                openRequestDetailDialog(reqId)
                                pendingRequestId = null
                            }
                        }
                    }

                } else {

                    studentViewModel.allReqUiState.collect { state ->
                        // Post the adapter update to avoid modifying adapter during layout/scroll
                        recyclerView.post { adapter.submitList(state.maintenanceRequests) }

                        pendingRequestId?.let { reqId ->
                            if (state.maintenanceRequests.any { it.id == reqId }) {
                                openRequestDetailDialog(reqId)
                                pendingRequestId = null
                            }
                        }
                    }

                }
            }
        }
    }

    // load initial data based on role
    private fun loadInitialData() {

        if (isAdmin) {
            adminViewModel.fetchAllRequests()
        } else {
            studentViewModel.fetchMyRequests()
        }
    }

    // setup pagination by detecting when user scrolls near bottom
    private fun setupPagination() {

        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {

            override fun onScrolled(
                recyclerView: RecyclerView,
                dx: Int,
                dy: Int
            ) {
                super.onScrolled(recyclerView, dx, dy)

                val layoutManager =
                    recyclerView.layoutManager as LinearLayoutManager

                val totalItems = layoutManager.itemCount
                val lastVisible = layoutManager.findLastVisibleItemPosition()

                if (lastVisible >= totalItems - 3) {

                    if (isAdmin) {
                        adminViewModel.loadNextPage()
                    } else {
                        studentViewModel.loadNextPage()
                    }
                }
            }
        })
    }
}