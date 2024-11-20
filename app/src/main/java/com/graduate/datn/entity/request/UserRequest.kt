package com.graduate.datn.entity.request


import com.google.gson.annotations.SerializedName

data class UserRequest(
    @SerializedName("data")
    val `data`: List<Data>,
    @SerializedName("links")
    val links: Links,
    @SerializedName("meta")
    val meta: Meta
)