package com.graduate.datn.entity.profile


import com.google.gson.annotations.SerializedName

data class Profile(
    @SerializedName("address")
    val address: String,
    @SerializedName("avatar")
    val avatar: Any?,
    @SerializedName("citizen_identity")
    val citizenIdentity: String,
    @SerializedName("company_role_id")
    val companyRoleId: Int,
    @SerializedName("created_at")
    val createdAt: String,
    @SerializedName("date_of_birth")
    val dateOfBirth: String,
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
    @SerializedName("updated_at")
    val updatedAt: String,
    @SerializedName("height")
    val height: Double,
    @SerializedName("weight")
    val weight: Double,
    @SerializedName("blood_type")
    val bloodType: String,
    @SerializedName("wage_id")
    val wageId: Int
)