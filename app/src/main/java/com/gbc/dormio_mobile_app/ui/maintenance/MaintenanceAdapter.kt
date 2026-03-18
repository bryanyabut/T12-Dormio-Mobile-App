package com.gbc.dormio_mobile_app.ui.maintenance

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.gbc.dormio_mobile_app.R
import com.gbc.dormio_mobile_app.data.model.MaintenanceRequestDto
import com.gbc.dormio_mobile_app.data.model.RequestStatus
import com.gbc.dormio_mobile_app.data.model.UserDto


class MaintenanceAdapter(
    private val showAdminButtons: Boolean = false,
    private val onAcceptStatus: ((MaintenanceRequestDto) -> Unit)? = null,
    private val onResolvedStatus: ((MaintenanceRequestDto) -> Unit)? = null,
    private val onDetail: ((String) -> Unit)? = null,
    private val onDelete: ((MaintenanceRequestDto) -> Unit)? = null
) : RecyclerView.Adapter<MaintenanceAdapter.MaintenanceViewHolder>() {

    private val items = mutableListOf<MaintenanceRequestDto>()

    fun submitList(newItems: List<MaintenanceRequestDto>) {
        items.clear()
        items.addAll(newItems)
        notifyDataSetChanged()
    }

    fun updateRequestStatus(requestId: String, status: RequestStatus, user: UserDto? = null) {
        val index = items.indexOfFirst { it.id == requestId }
        if (index != -1) {
            val oldItem = items[index]
            val resolvedUser: UserDto = when {
                user != null -> user
                else -> try {
                    oldItem.user
                } catch (e: Exception) {
                    UserDto(id = 0, email = "", firstName = "", lastName = "", role = "STUDENT")
                }
            }

            val updatedItem = oldItem.copy(
                status = status,
                user = resolvedUser
            )
            items[index] = updatedItem
            notifyItemChanged(index)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MaintenanceViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_maintenance_request, parent, false)
        return MaintenanceViewHolder(view)
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: MaintenanceViewHolder, position: Int) {
        holder.bind(items[position])
    }

    inner class MaintenanceViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvTitle: TextView = itemView.findViewById(R.id.tvTitle)
        private val tvDescription: TextView = itemView.findViewById(R.id.tvDescription)
        private val tvStatus: TextView = itemView.findViewById(R.id.tvStatus)
        private val tvUrgency: TextView = itemView.findViewById(R.id.tvUrgency)
        private val btnAccept: Button = itemView.findViewById(R.id.btnAccept)
        private val btnResolved: Button = itemView.findViewById(R.id.btnResolved)
        private val btnDetail: Button = itemView.findViewById(R.id.btnDetail)
        private val btnDelete: Button = itemView.findViewById(R.id.btnDelete)

        fun bind(request: MaintenanceRequestDto) {
            tvTitle.text = request.title
            tvDescription.text = request.description
            tvStatus.text = request.status.value
            tvUrgency.text = request.urgency.value

            btnAccept.visibility = if (showAdminButtons) View.VISIBLE else View.GONE
            btnResolved.visibility = if (showAdminButtons) View.VISIBLE else View.GONE
            btnDelete.visibility = if (showAdminButtons) View.VISIBLE else View.GONE

            btnAccept.setOnClickListener { onAcceptStatus?.invoke(request) }
            btnResolved.setOnClickListener { onResolvedStatus?.invoke(request) }
            btnDetail.setOnClickListener { onDetail?.invoke(request.id) }
            btnDelete.setOnClickListener { onDelete?.invoke(request) }
        }

    }
}