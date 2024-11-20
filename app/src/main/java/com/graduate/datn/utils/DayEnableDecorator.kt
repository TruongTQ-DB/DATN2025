package com.graduate.datn.utils

import android.graphics.Color
import android.text.style.ForegroundColorSpan
import com.prolificinteractive.materialcalendarview.CalendarDay
import com.prolificinteractive.materialcalendarview.DayViewDecorator
import com.prolificinteractive.materialcalendarview.DayViewFacade


class DayEnableDecorator constructor(private val dates: Collection<CalendarDay>) : DayViewDecorator {

    override fun shouldDecorate(day: CalendarDay?): Boolean {
        return !dates.contains(day)
    }

    override fun decorate(view: DayViewFacade?) {
        view?.setDaysDisabled(true)
        view?.addSpan(ForegroundColorSpan(Color.parseColor("#d7d7d7")));
    }
}