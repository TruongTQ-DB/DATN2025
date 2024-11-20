package com.graduate.datn.ui.common.capture

import android.os.Bundle
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import com.graduate.datn.R
import com.graduate.datn.base.BaseFragment
import com.graduate.datn.custom.dialog.PreviewImageDialog
import com.graduate.datn.extension.gone
import com.graduate.datn.extension.onAvoidDoubleClick
import com.graduate.datn.extension.visible
import com.graduate.datn.rx.RxBus
import com.graduate.datn.rx.RxEvent
import com.graduate.datn.ui.common.crop.CropImageFragment
import com.graduate.datn.utils.BundleKey
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_capture_image.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.File


@AndroidEntryPoint
class CaptureImageFragment : BaseFragment() {

    private val viewModel: CaptureViewModel by viewModels()
    private var isCapture = false
    private var isCropCapture = false
    private var isAttachFile = false

    override fun backFromAddFragment() {

    }

    override val layoutId: Int
        get() = R.layout.fragment_capture_image

    override fun initView() {
        viewModel.initCamera(requireContext(), cameraView, focusView)
        lifecycleScope.launch {
            delay(800)
            viewModel.startCamera()
        }
    }

    override fun initData() {
        arguments?.let {
//            if (it.containsKey(BundleKey.KEY_CAPTURE)) {
//                isCapture = it.getBoolean(BundleKey.KEY_CAPTURE)
//                setUpUI(isCapture)
//            }

            if (it.containsKey(BundleKey.KEY_CROP_CAPTURE)) {
                isCropCapture = it.getBoolean(BundleKey.KEY_CROP_CAPTURE)
                setUpUI(isCropCapture)
            }

//            if (it.containsKey(BundleKey.KEY_ATTACH_FILE)) {
//                isAttachFile = it.getBoolean(BundleKey.KEY_ATTACH_FILE)
//                setUpUI(isAttachFile)
//            }
        }
        btn_back.onAvoidDoubleClick {
            backPressed()
        }

        btn_capture.onAvoidDoubleClick {
            btn_capture.isEnabled = false
            viewModel.takePicture()
        }

        viewModel.picture.observe(this, object : Observer<File> {
            override fun onChanged(it: File) {
                btn_capture.isEnabled = true
                if (isCropCapture) {
                    getVC().addFragment(CropImageFragment::class.java, Bundle().apply {
                        putString(BundleKey.KEY_URI_CAPTURE, it.absolutePath)
                    })
                } else if (isAttachFile) {
                    getVC().backFromAddFragment(Bundle().apply {
                        putString(BundleKey.KEY_CAPTURE_IMAGE,it.absolutePath)
                    })
                } else {
                    val previewImageDialog = PreviewImageDialog.newInstance(it.absolutePath,false)
                    previewImageDialog.apply {
                        onRecap = {
                            viewModel.startCamera()
                            previewImageDialog.dismissAllowingStateLoss()
                        }
                        onUpload = {
                            if (isCapture) {
                                previewImageDialog.dismissAllowingStateLoss()
                                getVC().backFromAddFragmentWithCondition(Bundle().apply {},1)
                                RxBus.publish(RxEvent.GetImageCameraEvent(it))

//                                if (isByChat) {
//                                    getVC().backFromAddFragment(Bundle().apply {
//                                        putString(BundleKey.KEY_CAPTURE_IMAGE, it.absolutePath)
//                                    })
//                                } else {
//                                    getVC().backFromAddFragmentWithCondition(Bundle().apply {},1)
//                                    RxBus.publish(RxEvent.GetImageCameraEvent(it))
//                                }
                            } else {
                                previewImageDialog.dismissAllowingStateLoss()
                                getVC().backFromAddFragmentWithCondition(Bundle().apply {
                                    putString(BundleKey.KEY_CAPTURE_IMAGE,it.absolutePath)
                                },2)
                            }
                        }
                        onDestroyDialog = {
                            viewModel.startCamera()
                        }
                    }
                    previewImageDialog.show(childFragmentManager,previewImageDialog.tag)
                }
            }
        })
    }

    fun setUpUI(isShow: Boolean) {
        if (isShow) {
            playerContainer.gone()
        } else {
            playerContainer.visible()
        }
    }

    override fun initListener() {
        imv_flash.onAvoidDoubleClick {
            viewModel.openFlash = !viewModel.openFlash
            viewModel.openFlash()
            if (viewModel.openFlash) {
                imv_flash.setImageDrawable(context?.getDrawable(R.drawable.ic_flash_open))
            } else {
                imv_flash.setImageDrawable(context?.getDrawable(R.drawable.ic_flash_close))
            }
        }
    }

    override fun backPressed(): Boolean {
        getVC().backFromAddFragment()
        return false
    }

    override fun onStop() {
        super.onStop()
        viewModel.stopCamera()
    }

    override fun onResume() {
        super.onResume()
        viewModel.startCamera()
    }

    override fun onPause() {
        super.onPause()
        viewModel.stopCamera()
    }
}