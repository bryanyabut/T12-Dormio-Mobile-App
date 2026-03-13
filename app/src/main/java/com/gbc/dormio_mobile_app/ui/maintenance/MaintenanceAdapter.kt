package com.gbc.dormio_mobile_app.ui.maintenance

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.gbc.dormio_mobile_app.R
import com.gbc.dormio_mobile_app.data.model.MaintenanceRequestDto


class MaintenanceAdapter(
    private val showAdminButtons: Boolean = false,
    private val onUpdateStatus: ((MaintenanceRequestDto) -> Unit)? = null,
    private val onDetail: ((String) -> Unit)? = null,
    private val onDelete: ((MaintenanceRequestDto) -> Unit)? = null
) : RecyclerView.Adapter<MaintenanceAdapter.MaintenanceViewHolder>() {

    private val items = mutableListOf<MaintenanceRequestDto>()

    fun submitList(newItems: List<MaintenanceRequestDto>) {
        items.clear()
        items.addAll(newItems)
        notifyDataSetChanged()
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
        private val btnUpdate: Button = itemView.findViewById(R.id.btnUpdate)
        private val btnDetail: Button = itemView.findViewById(R.id.btnDetail)
        private val btnDelete: Button = itemView.findViewById(R.id.btnDelete)

        fun bind(request: MaintenanceRequestDto) {
            tvTitle.text = request.title
            tvDescription.text = request.description
            tvStatus.text = request.status.value
            tvUrgency.text = request.urgency.value

            btnUpdate.visibility = if (showAdminButtons) View.VISIBLE else View.GONE
            btnDelete.visibility = if (showAdminButtons) View.VISIBLE else View.GONE

            btnUpdate.setOnClickListener { onUpdateStatus?.invoke(request) }
            btnDetail.setOnClickListener { onDetail?.invoke(request.id) }
            btnDelete.setOnClickListener { onDelete?.invoke(request) }
        }
    }
}