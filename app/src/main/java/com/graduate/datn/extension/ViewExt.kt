package com.graduate.datn.extension

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.ColorDrawable
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.view.marginBottom
import androidx.core.view.marginLeft
import androidx.core.view.marginRight
import androidx.core.view.marginTop
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.viewpager.widget.ViewPager
import com.graduate.datn.BaseApplication.Companion.context
import com.graduate.datn.R
import com.graduate.datn.utils.DateUtil
import com.bumptech.glide.Glide
import com.prolificinteractive.materialcalendarview.CalendarDay
import kotlinx.coroutines.delay
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

fun View.visible() {
    visibility = View.VISIBLE
}

fun View.gone() {
    visibility = View.GONE
}

fun View.invisible() {
    visibility = View.INVISIBLE
}

fun ViewGroup.inflate(@LayoutRes layout: Int): View {
    return LayoutInflater.from(context).inflate(layout, this, false)
}

const val CLICK_THROTTLE_DELAY = 800L

fun View.onAvoidDoubleClick(
    throttleDelay: Long = CLICK_THROTTLE_DELAY,
    onClick: (View) -> Unit
) {
    setOnClickListener {
        onClick(this)
        isClickable = false
        postDelayed({ isClickable = true }, throttleDelay)
    }
}

infix fun TextView.textChangedListener(init: TextWatcherWrapper.() -> Unit) {
    val wrapper = TextWatcherWrapper().apply { init() }
    addTextChangedListener(object : TextWatcher {
        override fun afterTextChanged(p0: Editable) {
            wrapper.after?.invoke(p0)
        }

        override fun beforeTextChanged(p0: CharSequence, p1: Int, p2: Int, p3: Int) {
            wrapper.before?.invoke(p0, p1, p2, p3)
        }

        override fun onTextChanged(p0: CharSequence, p1: Int, p2: Int, p3: Int) {
            wrapper.on?.invoke(p0, p1, p2, p3)
        }

    })
}

infix fun ViewPager.pageChangeListener(init: OnPageChangeListenerWrapper.() -> Unit) {
    val wrapper = OnPageChangeListenerWrapper().apply { init() }
    addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
        override fun onPageScrollStateChanged(state: Int) {
            wrapper.onPageScrollStateChanged?.invoke(state)
        }

        override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
            wrapper.onPageScrolled?.invoke(position, positionOffset, positionOffsetPixels)
        }

        override fun onPageSelected(position: Int) {
            wrapper.onPageSelected?.invoke(position)
        }

    })
}

fun View.showKeyboard() {
    val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    this.requestFocus()
    imm.showSoftInput(this, 0)
}

fun View.hideKeyboard(): Boolean {
    try {
        val inputMethodManager = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        return inputMethodManager.hideSoftInputFromWindow(windowToken, 0)
    } catch (ignored: RuntimeException) { }
    return false
}

val EditText.stringVal: String
    get() = this.text.toString().trim()

enum class Category{
    EmptyAccount,
    EmptyPassword,
    EmptyAll,
    Successfully,
    Logged
}
val Category.CategoryError: String
    get() = when (this) {
        Category.EmptyAccount -> context.getString(R.string.str_empty_username)
        Category.EmptyPassword -> context.getString(R.string.str_empty_password)
        Category.EmptyAll -> context.getString(R.string.str_empty_all)
        Category.Successfully -> context.getString(R.string.str_login_success)
        Category.Logged -> context.getString(R.string.lable_Logged)
    }

enum class SharePref{
    MyPref,
    KeyPref}

val SharePref.CategotySharePref: String
    get() = when (this) {
        SharePref.MyPref -> context.getString(R.string.dialog_title_key_pref)
        SharePref.KeyPref -> context.getString(R.string.dialog_title_key_pref)}

val KEY_BUNDLE = context.getString(R.string.tab_status_tablayout)

fun AppCompatEditText.passwordType() {
    inputType = InputType.TYPE_TEXT_VARIATION_PASSWORD or InputType.TYPE_CLASS_TEXT
    typeface = Typeface.DEFAULT
}

fun View.setMargins(
    left: Int = this.marginLeft,
    top: Int = this.marginTop,
    right: Int = this.marginRight,
    bottom: Int = this.marginBottom,
) {
    layoutParams = (layoutParams as ViewGroup.MarginLayoutParams).apply {
        setMargins(left, top, right, bottom)
    }

}

fun View.setOnSafeClickListener(
    onClickListener: View.OnClickListener
) = setOnClickListener {
    val lifecycleScope = (context as? AppCompatActivity)?.lifecycleScope
    lifecycleScope?.launchWhenStarted {
        if (!isClickable) return@launchWhenStarted
        isClickable = false
        onClickListener.onClick(this@setOnSafeClickListener)
        delay(500)
        isClickable = true
    } ?: onClickListener.onClick(this)
}

fun ImageView.loadImageUrl(url: Any?) {
    Glide.with(this.context)
        .load(url)
        .placeholder(R.drawable.image_avt)
        .error(R.drawable.image_avt)
        .centerCrop()
        .into(this)
}

fun Fragment.showDatePickerDialog(
    isSetMaxDate: Boolean,
    beforeDate: Date,
    context: Context,
    callBack: (String) -> Unit
) {
    val newCalendar: Calendar = Calendar.getInstance()
    newCalendar.time = beforeDate
    val startTime = DatePickerDialog(
        context, android.R.style.Theme_Holo_Light_Dialog_MinWidth,
        { _, year, monthOfYear, dayOfMonth ->
            val newDate: Calendar = Calendar.getInstance()
            newDate.set(year, monthOfYear, dayOfMonth)
            callBack(DateUtil.dateToString(newDate.time, "yyyy-MM-dd"))
        },
        newCalendar.get(Calendar.YEAR),
        newCalendar.get(Calendar.MONTH),
        newCalendar.get(Calendar.DAY_OF_MONTH)
    )
    if (isSetMaxDate) {
        startTime.datePicker.maxDate = Date().time
    }
    // an border xung dialog_date
    startTime.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    startTime.show()
}

@SuppressLint("SimpleDateFormat")
fun formatDatetoDayMonthYear(bir: String): String {
    val dateF = SimpleDateFormat(DATE_FORMAT_6)
    val birday = dateF.parse(bir)
    val dFormat = SimpleDateFormat(DATE_FORMAT_2)
    return dFormat.format(birday)
}

fun EditText.disablePaste() {
    val callback: ActionMode.Callback = object : ActionMode.Callback {
        override fun onCreateActionMode(mode: ActionMode?, menu: Menu?): Boolean {
            return true
        }

        override fun onPrepareActionMode(mode: ActionMode?, menu: Menu?): Boolean {
            menu?.removeItem(android.R.id.paste)
            menu?.removeItem(android.R.id.autofill)
            return true
        }

        override fun onActionItemClicked(mode: ActionMode?, item: MenuItem?): Boolean {
            return false
        }

        override fun onDestroyActionMode(mode: ActionMode?) {}
    }
    this.customInsertionActionModeCallback = callback
    this.customSelectionActionModeCallback = callback
}

fun ImageView.loadImageUrlNoPlaceHolder(url: Any?) {
    Glide.with(this.context)
        .load(url)
        .into(this)
}

fun ImageView.loadAvatar(url: Any?) {
    Glide.with(this.context)
        .load(url)
        .placeholder(R.drawable.image_avt)
        .error(R.drawable.image_avt)
        .centerCrop()
        .into(this)
}

@SuppressLint("SimpleDateFormat")
fun String.stringToCalendarDay(): CalendarDay {
    val dateFormat: DateFormat = SimpleDateFormat(DATE_FORMAT_11)
    val date = dateFormat.parse(this)
    val cal = Calendar.getInstance()
    if (date != null) {
        cal.time = date
    } else
        cal.time = Date()
    return CalendarDay.from(
        cal[Calendar.YEAR],
        cal[Calendar.MONTH] + 1,
        cal[Calendar.DAY_OF_MONTH]
    )
}


