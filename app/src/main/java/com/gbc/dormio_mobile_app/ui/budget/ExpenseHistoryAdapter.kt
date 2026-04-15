package com.gbc.dormio_mobile_app.ui.budget

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.gbc.dormio_mobile_app.R
import com.gbc.dormio_mobile_app.data.model.budget.ExpenseDto

class ExpenseHistoryAdapter(
    private var expenses: List<ExpenseDto> = emptyList()
) : RecyclerView.Adapter<ExpenseHistoryAdapter.ExpenseHistoryViewHolder>() {

    fun updateExpenses(newExpenses: List<ExpenseDto>) {
        expenses = newExpenses
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExpenseHistoryViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_expense_history, parent, false)
        return ExpenseHistoryViewHolder(view)
    }

    override fun onBindViewHolder(holder: ExpenseHistoryViewHolder, position: Int) {
        holder.bind(expenses[position])
    }

    override fun getItemCount(): Int = expenses.size

    inner class ExpenseHistoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvExpenseName: TextView = itemView.findViewById(R.id.tvExpenseName)
        private val tvExpenseCategory: TextView = itemView.findViewById(R.id.tvExpenseCategory)
        private val tvExpenseAmount: TextView = itemView.findViewById(R.id.tvExpenseAmount)

        fun bind(expense: ExpenseDto) {
            tvExpenseName.text = expense.description
            tvExpenseCategory.text = expense.category ?: "Other"
            tvExpenseAmount.text = "$${expense.amount}"
        }
    }
}
