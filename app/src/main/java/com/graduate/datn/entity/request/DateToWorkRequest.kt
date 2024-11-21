package com.graduate.datn.entity.request

import com.google.firebase.Timestamp
import java.io.Serializable

data class DateToWorkRequest(
    val idDocterName: String?= null,
    val avatar: String ?= null,
    val name: String ?= null,
    val date: String?= null,
    val timeRanges: List<TimeRange>?= null,
    val dateTimestamp: Timestamp?= null,
    val approve: Boolean ?= true,
) : Serializable
