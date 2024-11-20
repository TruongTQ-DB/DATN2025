package com.graduate.datn.ui.admin.add_the_new

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.activityViewModels
import com.graduate.datn.R
import com.graduate.datn.base.BaseFragment
import com.graduate.datn.base.permission.PermissionHelper
import com.graduate.datn.custom.dialog.PickImageDialog
import com.graduate.datn.entity.request.TheNewRequest
import com.graduate.datn.extension.*
import com.graduate.datn.ui.common.crop.CropImageFragment
import com.graduate.datn.utils.BundleKey
import com.graduate.datn.utils.Constant
import com.graduate.datn.utils.FileUtil
import com.graduate.datn.utils.RealPathUtil
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.sangcomz.fishbun.FishBun
import kotlinx.android.synthetic.main.fragment_add_optional_service.*
import kotlinx.android.synthetic.main.fragment_add_optional_service.btn_create
import kotlinx.android.synthetic.main.fragment_add_optional_service.cl_avt
import kotlinx.android.synthetic.main.fragment_add_the_new.*
import kotlinx.android.synthetic.main.fragment_add_the_new.imv_avt
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class AddTheNewFragment : BaseFragment() {
    private val viewModel: AddTheNewViewModel by activityViewModels()
    private val permissionHelper: PermissionHelper by lazy {
        PermissionHelper()
    }
    private val currentTimeMillis = System.currentTimeMillis()
    private val db = FirebaseFirestore.getInstance()
    private val theNewCollection = db.collection(Constant.TABLE_THE_NEW)

    override fun backFromAddFragment() {
    }

    override val layoutId: Int
        get() = R.layout.fragment_add_the_new


    override fun initView() {
        viewModel.apply {
            imageFromCamera.value = null
            avatarFile = null
            serviceId = ""
            addressId = ""
            optionalServiceData = null
        }

    }

    override fun initData() {
        viewModel.imageFromCamera.observe(viewLifecycleOwner) {
            if(it != null){
                imv_avt.loadAvatar(it.file.absolutePath)
            }
        }
    }

    override fun initListener() {
        header.onLeftClick = {
            backPressed()
        }

        ct_content.onAvoidDoubleClick {
            input_content.requestFocus()
        }

        btn_create.onAvoidDoubleClick {
            showLoading()
            if (validateCreate()) {
                if (viewModel.avatarFile != null) {
                    uploadFile()
                } else {
                    viewModel.optionalServiceData?.id?.let { it1 ->
                        val newData = mapOf(
                            "title" to input_title.text.toString().trim(),
                            "content" to input_content.text.toString().trim(),
                            "date" to getDayString(),
                            "timestamp" to getCurrentDaystamp(),
                        )
                        theNewCollection.document(it1)
                            .update(newData)
                            .addOnSuccessListener {
                                toast("Cập nhật thành công!")
                                hideLoading()
                                backPressed()
                            }
                            .addOnFailureListener { e ->
                                toast(e.toString())
                                hideLoading()
                            }
                    }
                }
            }
        }

        cl_avt.onAvoidDoubleClick {
            val pickImageDialog = PickImageDialog(requireContext())
            pickImageDialog.show(childFragmentManager, "")
            pickImageDialog.onclickCamera = {
                openScreenCropCapture(permissionHelper)
            }

            pickImageDialog.onclickGallery = {
                goToGallery(permissionHelper = permissionHelper, listener = {
                    openImageGallery(true)
                })
            }
        }

    }

    private fun getCurrentDaystamp(): Timestamp? {
        return SimpleDateFormat("dd/MM/yyyy").parse(getDayString())?.let { Timestamp(it) }
    }

    private fun getDayString(): String {
        return SimpleDateFormat("dd/MM/yyyy",
            Locale.getDefault()).format(Calendar.getInstance().time)
    }

    private fun uploadFile() {
        val imageReference = Firebase.storage.reference.child("image_$currentTimeMillis.jpg")
        imageReference.putFile(Uri.fromFile(viewModel.avatarFile?.absolutePath?.let {
            File(it)
        })).apply {
            addOnSuccessListener {
                imageReference.downloadUrl.addOnSuccessListener { imageUrl ->
                    Log.d("thiss", imageUrl.toString())
                    val data = TheNewRequest(
                        title = input_title.text.toString().trim(),
                        image = imageUrl.toString(),
                        status = 0,
                        date = getDayString(),
                        content = input_content.text.toString().trim(),
                        timestamp = getCurrentDaystamp(),
                    )
                    if (viewModel.optionalServiceData == null) {
                        theNewCollection.add(data)
                            .addOnSuccessListener { documentReference ->
                                toast("Thêm tin tức thành công!")
                                hideLoading()
                                backPressed()
                            }
                            .addOnFailureListener { e ->
                                hideLoading()
                                toast(e.toString())
                            }
                    } else {
                        viewModel.optionalServiceData?.id?.let { it1 ->
                            val newData = mapOf(
                                "title" to input_title.text.toString().trim(),
                                "image" to imageUrl.toString(),
                                "content" to input_content.text.toString().trim(),
                                "date" to getDayString(),
                                "timestamp" to getCurrentDaystamp(),
                            )
                            theNewCollection.document(it1)
                                .update(newData)
                                .addOnSuccessListener {
                                    toast("Cập nhật thành công!")
                                    hideLoading()
                                    backPressed()
                                }
                                .addOnFailureListener { e ->
                                    toast(e.toString())
                                    hideLoading()
                                }
                        }
                    }

                }
            }
            addOnFailureListener { exception ->
                hideLoading()
                toast(exception.message.toString())
            }
        }
    }

    private fun validateCreate(): Boolean {
        if (viewModel.avatarFile?.absolutePath == null && viewModel.optionalServiceData == null) {
            toast(R.string.add_optional_service_no_image)
            hideLoading()
            return false
        }
        if (input_title.text.toString().trim().isNullOrEmpty()) {
            toast("Vui lòng nhập tiêu đề Tin tức!")
            hideLoading()
            return false
        }
        if (input_content.text.toString().trim().isNullOrEmpty()) {
            toast("Vui lòng nhập tiêu đề Tin tức!")
            hideLoading()
            return false
        }
        return true
    }

    override fun backPressed(): Boolean {
        getVC().backFromAddFragment()
        return false
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                FishBun.FISHBUN_REQUEST_CODE -> {
                    val path: ArrayList<Uri> =
                        data?.getParcelableArrayListExtra(FishBun.INTENT_PATH) ?: return
                    val file = File(RealPathUtil.getRealPath(requireContext(), path.firstOrNull()))
                    if (!FileUtil.isFileLessThan7MB(file)) {
                        toast("vùi lòng chọn ảnh nhỏ hon 10mb")
                        return
                    }
                    path.firstOrNull()?.let {
                        ((parentFragment as BaseFragment).parentFragment as BaseFragment).getVC()
                            .addFragment(CropImageFragment::class.java, Bundle().apply {
                                putBoolean(BundleKey.KEY_URI_CAPTURE_GALLERY, true)
                                putString(BundleKey.KEY_URI_FROM_GALLERY, it.toString())
                            })
                    }
                }
            }
        }

    }

}