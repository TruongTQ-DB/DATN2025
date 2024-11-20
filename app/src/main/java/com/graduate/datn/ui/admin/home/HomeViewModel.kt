package com.graduate.datn.ui.admin.home

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.graduate.datn.base.BaseViewModel
import com.graduate.datn.base.entity.BaseListResponse
import com.graduate.datn.entity.User
import com.graduate.datn.entity.home.SpecialtysOrService
import com.graduate.datn.extension.ListResponse
import com.graduate.datn.extension.ObjectResponse
import com.graduate.datn.extension.backgroundThread
import com.graduate.datn.network.Repository
import com.graduate.datn.rx.RxBus
import com.graduate.datn.rx.RxEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class HomeViewModel  @Inject constructor(var repo: Repository): BaseViewModel() {
    var dataSpecialty: ListResponse<SpecialtysOrService> = MutableLiveData()
    var dataService: ListResponse<SpecialtysOrService> = MutableLiveData()

    var dataUser: User ?= null
    var reloadData: ObjectResponse<Boolean> = MutableLiveData()

    init {
        mDisposable.add(RxBus.listen(RxEvent.GetImageCameraEvent::class.java)
            .backgroundThread()
            .subscribe(
                {
                    Log.d("thiss", "DDax upastwasdasd")
                },
                {

                }
            )
        )
    }

    fun getDataSpecialtys(){
        mDisposable.add(
            repo.getDataSpecialtys()
                .subscribe(
                    {
                        dataSpecialty.value = BaseListResponse<SpecialtysOrService>().success(it.data)
                    },
                    {
                        dataSpecialty.value = BaseListResponse<SpecialtysOrService>().error(it)
                    }
                )
        )
    }

    fun getDataService(){
        mDisposable.add(
            repo.getDataService()
                .subscribe(
                    {
                        dataService.value = BaseListResponse<SpecialtysOrService>().success(it.data)
                    },
                    {
                        dataService.value = BaseListResponse<SpecialtysOrService>().error(it)
                    }
                )
        )
    }
}