package com.gbc.dormio_mobile_app.ui.notifications

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.gbc.dormio_mobile_app.R
import com.gbc.dormio_mobile_app.data.model.notification.NotificationModel
import com.gbc.dormio_mobile_app.data.model.notification.NotificationType
import com.gbc.dormio_mobile_app.fcm.ChoreUpdateBus
import com.gbc.dormio_mobile_app.fcm.MaintenanceUpdateBus
import com.gbc.dormio_mobile_app.viewmodel.notifications.NotificationViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class NotificationsFragment : Fragment(R.layout.activity_notifications) {

    private lateinit var adapter: NotificationAdapter
    private val viewModel: NotificationViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        observeUiState()
        setupRealTimeListeners()
    }

    private fun setupRecyclerView() {
        val recyclerView = view?.findViewById<RecyclerView>(R.id.notificationsRecyclerView)
        adapter = NotificationAdapter(mutableListOf())
        recyclerView?.layoutManager = LinearLayoutManager(context)
        recyclerView?.adapter = adapter
    }

    private fun observeUiState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->
                    val progressBar = view?.findViewById<View>(R.id.progressBar)
                    progressBar?.visibility = if (state.isLoading) View.VISIBLE else View.GONE

                    if (state.notifications.isNotEmpty()) {
                        adapter.updateItems(state.notifications)
                    }

                    state.errorMessage?.let {
                        Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    private fun setupRealTimeListeners() {
        viewLifecycleOwner.lifecycleScope.launch {
            ChoreUpdateBus.updatesFlow.collect { event ->
                val newNotif = NotificationModel(
                    title = "Chore Update",
                    message = "Chore #${event.choreId} is now ${event.status}",
                    timestamp = System.currentTimeMillis(),
                    type = NotificationType.CHORE
                )
                viewModel.addRealTimeNotification(newNotif)
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            MaintenanceUpdateBus.updatesFlow.collect { (id, status, _) ->
                val newNotif = NotificationModel(
                    title = "Maintenance Update",
                    message = "Request #$id updated to $status",
                    timestamp = System.currentTimeMillis(),
                    type = NotificationType.MAINTENANCE
                )
                viewModel.addRealTimeNotification(newNotif)
            }
        }
    }
}
