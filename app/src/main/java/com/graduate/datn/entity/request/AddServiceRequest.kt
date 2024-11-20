package com.graduate.datn.entity.request


data class AddServiceRequest(
    val barberShopAddressId: String,

    val addressName: String,

    val name: String,

    val image: String,

    val status: Int,
)
