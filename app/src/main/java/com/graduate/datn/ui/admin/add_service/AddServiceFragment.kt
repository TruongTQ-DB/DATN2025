package com.graduate.datn.ui.admin.add_service

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import androidx.fragment.app.activityViewModels
import com.graduate.datn.R
import com.graduate.datn.adapter.recyclerview.AddressItem
import com.graduate.datn.adapter.recyclerview.ServiceItem
import com.graduate.datn.base.BaseFragment
import com.graduate.datn.base.entity.BaseError
import com.graduate.datn.base.permission.PermissionHelper
import com.graduate.datn.custom.dialog.BottomSheetWheelPicker
import com.graduate.datn.custom.dialog.PickImageDialog
import com.graduate.datn.entity.request.AddServiceRequest
import com.graduate.datn.entity.response.ListAddressResponse
import com.graduate.datn.extension.*
import com.graduate.datn.ui.common.crop.CropImageFragment
import com.graduate.datn.utils.BundleKey
import com.graduate.datn.utils.Constant
import com.graduate.datn.utils.Constant.TABLE_SERVICE
import com.graduate.datn.utils.FileUtil
import com.graduate.datn.utils.RealPathUtil
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.sangcomz.fishbun.FishBun
import kotlinx.android.synthetic.main.fragment_add_service.*
import java.io.File

class AddServiceFragment : BaseFragment() {
    private val viewModel: AddServiceViewModel by activityViewModels()

    private val permissionHelper: PermissionHelper by lazy {
        PermissionHelper()
    }
    private val currentTimeMillis = System.currentTimeMillis()
    private val serviceCollection = Firebase.firestore.collection(TABLE_SERVICE)
    private val addressCollection =
        FirebaseFirestore.getInstance().collection(Constant.TABLE_BOOKING_CLINIC_ADDRESS)
    private lateinit var pickerAddress: BottomSheetWheelPicker
    override fun backFromAddFragment() {
    }

    override val layoutId: Int
        get() = R.layout.fragment_add_service

    override fun initView() {
        viewModel.imageFromCamera.value = null
        viewModel.avatarFile = null
        viewModel.serviceData = null
    }

    override fun initData() {
        arguments?.let {
            if (it.containsKey(BundleKey.KEY_SERVICE_BARBER_SHOP)) {
                viewModel.serviceData =
                    it.getSerializable(BundleKey.KEY_SERVICE_BARBER_SHOP) as ServiceItem?
            }
        }

        viewModel.imageFromCamera.observe(viewLifecycleOwner) {
            if (it != null) {
                imv_avt.loadAvatar(it.file.absolutePath)
            }
        }
        viewModel.dataAddress.observe(viewLifecycleOwner) {
            if (::pickerAddress.isInitialized) {
                pickerAddress.addValueObject(it, viewModel.addressId)
                hideLoading()
            }
        }
        if (viewModel.serviceData != null) {
            custom_header.setTitle("Chi tiết Dịch vụ")
            btn_create.text = "Cập nhật"
            imv_avt.loadImageUrl(viewModel.serviceData?.data?.image)
            imv_chevron_right1.gone()
            edt_address.setText(viewModel.serviceData?.data?.addressName)
            input_service_name.setText(viewModel.serviceData?.data?.name)
            tv_title_address_name.text = requireContext().getText(R.string.str_service_barber_shop_no_star)
        }
    }

    override fun initListener() {
        custom_header.onLeftClick = {
            backPressed()
        }

        btn_create.onAvoidDoubleClick {
            showLoading()
            if (validateCreate(input_service_name.getText(), edt_address.text.toString())) {
                if (viewModel.avatarFile != null) {
                    uploadFile()
                } else {
                    viewModel.serviceData?.id?.let { it1 ->
                        val newData = mapOf(
                            "name" to input_service_name.getText(),
                        )
                        serviceCollection.document(it1)
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
        edt_address.onAvoidDoubleClick {
            if (viewModel.serviceData == null){
                showLoading()
                pickerAddress = BottomSheetWheelPicker(requireContext(), isAddress = true)
                getDataListAddress()
                pickerAddress.show(childFragmentManager, "")
                pickerAddress.onclickProvincesPicker = { data ->
                    if (data is AddressItem) {
                        edt_address.setText(data.data?.name)
                        viewModel.addressId = data.id.toString()
                    }
                }
            }

        }

    }

    private fun getDataListAddress() {
        addressCollection.orderBy("name")
            .get().addOnSuccessListener { documents ->
                if (documents.isEmpty) {
                    toast("Không có dữ liệu Dịch vụ")
                } else {
                    val dataList = documents.map { documentSnapshot ->
                        val id = documentSnapshot.id
                        val data = documentSnapshot.toObject(ListAddressResponse::class.java)
                        AddressItem(id, data)
                    }
                    viewModel.dataAddress.value = dataList
                }
            }.addOnFailureListener {
                toast(R.string.error_400)
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
                    val data = AddServiceRequest(
                        name = input_service_name.getText(),
                        image = imageUrl.toString(),
                        status = 0,
                        barberShopAddressId = viewModel.addressId,
                        addressName = edt_address.text.toString(),
                    )
                    if (viewModel.serviceData == null) {
                        serviceCollection.add(data)
                            .addOnSuccessListener { documentReference ->
                                toast("Thêm Dịch vụ thành công!")
                                hideLoading()
                                backPressed()
                            }
                            .addOnFailureListener { e ->
                                hideLoading()
                                toast(e.toString())
                            }
                    } else {
                        viewModel.serviceData?.id?.let { it1 ->
                            val newData = mapOf(
                                "name" to input_service_name.getText(),
                                "image" to imageUrl.toString(),
                            )
                            serviceCollection.document(it1)
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
        if (viewModel.avatarFile?.absolutePath == null && viewModel.serviceData == null) {
            toast(R.string.add_service_no_image)
            hideLoading()
            return false
        }
        if (TextUtils.isEmpty(name.trim())) {
            toast(R.string.add_service_no_name)
            hideLoading()
            return false
        }
        if (TextUtils.isEmpty(address.trim())) {
            toast(R.string.add_service_no_address)
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