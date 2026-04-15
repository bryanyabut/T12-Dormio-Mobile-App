package com.gbc.dormio_mobile_app.ui.budget

import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.gbc.dormio_mobile_app.R
import com.gbc.dormio_mobile_app.data.model.budget.BillShareUserDto

class HousemateSelectionAdapter(
    private var housemates: List<BillShareUserDto> = emptyList(),
    private val onShareAmountChanged: (userId: Int, amount: String) -> Unit
) : RecyclerView.Adapter<HousemateSelectionAdapter.HousemateViewHolder>() {

    private var selectedShares: Map<Int, String> = emptyMap()

    fun updateHousemates(newHousemates: List<BillShareUserDto>) {
        housemates = newHousemates
        notifyDataSetChanged()
    }

    fun updateSelectedShares(newShares: Map<Int, String>) {
        selectedShares = newShares
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HousemateViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_housemate_selection, parent, false)
        return HousemateViewHolder(view)
    }

    override fun onBindViewHolder(holder: HousemateViewHolder, position: Int) {
        holder.bind(housemates[position], selectedShares)
    }

    override fun getItemCount(): Int = housemates.size

    inner class HousemateViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val checkboxHousemate: CheckBox = itemView.findViewById(R.id.checkboxHousemate)
        private val tvHousemateName: TextView = itemView.findViewById(R.id.tvHousemateName)
        private val etShareAmount: com.google.android.material.textfield.TextInputEditText = itemView.findViewById(R.id.etShareAmount)

        private var currentUserId: Int = -1

        fun bind(housemate: BillShareUserDto, selectedShares: Map<Int, String>) {
            currentUserId = housemate.id
            tvHousemateName.text = "${housemate.firstName} ${housemate.lastName}"

            val currentAmount = selectedShares[housemate.id]
            val isSelected = currentAmount != null

            checkboxHousemate.isChecked = isSelected
            etShareAmount.setText(currentAmount ?: "")
            etShareAmount.isEnabled = isSelected

            // Prevent infinite loop when setting text programmatically
            etShareAmount.removeTextChangedListener(textWatcher)
            checkboxHousemate.setOnCheckedChangeListener(null)
            etShareAmount.addTextChangedListener(textWatcher)

            checkboxHousemate.setOnCheckedChangeListener { _, checked ->
                if (checked) {
                    etShareAmount.isEnabled = true
                    etShareAmount.requestFocus()
                    val amount = etShareAmount.text?.toString() ?: ""
                    if (amount.isNotEmpty()) {
                        onShareAmountChanged(currentUserId, amount)
                    }
                } else {
                    etShareAmount.isEnabled = false
                    onShareAmountChanged(currentUserId, "")
                }
            }
        }

        private val textWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                val amount = s?.toString() ?: ""
                if (amount.isNotEmpty()) {
                    checkboxHousemate.isChecked = true
                    onShareAmountChanged(currentUserId, amount)
                } else {
                    checkboxHousemate.isChecked = false
                    onShareAmountChanged(currentUserId, "")
                }
            }
        }
    }
}
