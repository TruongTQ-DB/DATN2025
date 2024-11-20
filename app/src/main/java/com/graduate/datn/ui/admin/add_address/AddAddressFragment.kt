package com.graduate.datn.ui.admin.add_address

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import androidx.fragment.app.activityViewModels
import com.graduate.datn.R
import com.graduate.datn.adapter.recyclerview.AddressItem
import com.graduate.datn.base.BaseFragment
import com.graduate.datn.base.entity.BaseError
import com.graduate.datn.base.permission.PermissionHelper
import com.graduate.datn.custom.dialog.PickImageDialog
import com.graduate.datn.entity.request.AddressRequest
import com.graduate.datn.extension.*
import com.graduate.datn.ui.common.crop.CropImageFragment
import com.graduate.datn.utils.BundleKey
import com.graduate.datn.utils.Constant.TABLE_BOOKING_CLINIC_ADDRESS
import com.graduate.datn.utils.FileUtil
import com.graduate.datn.utils.RealPathUtil
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.sangcomz.fishbun.FishBun
import kotlinx.android.synthetic.main.fragment_add_address.*
import java.io.File

class AddAddressFragment : BaseFragment() {
    private val viewModel: AddAddressModel by activityViewModels()

    private val permissionHelper: PermissionHelper by lazy {
        PermissionHelper()
    }
    private val currentTimeMillis = System.currentTimeMillis()
    private val addressesCollection = Firebase.firestore.collection(TABLE_BOOKING_CLINIC_ADDRESS)
    override fun backFromAddFragment() {
    }

    override val layoutId: Int
        get() = R.layout.fragment_add_address

    override fun initView() {
        viewModel.imageFromCamera.value = null
        viewModel.avatarFile = null
        viewModel.addressData = null
    }

    override fun initData() {
        arguments?.let {
            if (it.containsKey(BundleKey.KEY_ADDRESS_BOOKING_CLINIC)) {
                viewModel.addressData =
                    it.getSerializable(BundleKey.KEY_ADDRESS_BOOKING_CLINIC) as AddressItem?
            }
        }
        viewModel.imageFromCamera.observe(viewLifecycleOwner) {
            if (it != null) {
                imv_avt.loadAvatar(it.file.absolutePath)
            }
        }

        if (viewModel.addressData != null) {
            custom_header.setTitle("Chi tiết Phòng khám")
            btn_create.text = "Cập nhật"
            imv_avt.loadImageUrl(viewModel.addressData?.data?.avata)
            input_barber_shop_name.setText(viewModel.addressData?.data?.name)
            input_barber_shop_address.setText(viewModel.addressData?.data?.address)
            input_barber_shop_phone.setText(viewModel.addressData?.data?.phone)
        }
    }

    override fun initListener() {
        custom_header.onLeftClick = {
            backPressed()
        }

        btn_create.onAvoidDoubleClick {
            showLoading()
            if (validateCreate(input_barber_shop_name.getText(),
                    input_barber_shop_address.getText())
            ) {
                if (viewModel.avatarFile != null) {
                    uploadFile()
                } else {
                    viewModel.addressData?.id?.let { it1 ->
                        val newData = mapOf(
                            "name" to input_barber_shop_name.getText(),
                            "address" to input_barber_shop_address.getText(),
                            "phone" to input_barber_shop_phone.getText().trim()
                        )

                        addressesCollection.document(it1)
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

    private fun uploadFile() {
        val imageReference = Firebase.storage.reference.child("image_$currentTimeMillis.jpg")
        imageReference.putFile(Uri.fromFile(viewModel.avatarFile?.absolutePath?.let {
            File(it)
        })).apply {
            addOnSuccessListener {
                imageReference.downloadUrl.addOnSuccessListener { imageUrl ->
                    Log.d("thiss", imageUrl.toString())

                    val data = AddressRequest(
                        name = input_barber_shop_name.getText(),
                        address = input_barber_shop_address.getText(),
                        avata = imageUrl.toString(),
                        status = 0,
                        phone = input_barber_shop_phone.getText().trim()
                    )
                    if (viewModel.addressData == null) {
                        addressesCollection.add(data)
                            .addOnSuccessListener { documentReference ->
                                toast("Thêm Phòng khám thành công!")
                                backPressed()
                                hideLoading()
                            }
                            .addOnFailureListener { e ->
                                toast(e.toString())
                                hideLoading()
                            }
                    } else {
                        viewModel.addressData?.id?.let { it1 ->
                            val newData = mapOf(
                                "name" to input_barber_shop_name.getText(),
                                "address" to input_barber_shop_address.getText(),
                                "avata" to imageUrl.toString(),
                                "phone" to input_barber_shop_phone.getText().trim()
                            )

                            addressesCollection.document(it1)
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

    private fun validateCreate(name: String, address: String): Boolean {
        if (viewModel.avatarFile?.absolutePath == null && viewModel.addressData == null) {
            toast(R.string.add_address_no_iamge)
            hideLoading()
            return false
        }
        if (TextUtils.isEmpty(name.trim())) {
            toast(R.string.add_address_no_name)
            hideLoading()
            return false
        }
        if (TextUtils.isEmpty(address)) {
            toast(R.string.add_address_no_address)
            hideLoading()
            return false
        }
        if (input_barber_shop_phone.getText().trim().isNullOrEmpty()) {
            toast(R.string.register_input_phone)
            hideLoading()
            return false
        }
        if (!input_barber_shop_phone.getText().trim().validatePhoneInRegister()) {
            toast(R.string.register_input_email_not_phone)
            hideLoading()
            return false
        }
        return true
    }

    override fun backPressed(): Boolean {
        getVC().backFromAddFragment()
        return false
    }

    override fun handleValidateError(throwable: BaseError?) {
        toast(throwable!!.error)
    }

    override fun handleNetworkError(throwable: Throwable?, isShowDialog: Boolean) {
        throwable?.getErrorBody()?.msg?.let { toast(it) }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                FishBun.FISHBUN_REQUEST_CODE -> {
                    val path: ArrayList<Uri> =
                        data?.getParcelableArrayListExtra(FishBun.INTENT_PATH) ?: return
                    val file =
                        File(RealPathUtil.getRealPath(requireContext(), path.firstOrNull()))
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