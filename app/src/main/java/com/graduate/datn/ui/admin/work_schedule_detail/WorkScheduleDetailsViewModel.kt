package com.graduate.datn.ui.admin.work_schedule_detail

import com.graduate.datn.base.BaseViewModel
import com.graduate.datn.network.Repository
import com.graduate.datn.ui.admin.list_work_schedule.DateToWorkItem
import com.google.firebase.firestore.DocumentSnapshot
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class WorkScheduleDetailsViewModel @Inject constructor(var repo: Repository) : BaseViewModel(){
    var lastVisibleDocument: DocumentSnapshot? = null
    var data: DateToWorkItem ?= null
    var isApprove: Boolean?= false
}