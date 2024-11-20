package com.graduate.datn.ui.admin.user_management

import com.graduate.datn.base.BaseViewModel
import com.graduate.datn.network.Repository
import com.google.firebase.firestore.DocumentSnapshot
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ContainerUserManagerViewModel @Inject constructor(var repo: Repository) : BaseViewModel(){
    var lastVisibleDocument: DocumentSnapshot? = null
    var tabLayout: Int ?= 1
    var bookingDate: String ?= ""
    var fillerALL: Boolean = false
}