package com.graduate.datn.base.entity

import com.google.gson.annotations.SerializedName

open class BaseResponse {
    //    @SerializedName("status") val status: Int? = null
    @SerializedName("message")
    val msg: String? = null
}