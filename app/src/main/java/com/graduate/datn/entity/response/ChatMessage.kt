package com.graduate.datn.entity.response

data class ChatMessage(
    var senderId: String? = null,

    var receiverId: String? = null,

    val message: String?= null,

    var dateTime: String?= null,
)
