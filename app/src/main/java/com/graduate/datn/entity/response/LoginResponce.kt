package com.graduate.datn.entity.response


import com.google.gson.annotations.SerializedName

data class LoginResponce(
    @SerializedName("expires_in")
    val expiresIn: Long ?= null,
    @SerializedName("token")
    val token: String ?= null,
    @SerializedName("token_type")
    val tokenType: String ?= null,
    @SerializedName("user")
    val user: User ?= null
)