package com.graduate.datn.utils

import android.app.Activity
import android.content.Context
import android.net.ConnectivityManager
import android.util.TypedValue
import android.view.inputmethod.InputMethodManager


object DeviceUtil {

    /**
     * Check internet connected
     *
     * @param context
     * @return
     */
    fun hasConnection(context: Context?): Boolean {
        if (context == null) {
            return false
        }

        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            ?: return false

        val wifiNetwork = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI)
        if (wifiNetwork != null && wifiNetwork.isConnected) {
            return true
        }

        val mobileNetwork = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE)
        if (mobileNetwork != null && mobileNetwork.isConnected) {
            return true
        }

        val activeNetwork = cm.activeNetworkInfo
        return activeNetwork != null && activeNetwork.isConnected
    }

    fun hideSoftKeyboard(activity: Activity?) {
        if (activity != null && activity.window != null) {
            val imm = activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm?.hideSoftInputFromWindow(activity.window.decorView.windowToken, 0)
        }
    }

    fun clearFocus(activity: Activity?) {
        if (activity != null && activity.currentFocus != null) {
            activity.currentFocus!!.clearFocus()
        }
    }

    fun convertDpToPx(context: Context, inputDp: Int): Int {
        val resources = context.resources
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            inputDp.toFloat(),
            resources.displayMetrics
        ).toInt()
    }

    fun getSoftButtonsBarHeight(context: Context): Int {
        val resources = context.resources
        val resourceId = resources.getIdentifier("navigation_bar_height", "dimen", "android")
        return if (resourceId > 0) {
            resources.getDimensionPixelSize(resourceId)
        } else 0
    }

    fun widthScreenPixel(context: Context?): Int {
        if (context == null) {
            return 0
        }
        val resources = context.resources
        val metrics = resources.displayMetrics

        return metrics.widthPixels
    }

    fun getStatusBarHeight(context: Context): Int {
        var result = 0
        val resourceId: Int = context.resources.getIdentifier("status_bar_height", "dimen", "android")
        if (resourceId > 0) {
            result = context.resources.getDimensionPixelSize(resourceId)
        }
        return result
    }
}
