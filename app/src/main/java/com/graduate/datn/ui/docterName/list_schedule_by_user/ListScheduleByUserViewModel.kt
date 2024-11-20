package com.graduate.datn.ui.docterName.list_schedule_by_user

import com.graduate.datn.base.BaseViewModel
import com.graduate.datn.network.Repository
import com.google.firebase.firestore.DocumentSnapshot
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ListScheduleByUserViewModel @Inject constructor(var repo: Repository) : BaseViewModel(){
    var lastVisibleDocument: DocumentSnapshot? = null

    var userId: String ?= null
}