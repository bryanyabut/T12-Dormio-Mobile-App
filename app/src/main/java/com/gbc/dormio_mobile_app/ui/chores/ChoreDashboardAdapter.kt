package com.gbc.dormio_mobile_app.ui.chores

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.gbc.dormio_mobile_app.R
import com.gbc.dormio_mobile_app.data.model.chores.AssignedUser
import com.gbc.dormio_mobile_app.data.model.chores.Chore
import com.gbc.dormio_mobile_app.databinding.ItemChoreDashboardBinding

class ChoreDashboardAdapter(
    private val onCompleteClick: (Chore) -> Unit,
    private val onEditClick: (Chore) -> Unit
) : ListAdapter<Chore, ChoreDashboardAdapter.ChoreViewHolder>(ChoreDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChoreViewHolder {
        val binding = ItemChoreDashboardBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ChoreViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ChoreViewHolder, position: Int) {
        holder.bind(getItem(position), onCompleteClick, onEditClick)

    }

    class ChoreViewHolder(private val binding: ItemChoreDashboardBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(chore: Chore, onComplete: (Chore) -> Unit, onEdit: (Chore) -> Unit) {
            binding.tvChoreName.text = chore.name

            if (chore.status == "COMPLETED") {
                binding.tvChoreName.paintFlags = binding.tvChoreName.paintFlags or android.graphics.Paint.STRIKE_THRU_TEXT_FLAG

                binding.tvChoreName.setTextColor(ContextCompat.getColor(itemView.context, android.R.color.darker_gray))

                binding.btnCompleteChore.isEnabled = false
                binding.btnCompleteChore.alpha = 0.5f
            } else {
                binding.tvChoreName.paintFlags = binding.tvChoreName.paintFlags and android.graphics.Paint.STRIKE_THRU_TEXT_FLAG.inv()
                binding.tvChoreName.setTextColor(ContextCompat.getColor(itemView.context, R.color.black))
                binding.btnCompleteChore.isEnabled = true
                binding.btnCompleteChore.alpha = 1.0f
            }

            val formattedDate = formatDate(chore.dueDate)

            when {
                chore.status == "COMPLETED" -> {
                    binding.tvChoreDueDate.text = "Finished"
                    binding.tvChoreDueDate.setTextColor(ContextCompat.getColor(itemView.context, android.R.color.darker_gray))
                }
                chore.isOverdue -> {
                    binding.tvChoreDueDate.text = "Overdue: $formattedDate"
                    binding.tvChoreDueDate.setTextColor(ContextCompat.getColor(itemView.context, R.color.feature_red))
                }
                chore.isDueToday -> {
                    binding.tvChoreDueDate.text = "Today: $formattedDate"
                    binding.tvChoreDueDate.setTextColor(ContextCompat.getColor(itemView.context, R.color.feature_red))
                }
                else -> {
                    binding.tvChoreDueDate.text = formattedDate
                    binding.tvChoreDueDate.setTextColor(ContextCompat.getColor(itemView.context, android.R.color.darker_gray))
                }
            }

            setupAssignedUsers(chore.assignedUsers)

            binding.btnCompleteChore.setOnClickListener { onComplete(chore) }
            binding.btnEditChore.setOnClickListener { onEdit(chore) }
        }

        private fun setupAssignedUsers(users: List<AssignedUser>) {
            val llUsers = binding.llAssignedUsers
            val childCount = llUsers.childCount
            if (childCount > 1) {
                llUsers.removeViews(1, childCount - 1)
            }

            users.forEach { user ->
                val bubble = TextView(itemView.context).apply {
                    val size = (28 * resources.displayMetrics.density).toInt()
                    layoutParams = LinearLayout.LayoutParams(size, size).apply {
                        setMargins(0, 0, (4 * resources.displayMetrics.density).toInt(), 0)
                    }
                    background = ContextCompat.getDrawable(context, R.drawable.bg_avatar_blue)
                    gravity = android.view.Gravity.CENTER
                    text = user.initials
                    setTextColor(ContextCompat.getColor(context, R.color.white))
                    textSize = 9f
                    setTypeface(null, android.graphics.Typeface.BOLD)
                }
                llUsers.addView(bubble)
            }
        }
    }


}
private fun formatDate(dateString: String): String {
    return try {
        val inputFormat = java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", java.util.Locale.getDefault())
        inputFormat.timeZone = java.util.TimeZone.getTimeZone("UTC")

        val outputFormat = java.text.SimpleDateFormat("MMM dd", java.util.Locale.getDefault())

        val date = inputFormat.parse(dateString)
        date?.let { outputFormat.format(it) } ?: dateString
    } catch (e: Exception) {
        dateString
    }
}

class ChoreDiffCallback : DiffUtil.ItemCallback<Chore>() {
    override fun areItemsTheSame(oldItem: Chore, newItem: Chore) = oldItem.id == newItem.id
    override fun areContentsTheSame(oldItem: Chore, newItem: Chore) = oldItem == newItem
}