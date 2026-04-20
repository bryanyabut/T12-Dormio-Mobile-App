package com.gbc.dormio_mobile_app.viewmodel.budget

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gbc.dormio_mobile_app.data.model.budget.BudgetUiState
import com.gbc.dormio_mobile_app.data.model.budget.CreateBillRequest
import com.gbc.dormio_mobile_app.data.model.budget.CreateExpenseRequest
import com.gbc.dormio_mobile_app.data.model.budget.ShareItem
import com.gbc.dormio_mobile_app.data.model.budget.SplitBillRequest
import com.gbc.dormio_mobile_app.data.repository.BillRepository
import com.gbc.dormio_mobile_app.data.repository.ChoresRepository
import com.gbc.dormio_mobile_app.utils.NetworkResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BudgetViewModel @Inject constructor(
    private val billRepository: BillRepository,
    private val choresRepository: ChoresRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(BudgetUiState())
    val uiState: StateFlow<BudgetUiState> = _uiState.asStateFlow()

    init {
        loadBills()
        loadMyShares()
        loadExpenses()
        loadHousemates()
    }

    fun loadBills() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            when (val result = billRepository.getMyBills()) {
                is NetworkResult.Success -> {
                    _uiState.update { it.copy(
                        isLoading = false,
                        myBills = result.data.bills,
                        errorMessage = null
                    )}
                }
                is NetworkResult.Error -> {
                    _uiState.update { it.copy(
                        isLoading = false,
                        errorMessage = result.apiError.message
                    )}
                }
                else -> {}
            }
        }
    }

    fun loadMyShares() {
        viewModelScope.launch {
            when (val result = billRepository.getMyShares()) {
                is NetworkResult.Success -> {
                    _uiState.update { it.copy(
                        myShares = result.data.shares,
                        errorMessage = null
                    )}
                }
                is NetworkResult.Error -> {
                    _uiState.update { it.copy(
                        errorMessage = result.apiError.message
                    )}
                }
                else -> {}
            }
        }
    }

    fun loadExpenses(category: String? = null) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            when (val result = billRepository.getMyExpenses(category)) {
                is NetworkResult.Success -> {
                    _uiState.update { it.copy(
                        isLoading = false,
                        expenses = result.data.expenses,
                        errorMessage = null
                    )}
                }
                is NetworkResult.Error -> {
                    _uiState.update { it.copy(
                        isLoading = false,
                        errorMessage = result.apiError.message
                    )}
                }
                else -> {}
            }
        }
    }

    fun loadHousemates() {
        viewModelScope.launch {
            when (val result = choresRepository.getHousemates()) {
                is NetworkResult.Success -> {
                    val housemates = result.data.map { housemate ->
                        com.gbc.dormio_mobile_app.data.model.budget.BillShareUserDto(
                            id = housemate.id,
                            firstName = housemate.firstName,
                            lastName = housemate.lastName,
                            email = ""
                        )
                    }
                    _uiState.update { it.copy(
                        housemates = housemates,
                        errorMessage = null
                    )}
                }
                is NetworkResult.Error -> {
                    _uiState.update { it.copy(
                        errorMessage = result.apiError.message
                    )}
                }
                else -> {}
            }
        }
    }

    fun createBillAndOptionalSplit(
        billName: String,
        totalAmount: String,
        dueDate: String,
        category: String?,
        splitWithHousemates: Boolean,
        shares: List<ShareItem> = emptyList()
    ) {
        viewModelScope.launch {
            _uiState.update { it.copy(actionLoading = true, errorMessage = null) }

            val createRequest = CreateBillRequest(
                billName = billName,
                totalAmount = totalAmount,
                dueDate = dueDate,
                category = category
            )

            when (val result = billRepository.createBill(createRequest)) {
                is NetworkResult.Success -> {
                    val bill = result.data.bill
                    if (splitWithHousemates && shares.isNotEmpty()) {
                        splitBillAfterCreate(bill.id, shares)
                    } else {
                        _uiState.update { it.copy(
                            actionLoading = false,
                            successMessage = "Bill created successfully!"
                        )}
                        refreshAll()
                    }
                }
                is NetworkResult.Error -> {
                    _uiState.update { it.copy(
                        actionLoading = false,
                        errorMessage = result.apiError.message
                    )}
                }
                else -> {}
            }
        }
    }

    private suspend fun splitBillAfterCreate(billId: Int, shares: List<ShareItem>) {
        val splitRequest = SplitBillRequest(shares = shares)
        when (val result = billRepository.splitBill(billId, splitRequest)) {
            is NetworkResult.Success -> {
                _uiState.update { it.copy(
                    actionLoading = false,
                    successMessage = "Bill created and split successfully!"
                )}
                refreshAll()
            }
            is NetworkResult.Error -> {
                _uiState.update { it.copy(
                    actionLoading = false,
                    errorMessage = "Bill created but split failed: ${result.apiError.message}"
                )}
                refreshAll()
            }
            else -> {}
        }
    }

    // Expense standalone, no bill

    fun createExpense(
        description: String,
        amount: String,
        category: String?,
        expenseDate: String
    ) {
        viewModelScope.launch {
            _uiState.update { it.copy(actionLoading = true, errorMessage = null) }

            val request = CreateExpenseRequest(
                description = description,
                amount = amount,
                category = category?.uppercase(),
                expenseDate = expenseDate
            )

            when (val result = billRepository.createExpense(request)) {
                is NetworkResult.Success -> {
                    _uiState.update { it.copy(
                        actionLoading = false,
                        successMessage = "Expense added successfully!"
                    )}
                    loadExpenses()
                }
                is NetworkResult.Error -> {
                    _uiState.update { it.copy(
                        actionLoading = false,
                        errorMessage = result.apiError.message
                    )}
                }
                else -> {}
            }
        }
    }

    fun markShareAsPaid(billId: Int, shareId: Int) {
        viewModelScope.launch {
            _uiState.update { it.copy(actionLoading = true, errorMessage = null) }

            when (val result = billRepository.markShareAsPaid(billId, shareId)) {
                is NetworkResult.Success -> {
                    _uiState.update { it.copy(
                        actionLoading = false,
                        successMessage = "Share marked as paid!"
                    )}
                    refreshAll()
                }
                is NetworkResult.Error -> {
                    _uiState.update { it.copy(
                        actionLoading = false,
                        errorMessage = result.apiError.message
                    )}
                }
                else -> {}
            }
        }
    }

    fun deleteBill(billId: Int) {
        viewModelScope.launch {
            _uiState.update { it.copy(actionLoading = true, errorMessage = null) }

            when (val result = billRepository.deleteBill(billId)) {
                is NetworkResult.Success -> {
                    _uiState.update { it.copy(
                        actionLoading = false,
                        successMessage = "Bill deleted!"
                    )}
                    refreshAll()
                }
                is NetworkResult.Error -> {
                    _uiState.update { it.copy(
                        actionLoading = false,
                        errorMessage = result.apiError.message
                    )}
                }
                else -> {}
            }
        }
    }

    fun refreshAll() {
        loadBills()
        loadMyShares()
        loadExpenses()
    }

    fun clearMessages() {
        _uiState.update { it.copy(successMessage = null, errorMessage = null) }
    }

    fun updateShareAmount(userId: Int, amount: String) {
        _uiState.update { state ->
            val newSelectedShares = if (amount.isEmpty()) {
                state.selectedShares - userId
            } else {
                state.selectedShares + (userId to amount)
            }
            state.copy(selectedShares = newSelectedShares)
        }
    }

    fun clearSelectedShares() {
        _uiState.update { it.copy(selectedShares = emptyMap()) }
    }

    fun getSelectedShareItems(): List<ShareItem> {
        return _uiState.value.selectedShares.map { (userId, amount) ->
            ShareItem(userId = userId, shareAmount = amount)
        }
    }
}
