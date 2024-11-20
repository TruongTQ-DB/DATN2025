package com.graduate.datn.custom.view

import android.content.Context
import android.content.res.TypedArray
import android.util.AttributeSet
import androidx.core.view.isVisible
import com.graduate.datn.R
import com.graduate.datn.adapter.recyclerview.TypeServiceAdapter
import com.graduate.datn.extension.gone
import com.graduate.datn.extension.onAvoidDoubleClick
import com.graduate.datn.extension.textChangedListener
import com.graduate.datn.extension.visible
import kotlinx.android.synthetic.main.view_type_service_group.view.*

class TypeServiceView(context: Context, attrs: AttributeSet?) :
    CustomViewRelativeLayout(context, attrs) {
    override fun getLayoutRes(): Int {
        return R.layout.view_type_service_group
    }

    private var bloodGroupAdapter: TypeServiceAdapter? = null

    override fun getStyableRes(): IntArray? = null

    override fun initView() {
        bloodGroupAdapter = TypeServiceAdapter(context)
        rcv_type_service.adapter = bloodGroupAdapter
        bloodGroupAdapter?.refresh(TypeServiceGroup.values().toList())
        bloodGroupAdapter?.onClickItem = {
            tv_type_service_select.setText(it.title)
            rcv_type_service.gone()
            iv_select.rotation = 0f
            onClick(it.title)
        }
    }

    override fun initListener() {
        ct_blood_select.onAvoidDoubleClick {
            onHideVisible()
        }
    }


    private fun onHideVisible() {
        if (rcv_type_service.isVisible) {
            iv_select.rotation = 0f
            rcv_type_service.gone()
        } else {
            iv_select.rotation = 90f
            rcv_type_service.visible()
        }
    }

    override fun initData() {

    }

    override fun initDataFromStyable(mTypedArray: TypedArray?) {

    }

    enum class TypeServiceGroup(val title: String) {
        Cutting("Cắt tóc"), HeadWashing("Gội đầu")
    }

    fun textChangedListener(action: (text: String) -> Unit) {
        tv_type_service_select.textChangedListener {
            after {
                action(it.toString().trim())
            }
        }
    }

    fun getTypeService() = tv_type_service_select.text.toString()

    fun setTypeService(typeService : String) {
        tv_type_service_select.setText(typeService)
        val bloodPosition = TypeServiceGroup.values().indexOfFirst { it.title == typeService }
        if(bloodPosition != -1){
            bloodGroupAdapter?.setSelectedItem(bloodPosition,true)
        }
        if (typeService == ""){
            bloodGroupAdapter?.setSelectedMode(false)
        }
    }

    var onClick: (String) -> Unit = {}
}