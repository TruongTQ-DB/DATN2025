package com.graduate.datn.ui.admin.add_docter_name

import androidx.lifecycle.MutableLiveData
import com.graduate.datn.adapter.recyclerview.AddressItem
import com.graduate.datn.adapter.recyclerview.OptionalServiceItem
import com.graduate.datn.adapter.recyclerview.ServiceItem
import com.graduate.datn.base.BaseViewModel
import com.graduate.datn.entity.OptionalService
import com.graduate.datn.entity.Service
import com.graduate.datn.entity.User
import com.graduate.datn.extension.backgroundThread
import com.graduate.datn.network.Repository
import com.graduate.datn.rx.RxBus
import com.graduate.datn.rx.RxEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import java.io.File
import javax.inject.Inject

@HiltViewModel
class AddDocterNameViewModel @Inject constructor(var repo: Repository) : BaseViewModel() {
    var avatarFile: File? = null
    var imageFromCamera = MutableLiveData<RxEvent.GetImageCameraEvent>()
    var dataService = MutableLiveData<List<ServiceItem>>()
    var dataAddress = MutableLiveData<List<AddressItem>>()
    var dataOptionalService = MutableLiveData<List<OptionalServiceItem>>()
    var serviceId: String = ""
    var addressId: String = ""
    var optionalServiceId: String = ""
    var optionalServiceSelected = mutableListOf<OptionalService>()
    var serviceSelected = mutableListOf<Service>()
    var gender: Int = -1
    var isAddBarber: Boolean? = null
    var isUpdateBarber: Boolean? = null
    var dataUser: User? = null

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