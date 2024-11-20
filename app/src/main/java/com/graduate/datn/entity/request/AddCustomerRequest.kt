package com.graduate.datn.entity.request

data class AddCustomerRequest(
    var id: String? = null,

    var email: String? = null,

    var passWord: String? = null,

    var avatar: String? = "",

    var isAccount: Int? = null,

    var name: String? = null,

    var birthday: String? = null,

    var gander: Int? = null,

    var phone: String? = null,

    var address: String? = null
    )
