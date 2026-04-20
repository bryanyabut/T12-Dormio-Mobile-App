package com.gbc.dormio_mobile_app.network.ApiServices

import com.gbc.dormio_mobile_app.data.model.budget.CreateExpenseRequest
import com.gbc.dormio_mobile_app.data.model.budget.ExpenseDeleteResponse
import com.gbc.dormio_mobile_app.data.model.budget.ExpenseItemResponse
import com.gbc.dormio_mobile_app.data.model.budget.ExpenseListResponse
import com.gbc.dormio_mobile_app.utils.Constants
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface ExpenseApiService {

    @POST(Constants.API_EXPENSES)
    suspend fun createExpense(
        @Body request: CreateExpenseRequest
    ): Response<ExpenseItemResponse>

    @GET(Constants.API_EXPENSES)
    suspend fun getMyExpenses(
        @Query("category") category: String? = null,
        @Query("startDate") startDate: String? = null,
        @Query("endDate") endDate: String? = null
    ): Response<ExpenseListResponse>

    @GET(Constants.API_EXPENSE_DETAIL)
    suspend fun getExpenseById(
        @Path("id") id: Int
    ): Response<ExpenseItemResponse>

    @DELETE(Constants.API_EXPENSE_DETAIL)
    suspend fun deleteExpense(
        @Path("id") id: Int
    ): Response<ExpenseDeleteResponse>
}
