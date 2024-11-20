package com.graduate.datn.entity

import com.google.gson.annotations.SerializedName

class LoginRequest {
    @SerializedName("email_work")
    var username:String? = null
    @SerializedName("password")
    var password:String? = null
}