package com.graduate.datn.custom.view

import android.content.Context
import android.content.res.TypedArray
import android.util.AttributeSet
import androidx.core.content.ContextCompat
import com.graduate.datn.R
import com.graduate.datn.extension.onAvoidDoubleClick
import kotlinx.android.synthetic.main.custom_tab_3_layout_view.view.*

class Custom3TabLayoutView(context: Context, attrs: AttributeSet?) :
    CustomViewConstraintLayout(context, attrs) {

    var onClickLeft: () -> Unit = {}
    var onClickMiddle: () -> Unit = {}
    var onClickRight: () -> Unit = {}


    override fun getLayoutRes(): Int {
        return R.layout.custom_tab_3_layout_view
    }

    override fun getStyableRes(): IntArray? {
        return null
    }

    override fun initView() {


    }

    override fun initListener() {

        cl_left.onAvoidDoubleClick {
            selectTab(TYPE_RIGHT)
            onClickLeft()
        }

        cl_middle.onAvoidDoubleClick {
            selectTab(TYPE_MIDDLE)
            onClickMiddle()
        }

        cl_right.onAvoidDoubleClick {
            selectTab(TYPE_LEFT)
            onClickRight()
        }
    }

    override fun initData() {

    }

    override fun initDataFromStyable(mTypedArray: TypedArray?) {

    }

    fun selectTab(position: Int) {
        tv_left.setTextColor(ContextCompat.getColor(context, R.color.color_text_pick_camera))
        line_offline.setBackgroundColor(ContextCompat.getColor(context, R.color.color_bg_container))

        tv_middle.setTextColor(ContextCompat.getColor(context, R.color.color_text_pick_camera))
        line_middle.setBackgroundColor(ContextCompat.getColor(context, R.color.color_bg_container))

        tv_right.setTextColor(ContextCompat.getColor(context, R.color.color_text_pick_camera))
        line_online.setBackgroundColor(ContextCompat.getColor(context, R.color.color_bg_container))

        when (position) {
            TYPE_RIGHT -> {
                tv_left.setTextColor(ContextCompat.getColor(context, R.color.color_selected_menu))
                line_offline.setBackgroundColor(ContextCompat.getColor(context, R.color.color_selected_menu))
            }
            TYPE_MIDDLE -> {
                tv_middle.setTextColor(ContextCompat.getColor(context, R.color.color_selected_menu))
                line_middle.setBackgroundColor(ContextCompat.getColor(context, R.color.color_selected_menu))
            }
            TYPE_LEFT -> {
                tv_right.setTextColor(ContextCompat.getColor(context, R.color.color_selected_menu))
                line_online.setBackgroundColor(ContextCompat.getColor(context, R.color.color_selected_menu))
            }
        }
    }

    fun selectTabOffline() {
        tv_left.setTextColor(ContextCompat.getColor(context, R.color.color_text_pick_camera))
        line_online.setBackgroundColor(ContextCompat.getColor(context, R.color.color_bg_container))
        tv_right.setTextColor(ContextCompat.getColor(context, R.color.color_selected_menu))
        line_offline.setBackgroundColor(ContextCompat.getColor(context, R.color.color_selected_menu))
    }

    private companion object {
        const val TYPE_LEFT = 1
        const val TYPE_MIDDLE = 2
        const val TYPE_RIGHT = 3
    }
}