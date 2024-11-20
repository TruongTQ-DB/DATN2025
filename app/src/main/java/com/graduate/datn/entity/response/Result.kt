package com.graduate.datn.entity.response


import com.google.gson.annotations.SerializedName

data class Result(
    @SerializedName("message_id")
    val messageId: String
)