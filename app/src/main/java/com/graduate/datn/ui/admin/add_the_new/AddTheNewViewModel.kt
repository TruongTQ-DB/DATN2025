package com.graduate.datn.ui.admin.add_the_new

import androidx.lifecycle.MutableLiveData
import com.graduate.datn.adapter.recyclerview.OptionalServiceItem
import com.graduate.datn.base.BaseViewModel
import com.graduate.datn.extension.backgroundThread
import com.graduate.datn.network.Repository
import com.graduate.datn.rx.RxBus
import com.graduate.datn.rx.RxEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import java.io.File
import javax.inject.Inject

@HiltViewModel
class AddTheNewViewModel @Inject constructor(var repo: Repository) : BaseViewModel() {
    var avatarFile: File? = null
    var imageFromCamera = MutableLiveData<RxEvent.GetImageCameraEvent>()
    var serviceId: String = ""
    var addressId: String = ""
    var optionalServiceData: OptionalServiceItem? = null

    init {
        mDisposable.add(RxBus.listen(RxEvent.GetImageCameraEvent::class.java).backgroundThread()
            .subscribe({
                avatarFile = it.file
                imageFromCamera.value = it
            }, {

            }))
    }

}