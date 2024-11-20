package com.graduate.datn.entity.calendar


import com.google.gson.annotations.SerializedName

data class Data(
    @SerializedName("address")
    val address: String,
    @SerializedName("avatar")
    val avatar: Any?,
    @SerializedName("citizen_identity")
    val citizenIdentity: String,
    @SerializedName("company_role_id")
    val companyRoleId: Int,
    @SerializedName("date_of_birth")
    val dateOfBirth: String,
    @SerializedName("date_time")
    val dateTime: String,
    @SerializedName("email_personal")
    val emailPersonal: String,
    @SerializedName("email_work")
    val emailWork: String,
    @SerializedName("id_user")
    val idUser: Int,
    @SerializedName("insurance_id")
    val insuranceId: Int,
    @SerializedName("name")
    val name: String,
    @SerializedName("phone")
    val phone: String,
    @SerializedName("wage_id")
    val wageId: Int
)