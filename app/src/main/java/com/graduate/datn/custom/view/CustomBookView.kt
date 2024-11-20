package com.graduate.datn.custom.view

import android.content.Context
import android.content.res.TypedArray
import android.text.style.ForegroundColorSpan
import android.util.AttributeSet
import androidx.core.content.ContextCompat
import com.graduate.datn.R
import com.graduate.datn.adapter.recyclerview.TimeScheduleAdapter
import com.graduate.datn.entity.request.TimeRange
import com.graduate.datn.extension.*
import com.graduate.datn.ui.admin.list_work_schedule.DateResponse
import com.graduate.datn.utils.DayEnableDecorator
import com.prolificinteractive.materialcalendarview.CalendarDay
import com.prolificinteractive.materialcalendarview.DayViewDecorator
import com.prolificinteractive.materialcalendarview.DayViewFacade
import com.prolificinteractive.materialcalendarview.spans.DotSpan
import kotlinx.android.synthetic.main.view_custom_book.view.*
import java.text.MessageFormat
import java.util.*


class CustomBookView(context: Context, attrs: AttributeSet?) :
    CustomViewConstraintLayout(context, attrs) {

    private var bookingDate: String? = null

    var onDateChangeListener: (string: String) -> Unit = {}
    private var listSelectTime: List<TimeRange> = emptyList()

    private var positionTime: Int? = 0
    private var checkClick: Boolean = false

    var isHasToday: Boolean = true
    var isHasSelectDay = true

    var onSelectDate: (String) -> Unit = {}
    var onSelectTime: (String, String) -> Unit = {timeFrom, timeTo ->}

    private var timeAdapter: TimeScheduleAdapter ?= null


    override fun getLayoutRes(): Int {
        return R.layout.view_custom_book
    }

    override fun getStyableRes(): IntArray? {
        return null
    }

    private val today = CalendarDay.today()
    private var selectedDay : CalendarDay? = null
    override fun initView() {
        //set highlight today
        setHighlightToday()
        timeAdapter = TimeScheduleAdapter(context)
        rcv_time_schedule.adapter = timeAdapter
    }

    fun setHighlightToday() {
        calendar_view.addDecorator(object : DayViewDecorator {
            override fun shouldDecorate(day: CalendarDay): Boolean {
                return day == today
            }

            override fun decorate(view: DayViewFacade) {
                view.addSpan(ForegroundColorSpan(ContextCompat.getColor(context, R.color.color_36A693)))
                view.addSpan(DotSpan(5f, ContextCompat.getColor(context, R.color.color_36A693)))
//                if (!checkClick) {
//                    view.addSpan(ForegroundColorSpan(ContextCompat.getColor(context, R.color.color_selected_menu)))
//                } else {
//                    view.addSpan(ForegroundColorSpan(ContextCompat.getColor(context, R.color.black)))
//                }
                if (!isHasToday) {
                    view.setDaysDisabled(true)
                    view.addSpan(ForegroundColorSpan(ContextCompat.getColor(context, R.color.color_36A693)))
                }
            }
        })
        calendar_view.addDecorator(object : DayViewDecorator {
            override fun shouldDecorate(day: CalendarDay): Boolean {
                return day == selectedDay
            }

            override fun decorate(view: DayViewFacade) {
                view.addSpan(ForegroundColorSpan(ContextCompat.getColor(context, R.color.color_36A693)))
            }
        })
    }
    fun resetSelectedDay(){
        selectedDay = null
    }
    override fun initListener() {
        btn_day.setOnClickListener {
            setHighlightToday()
            val color: Int = btn_day.currentTextColor
            val hexColor = String.format("#%06X", 0xFFFFFF and color)
            if(hexColor == "#000000")
                setSelect(true)
            else
                setSelect(isSelected = true, isReset = true)
        }
        btn_hour.setOnClickListener {
            if(bookingDate.isNullOrBlank()){
                context.toast("Vui lòng chọn ngày trước!")
                return@setOnClickListener
            }
            val color: Int = btn_hour.currentTextColor
            val hexColor = String.format("#%06X", 0xFFFFFF and color)
            if(hexColor == "#000000")
                setSelect(false)
            else
                setSelect(isSelected = false, isReset = true)
        }
        calendar_view.setOnDateChangedListener { widget, date, selected ->
            bookingDate =
                "${date.year}-${checkDegit(date.month)}-${checkDegit(date.day)}"
            onDateChangeListener(bookingDate!!)
            checkClick = today == date
            selectedDay = date
            setHighlightToday()

            btn_day.text = MessageFormat.format(
                context.getString(R.string.make_an_appointment_day_format),
                date.day, checkDegit(date.month), checkDegit(date.year)
            )
            onSelectDate.invoke("${checkDegit(date.day)}/${checkDegit(date.month)}/${checkDegit(date.year)}")
            btn_hour.text = context.getString(R.string.make_an_appointment_hour)
        }
    }

    fun setSelect(isSelected: Boolean, isReset: Boolean = false) {
        if (!isReset) {
            btn_day.textColors
            btn_day.setBackgroundResource(
                if (isSelected) R.drawable.bg_select_date_time_select else
                    R.drawable.bg_select_date_time
            )
            btn_day.setTextColor(
                if (isSelected) resources.getColor(R.color.md_red_600, null) else
                    resources.getColor(R.color.black, null)
            )
            btn_hour.setTextColor(
                if (!isSelected) resources.getColor(R.color.md_red_600, null) else
                    resources.getColor(R.color.black, null)
            )
            btn_hour.setBackgroundResource(
                if (!isSelected) R.drawable.bg_select_date_time_select else
                    R.drawable.bg_select_date_time
            )

            if (isSelected) {
                calendar_view.visible()
                time_picker_layout.gone()
            } else {
                time_picker_layout.visible()
                calendar_view.gone()
            }
        } else {
            btn_day.setBackgroundResource(R.drawable.bg_select_date_time)
            btn_day.setTextColor(resources.getColor(R.color.black, null))
            btn_hour.setTextColor(resources.getColor(R.color.black, null))
            btn_hour.setBackgroundResource(R.drawable.bg_select_date_time)
            calendar_view.gone()
            time_picker_layout.gone()
        }
    }

    fun getDate(): String? = bookingDate

    fun clearBookingDate() {
        bookingDate = ""
    }

    fun getTime(): String? {
        return if (listSelectTime.isEmpty()) {
            null
        } else {
            positionTime?.let {
                if (it >= 0){
                    "${listSelectTime[it].startTime}"
                }else{
                    null
                }

            }
        }
    }

    fun getTimeTo(): String? {
        return if (listSelectTime.isEmpty()) {
            null
        } else {
            positionTime?.let {
                if (it >= 0){
                    "${listSelectTime[it].endTime}"
                }else{
                    null
                }
            }
        }
    }

    fun validateSelectedTime(): Boolean {
        return try {
            positionTime?.let {
                val from = listSelectTime[it].startTime
                val to = listSelectTime[it].endTime
                var isValid = true
                if (bookingDate?.split("-")?.lastOrNull()?.toIntOrNull() == Calendar.getInstance()
                        .get(Calendar.DAY_OF_MONTH)
                ) {
                    val toSelect = "$bookingDate $to".convertToDate(DATE_FORMAT_3)
                    val now = Calendar.getInstance().time
                    if (toSelect != null && toSelect < now) {
                        isValid = false
                    }
                }
                isValid
            } ?: kotlin.run {
                false
            }
        } catch (e: Exception) {
            false
        }
    }

    private fun checkDegit(number: Int): String =
        if (number <= 9) "0${number}" else number.toString()

    override fun initData() {

    }

    fun setListAppoint(list: List<DateResponse>) {
        val enabledDates: ArrayList<CalendarDay> = arrayListOf()
        list.forEach {
            val arr = it.date.split("/").map { it.toIntOrNull() ?: 0 }
            enabledDates.add(CalendarDay.from(arr[2], arr[1], arr[0]))
        }
        addEnableDayDecorator()
        calendar_view.addDecorator(DayEnableDecorator(enabledDates))

        val paramsLine = rcv_time_schedule.layoutParams
        paramsLine.height = calendar_view.height - 40.dpToPx
        rcv_time_schedule.layoutParams = paramsLine
    }

    fun setTime(list: List<TimeRange>) {
        this.listSelectTime = list
        timeAdapter?.clear()

        if (list.isEmpty()) {
//            time_picker_view.gone()
            rcv_time_schedule.visible()
            tv_no_time_to_book.visible()
        } else {
//            time_picker_view.visible()
            timeAdapter?.addModels(list, false)
            rcv_time_schedule.visible()
            tv_no_time_to_book.gone()
        }

//        time_picker_view.setOptions(
//            PicketOptions.Builder()
//                .linkage(false) // 是否联动
//                .dividedEqually(false) // 每列宽度是否均等分
//                .backgroundColor(Color.WHITE) // 背景颜色
//                .dividerColor(Color.parseColor("#999999")) // 选中项分割线颜色
//                .build()
//        )

        timeAdapter?.onClickItem = { model, position ->
            positionTime = position
            model.startTime?.let { model.endTime?.let { it1 -> onSelectTime.invoke(it, it1) } }
        }
//        time_picker_view.setAdapter(adapter)
//        time_picker_view.setOnItemSelectedListener { parentView, position ->
//            positionTime = position[0]
//            positionTime?.let {
//                btn_hour.text = listSelectTime[it].from
//                onSelectTime.invoke(listSelectTime[it].from)
//            }
//        }
        if (listSelectTime.isNotEmpty()) {
            btn_hour.text = listSelectTime[0].startTime
        }
    }

    override fun initDataFromStyable(mTypedArray: TypedArray?) {

    }

    fun refresh() {
        bookingDate = ""
        time_picker_layout.gone()
        calendar_view.visible()
    }

    fun showDate(isShow: Boolean = true){
        if (isShow) {
            time_picker_layout.gone()
            calendar_view.visible()
            tv_message_date_null.gone()
        } else {
            time_picker_layout.gone()
            calendar_view.invisible()
            tv_message_date_null.visible()
        }
    }

    fun showTime(){
        time_picker_layout.visible()
        calendar_view.gone()
    }

    fun clearTime(){
        positionTime = -1
    }
    private fun addEnableDayDecorator() {
        calendar_view.clearSelection()
        calendar_view.addDecorator(object : DayViewDecorator {
            override fun shouldDecorate(day: CalendarDay?): Boolean {
                return true
            }

            override fun decorate(view: DayViewFacade?) {
                view?.addSpan(
                    ForegroundColorSpan(
                        ContextCompat.getColor(
                            context,
                            R.color.color_1B1B1F
                        )
                    )
                )
            }
        })
    }
    fun clearData() {
        bookingDate = ""
        clearTime()
        timeAdapter?.clear()
    }

    fun activeCurrentDate(selectedDate : String, isActive: Boolean = true){
        bookingDate = selectedDate
        selectedDay = selectedDate.stringToCalendarDay()
        calendar_view?.setCurrentDate(selectedDate)
        setHighlightText(selectedDate.stringToCalendarDay(), isActive)
    }
    private fun setHighlightText(calendarDay: CalendarDay, isActive: Boolean = true) {
        calendar_view?.addDecorator(object : DayViewDecorator {
            override fun shouldDecorate(day: CalendarDay): Boolean {
                return day == calendarDay
            }

            override fun decorate(view: DayViewFacade?) {
                if (isActive) {
                    view?.setBackgroundDrawable(context?.getDrawable(R.drawable.cus_selected_today)!!)
                    view?.addSpan(
                        ForegroundColorSpan(
                            ContextCompat.getColor(
                                context,
                                R.color.color_36A693
                            )
                        )
                    )
                } else {
                    view?.setBackgroundDrawable(context?.getDrawable(R.drawable.bg_calendar_unactive)!!)
                    view?.addSpan(
                        ForegroundColorSpan(
                            ContextCompat.getColor(
                                context,
                                R.color.black
                            )
                        )
                    )
                    if (!isHasSelectDay) {
                        view?.setDaysDisabled(true)
                        view?.addSpan(
                            ForegroundColorSpan(
                                ContextCompat.getColor(
                                    context,
                                    R.color.color_calendar_null
                                )
                            )
                        )
                    }
                }
            }
        })
    }
}