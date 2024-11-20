package com.graduate.datn.entity.response

import java.io.Serializable


data class ListAddressResponse(
    var address: String? = null,

    var avata: String? = null,

    val name: String?= null,

    var status: Int?= null,

    var phone: String?= null,
): Serializable
