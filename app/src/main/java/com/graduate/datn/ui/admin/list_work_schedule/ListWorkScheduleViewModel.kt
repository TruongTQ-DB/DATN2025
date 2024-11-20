package com.graduate.datn.ui.admin.list_work_schedule

import com.graduate.datn.base.BaseViewModel
import com.graduate.datn.network.Repository
import com.google.firebase.firestore.DocumentSnapshot
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ListWorkScheduleViewModel @Inject constructor(var repo: Repository) : BaseViewModel(){
    var lastVisibleDocument: DocumentSnapshot? = null
}