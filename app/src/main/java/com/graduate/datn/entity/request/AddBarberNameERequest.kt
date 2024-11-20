package com.graduate.datn.entity.request

data class AddBarberNameERequest (
    var id: String? = null,

    var email: String? = null,

    var passWord: String? = null,

    var avatar: String? = "",

    var isAccount: Int? = null,

    var name: String? = null,

    var birthday: String? = null,

    var gander: Int? = null,

    var phone: String? = null,

    var barberShopAddressId: String? = null,

    var addressName: String? = null,

    var serviceId: String? = null,

    var serviceName: String? = null,

    var typeService: Int? = null,

    var typeServiceName: String? = null,

    var optionalServiceId: String? = null,

    var optionalServiceName: String? = null,

    var address: String? = null
)