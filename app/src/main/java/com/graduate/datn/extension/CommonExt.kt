package com.graduate.datn.extension

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.*

import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.graduate.datn.base.entity.BaseResponse
import com.google.gson.Gson
import com.sangcomz.fishbun.FishBun
import com.sangcomz.fishbun.MimeType
import com.sangcomz.fishbun.adapter.image.impl.GlideAdapter
import retrofit2.HttpException
import java.io.IOException

fun Collection<*>?.isNullOrEmpty(): Boolean {
    return this == null || this.isEmpty()
}

@ColorInt
fun Context.color(@ColorRes res: Int): Int {
    return ContextCompat.getColor(this, res)
}

fun Context.drawable(@DrawableRes res: Int): Drawable? {
    return ContextCompat.getDrawable(this, res)
}

fun Context.string(@StringRes res: Int): String {
    return getString(res)
}

fun Context.inflate(@LayoutRes res: Int, parent: ViewGroup, attachView: Boolean = true) : View {
    return LayoutInflater.from(this).inflate(res, parent, attachView)
}

fun Context.statusBarHeight(restrictToLollipop: Boolean = true): Int {
    var result = 0
    val resourceId = resources.getIdentifier("status_bar_height", "dimen", "android")
    if (resourceId > 0 && (!restrictToLollipop || (restrictToLollipop && Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP))) {
        result = resources.getDimensionPixelSize(resourceId)
    }
    return result
}

fun Throwable.getErrorBody(): BaseResponse {
    if (this is HttpException) {
        val body = response()?.errorBody()
        val gson = Gson()
        val adapter = gson.getAdapter(BaseResponse::class.java)
        try {
            return adapter.fromJson(body?.string())
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }
    return BaseResponse()
}

fun Fragment.openImageGallery(isSelectAvatar : Boolean = false, isShowCamera : Boolean = false) {
    val creator = FishBun.with(this)
        .setImageAdapter(GlideAdapter())
        .setIsUseDetailView(false)
        .setActionBarColor(Color.parseColor("#f0c630"))
        .setAllViewTitle("Tất cả")
        .exceptMimeType(listOf(MimeType.GIF))
        .setMaxCount(30)
        .setPickerSpanCount(3)
        .textOnImagesSelectionLimitReached("Chọn tối đa 30 ảnh")
        .textOnNothingSelected("Chưa chọn ảnh")

    if (isSelectAvatar){
        creator.setMaxCount(1)
        creator.setReachLimitAutomaticClose(true)
    }
    if(isShowCamera){
        creator.hasCameraInPickerPage(isShowCamera)
    }
    creator.startAlbum()
}

