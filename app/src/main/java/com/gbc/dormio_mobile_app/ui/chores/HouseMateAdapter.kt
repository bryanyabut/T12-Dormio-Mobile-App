package com.gbc.dormio_mobile_app.ui.chores

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.gbc.dormio_mobile_app.R
import com.gbc.dormio_mobile_app.data.model.chores.Housemate

class HousemateAdapter(
    private val onUserSelected: (Int) -> Unit
) : ListAdapter<Housemate, HousemateAdapter.ViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_chore_assign, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item)
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val tvAvatar = view.findViewById<TextView>(R.id.tvAvatar)
        private val tvName = view.findViewById<TextView>(R.id.tvName)
        private val cbAssign = view.findViewById<CheckBox>(R.id.cbAssign)

        fun bind(housemate: Housemate) {
            tvName.text = if (housemate.isCurrentUser) "${housemate.firstName} (You)" else housemate.firstName

            val initials = "${housemate.firstName.take(1)}${housemate.lastName.take(1)}".uppercase()
            tvAvatar.text = initials

            cbAssign.setOnCheckedChangeListener(null)
            cbAssign.isChecked = housemate.isSelected

            cbAssign.setOnCheckedChangeListener { _, _ ->
                onUserSelected(housemate.id)
            }

            itemView.setOnClickListener {
                onUserSelected(housemate.id)
            }
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<Housemate>() {
        override fun areItemsTheSame(old: Housemate, new: Housemate) = old.id == new.id
        override fun areContentsTheSame(old: Housemate, new: Housemate) = old == new
    }
}