package com.graduate.datn.ui.common.capture

import android.content.Context
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.graduate.datn.BaseApplication
import com.graduate.datn.base.BaseViewModel
import com.graduate.datn.utils.Define
import com.graduate.datn.utils.FileUtil
import dagger.hilt.android.lifecycle.HiltViewModel
import io.fotoapparat.Fotoapparat
import io.fotoapparat.configuration.CameraConfiguration
import io.fotoapparat.log.logcat
import io.fotoapparat.parameter.ScaleType
import io.fotoapparat.result.BitmapPhoto
import io.fotoapparat.result.PhotoResult
import io.fotoapparat.result.WhenDoneListener
import io.fotoapparat.selector.*
import io.fotoapparat.view.CameraView
import io.fotoapparat.view.FocusView
import java.io.File
import javax.inject.Inject

@HiltViewModel
class CaptureViewModel @Inject constructor(
    private val context: Context,
) : BaseViewModel() {
    var picture = MutableLiveData<File>()
    private lateinit var fotoapparat: Fotoapparat
    private var photoResult: PhotoResult? = null

    var isSwitchCamera: Boolean = false
    var openFlash: Boolean = false

    var limitImage = 0
    var tabSelected = 1

    init {

    }

    fun takePicture() {
        val path =
            File("${BaseApplication.context.getExternalFilesDir(Define.IMAGE_IDENTIFY_PATH)}/${System.currentTimeMillis()}.jpg")
        photoResult = fotoapparat
            .autoFocus()
            .takePicture()

        photoResult?.saveToFile((path))
        photoResult?.toBitmap()
            ?.whenDone(object : WhenDoneListener<BitmapPhoto> {
                override fun whenDone(it: BitmapPhoto?) {
                    it?.let {
                        val bitmap = FileUtil.getCorrectAngleBitmap(path.absolutePath,it.bitmap)
                        picture.value = File(FileUtil.saveImageToExternal(bitmap))
                        fotoapparat.stop()
                    } ?: kotlin.run {
                        Log.e("error", "Couldn't capture photo.")
                    }
                }
            })
    }

    private sealed class Camera(
        val lensPosition: LensPositionSelector,
        val configuration: CameraConfiguration
    ) {

        object Back : Camera(
            lensPosition = back(),
            configuration = CameraConfiguration(
                previewResolution = firstAvailable(
                    wideRatio(highestResolution()),
                    standardRatio(highestResolution())
                ),
                previewFpsRange = highestFps(),
                flashMode = off(),
                frameProcessor = {
                    // Do something with the preview frame
                }
            )
        )
    }

    fun initCamera(context: Context, cameraView: CameraView, focusView: FocusView) {
        fotoapparat = Fotoapparat(
            context = context,
            view = cameraView,
            focusView = focusView,
            logger = logcat(),
            scaleType = ScaleType.CenterCrop,
            lensPosition = Camera.Back.lensPosition,
            cameraConfiguration = Camera.Back.configuration,
            cameraErrorCallback = { Log.e("error", "Camera error: ", it) }
        )
    }

    fun startCamera() {
        fotoapparat.start()
    }

    fun stopCamera() {
        fotoapparat.stop()
    }

    fun switchCamera() {
        fotoapparat.switchTo(if (isSwitchCamera) front() else back(), Camera.Back.configuration)
    }

    fun openFlash() {
        fotoapparat.updateConfiguration(CameraConfiguration(flashMode = if (openFlash) torch() else off()))
    }

    fun checkMaxCountImage(count: Int): Boolean {
        return count < 10
    }

}