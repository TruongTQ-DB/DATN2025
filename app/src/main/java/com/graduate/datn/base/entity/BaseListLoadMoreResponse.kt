package com.graduate.datn.base.entity

import com.graduate.datn.utils.Define
import com.google.gson.annotations.SerializedName

class BaseListLoadMoreResponse<T> : BaseListResponse<T> {
    @SerializedName("current_page")
    var currentPage: Int = 0
    @SerializedName("total_page")
    var totalPage: Int = 0
    var isRefresh: Boolean = false
    var isLoadmore: Boolean = false

    constructor() {

    }

    constructor(
        type: Int,
        data: List<T>?,
        error: Throwable?,
        isRefresh: Boolean,
        isLoadmore: Boolean,
        isShowingError: Boolean = true
    ) : super(type, data, error) {
        this.isRefresh = isRefresh
        this.isLoadmore = isLoadmore
        this.isShowingError = isShowingError
    }

    override fun loading(): BaseListLoadMoreResponse<T>? {
        return BaseListLoadMoreResponse(
            Define.ResponseStatus.LOADING,
            null,
            null,
            isRefresh,
            isLoadmore
        )
    }

    fun success(
        data: List<T>?,
        isRefresh: Boolean,
        isLoadmore: Boolean
    ): BaseListLoadMoreResponse<T> {
        this.isLoadmore = isLoadmore
        this.isRefresh = isRefresh
        return BaseListLoadMoreResponse(
            Define.ResponseStatus.SUCCESS,
            data,
            null,
            isRefresh,
            isLoadmore
        )
    }

    override fun error(throwable: Throwable,isShowingError:Boolean): BaseListLoadMoreResponse<T> {
        return BaseListLoadMoreResponse(
            Define.ResponseStatus.ERROR,
            null,
            throwable,
            isRefresh,
            isLoadmore,
            isShowingError
        )
    }
}