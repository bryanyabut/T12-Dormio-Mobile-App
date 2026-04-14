package com.gbc.dormio_mobile_app.network.ApiServices

import com.gbc.dormio_mobile_app.data.model.budget.BillDeleteResponse
import com.gbc.dormio_mobile_app.data.model.budget.BillItemResponse
import com.gbc.dormio_mobile_app.data.model.budget.BillListResponse
import com.gbc.dormio_mobile_app.data.model.budget.CreateBillRequest
import com.gbc.dormio_mobile_app.data.model.budget.MarkSharePaidResponse
import com.gbc.dormio_mobile_app.data.model.budget.SharesListResponse
import com.gbc.dormio_mobile_app.data.model.budget.SplitBillRequest
import com.gbc.dormio_mobile_app.data.model.budget.SplitBillResponse
import com.gbc.dormio_mobile_app.data.model.budget.UpdateBillRequest
import com.gbc.dormio_mobile_app.utils.Constants
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface BillApiService {

    @POST(Constants.API_BILLS)
    suspend fun createBill(
        @Body request: CreateBillRequest
    ): Response<BillItemResponse>

    @GET(Constants.API_BILLS)
    suspend fun getMyBills(
        @Query("status") status: String? = null,
        @Query("page") page: Int? = null,
        @Query("limit") limit: Int? = null
    ): Response<BillListResponse>

    @GET(Constants.API_BILL_MY_SHARES)
    suspend fun getMyShares(
        @Query("hasPaid") hasPaid: String? = null
    ): Response<SharesListResponse>

    @GET(Constants.API_BILL_DETAIL)
    suspend fun getBillById(
        @Path("id") id: Int
    ): Response<BillItemResponse>

    @PUT(Constants.API_BILL_DETAIL)
    suspend fun updateBill(
        @Path("id") id: Int,
        @Body request: UpdateBillRequest
    ): Response<BillItemResponse>

    @DELETE(Constants.API_BILL_DETAIL)
    suspend fun deleteBill(
        @Path("id") id: Int
    ): Response<BillDeleteResponse>

    @POST(Constants.API_BILL_SPLIT)
    suspend fun splitBill(
        @Path("id") id: Int,
        @Body request: SplitBillRequest
    ): Response<SplitBillResponse>

    @GET(Constants.API_BILL_SHARES)
    suspend fun getSharesForBill(
        @Path("id") id: Int
    ): Response<SharesListResponse>

    @PATCH(Constants.API_BILL_SHARE_PAY)
    suspend fun markShareAsPaid(
        @Path("id") billId: Int,
        @Path("shareId") shareId: Int
    ): Response<MarkSharePaidResponse>
}
