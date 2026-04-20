package com.gbc.dormio_mobile_app.data.repository

import com.gbc.dormio_mobile_app.data.model.budget.BillDeleteResponse
import com.gbc.dormio_mobile_app.data.model.budget.BillItemResponse
import com.gbc.dormio_mobile_app.data.model.budget.BillListResponse
import com.gbc.dormio_mobile_app.data.model.budget.CreateBillRequest
import com.gbc.dormio_mobile_app.data.model.budget.CreateExpenseRequest
import com.gbc.dormio_mobile_app.data.model.budget.ExpenseItemResponse
import com.gbc.dormio_mobile_app.data.model.budget.ExpenseListResponse
import com.gbc.dormio_mobile_app.data.model.budget.MarkSharePaidResponse
import com.gbc.dormio_mobile_app.data.model.budget.SharesListResponse
import com.gbc.dormio_mobile_app.data.model.budget.SplitBillRequest
import com.gbc.dormio_mobile_app.data.model.budget.SplitBillResponse
import com.gbc.dormio_mobile_app.data.model.budget.UpdateBillRequest
import com.gbc.dormio_mobile_app.network.ApiServices.BillApiService
import com.gbc.dormio_mobile_app.network.ApiServices.ExpenseApiService
import com.gbc.dormio_mobile_app.utils.NetworkResult
import com.gbc.dormio_mobile_app.utils.safeApiCall
import javax.inject.Inject

class BillRepository @Inject constructor(
    private val billApiService: BillApiService,
    private val expenseApiService: ExpenseApiService
) {

    

    suspend fun createBill(request: CreateBillRequest): NetworkResult<BillItemResponse> {
        return safeApiCall { billApiService.createBill(request) }
    }

    suspend fun getMyBills(
        status: String? = null,
        page: Int? = null,
        limit: Int? = null
    ): NetworkResult<BillListResponse> {
        return safeApiCall { billApiService.getMyBills(status, page, limit) }
    }

    suspend fun getBillById(id: Int): NetworkResult<BillItemResponse> {
        return safeApiCall { billApiService.getBillById(id) }
    }

    suspend fun updateBill(id: Int, request: UpdateBillRequest): NetworkResult<BillItemResponse> {
        return safeApiCall { billApiService.updateBill(id, request) }
    }

    suspend fun deleteBill(id: Int): NetworkResult<BillDeleteResponse> {
        return safeApiCall { billApiService.deleteBill(id) }
    }


    suspend fun splitBill(billId: Int, request: SplitBillRequest): NetworkResult<SplitBillResponse> {
        return safeApiCall { billApiService.splitBill(billId, request) }
    }

    suspend fun getSharesForBill(billId: Int): NetworkResult<SharesListResponse> {
        return safeApiCall { billApiService.getSharesForBill(billId) }
    }

    suspend fun getMyShares(hasPaid: String? = null): NetworkResult<SharesListResponse> {
        return safeApiCall { billApiService.getMyShares(hasPaid) }
    }

    suspend fun markShareAsPaid(billId: Int, shareId: Int): NetworkResult<MarkSharePaidResponse> {
        return safeApiCall { billApiService.markShareAsPaid(billId, shareId) }
    }


    suspend fun createExpense(request: CreateExpenseRequest): NetworkResult<ExpenseItemResponse> {
        return safeApiCall { expenseApiService.createExpense(request) }
    }

    suspend fun getMyExpenses(
        category: String? = null,
        startDate: String? = null,
        endDate: String? = null
    ): NetworkResult<ExpenseListResponse> {
        return safeApiCall { expenseApiService.getMyExpenses(category, startDate, endDate) }
    }
}
