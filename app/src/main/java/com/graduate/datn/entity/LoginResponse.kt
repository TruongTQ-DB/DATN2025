package com.graduate.datn.entity

import com.google.gson.annotations.SerializedName

data class LoginResponse(

	@field:SerializedName("expireIn")
	val expireIn: Int? = null,

	@field:SerializedName("id")
	val id: String? = null,

	@field:SerializedName("accessToken")
	val accessToken: String? = null,

	@field:SerializedName("refreshToken")
	val refreshToken: String? = null
)