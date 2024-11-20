package com.graduate.datn.custom

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import com.graduate.datn.R
import kotlinx.android.synthetic.main.my_custom_view.view.*

class MyCustomView: ConstraintLayout {
    var onBackClick: () -> Unit = {}
    init {
        LayoutInflater.from(context).inflate(R.layout.my_custom_view, this, true)
    }

    constructor(context: Context) : super(context) {
        initListener()
    }

    constructor(context: Context, attrs: AttributeSet) : super(
        context,
        attrs
    ){
        initListener()
    }
    fun setTitle(title: String) {
        tv_title.text = title
    }

    private fun initListener() {
        imv_back.setOnClickListener {
            onBackClick()
        }
    }




}