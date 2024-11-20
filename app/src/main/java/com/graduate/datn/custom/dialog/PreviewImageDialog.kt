package com.graduate.datn.custom.dialog

import android.graphics.Bitmap
import android.os.Bundle
import com.graduate.datn.R
import com.graduate.datn.base.custom.BaseFullScreenDialog
import com.graduate.datn.extension.*
import kotlinx.android.synthetic.main.dialog_preview_image.*

class PreviewImageDialog : BaseFullScreenDialog() {
    override fun initView() {
        arguments?.let {
            if(it.containsKey(KEY_IMAGE_PREVIEW)){
                image_preview.loadImageUrlNoPlaceHolder(it.getString(KEY_IMAGE_PREVIEW))
            }

            if(it.containsKey(KEY_PREVIEW)){
                if(it.getBoolean(KEY_PREVIEW)){
                    btn_upload.gone()
                    btn_recap.gone()
                }
            }
        }
    }

    override fun initListener() {
        btn_recap.onAvoidDoubleClick {
            onRecap()
        }

        btn_upload.onAvoidDoubleClick {
            onUpload()
        }
        btn_close.onAvoidDoubleClick {
            dismissAllowingStateLoss()
        }
    }

    override val layoutId: Int
        get() = R.layout.dialog_preview_image

    companion object{
        fun newInstance(path : String,isOnlyPreview : Boolean) : PreviewImageDialog{
            return PreviewImageDialog().apply {
                arguments = Bundle().apply {
                    putString(KEY_IMAGE_PREVIEW,path)
                    putBoolean(KEY_PREVIEW,isOnlyPreview)
                }
            }
        }

        private const val KEY_IMAGE_PREVIEW = "KEY_IMAGE_PREVIEW"
        private const val KEY_PREVIEW = "KEY_PREVIEW"
    }

    fun setBitmap(bitmap: Bitmap){
        image_preview?.loadImageUrl(bitmap)
    }

    override fun onDestroy() {
        super.onDestroy()
        onDestroyDialog()
    }

    var onRecap : () -> Unit = {}
    var onUpload : () -> Unit = {}
    var onDestroyDialog : () -> Unit = {}
}