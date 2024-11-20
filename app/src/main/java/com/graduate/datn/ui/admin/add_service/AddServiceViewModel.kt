package com.graduate.datn.ui.admin.add_service

import androidx.lifecycle.MutableLiveData
import com.graduate.datn.adapter.recyclerview.AddressItem
import com.graduate.datn.adapter.recyclerview.ServiceItem
import com.graduate.datn.base.BaseViewModel
import com.graduate.datn.extension.backgroundThread
import com.graduate.datn.network.Repository
import com.graduate.datn.rx.RxBus
import com.graduate.datn.rx.RxEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import java.io.File
import javax.inject.Inject

@HiltViewModel
class AddServiceViewModel @Inject constructor(var repo: Repository) : BaseViewModel() {
    var avatarFile: File? = null
    var imageFromCamera = MutableLiveData<RxEvent.GetImageCameraEvent>()
    var dataAddress = MutableLiveData<List<AddressItem>>()
    var serviceData: ServiceItem ?= null
    var addressId: String = ""

    init {
        mDisposable.add(RxBus.listen(RxEvent.GetImageCameraEvent::class.java)
            .backgroundThread()
            .subscribe(
                {
                    avatarFile = it.file
                    imageFromCamera.value = it
                },
                {

                }
            ))
    }

}