package com.gbc.dormio_mobile_app.ui.budget

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.gbc.dormio_mobile_app.R
import com.gbc.dormio_mobile_app.data.model.budget.BillDto
import java.text.SimpleDateFormat
import java.util.Locale

class BillCardAdapter(
    private var bills: List<BillDto> = emptyList()
) : RecyclerView.Adapter<BillCardAdapter.BillCardViewHolder>() {

    fun updateBills(newBills: List<BillDto>) {
        bills = newBills
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BillCardViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_bill_card, parent, false)
        return BillCardViewHolder(view)
    }

    override fun onBindViewHolder(holder: BillCardViewHolder, position: Int) {
        holder.bind(bills[position])
    }

    override fun getItemCount(): Int = bills.size

    inner class BillCardViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvBillName: TextView = itemView.findViewById(R.id.tvBillName)
        private val tvBillDueDate: TextView = itemView.findViewById(R.id.tvBillDueDate)
        private val tvBillAmount: TextView = itemView.findViewById(R.id.tvBillAmount)
        private val llBillShares: LinearLayout = itemView.findViewById(R.id.llBillShares)
        private val tvBillCategory: TextView = itemView.findViewById(R.id.tvBillCategory)
        private val tvBillStatus: TextView = itemView.findViewById(R.id.tvBillStatus)

        private val dateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
        private val inputDateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

        fun bind(bill: BillDto) {
            tvBillName.text = bill.billName
            // Format date from ISO or yyyy-MM-dd to readable format
            val formattedDate = try {
                val date = inputDateFormat.parse(bill.dueDate.substring(0, 10))
                dateFormat.format(date!!)
            } catch (e: Exception) {
                bill.dueDate.substring(0, 10)
            }
            tvBillDueDate.text = formattedDate
            tvBillAmount.text = "$${bill.totalAmount}"
            tvBillCategory.text = bill.category?.replaceFirstChar { it.uppercase() } ?: "Other"

            // Status
            val statusText = when (bill.status) {
                "PAID" -> "Paid"
                "PARTIALLY_PAID" -> "Partially Paid"
                else -> "Unpaid"
            }
            tvBillStatus.text = statusText

            val statusColor = when (bill.status) {
                "PAID" -> R.color.feature_green
                "PARTIALLY_PAID" -> R.color.orange
                else -> R.color.feature_red
            }
            tvBillStatus.setTextColor(ContextCompat.getColor(itemView.context, statusColor))

            // Build shares row dynamically
            llBillShares.removeAllViews()
            bill.billSharing?.forEach { share ->
                val shareTextView = TextView(itemView.context).apply {
                    text = "${share.user?.firstName ?: "Unknown"} \u00b7 $${share.shareAmount}"
                    setTextColor(ContextCompat.getColor(context, R.color.primary_blue))
                    textSize = 13f
                    layoutParams = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                    ).apply {
                        marginEnd = 16
                    }
                }
                llBillShares.addView(shareTextView)
            }
        }
    }
}
