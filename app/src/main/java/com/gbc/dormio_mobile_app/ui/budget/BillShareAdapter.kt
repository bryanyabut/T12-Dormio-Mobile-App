package com.gbc.dormio_mobile_app.ui.budget

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.gbc.dormio_mobile_app.R
import com.gbc.dormio_mobile_app.data.model.budget.BillSharingDto

class BillShareAdapter(
    private var shares: List<BillSharingDto> = emptyList(),
    private val onMarkPaid: ((BillSharingDto) -> Unit)? = null
) : RecyclerView.Adapter<BillShareAdapter.BillShareViewHolder>() {

    fun updateShares(newShares: List<BillSharingDto>) {
        shares = newShares
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BillShareViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_bill_share, parent, false)
        return BillShareViewHolder(view)
    }

    override fun onBindViewHolder(holder: BillShareViewHolder, position: Int) {
        holder.bind(shares[position])
    }

    override fun getItemCount(): Int = shares.size

    inner class BillShareViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvInitial: TextView = itemView.findViewById(R.id.tvInitial)
        private val tvShareUserName: TextView = itemView.findViewById(R.id.tvShareUserName)
        private val tvShareLabel: TextView = itemView.findViewById(R.id.tvShareLabel)
        private val tvShareAmount: TextView = itemView.findViewById(R.id.tvShareAmount)
        private val tvShareStatus: TextView = itemView.findViewById(R.id.tvShareStatus)

        fun bind(share: BillSharingDto) {
            val userName = share.user?.let { "${it.firstName} ${it.lastName}" } ?: "Unknown"
            val initial = share.user?.firstName?.firstOrNull()?.toString() ?: "?"

            tvInitial.text = initial
            tvShareUserName.text = userName
            tvShareLabel.text = if (share.hasPaid) "Paid" else "Owes you"
            tvShareAmount.text = "$${share.shareAmount}"

            if (share.hasPaid) {
                tvShareStatus.text = "Paid"
                tvShareStatus.setTextColor(itemView.context.getColor(R.color.feature_green))
                tvShareAmount.setTextColor(itemView.context.getColor(R.color.feature_green))
            } else {
                tvShareStatus.text = "Pending"
                tvShareStatus.setTextColor(itemView.context.getColor(android.R.color.darker_gray))
                tvShareAmount.setTextColor(itemView.context.getColor(R.color.feature_green))
            }

            if (share.hasPaid) {
                itemView.setOnClickListener(null)
                itemView.alpha = 0.6f
            } else {
                itemView.setOnClickListener { onMarkPaid?.invoke(share) }
                itemView.alpha = 1.0f
            }
        }
    }
}
