package com.graduate.datn.entity.request

import com.google.gson.annotations.SerializedName

data class AddressRequest(
    @SerializedName("name")
    val name: String,

    @SerializedName("address")
    val address: String,

    @SerializedName("avata")
    val avata: String,

    @SerializedName("phone")
    val phone: String,

    @SerializedName("status")
    val status: Int,
)
