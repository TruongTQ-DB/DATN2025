package com.graduate.datn.entity.dateWorkLog


import com.google.gson.annotations.SerializedName

data class DateWorkLog(
    @SerializedName("id")
    val id: Int,
    @SerializedName("user_id")
    val user_id: Int,
    @SerializedName("date_time")
    val date_time: String,
    @SerializedName("created_at")
    val created_at: String,
    @SerializedName("updated_at")
    val updated_at: String

)