package com.graduate.datn.ui.docterName.create_time_work_schedule

import com.graduate.datn.base.BaseViewModel
import com.graduate.datn.network.Repository
import com.graduate.datn.ui.admin.work_schedule.DateWorkResponse
import com.graduate.datn.ui.admin.work_schedule.ScheduleTime
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class CreateTimeWorkScheduleViewModel @Inject constructor(var repo: Repository) : BaseViewModel() {
    var dataTimeSchedule = mutableListOf<DateWorkResponse>()
    var avatar: String = ""
    var name: String = ""
    var scheduleTimeValue: ScheduleTime?= null
    var alreadyTime: Boolean ?= false
}