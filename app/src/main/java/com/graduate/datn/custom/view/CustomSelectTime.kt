package com.graduate.datn.custom.view

import android.content.Context
import android.content.res.TypedArray
import android.util.AttributeSet
import androidx.core.view.isVisible
import com.graduate.datn.R
import com.graduate.datn.adapter.recyclerview.CustomTimeAdapter
import com.graduate.datn.extension.gone
import com.graduate.datn.extension.visible
import kotlinx.android.synthetic.main.view_custom_request_type.view.*

class CustomSelectTime (context: Context, attrs: AttributeSet?) :
    CustomViewConstraintLayout(context, attrs) {

    private var requestTypeAdapter: CustomTimeAdapter? = null
    override fun getLayoutRes(): Int {
        return R.layout.view_custom_request_type
    }

    override fun getStyableRes(): IntArray? {
        return null
    }

    override fun initView() {
        requestTypeAdapter = CustomTimeAdapter(context)
        rc_select_request.adapter = requestTypeAdapter
        requestTypeAdapter?.refresh(RequestType.values().toList())
    }

    override fun initListener() {
        requestTypeAdapter?.onClickItem = {
            tv_request_type.text = it.title
            rc_select_request.gone()
        }
        imv_select.setOnClickListener {
            showOrHide()
        }
    }

    private fun showOrHide() {
        if (rc_select_request.isVisible) {
            imv_select.rotation = 0f
            rc_select_request.gone()
        } else {
            imv_select.rotation = -90f
            rc_select_request.visible()
        }
    }

    override fun initData() {

    }

    override fun initDataFromStyable(mTypedArray: TypedArray?) {

    }

    enum class RequestType(val title: String) {
        morning("Buổi sáng"), afternoon("Buổi chiều"), allDay("Cả ngày")
    }

    fun getRequestType() = tv_request_type.text.toString()

    fun setRequestType(requestType: String) {
        tv_request_type.text = requestType
        val requestTypePosition = RequestType.values().indexOfFirst { it.title == requestType }
        if (requestTypePosition != -1) {
            requestTypeAdapter?.setSelectedItem(requestTypePosition, true)
        }
    }

}