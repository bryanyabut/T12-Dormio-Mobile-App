package com.gbc.dormio_mobile_app.ui.budget

import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast
import com.google.android.material.switchmaterial.SwitchMaterial
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.gbc.dormio_mobile_app.R
import com.gbc.dormio_mobile_app.viewmodel.budget.BudgetViewModel
import com.google.android.material.chip.Chip
import com.google.android.material.textfield.TextInputEditText
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class BudgetFragment : Fragment(R.layout.fragment_budget) {

    private lateinit var viewModel: BudgetViewModel

    private lateinit var tabAddExpense: TextView
    private lateinit var tabBillSplit: TextView
    private lateinit var tabHistory: TextView
    private lateinit var sectionAddExpense: ScrollView
    private lateinit var sectionBillSplit: ScrollView
    private lateinit var sectionHistory: ScrollView

    private lateinit var etDescription: TextInputEditText
    private lateinit var etAmount: TextInputEditText
    private lateinit var etCategory: TextInputEditText
    private lateinit var etDueDate: TextInputEditText
    private lateinit var switchSplitRoommates: SwitchMaterial
    private lateinit var btnAddExpense: Button
    private lateinit var btnCloseAddExpense: ImageView

    // History section
    private lateinit var etSearchTransactions: TextInputEditText
    private lateinit var chipAll: Chip
    private lateinit var chipFood: Chip
    private lateinit var chipUtilities: Chip
    private lateinit var chipEntertainment: Chip
    private lateinit var tvTotalOwed: TextView

    // Housemate selection
    private lateinit var tvSelectRoommates: TextView
    private lateinit var rvHousemateSelection: RecyclerView
    private lateinit var housemateSelectionAdapter: HousemateSelectionAdapter

    private lateinit var billShareAdapter: BillShareAdapter
    private lateinit var billCardAdapter: BillCardAdapter
    private lateinit var expenseHistoryAdapter: ExpenseHistoryAdapter
    private lateinit var rvMyShares: RecyclerView
    private lateinit var rvMyBills: RecyclerView
    private lateinit var rvExpenseHistory: RecyclerView

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(this)[BudgetViewModel::class.java]

        setupViews(view)
        setupAdapters()
        setupListeners()
        observeViewModel()
    }

    private fun setupViews(view: View) {
        
        tabAddExpense = view.findViewById(R.id.tabAddExpense)
        tabBillSplit = view.findViewById(R.id.tabBillSplit)
        tabHistory = view.findViewById(R.id.tabHistory)

        sectionAddExpense = view.findViewById(R.id.sectionAddExpense)
        sectionBillSplit = view.findViewById(R.id.sectionBillSplit)
        sectionHistory = view.findViewById(R.id.sectionHistory)

        etDescription = view.findViewById(R.id.etDescription)
        etAmount = view.findViewById(R.id.etAmount)
        etCategory = view.findViewById(R.id.etCategory)
        etDueDate = view.findViewById(R.id.etDueDate)
        switchSplitRoommates = view.findViewById(R.id.switchSplitRoommates)
        btnAddExpense = view.findViewById(R.id.btnAddExpense)
        btnCloseAddExpense = view.findViewById(R.id.btnCloseAddExpense)

        // History section
        etSearchTransactions = view.findViewById(R.id.etSearchTransactions)
        chipAll = view.findViewById(R.id.chipAll)
        chipFood = view.findViewById(R.id.chipFood)
        chipUtilities = view.findViewById(R.id.chipUtilities)
        chipEntertainment = view.findViewById(R.id.chipEntertainment)
        tvTotalOwed = view.findViewById(R.id.tvTotalOwed)

        // Housemate selection
        tvSelectRoommates = view.findViewById(R.id.tvSelectRoommates)
        rvHousemateSelection = view.findViewById(R.id.rvHousemateSelection)

        rvMyShares = view.findViewById(R.id.rvMyShares)
        rvMyBills = view.findViewById(R.id.rvMyBills)
        rvExpenseHistory = view.findViewById(R.id.rvExpenseHistory)

        rvMyShares.layoutManager = LinearLayoutManager(requireContext())
        rvMyBills.layoutManager = LinearLayoutManager(requireContext())
        rvExpenseHistory.layoutManager = LinearLayoutManager(requireContext())

        // Housemate selection adapter
        housemateSelectionAdapter = HousemateSelectionAdapter(
            onShareAmountChanged = { userId, amount ->
                viewModel.updateShareAmount(userId, amount)
            }
        )
        rvHousemateSelection.layoutManager = LinearLayoutManager(requireContext())
        rvHousemateSelection.adapter = housemateSelectionAdapter

        // Toggle housemate selection visibility
        switchSplitRoommates.setOnCheckedChangeListener { _, isChecked ->
            tvSelectRoommates.visibility = if (isChecked) View.VISIBLE else View.GONE
            rvHousemateSelection.visibility = if (isChecked) View.VISIBLE else View.GONE
        }
    }

    private fun setupAdapters() {
        billShareAdapter = BillShareAdapter { share ->
            viewModel.markShareAsPaid(share.billId, share.id)
        }
        rvMyShares.adapter = billShareAdapter

        billCardAdapter = BillCardAdapter()
        rvMyBills.adapter = billCardAdapter

        expenseHistoryAdapter = ExpenseHistoryAdapter()
        rvExpenseHistory.adapter = expenseHistoryAdapter
    }

    private fun setupListeners() {
        
        tabAddExpense.setOnClickListener { selectTab(0) }
        tabBillSplit.setOnClickListener { selectTab(1) }
        tabHistory.setOnClickListener { selectTab(2) }

        btnAddExpense.setOnClickListener {
            handleAddExpense()
        }

        btnCloseAddExpense.setOnClickListener {
            clearExpenseForm()
        }

        etSearchTransactions.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                // Filter expenses based on search
                val filtered = viewModel.uiState.value.expenses.filter {
                    it.description.contains(s.toString(), ignoreCase = true) ||
                    it.category?.contains(s.toString(), ignoreCase = true) == true
                }
                expenseHistoryAdapter.updateExpenses(filtered)
            }
        })

        chipAll.setOnClickListener {
            chipAll.isChecked = true
            expenseHistoryAdapter.updateExpenses(viewModel.uiState.value.expenses)
        }
        chipFood.setOnClickListener {
            expenseHistoryAdapter.updateExpenses(viewModel.uiState.value.expenses.filter { it.category == "FOOD" })
        }
        chipUtilities.setOnClickListener {
            expenseHistoryAdapter.updateExpenses(viewModel.uiState.value.expenses.filter { it.category == "UTILITIES" })
        }
        chipEntertainment.setOnClickListener {
            expenseHistoryAdapter.updateExpenses(viewModel.uiState.value.expenses.filter { it.category == "ENTERTAINMENT" })
        }

        selectTab(0)
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.uiState.collect { state ->
                
                if (state.isLoading) {
                    // Show loading indicator if needed
                }

                billShareAdapter.updateShares(state.myShares)
                billCardAdapter.updateBills(state.myBills)
                expenseHistoryAdapter.updateExpenses(state.expenses)

                // Calculate total owed from unpaid shares
                val totalOwed = state.myShares
                    .filter { !it.hasPaid }
                    .sumOf { it.shareAmount.toDoubleOrNull() ?: 0.0 }
                tvTotalOwed.text = String.format("$%.2f", totalOwed)

                housemateSelectionAdapter.updateHousemates(state.housemates)
                housemateSelectionAdapter.updateSelectedShares(state.selectedShares)

                state.successMessage?.let { message ->
                    Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
                    viewModel.clearMessages()
                }
                state.errorMessage?.let { error ->
                    Toast.makeText(requireContext(), error, Toast.LENGTH_SHORT).show()
                    viewModel.clearMessages()
                }
            }
        }
    }

    private fun handleAddExpense() {
        val description = etDescription.text?.toString()?.trim() ?: ""
        val amount = etAmount.text?.toString()?.trim() ?: ""
        val category = etCategory.text?.toString()?.trim()
        val dueDate = etDueDate.text?.toString()?.trim() ?: ""

        if (description.isEmpty() || amount.isEmpty() || dueDate.isEmpty()) {
            Toast.makeText(requireContext(), "Please fill in required fields", Toast.LENGTH_SHORT).show()
            return
        }

        if (switchSplitRoommates.isChecked) {
            val selectedShares = viewModel.getSelectedShareItems()
            if (selectedShares.isEmpty()) {
                Toast.makeText(requireContext(), "Please select at least one roommate and enter their share amount", Toast.LENGTH_SHORT).show()
                return
            }
            // Create bill with split
            viewModel.createBillAndOptionalSplit(
                billName = description,
                totalAmount = amount,
                dueDate = dueDate,
                category = category,
                splitWithHousemates = true,
                shares = selectedShares
            )
        } else {
            // Create standalone expense
            viewModel.createExpense(
                description = description,
                amount = amount,
                category = category,
                expenseDate = dueDate
            )
        }

        clearExpenseForm()
    }

    private fun clearExpenseForm() {
        etDescription.text?.clear()
        etAmount.text?.clear()
        etCategory.text?.clear()
        etDueDate.text?.clear()
        switchSplitRoommates.isChecked = false
        viewModel.clearSelectedShares()
    }

    private fun selectTab(index: Int) {
        val blueColor = requireContext().getColor(R.color.primary_blue)
        val whiteColor = Color.WHITE

        val tabs = listOf(tabAddExpense, tabBillSplit, tabHistory)
        val sections = listOf(sectionAddExpense, sectionBillSplit, sectionHistory)
        val selectedBg = R.drawable.bg_tab_selected
        val unselectedBg = R.drawable.bg_tab_unselected

        tabs.forEachIndexed { i, tab ->
            if (i == index) {
                tab.setBackgroundResource(selectedBg)
                tab.setTextColor(whiteColor)
            } else {
                tab.setBackgroundResource(unselectedBg)
                tab.setTextColor(blueColor)
            }
        }

        sections.forEachIndexed { i, section ->
            section.visibility = if (i == index) View.VISIBLE else View.GONE
        }

        // Refresh data when switching tabs
        when (index) {
            1 -> viewModel.loadMyShares()
            2 -> viewModel.loadExpenses()
        }
    }
}
