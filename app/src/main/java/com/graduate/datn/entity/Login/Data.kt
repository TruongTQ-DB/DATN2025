package com.graduate.datn.entity.Login

import com.google.gson.annotations.SerializedName

data class Data(
    @field:SerializedName("accessToken")
    val accessToken: String? = null,
    @field:SerializedName("address")
    val address: String? = null,
    @field:SerializedName("apple_id")
    val apple_id: Any? = null,
    @field:SerializedName("avatar")
    val avatar: String? = null,
    @field:SerializedName("birthday")
    val birthday: String? = null,
    @field:SerializedName("check_user")
    val check_user: Any? = null,
    @field:SerializedName("created_at")
    val created_at: Any? = null,
    @field:SerializedName("deleted_at")
    val deleted_at: Any? = null,
    @field:SerializedName("email")
    val email: String? = null,
    @field:SerializedName("facebook_id")
    val facebook_id: Any? = null,
    @field:SerializedName("gender")
    val gender: Int? = null,
    @field:SerializedName("google_id")
    val google_id: Any? = null,
    @field:SerializedName("id")
    val id: Int? = null,
    @field:SerializedName("login_type")
    val login_type: Int? = null,
    @field:SerializedName("name")
    val name: String? = null,
    @field:SerializedName("phone")
    val phone: String? = null,
    @field:SerializedName("status")
    val status: Int? = null,
    @field:SerializedName("typeAccount")
    val typeAccount: Int? = null,
    @field:SerializedName("updated_at")
    val updated_at: String? = null,
    @field:SerializedName("username")
    val username: String? = null,
    @field:SerializedName("zalo_id")
    val zalo_id: Any? = null
)