package com.graduate.datn.extension

import android.Manifest
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import com.graduate.datn.R
import com.graduate.datn.base.BaseFragment
import com.graduate.datn.base.permission.PermissionHelper
import com.graduate.datn.entity.response.PaymentQueryResponse
import com.graduate.datn.ui.common.capture.CaptureImageFragment
import com.graduate.datn.utils.BundleKey
import com.graduate.datn.utils.Constant
import com.graduate.datn.utils.DialogUtil
import com.vnpay.authentication.VNP_AuthenticationActivity


fun BaseFragment.openScreenCropCapture(permissionHelper: PermissionHelper) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        permissionHelper.withFragment(this)
            .check(
                Manifest.permission.CAMERA,
                Manifest.permission.READ_MEDIA_IMAGES,
                Manifest.permission.READ_MEDIA_VIDEO
            )
            .onSuccess(Runnable {
                ((parentFragment as BaseFragment).parentFragment as BaseFragment).getVC()
                    .addFragment(CaptureImageFragment::class.java, Bundle().apply {
                        putBoolean(BundleKey.KEY_CROP_CAPTURE, true)
                    })
            })
            .onDenied(Runnable {
            })
            .onNeverAskAgain(Runnable {
                openPermissionSetting(permissionHelper)
            })
            .run()
    }else{
        permissionHelper.withFragment(this)
            .check(
                Manifest.permission.CAMERA,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE
            )
            .onSuccess(Runnable {
                ((parentFragment as BaseFragment).parentFragment as BaseFragment).getVC()
                    .addFragment(CaptureImageFragment::class.java, Bundle().apply {
                        putBoolean(BundleKey.KEY_CROP_CAPTURE, true)
                    })
            })
            .onDenied(Runnable {
            })
            .onNeverAskAgain(Runnable {
                openPermissionSetting(permissionHelper)
            })
            .run()
    }
}

fun Fragment.goToGallery(listener: () -> Unit, permissionHelper: PermissionHelper) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU){
        permissionHelper.withFragment(this)
            .check(
                Manifest.permission.READ_MEDIA_IMAGES,
                Manifest.permission.READ_MEDIA_VIDEO
            )
            .onSuccess(Runnable {
                listener()
            })
            .onDenied(Runnable {
            })
            .onNeverAskAgain(Runnable {
                openPermissionSetting(permissionHelper)
            })
            .run()
    }else{
        permissionHelper.withFragment(this)
            .check(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
            .onSuccess(Runnable {
                listener()
            })
            .onDenied(Runnable {
            })
            .onNeverAskAgain(Runnable {
                openPermissionSetting(permissionHelper)
            })
            .run()
    }
}

fun Fragment.openPermissionSetting(permissionHelper: PermissionHelper) {
    DialogUtil.displayDialogSettings(
        activity!!,
        "bạn chưa cấp quyền"
    ) {
        permissionHelper.startApplicationSettingsActivity()
    }.show()
}

fun Fragment.openVnPay(url: String){
    val intent = Intent(requireContext(), VNP_AuthenticationActivity::class.java)
    intent.putExtra("url", url) //bắt buộc, VNPAY cung cấp
    intent.putExtra("tmn_code", "BTWETE01") //bắt buộc, VNPAY cung cấp
    intent.putExtra(
        "scheme",
        getString(R.string.scheme)
    ) //bắt buộc, scheme để mở lại app khi có kết quả thanh toán từ mobile banking
    intent.putExtra(
        "is_sandbox",
        false
    ) //bắt buộc, true <=> môi trường test, true <=> môi trường live
    VNP_AuthenticationActivity.setSdkCompletedCallback { action ->
        Log.wtf("SplashActivity", "action: $action")
        //action == AppBackAction
        //Người dùng nhấn back từ sdk để quay lại

        //action == CallMobileBankingApp
        //Người dùng nhấn chọn thanh toán qua app thanh toán (Mobile Banking, Ví...)
        //lúc này app tích hợp sẽ cần lưu lại cái PNR, khi nào người dùng mở lại app tích hợp thì sẽ gọi kiểm tra trạng thái thanh toán của PNR Đó xem đã thanh toán hay chưa.

        //action == WebBackAction
        //Người dùng nhấn back từ trang thanh toán thành công khi thanh toán qua thẻ khi url có chứa: cancel.sdk.merchantbackapp

        //action == FaildBackAction
        //giao dịch thanh toán bị failed

        //action == SuccessBackAction
        //thanh toán thành công trên webview
        when (action) {
            "CallMobileBankingApp" -> {

            }
            "SuccessBackAction" -> {

            }
        }
    }
    startActivity(intent)
}

fun Fragment.checkStatusPayment(data: PaymentQueryResponse, paymentSuccess: () -> Unit,
                                paymentError: () -> Unit) {
    when (data.vnpTransactionStatus) {
        Constant.PAYMENT_VNPAY_SUCCESS -> {
            toast(R.string.str_payment_00)
            paymentSuccess()
        }
        Constant.PAYMENT_VNPAY_01 -> {
            toast(R.string.str_payment_01)
            paymentError()
        }
        Constant.PAYMENT_VNPAY_02 -> {
            toast(R.string.str_payment_02)
            paymentError()
        }
        Constant.PAYMENT_VNPAY_04 -> {
            toast(R.string.str_payment_04)
            paymentError()
        }
        Constant.PAYMENT_VNPAY_05 -> {
            toast(R.string.str_payment_05)
            paymentError()
        }
        Constant.PAYMENT_VNPAY_06 -> {
            toast(R.string.str_payment_06)
            paymentError()
        }
        Constant.PAYMENT_VNPAY_07 -> {
            toast(R.string.str_payment_07)
            paymentError()
        }
        Constant.PAYMENT_VNPAY_09 -> {
            toast(R.string.str_payment_09)
            paymentError()
        }
        else -> {
            toast(R.string.error_place_holder)
            paymentError()
        }
    }
}