package com.graduate.datn.custom.view

import android.content.Context
import android.util.AttributeSet
import android.widget.RelativeLayout
import android.os.Build
import android.os.Build.VERSION.SDK_INT
import android.os.Build.VERSION_CODES.KITKAT
import android.util.Log

import android.view.WindowInsets

class FixedRelativeLayout @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : RelativeLayout(context, attrs, defStyleAttr){
    private val mInsets = IntArray(4)

    override fun onApplyWindowInsets(insets: WindowInsets): WindowInsets {
        Log.d("TAG", "$insets $SDK_INT $KITKAT")
        Log.d("TAG", "$insets ${Build.VERSION.SDK_INT} ${Build.VERSION_CODES.KITKAT}")
        return if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT) {
            mInsets[0] = insets.systemWindowInsetLeft
            mInsets[1] = insets.systemWindowInsetTop
            mInsets[2] = insets.systemWindowInsetRight
            super.onApplyWindowInsets(
                insets.replaceSystemWindowInsets(
                    0,
                    0,
                    0,
                    insets.systemWindowInsetBottom
                )
            )
        } else {
            insets
        }
    }
}