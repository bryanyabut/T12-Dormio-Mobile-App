package com.gbc.dormio_mobile_app.ui.budget

import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.ScrollView
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.gbc.dormio_mobile_app.R

class BudgetFragment : Fragment(R.layout.fragment_budget) {

    private lateinit var tabAddExpense: TextView
    private lateinit var tabBillSplit: TextView
    private lateinit var tabHistory: TextView

    private lateinit var sectionAddExpense: ScrollView
    private lateinit var sectionBillSplit: ScrollView
    private lateinit var sectionHistory: ScrollView

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        tabAddExpense = view.findViewById(R.id.tabAddExpense)
        tabBillSplit = view.findViewById(R.id.tabBillSplit)
        tabHistory = view.findViewById(R.id.tabHistory)

        sectionAddExpense = view.findViewById(R.id.sectionAddExpense)
        sectionBillSplit = view.findViewById(R.id.sectionBillSplit)
        sectionHistory = view.findViewById(R.id.sectionHistory)

        tabAddExpense.setOnClickListener { selectTab(0) }
        tabBillSplit.setOnClickListener { selectTab(1) }
        tabHistory.setOnClickListener { selectTab(2) }

        selectTab(0)
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
    }
}
