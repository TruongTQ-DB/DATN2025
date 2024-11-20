package com.graduate.datn.entity.request


import com.google.gson.annotations.SerializedName

data class SendNotificationRequest(
    @SerializedName("notification")
    val notification: Notification,
    @SerializedName("to")
    val to: String
)