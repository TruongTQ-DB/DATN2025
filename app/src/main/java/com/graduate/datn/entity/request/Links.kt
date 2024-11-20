package com.graduate.datn.entity.request


import com.google.gson.annotations.SerializedName

data class Links(
    @SerializedName("first")
    val first: String,
    @SerializedName("last")
    val last: String,
    @SerializedName("next")
    val next: Any?,
    @SerializedName("prev")
    val prev: Any?
)