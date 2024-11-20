package com.graduate.datn.extension

import android.util.Log
import com.prolificinteractive.materialcalendarview.CalendarDay
import java.text.SimpleDateFormat
import java.util.*


const val DATE_FORMAT_1 = "yyyy-MM-dd HH:mm:ss"
const val DATE_FORMAT_11 = "yyyy-MM-dd"
const val DATE_FORMAT_2 = "dd/MM/yyyy"
const val DATE_FORMAT_3 = "HH:mm:ss"
const val DATE_FORMAT_4 = "yyyy-MM-dd"
const val DATE_FORMAT_5 = "yyyy/MM/dd"
const val DATE_FORMAT_6 = "yyyy-MM-dd"
const val DATE_FORMAT_7 = "dd-MM-yyyy"

fun String.getReadableDateTime(date: Date): String {
    return  SimpleDateFormat("MMMM dd, yyyy - hh:mm a", Locale.getDefault()).format(date)
}
fun String.convertToDate(formatDate: String): Date? {
    try {
        return SimpleDateFormat(formatDate).parse(this)
    } catch (ex: Exception) {
    }
    return null
}

fun Date.convertToString(formatDate: String): String {
    return SimpleDateFormat(formatDate).format(this)
}

fun convertCalendarDayToString(calendarDay: CalendarDay): String {
    val year = calendarDay.year
    val month = calendarDay.month
    val day = calendarDay.day

    val formattedMonth = if (month < 10) "0$month" else "$month"
    val formattedDay = if (day < 10) "0$day" else "$day"

    return "$formattedDay/$formattedMonth/$year" // Hoán đổi vị trí ngày và tháng ở đây
}


fun String.convertDate(): String {
    val defaultDate = SimpleDateFormat(DATE_FORMAT_1)
    val date = defaultDate.parse(this)
    return SimpleDateFormat(DATE_FORMAT_2).format(date)
}

fun String.convertDate(format: String,targetFormat : String): String {
    return try {
        val defaultDate = SimpleDateFormat(format)
        val date = defaultDate.parse(this)
        return SimpleDateFormat(targetFormat).format(date)
    } catch (e:Exception){
        ""
    }

}

fun String.convertTime(): String {
    val defaultDate = SimpleDateFormat(DATE_FORMAT_1)
    val time = defaultDate.parse(this)
    return SimpleDateFormat(DATE_FORMAT_3).format(time)
}

fun String.convertday(): String {
    val defaultDate = SimpleDateFormat(DATE_FORMAT_4)
    val time = defaultDate.parse(this)
    return SimpleDateFormat(DATE_FORMAT_5).format(time)
}
fun calendarDayToDate(calendarDay: CalendarDay): Date {
    val calendar = Calendar.getInstance()
    calendar.set(calendarDay.year, calendarDay.month, calendarDay.day)
    return calendar.time
}

fun convertDateFormat(inputDate: String, isReturnDay: Boolean = false): String {
    val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    val date = inputFormat.parse(inputDate)

    val calendar = Calendar.getInstance()
    calendar.time = date
    val dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)
    val dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH)
    val month = calendar.get(Calendar.MONTH) + 1
    val year = calendar.get(Calendar.YEAR)

    val outputFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

    return if (!isReturnDay) {
        "${getDayOfWeekName(dayOfWeek)}, ${outputFormat.format(date)}"
    } else {
        outputFormat.format(date)
    }
}

private fun getDayOfWeekName(dayOfWeek: Int): String {
    return when (dayOfWeek) {
        Calendar.SUNDAY -> "Chủ nhật"
        Calendar.MONDAY -> "Thứ 2"
        Calendar.TUESDAY -> "Thứ 3"
        Calendar.WEDNESDAY -> "Thứ 4"
        Calendar.THURSDAY -> "Thứ 5"
        Calendar.FRIDAY -> "Thứ 6"
        Calendar.SATURDAY -> "Thứ 7"
        else -> ""
    }
}

fun String.formatDateSchedule(): String {
    try {
        val dateFormat = SimpleDateFormat(DATE_FORMAT_2)
        val date = dateFormat.parse(this)
        val dateTargetFormat = SimpleDateFormat("EEE", Locale("vi"))
        val dateTarget = dateTargetFormat.format(date)
        val calendar = Calendar.getInstance()
        calendar.time = date
        Log.d("vmh", "formatDateSchedule: $dateTarget")
        return when(calendar.get(Calendar.DAY_OF_WEEK)) {
            Calendar.MONDAY -> {
                "T2, $this"
            }
            Calendar.TUESDAY -> {
                "T3, $this"
            }
            Calendar.WEDNESDAY -> {
                "T4, $this"
            }
            Calendar.THURSDAY -> {
                "T5, $this"
            }
            Calendar.FRIDAY -> {
                "T6, $this"
            }
            Calendar.SATURDAY -> {
                "T7, $this"
            }
            Calendar.SUNDAY -> {
                "CN, $this"
            }
            else -> {""}
        }
    }catch (e:Exception){
        return ""
    }

}