package com.gbc.dormio_mobile_app.data.model.budget

import com.google.gson.annotations.SerializedName

// Enums Some expense category are not used

enum class ExpenseCategory {
    FOOD, UTILITIES, ENTERTAINMENT, SUPPLIES, TRANSPORTATION, EDUCATION, HEALTHCARE, OTHER
}

enum class BillStatus {
    UNPAID, PARTIALLY_PAID, PAID
}

data class BillShareUserDto(
    val id: Int,
    val firstName: String,
    val lastName: String,
    val email: String
)

data class BillSharingDto(
    val id: Int,
    val billId: Int,
    val userId: Int,
    val shareAmount: String,
    val hasPaid: Boolean,
    val paidAt: String?,
    val user: BillShareUserDto? = null,
    val bill: BillDto? = null
)

data class BillDto(
    val id: Int,
    val userId: Int,
    val billName: String,
    val totalAmount: String,
    val dueDate: String,
    val category: String?,
    val status: String,
    val createdAt: String?,
    val updatedAt: String?,
    val billSharing: List<BillSharingDto>? = null,
    val user: BillShareUserDto? = null
)

data class ExpenseDto(
    val id: Int,
    val userId: Int,
    val description: String,
    val amount: String,
    val category: String?,
    val expenseDate: String,
    val createdAt: String?
)

// Server response wrappers

data class BillListResponse(
    val bills: List<BillDto>,
    val page: Int,
    val limit: Int,
    val total: Int,
    val totalPages: Int
)

data class BillItemResponse(
    val message: String?,
    val bill: BillDto
)

data class BillDeleteResponse(
    val message: String?
)

data class SplitBillResponse(
    val message: String?,
    val billShares: List<BillSharingDto>
)

data class SharesListResponse(
    val shares: List<BillSharingDto>
)

data class MarkSharePaidResponse(
    val message: String?,
    val share: BillSharingDto
)

data class ExpenseListResponse(
    val expenses: List<ExpenseDto>
)

data class ExpenseItemResponse(
    val message: String?,
    val expense: ExpenseDto
)

data class ExpenseDeleteResponse(
    val message: String?
)

//Request bodies

data class CreateBillRequest(
    val billName: String,
    val totalAmount: String,
    val dueDate: String,
    val category: String? = null
)

data class UpdateBillRequest(
    val billName: String? = null,
    val totalAmount: String? = null,
    val dueDate: String? = null,
    val category: String? = null,
    val status: String? = null
)

data class SplitBillRequest(
    val shares: List<ShareItem>
)

data class ShareItem(
    val userId: Int,
    val shareAmount: String
)

data class CreateExpenseRequest(
    val description: String,
    val amount: String,
    val category: String? = null,
    val expenseDate: String
)

data class BudgetUiState(
    val isLoading: Boolean = false,
    val actionLoading: Boolean = false,
    val errorMessage: String? = null,
    val successMessage: String? = null,
    val myBills: List<BillDto> = emptyList(),
    val myShares: List<BillSharingDto> = emptyList(),
    val expenses: List<ExpenseDto> = emptyList(),
    val housemates: List<BillShareUserDto> = emptyList(),
    val selectedShares: Map<Int, String> = emptyMap()
)
