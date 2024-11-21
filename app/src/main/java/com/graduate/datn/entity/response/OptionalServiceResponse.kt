package com.graduate.datn.entity.response

import java.io.Serializable

data class OptionalServiceResponse(
    val serviceId: String ?= null,
    val serviceName: String ?= null,
    val clinicShopAddressId: String ?= null,
    val addressName: String ?= null,
    val name: String ?= null,
    val image: String ?= null,
    val status: Int ?= 0,
    val price: String ?= null,
): Serializable
