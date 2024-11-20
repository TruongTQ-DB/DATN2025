package com.graduate.datn.entity.request


import com.google.gson.annotations.SerializedName

data class Data(
    @SerializedName("approved_by")
    val approvedBy: Int,
    @SerializedName("created_at")
    val createdAt: String?,
    @SerializedName("email_work")
    val emailWork: String,
    @SerializedName("from_date")
    val fromDate: String,
    @SerializedName("id_date_off")
    val idDateOff: Int,
    @SerializedName("name")
    val name: String,
    @SerializedName("reason")
    val reason: String,
    @SerializedName("status")
    val status: Int,
    @SerializedName("time")
    val time: Int,
    @SerializedName("to_date")
    val toDate: String,
    @SerializedName("type")
    val type: Int,
    @SerializedName("updated_at")
    val updatedAt: String?,
    @SerializedName("user_id")
    val userId: Int,
    @SerializedName("why_reject")
    val whyReject: String?,
    @SerializedName("work_off")
    val workOff: Double
)