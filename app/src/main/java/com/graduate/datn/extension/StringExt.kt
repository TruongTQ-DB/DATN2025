package com.graduate.datn.extension

import android.content.res.Resources
import java.text.SimpleDateFormat
import java.util.*

val Int.dpToPx: Int get() = Math.round(this * Resources.getSystem().displayMetrics.density)

val Int.degit: String get() = if (this <= 9) "0${this}" else this.toString()

fun getToday(): String {
    val date = Calendar.getInstance().time
    return date.convertFomat("dd/MM/yyyy")
}

fun Date.convertFomat(format: String, locale: Locale = Locale.getDefault()): String {
    val formatter = SimpleDateFormat(format, locale)
    return formatter.format(this)
}
