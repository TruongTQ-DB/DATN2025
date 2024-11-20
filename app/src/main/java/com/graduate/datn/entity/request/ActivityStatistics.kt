package com.graduate.datn.entity.request

import com.google.firebase.Timestamp

data class ActivityStatistics(
    var userId: String? = null,

    val userName: String?= null,

    var date: String? = null,

    var timestamp: Timestamp?= null
)