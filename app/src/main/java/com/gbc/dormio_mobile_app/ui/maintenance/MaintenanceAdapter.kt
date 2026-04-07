package com.gbc.dormio_mobile_app.ui.maintenance

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.gbc.dormio_mobile_app.R
import com.gbc.dormio_mobile_app.data.model.maintenance.MaintenanceRequestDto
import com.gbc.dormio_mobile_app.data.model.maintenance.UrgencyLevel
import com.gbc.dormio_mobile_app.databinding.ItemMaintenanceRequestBinding

class MaintenanceAdapter(private val onItemClick: (Int) -> Unit) :
    ListAdapter<MaintenanceRequestDto, MaintenanceAdapter.ViewHolder>(DiffCallback) {

    inner class ViewHolder(private val binding: ItemMaintenanceRequestBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: MaintenanceRequestDto) {
            binding.tvTitle.text = item.title
            binding.tvStatus.text = "Status: ${item.status.name}"
            binding.tvDate.text = item.createdAt.take(10)

            // Dynamic Urgency Colors
            val colorRes = when (item.urgency) {
                UrgencyLevel.HIGH -> android.R.color.holo_red_light
                UrgencyLevel.MEDIUM -> android.R.color.holo_orange_light
                UrgencyLevel.LOW -> android.R.color.holo_green_light
            }
            binding.chipUrgency.text = item.urgency.name
            binding.chipUrgency.setChipBackgroundColorResource(colorRes)

            binding.root.setOnClickListener { onItemClick(item.id) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemMaintenanceRequestBinding.inflate(
            LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bind(getItem(position))

    companion object DiffCallback : DiffUtil.ItemCallback<MaintenanceRequestDto>() {
        override fun areItemsTheSame(old: MaintenanceRequestDto, new: MaintenanceRequestDto) = old.id == new.id
        override fun areContentsTheSame(old: MaintenanceRequestDto, new: MaintenanceRequestDto) = old == new
    }
}