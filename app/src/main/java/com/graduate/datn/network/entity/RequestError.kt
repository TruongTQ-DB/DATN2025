package com.graduate.datn.network.entity

import com.graduate.datn.utils.Define
import com.google.gson.annotations.SerializedName

class RequestError {
    @SerializedName(Define.Api.ERROR_CODE)
    var errorCode: String? = null
    @SerializedName(Define.Api.ERROR_MESSAGE)
    var errorMessage: String? = null

}