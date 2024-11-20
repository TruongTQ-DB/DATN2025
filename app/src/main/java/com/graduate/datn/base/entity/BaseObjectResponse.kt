package com.graduate.datn.base.entity

import com.graduate.datn.utils.Define
import com.google.gson.annotations.SerializedName

class BaseObjectResponse<T>(
    val type: Int = 0,
    @SerializedName("data")
    val data: T? = null,
    val error: Throwable? = null,
    var isShowingError:Boolean = true
) : BaseResponse() {
    fun loading(): BaseObjectResponse<T>? {
        return BaseObjectResponse(Define.ResponseStatus.LOADING,null,null)
    }

    fun success(data: T): BaseObjectResponse<T> {
        return BaseObjectResponse(Define.ResponseStatus.SUCCESS,data,null)
    }

    fun error(throwable: Throwable,isShowingError:Boolean = true): BaseObjectResponse<T> {
        return BaseObjectResponse(Define.ResponseStatus.ERROR,null,throwable,isShowingError)
    }
}