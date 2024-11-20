package com.graduate.datn.ui.admin.work_schedule

import androidx.lifecycle.MutableLiveData
import com.graduate.datn.base.BaseViewModel
import com.graduate.datn.entity.User
import com.graduate.datn.network.Repository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class CreateWorkScheduleViewModel @Inject constructor(var repo: Repository) : BaseViewModel() {

    var dataStaff = MutableLiveData<List<User>>()
    var dataTimeSchedule = mutableListOf<DateWorkResponse>()
    var staffId: String = ""
    var avatar: String = ""
    var name: String = ""
    var scheduleTimeValue: ScheduleTime ?= null
    var alreadyTime: Boolean ?= false
}