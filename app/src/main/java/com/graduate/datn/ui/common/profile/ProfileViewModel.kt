package com.graduate.datn.ui.common.profile

import androidx.lifecycle.MutableLiveData
import com.graduate.datn.base.BaseViewModel
import com.graduate.datn.extension.backgroundThread
import com.graduate.datn.network.Repository
import com.graduate.datn.rx.RxBus
import com.graduate.datn.rx.RxEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import java.io.File
import javax.inject.Inject


@HiltViewModel
class ProfileViewModel @Inject constructor(var repo: Repository) : BaseViewModel() {
    var gender: Int = -1

    var avatarFile: File? = null
    var imageFromCamera = MutableLiveData<RxEvent.GetImageCameraEvent>()

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