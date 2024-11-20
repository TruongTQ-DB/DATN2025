package com.graduate.datn.custom.dialog

import android.content.Context
import androidx.lifecycle.lifecycleScope
import com.graduate.datn.R
import com.graduate.datn.base.custom.BaseFullScreenDialog
import com.graduate.datn.custom.view.CustomDatePickerView
import com.graduate.datn.extension.*
import com.graduate.datn.utils.BundleKey
import kotlinx.android.synthetic.main.bottom_sheet_date_picker.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class BottomSheetDatePicker(context: Context, val maxDate: Boolean?= false) : BaseFullScreenDialog() {

    var valueSelected: String = ""
    private var formatter = SimpleDateFormat(DATE_FORMAT_7)
    private val calendar = Calendar.getInstance()

    override fun initView() {
        dialog?.window?.setBackgroundDrawableResource(android.R.color.transparent)
        cl_bottom_sheet.visible()
        val screenHeight = resources.displayMetrics.heightPixels
        cl_bottom_sheet.translationY = screenHeight.toFloat()
        cl_bottom_sheet.animate()
            .translationY(0f)
            .setDuration(600)
            .start()

        // When you want update date selected, use bundle to do that
        arguments.let {
            arguments?.let {
                if (it.containsKey(BundleKey.KEY_DATE_SELECTED)) {
                    val dateSelected = it.getString(BundleKey.KEY_DATE_SELECTED)
                    if (!dateSelected.isNullOrEmpty()){
                        setDateSelected(dateSelected)
                    }else{
                        formatter.format(calendar.time)
                    }
                }
            }
        }

        setVibrationFeedback(false)
    }

    fun setCircularPicker(isChecked: Boolean) {
        day_time_picker_view.isCircular = isChecked
    }

    fun setVibrationFeedback(isChecked: Boolean) {
        day_time_picker_view.isHapticFeedbackEnabled = isChecked
    }

    fun setDateSelected(value: String) {
        val format = SimpleDateFormat(DATE_FORMAT_7)
        val date = format.parse(value)
        val calendarSelected = Calendar.getInstance()
        date?.let {
            calendarSelected.time = it
            day_time_picker_view.setDate(
                calendarSelected.get(Calendar.YEAR),
                calendarSelected.get(Calendar.MONTH),
                calendarSelected.get(Calendar.DAY_OF_MONTH)
            )
        }
        // yyyy-MM-dd
        if (maxDate == true) {
            day_time_picker_view.maxDate = calendar.time
        }
    }

    override fun initListener() {
        container_bottom_sheet.onAvoidDoubleClick {
            hideBottomSheet()
        }
        day_time_picker_view.maxDate = Calendar.getInstance().time
        day_time_picker_view.setWheelListener(object : CustomDatePickerView.Listener {
            override fun didSelectData(year: Int, month: Int, day: Int) {
                calendar.set(Calendar.YEAR, year)
                calendar.set(Calendar.MONTH, month)
                calendar.set(Calendar.DAY_OF_MONTH, day)
                valueSelected = formatter.format(calendar.time)
            }
        })

        tv_ok.setOnClickListener {
            onclickDatePicker(valueSelected)
            // open this line to view value
            //toast("$valueSelected")
            hideBottomSheet()
        }

        tv_cancel.setOnClickListener {
            hideBottomSheet()
        }
    }

    private fun hideBottomSheet() {
        lifecycleScope.launch {
            delay(500)
            dismissAllowingStateLoss()
        }
        val screenHeight = resources.displayMetrics.heightPixels
        cl_bottom_sheet.animate()
            .translationY(screenHeight.toFloat())
            .setDuration(600)
//            .withEndAction { dismissAllowingStateLoss() }
            .start()
    }

    override val layoutId: Int
        get() = R.layout.bottom_sheet_date_picker


    var onclickDatePicker: (String) -> Unit = {}

}