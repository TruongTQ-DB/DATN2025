package com.graduate.datn.entity.response

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class ServiceResponse(
    @SerializedName("id")
    val id: String ?= null,
    @SerializedName("barber_shop_address_id")
    val barberShopAddressId: String ?= null,
    @SerializedName("name")
    val name: String ?= null,
    @SerializedName("image")
    val image: String ?= null,
    @SerializedName("status")
    val status: Int?= 0,
    val addressName: String?= null,
    var isSelected : Boolean = false
): Serializable
