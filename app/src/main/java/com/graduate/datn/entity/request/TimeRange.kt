package com.graduate.datn.entity.request

import com.google.firebase.Timestamp
import java.io.Serializable

data class TimeRange(
    val startTime: String ?= null,
    val endTime: String ?= null,
    val stater: Int ?= null,
    var selector: Boolean? = false,
    var timeStamp: Timestamp? = null
) : Serializable
