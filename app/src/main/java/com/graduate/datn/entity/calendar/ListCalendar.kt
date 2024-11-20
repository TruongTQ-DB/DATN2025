package com.graduate.datn.entity.calendar


import com.google.gson.annotations.SerializedName

data class ListCalendar(
    @SerializedName("data")
    val `data`: List<Data>,
    @SerializedName("links")
    val links: Links,
    @SerializedName("meta")
    val meta: Meta
)