package com.graduate.datn.entity.request


data class AddServiceRequest(
    val clinicShopAddressId: String,

    val addressName: String,

    val name: String,

    val image: String,

    val status: Int,
)
