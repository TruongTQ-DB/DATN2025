package com.graduate.datn.ui.common.crop

import android.graphics.Bitmap
import android.net.Uri
import android.util.Log
import com.graduate.datn.R
import com.graduate.datn.base.BaseFragment
import com.graduate.datn.extension.convertFile
import com.graduate.datn.extension.onAvoidDoubleClick
import com.graduate.datn.utils.BundleKey
import com.graduate.datn.rx.RxBus
import com.graduate.datn.rx.RxEvent
import com.isseiaoki.simplecropview.callback.CropCallback
import com.isseiaoki.simplecropview.callback.LoadCallback
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_crop_image.*
import java.io.File

@AndroidEntryPoint
class CropImageFragment : BaseFragment() {

    private var mUri: Uri? = null
    private var isGallery = false
    override fun backFromAddFragment() {

    }

    override val layoutId: Int
        get() = R.layout.fragment_crop_image


    override fun initView() {
        crop_image_view.setCustomRatio(9, 11)

    }

    override fun initData() {
        arguments?.let {
            if (it.containsKey(BundleKey.KEY_URI_CAPTURE)) {
                val mPath = it.getString(BundleKey.KEY_URI_CAPTURE)
                mUri = Uri.fromFile(File(mPath.toString()))
            }

            if (it.containsKey(BundleKey.KEY_URI_CAPTURE_GALLERY)) {
                isGallery = it.getBoolean(BundleKey.KEY_URI_CAPTURE_GALLERY)
            }

            if (it.containsKey(BundleKey.KEY_URI_FROM_GALLERY)) {
                val mPath = it.getString(BundleKey.KEY_URI_FROM_GALLERY)
                mUri = Uri.parse(mPath)
            }
        }

        crop_image_view.load(mUri).execute(object : LoadCallback {
            override fun onError(e: Throwable?) {
                Log.d("vmh", "load crop image error")
            }

            override fun onSuccess() {
                Log.d("vmh", "load crop image success")
            }
        })
    }

    override fun initListener() {
        tv_done.onAvoidDoubleClick {
            crop_image_view.crop(mUri)
                .execute(object : CropCallback {
                    override fun onSuccess(path: Bitmap) {
                        RxBus.publish(RxEvent.GetImageCameraEvent(path.convertFile(requireContext())))
                        backFragment()
                    }

                    override fun onError(e: Throwable) {
                        Log.d("vmh", "crop image error")
                    }
                })
        }
        tv_close.onAvoidDoubleClick {
            backFragment()
//            getVC().backFromAddFragmentWithCondition(null, 2)
        }
    }

    override fun backPressed(): Boolean {
        backFragment()
//        getVC().backFromAddFragmentWithCondition(null, 2)
        return false
    }

    fun backFragment() {
        if (isGallery) {
            getVC().backFromAddFragmentWithCondition(null, 1)
        } else {
            getVC().backFromAddFragmentWithCondition(null, 2)
        }
    }

}