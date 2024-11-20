package com.graduate.datn.base.custom

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.RelativeLayout
import androidx.fragment.app.DialogFragment
import io.reactivex.disposables.CompositeDisposable

abstract class BaseFullScreenDialog : DialogFragment() {
    var disposable = CompositeDisposable()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(layoutId, container, false)
        createFullScreenDialogFragment()
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        initListener()
    }


    private fun createFullScreenDialogFragment() {
        val root = RelativeLayout(activity)
        root.layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
        dialog?.apply {
            requestWindowFeature(Window.FEATURE_NO_TITLE)
            setContentView(root)
            window?.setBackgroundDrawable(ColorDrawable(colorBackground))
            window?.setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
        }
    }

    abstract fun initView()
    abstract fun initListener()
    abstract val layoutId: Int
    open val colorBackground = Color.parseColor("#57000000")

    override fun onDestroy() {
        super.onDestroy()
        disposable.clear()
    }
}