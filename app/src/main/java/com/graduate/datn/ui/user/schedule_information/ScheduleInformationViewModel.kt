package com.graduate.datn.ui.user.schedule_information

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.graduate.datn.base.BaseViewModel
import com.graduate.datn.entity.User
import com.graduate.datn.entity.request.SendNotificationRequest
import com.graduate.datn.entity.response.SendNotificationResponse
import com.graduate.datn.network.Repository
import com.graduate.datn.ui.user.make_appointment_time.BookingDaytime
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ScheduleInformationViewModel @Inject constructor(var repo: Repository) : BaseViewModel(){
    var startIndex: String?= null

    var addressId: String ?= ""
    var addressName: String ?= ""
    var address: String ?= ""
    var serviceId: String ?= ""
    var optionalServiceId: String ?= ""
    var serviceName: String ?= ""
    var optionalServiceName: String ?= ""
    var optionalServicePrice: String ?= ""

    var barberNameId: User?= null
    var date: String ?= ""
    var timeFrom: String ?= null
    var timeTo: String ?= null
    var intendTime: String ?= ""
    var user: User?= null
    var indexTime: BookingDaytime?= null
    var idBooking: String ?= null
    val data = MutableLiveData<SendNotificationResponse>()

    fun sendNotification(request: SendNotificationRequest) {

        mDisposable.add(repo.sendNotification(request)
            .doOnSubscribe {
                Log.d("FCM", it.toString())
            }.subscribe({
                data.value = it
            }, {
                data.value = null
            }))
    }
}