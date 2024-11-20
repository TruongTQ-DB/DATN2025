package com.graduate.datn.ui.docterName.schedule_details

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.graduate.datn.base.BaseViewModel
import com.graduate.datn.entity.request.SendNotificationRequest
import com.graduate.datn.entity.response.BookingResponse
import com.graduate.datn.entity.response.SendNotificationResponse
import com.graduate.datn.network.Repository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ScheduleDetailsViewModel @Inject constructor(var repo: Repository) : BaseViewModel(){
    var idSchedule: String? = null

    var phone: String?= null

    var data: BookingResponse?= null

    val dataNotif = MutableLiveData<SendNotificationResponse>()

    fun sendNotification(request: SendNotificationRequest) {

        mDisposable.add(repo.sendNotification(request)
            .doOnSubscribe {
                Log.d("FCM", it.toString())
            }.subscribe({
                dataNotif.value = it
            }, {
                dataNotif.value = null
            }))
    }
}