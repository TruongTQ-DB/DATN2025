package com.graduate.datn.ui.user.chooseOptionalService

import com.graduate.datn.base.BaseViewModel
import com.graduate.datn.network.Repository
import com.google.firebase.firestore.DocumentSnapshot
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ChooseOptionalServiceViewModel @Inject constructor(var repo: Repository) : BaseViewModel(){
    var lastVisibleService: DocumentSnapshot? = null
    var lastVisibleOptionService: DocumentSnapshot? = null

    var addressId: String ?= ""
    var addressName: String ?= ""
    var address: String ?= ""
    var serviceId: String ?= ""
    var serviceName: String ?= ""
}