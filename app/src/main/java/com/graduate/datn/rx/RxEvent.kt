package com.graduate.datn.rx

import java.io.File

class RxEvent {
    class GetImageCameraEvent(val file : File)
    class ChangeInforUser(val isUpdate: Boolean = false)
}