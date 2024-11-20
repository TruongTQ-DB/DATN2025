package com.graduate.datn.entity.profile


import com.google.gson.annotations.SerializedName

data class ProfileReponse(
    @SerializedName("address")
    val address: String,
    @SerializedName("avatar")
    val avatar: String?= null,
    @SerializedName("date_of_birth")
    val dateOfBirth: String,
    @SerializedName("email_personal")
    val emailPersonal: String,
    @SerializedName("citizen_identity")
    val citizenIdentity: String,
    @SerializedName("insurance_id")
    val insuranceId: Int,
    @SerializedName("name")
    val name: String,
    @SerializedName("phone")
    val phone: String,
    @SerializedName("height")
    val height: Double,
    @SerializedName("weight")
    val weight: Double,
    @SerializedName("blood_type")
    val bloodType: String ,

    @SerializedName("_method")
    val _method: String ?= "PUT",
)