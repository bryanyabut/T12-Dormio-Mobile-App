package com.gbc.dormio_mobile_app.ui.notifications

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.gbc.dormio_mobile_app.R
import com.gbc.dormio_mobile_app.data.model.notification.NotificationModel
import com.gbc.dormio_mobile_app.data.model.notification.NotificationType
import com.gbc.dormio_mobile_app.fcm.ChoreUpdateBus
import com.gbc.dormio_mobile_app.fcm.MaintenanceUpdateBus
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class NotificationsFragment : Fragment(R.layout.activity_notifications) {

    private lateinit var adapter: NotificationAdapter
    private val notificationsList = mutableListOf<NotificationModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()

//        addNewNotification("New Bill Split", "Sarah added 'Internet Bill' you owe $25", NotificationType.BILL)
//        addNewNotification("Chore Reminder", "Take Out Trash is due today", NotificationType.CHORE)
//        addNewNotification("Maintenance Update", "Ticket #24 Status changed to: In Progress", NotificationType.MAINTENANCE)

        viewLifecycleOwner.lifecycleScope.launch {
            ChoreUpdateBus.updatesFlow.collect { event ->
                addNewNotification(
                    "Chore Update",
                    "Chore #${event.choreId} is now ${event.status}",
                    NotificationType.CHORE)
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            MaintenanceUpdateBus.updatesFlow.collect { (id, status, user) ->
                addNewNotification(
                    "Maintenance Update",
                    "Request #$id updated to $status",
                    NotificationType.MAINTENANCE
                )
            }
        }
    }

    private fun setupRecyclerView() {
        val recyclerView = view?.findViewById<RecyclerView>(R.id.notificationsRecyclerView)
        adapter = NotificationAdapter(notificationsList)
        recyclerView?.layoutManager = LinearLayoutManager(context)
        recyclerView?.adapter = adapter
    }

    private fun addNewNotification(title: String, message: String, type: NotificationType) {
        val newNotif = NotificationModel(title, message, System.currentTimeMillis(), type)
        notificationsList.add(0, newNotif)

        adapter.notifyItemInserted(0)

        view?.findViewById<RecyclerView>(R.id.notificationsRecyclerView)?.scrollToPosition(0)
    }
}
