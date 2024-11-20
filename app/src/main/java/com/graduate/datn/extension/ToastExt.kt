package com.graduate.datn.extension

import android.content.Context
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.fragment.app.Fragment

fun Context.toast(@StringRes resourceId: Int, length: Int = Toast.LENGTH_SHORT) {
    toast(getString(resourceId), length)
}

fun Context.toast(message: String, length: Int = Toast.LENGTH_SHORT) {
    Toast.makeText(this, message, length).show()
}

fun Fragment.toast(@StringRes resourceId: Int, length: Int = Toast.LENGTH_SHORT) {
    toast(getString(resourceId), length)
}

fun Fragment.toast(message: String, length: Int = Toast.LENGTH_SHORT) {
    Toast.makeText(context, message, length).show()
}