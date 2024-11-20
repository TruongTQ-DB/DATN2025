package com.graduate.datn.ui.user.check_infomation

import com.graduate.datn.base.BaseViewModel
import com.graduate.datn.entity.User
import com.graduate.datn.network.Repository
import com.graduate.datn.ui.user.make_appointment_time.BookingDaytime
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class CheckInformationViewModel @Inject constructor(var repo: Repository) : BaseViewModel(){
    var startIndex: String?= null

    var addressId: String ?= ""
    var addressName: String ?= ""
    var address: String ?= ""
    var serviceId: String ?= ""
    var optionalServiceId: String ?= ""
    var serviceName: String ?= ""
    var optionalServiceName: String ?= ""
    var optionalServicePrice: String ?= ""

    var docterNameId: User?= null
    var date: String ?= ""
    var timeTo: String ?= null
    var timeFrom: String ?= null
    var intendTime: String ?= null
    var user: User?= null
    var indexTime: BookingDaytime ?= null



}