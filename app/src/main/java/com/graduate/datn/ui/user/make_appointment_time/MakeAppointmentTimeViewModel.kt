package com.graduate.datn.ui.user.make_appointment_time

import com.graduate.datn.base.BaseViewModel
import com.graduate.datn.entity.User
import com.graduate.datn.network.Repository
import com.google.firebase.firestore.DocumentSnapshot
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MakeAppointmentTimeViewModel @Inject constructor(var repo: Repository) : BaseViewModel(){
    var lastVisibleDocument: DocumentSnapshot? = null

    var addressId: String ?= ""
    var addressName: String ?= ""
    var address: String ?= ""
    var serviceId: String ?= ""
    var optionalServiceId: String ?= ""
    var serviceName: String ?= ""
    var optionalServiceName: String ?= ""
    var optionalServicePrice: String ?= ""

    var docterNameId: User?= null

    var dateIndex: String ?= ""
    var timeIndex: Int ?= -1

    var checkClickDate = false
    var dateOld: String ? = null
    var timeTo: String ?= null
    var timeFrom: String ?= null
    var intendTime: String ?= null
}