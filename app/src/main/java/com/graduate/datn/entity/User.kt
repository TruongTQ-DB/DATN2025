package com.graduate.datn.entity

import java.io.Serializable

data class User(
    var id: String? = null,

    var email: String? = null,

    var passWord: String? = null,

    var avatar: String? = "",

    var account: Int? = null,

    var name: String? = null,

    var detailname: String? = null,

    var birthday: String? = null,

    var gander: Int? = null,

    var phone: String? = null,

    var clinicShopAddressId: String? = null,

    var addressName: String? = null,

    var typeService: Int? = null,

    var typeServiceName: String? = null,

    var service: List<Service>?= null,

    var optionalService: List<OptionalService>?= null,

    var height: String ?= "",

    var weight: String ?= "",

    var blood_type: String ?= "",

    var citizen_identity: String ?= "",

    var insurance_id: String ?= "",
//    var serviceId: String? = null,
//
//    var serviceName: String? = null,

//    var optionalServiceId: String? = null,
//
//    var optionalServiceName: String? = null,

    var address: String? = null,

    var token: String? = null,
): Serializable

data class Service(
    var serviceId: String? = null,
    var serviceName: String? = null,
    var disable: Boolean ?= false
): Serializable

data class OptionalService(
    var serviceId: String? = null,
    var optionalServiceId: String? = null,
    var optionalServiceName: String? = null,
    var disable: Boolean ?= false
): Serializable