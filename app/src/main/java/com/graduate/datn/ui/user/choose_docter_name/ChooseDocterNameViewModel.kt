package com.graduate.datn.ui.user.choose_docter_name

import com.graduate.datn.base.BaseViewModel
import com.graduate.datn.network.Repository
import com.google.firebase.firestore.DocumentSnapshot
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ChooseDocterNameViewModel @Inject constructor(var repo: Repository) : BaseViewModel(){
    var startIndex: String?= null
    var lastVisibleDocument: DocumentSnapshot? = null

    var addressId: String ?= ""
    var addressName: String ?= ""
    var address: String ?= ""
    var serviceId: String ?= ""
    var optionalServiceId: String ?= ""
    var serviceName: String ?= ""
    var optionalServiceName: String ?= ""
    var optionalServicePrice: String ?= ""
}