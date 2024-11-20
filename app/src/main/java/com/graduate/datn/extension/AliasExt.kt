package com.graduate.datn.extension

import androidx.lifecycle.MutableLiveData
import com.graduate.datn.R
import com.graduate.datn.base.entity.BaseListLoadMoreResponse
import com.graduate.datn.base.entity.BaseListResponse
import com.graduate.datn.base.entity.BaseObjectResponse

typealias ObjectResponse<T> = MutableLiveData<BaseObjectResponse<T>>
typealias ListResponse<T> = MutableLiveData<BaseListResponse<T>>
typealias ListLoadMoreResponse<T> = MutableLiveData<BaseListLoadMoreResponse<T>>

typealias AndroidColors = android.R.color
typealias ProjectColors = R.color
