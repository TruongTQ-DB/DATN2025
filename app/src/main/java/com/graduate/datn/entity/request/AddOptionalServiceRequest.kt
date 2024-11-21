package com.graduate.datn.entity.request

import com.google.gson.annotations.SerializedName

data class AddOptionalServiceRequest(
    @SerializedName("name")
    val name: String,

    @SerializedName("service_id")
    val serviceId: String,

    @SerializedName("barber_shop_address_id")
    val clinicShopAddressId: String ?= null,

    @SerializedName("address_name")
    val addressName: String,

    @SerializedName("service_name")
    val serviceName: String,

    @SerializedName("image")
    val image: String,

    @SerializedName("status")
    val status: Int,

    val price: String,
)
