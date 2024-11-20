package com.graduate.datn.entity.response

import com.google.firebase.Timestamp

data class BookingResponse(
    var userId: String? = null,

    var barberId: String? = null,

    var serviceId: String? = null,

    var optionalServiceId: String? = null,

    var serviceName: String? = null,

    var optionalServiceName: String? = null,

    var barberAvatar: String? = null,

    var barberName: String? = null,

    var detailNameDocter: String? = null,

    val barberPhone: String?= null,

    var barberShopName: String?= null,

    var barberShopAddress: String? = null,

    var avatarUser: String? = null,

    val nameUser: String?= null,


    var phoneUser: String?= null,

    var birthdayUser: String? = null,

    var date: String? = null,

    val timeFrom: String?= null,

    var timeTo: String?= null,

    var pay: Int ?= 0,

    var price: String ?= null,

    var status: Int?= 0, // 0 Mới, 1 Đã Cắt, 2 Huỷ

    var timeStamp: Timestamp?= null,

    var timeStampCurrent: Timestamp?= null,

    var reason: String ?= null,
)
