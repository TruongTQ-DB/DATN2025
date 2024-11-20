package com.graduate.datn.entity.profile


import com.google.gson.annotations.SerializedName

data class UpdateProfile(
    @SerializedName("name")
    val name: String,
    @SerializedName("phone")
    val phone: String,
    @SerializedName("address")
    val address: String,
    @SerializedName("date_of_birth")
    val dateOfBirth: String,

)