package com.graduate.datn.entity.request

import com.google.firebase.Timestamp

data class TheNewRequest(
    val content: String ? =null,

    val title: String ? =null,

    val image: String ? =null,

    val status: Int ? =null,

    val date: String?= null,

    val timestamp: Timestamp?= null
)
