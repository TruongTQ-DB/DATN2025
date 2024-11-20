package com.graduate.datn.base.entity

import com.graduate.datn.utils.Define
import com.google.gson.annotations.SerializedName

open class BaseListResponse<T>(
    val type: Int = 0,
    @SerializedName("data")
    val data: List<T>? = null,
    val error: Throwable? = null,
    var isShowingError:Boolean = true
) : BaseResponse() {
    open fun loading(): BaseListResponse<T>? {
        return BaseListResponse(Define.ResponseStatus.LOADING,null,null)
    }

    open fun success(data: List<T>?): BaseListResponse<T> {
        return BaseListResponse(Define.ResponseStatus.SUCCESS,data,null)
    }

    open fun error(throwable: Throwable,isShowingError:Boolean= true): BaseListResponse<T> {
        return BaseListResponse(Define.ResponseStatus.ERROR,null,throwable,isShowingError)
    }
}