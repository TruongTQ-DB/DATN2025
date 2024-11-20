package com.graduate.datn.entity.request


import com.google.gson.annotations.SerializedName

data class RreateRequest(
    @SerializedName("approved_by")
    val approvedBy: String,
    @SerializedName("created_at")
    val createdAt: String,
    @SerializedName("from_date")
    val fromDate: String,
    @SerializedName("id_date_off")
    val idDateOff: Int,
    @SerializedName("reason")
    val reason: String,
    @SerializedName("status")
    val status: String,
    @SerializedName("time")
    val time: String,
    @SerializedName("to_date")
    val toDate: String,
    @SerializedName("type")
    val type: String,
    @SerializedName("updated_at")
    val updatedAt: String,
    @SerializedName("user_id")
    val userId: Int,
    @SerializedName("work_off")
    val workOff: String
)