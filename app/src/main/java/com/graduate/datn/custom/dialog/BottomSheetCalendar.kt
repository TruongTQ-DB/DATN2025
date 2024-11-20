package com.graduate.datn.custom.dialog

import android.content.Context
import android.text.style.ForegroundColorSpan
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.graduate.datn.R
import com.graduate.datn.base.custom.BaseFullScreenDialog
import com.graduate.datn.extension.*
//import com.beetech.hsba.share_preference.HSBASharePref
//import com.beetech.hsba.share_preference.HSBASharePrefImpl
//import com.beetech.hsba.utils.Constant
import com.prolificinteractive.materialcalendarview.CalendarDay
import com.prolificinteractive.materialcalendarview.DayViewDecorator
import com.prolificinteractive.materialcalendarview.DayViewFacade
import com.prolificinteractive.materialcalendarview.MaterialCalendarView
import com.prolificinteractive.materialcalendarview.spans.DotSpan
import kotlinx.android.synthetic.main.bottom_sheet_calendar.calendar_view
import kotlinx.android.synthetic.main.bottom_sheet_calendar.container_bottom_sheet
import kotlinx.android.synthetic.main.bottom_sheet_calendar.fl_bottom_sheet
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class BottomSheetCalendar(context: Context, var bookingDate: String,
                          var isLimitToDay: Boolean = false,
                          var birthdayUser: String ?= null,
                          var isSchedule: Boolean = false, var isWorkDoctor: Boolean = false) : BaseFullScreenDialog() {

    private var checkClick: Boolean = true
    private val today = CalendarDay.today()
    private var bookingToday: String = "${today.year}-${today.month.degit}-${today.day.degit}"



    fun activeCurrentDate(selectedDate : String){
        bookingDate = selectedDate
        calendar_view?.setCurrentDate(selectedDate)
        setHighlightText(selectedDate.stringToCalendarDay())
    }

    override fun initView() {
        dialog?.window?.setBackgroundDrawableResource(android.R.color.transparent)
        fl_bottom_sheet.visible()
        val screenHeight = resources.displayMetrics.heightPixels
        fl_bottom_sheet.translationY = screenHeight.toFloat()
        fl_bottom_sheet.animate()
            .translationY(0f)
            .setDuration(600)
            .start()
        calendar_view.setWeekDayFormatterNew()
        calendar_view.setCurrentDate(bookingDate)
        setHighlightText(bookingDate.stringToCalendarDay())
        setHighlightToday()

        if (isLimitToDay) {
            //today
            val day = getToday().convertDate(DATE_FORMAT_1, "dd").toInt()
            val month = getToday().convertDate(DATE_FORMAT_1, "MM").toInt()
            val year = getToday().convertDate(DATE_FORMAT_1, "yyyy").toInt()
            //birthday
            val dayUser = 1
            val monthUser = 1
            val yearUser = 2000

            calendar_view.state().edit()
                .setMinimumDate(CalendarDay.from(yearUser, monthUser, dayUser))
                .setMaximumDate(CalendarDay.from(year, month, day)).commit()
        }

        if (isSchedule) {
            //today
            val day = getToday().convertDate(DATE_FORMAT_1, "dd").toInt()
            val month = getToday().convertDate(DATE_FORMAT_1, "MM").toInt()
            val year = getToday().convertDate(DATE_FORMAT_1, "yyyy").toInt()

            calendar_view.state().edit()
                .setMinimumDate(CalendarDay.from(year, month, day)).commit()
        }
    }

    override fun initListener() {
        container_bottom_sheet.setOnClickListener {
            hideBottomSheet()
        }

        calendar_view.setOnDateChangedListener { widget, date, selected ->
            onDateChangedListener(date, calendar_view)
            hideBottomSheet()
        }
    }

    private fun hideBottomSheet() {
        lifecycleScope.launch {
            delay(500)
            dismissAllowingStateLoss()
        }
        val screenHeight = resources.displayMetrics.heightPixels
        fl_bottom_sheet.animate()
            .translationY(screenHeight.toFloat())
            .setDuration(600)
            .start()
    }

    private fun setHighlightToday() {
        if (bookingToday != bookingDate) {
            calendar_view.selectedDate = bookingDate.stringToCalendarDay()
        }
        setHighlightText(today, true)
    }

    private fun setHighlightText(calendarDay: CalendarDay, isToday: Boolean = false) {
        calendar_view?.addDecorator(object : DayViewDecorator {
            override fun shouldDecorate(day: CalendarDay): Boolean {
                return day == calendarDay
            }

            override fun decorate(view: DayViewFacade?) {
                if (checkClick) {
                    view?.addSpan(
                        ForegroundColorSpan(
                            ContextCompat.getColor(
                                requireContext(),
                                R.color.color_0957DF
                            )
                        )
                    )
                    if (isToday) {
                        if (isWorkDoctor) {
                            if (bookingToday == bookingDate) {
                                view?.setBackgroundDrawable(context?.getDrawable(R.drawable.cus_selected_today)!!)
                            } else {
                                view?.addSpan(0)
                            }
                        }
                        view?.addSpan(
                            DotSpan(
                                4f,
                                ContextCompat.getColor(requireContext(), R.color.color_0957DF)
                            )
                        )
                    }
                } else {
                    view?.addSpan(
                        ForegroundColorSpan(
                            ContextCompat.getColor(
                                requireContext(),
                                R.color.color_1B1B1F
                            )
                        )
                    )
                }
            }
        })
    }

    override val layoutId: Int
        get() = R.layout.bottom_sheet_calendar

    var onDateChangedListener: (CalendarDay, MaterialCalendarView) -> Unit =
        { calendarDay, calendar_view_bottom_sheet -> }

}