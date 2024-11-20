package com.graduate.datn.entity.response

import com.google.firebase.Timestamp

data class NotificationResponse(
    var userId: String? = null,

    var barberId: String? = null,

    var bookingId: String? = null,

    var title: String? = null,

    var message: String? = null,

    var barberName: String? = null,

    val nameUser: String?= null,

    var date: String? = null,

    val timeFrom: String?= null,

    var timeTo: String?= null,

    var reportedStatus: Int ?= 0, //trạng thái đã gửi thông báo (chưa gửi)

    var status: Int?= 0, // trang thái thông báo(chưa đọc)

    var typeNotification: Int ?= 0, // Loại thông báo nào (đăng ký lịch)

    var typeCanSendUserAndBarber: Int ?= 0, //type có thể gửi cho User

    var timestamp: Timestamp?= null
)
