package com.graduate.datn.custom.dialog

import android.content.Context
import com.graduate.datn.R
import com.graduate.datn.base.custom.BaseFullScreenDialog
import com.graduate.datn.extension.onAvoidDoubleClick
import kotlinx.android.synthetic.main.layout_pick_camera_image.*

class PickImageDialog(context: Context) : BaseFullScreenDialog() {

    override fun initView() {
        dialog?.window?.setBackgroundDrawableResource(android.R.color.transparent)

        btn_camera.onAvoidDoubleClick {
            onclickCamera()
            dismiss()
        }

        btn_gallery.onAvoidDoubleClick {
            onclickGallery()
            dismiss()
        }

        container.setOnClickListener {
            dismissAllowingStateLoss()
        }
        tv_change_avatar.setOnClickListener {
        }
    }

    override fun initListener() {
    }

    override val layoutId: Int
        get() = R.layout.layout_pick_camera_image

    var onclickCamera : ()->Unit = {

    }

    var onclickGallery : ()->Unit = {

    }
}