package com.graduate.datn.ui.user.success_appointment_booking

import com.graduate.datn.base.BaseViewModel
import com.graduate.datn.entity.User
import com.graduate.datn.network.Repository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SuccessAppointmentBookingViewModel @Inject constructor(var repo: Repository) : BaseViewModel(){
    var addressId: String ?= ""
    var addressName: String ?= ""
    var address: String ?= ""
    var serviceId: String ?= ""
    var optionalServiceId: String ?= ""
    var optionalServicePrice: String ?= ""
 //   var barberNameId: User?= null
    var docterNameId: User?= null
    var date: String ?= ""
    var timeFrom: String ?= null
    var timeTo: String ?= null
    var intendTime: String ?= ""
    var user: User?= null
    var bookingId: String ?= null
    var service: String ?= ""
}