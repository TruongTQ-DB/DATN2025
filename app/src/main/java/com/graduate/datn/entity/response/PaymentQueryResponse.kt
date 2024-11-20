package com.graduate.datn.entity.response

import com.google.gson.annotations.SerializedName

data class PaymentQueryResponse(
    @field:SerializedName("vnp_TransactionStatus")
    val vnpTransactionStatus: String,

    @field:SerializedName("vnp_OrderInfo")
    val vnpOrderInfo: String
)