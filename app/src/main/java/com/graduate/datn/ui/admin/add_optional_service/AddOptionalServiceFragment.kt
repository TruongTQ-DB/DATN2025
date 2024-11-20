package com.graduate.datn.ui.admin.add_optional_service

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import androidx.fragment.app.activityViewModels
import com.graduate.datn.R
import com.graduate.datn.adapter.recyclerview.AddressItem
import com.graduate.datn.adapter.recyclerview.OptionalServiceItem
import com.graduate.datn.adapter.recyclerview.ServiceItem
import com.graduate.datn.base.BaseFragment
import com.graduate.datn.base.entity.BaseError
import com.graduate.datn.base.permission.PermissionHelper
import com.graduate.datn.custom.dialog.BottomSheetWheelPicker
import com.graduate.datn.custom.dialog.PickImageDialog
import com.graduate.datn.entity.request.AddOptionalServiceRequest
import com.graduate.datn.entity.response.ListAddressResponse
import com.graduate.datn.entity.response.ServiceResponse
import com.graduate.datn.extension.*
import com.graduate.datn.ui.common.crop.CropImageFragment
import com.graduate.datn.utils.BundleKey
import com.graduate.datn.utils.Constant
import com.graduate.datn.utils.Constant.TABLE_OPTIONAL_SERVICE
import com.graduate.datn.utils.FileUtil
import com.graduate.datn.utils.RealPathUtil
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.sangcomz.fishbun.FishBun
import kotlinx.android.synthetic.main.fragment_add_optional_service.*
import java.io.File

class AddOptionalServiceFragment : BaseFragment() {
    private val viewModel: AddOptionalServiceViewModel by activityViewModels()

    private val permissionHelper: PermissionHelper by lazy {
        PermissionHelper()
    }
    private val currentTimeMillis = System.currentTimeMillis()
    private val addOptionalServiceCollection = Firebase.firestore.collection(TABLE_OPTIONAL_SERVICE)
    private val servicesCollection =
        FirebaseFirestore.getInstance().collection(Constant.TABLE_SERVICE)
    private val addressCollection =
        FirebaseFirestore.getInstance().collection(Constant.TABLE_BOOKING_CLINIC_ADDRESS)
    private lateinit var pickerService: BottomSheetWheelPicker
    override fun backFromAddFragment() {
    }

    override val layoutId: Int
        get() = R.layout.fragment_add_optional_service

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
        arguments?.let {
            if (it.containsKey(BundleKey.KEY_OPTIONAL_SERVICE_SHOP)) {
                viewModel.optionalServiceData =
                    it.getSerializable(BundleKey.KEY_OPTIONAL_SERVICE_SHOP) as OptionalServiceItem?
            }
        }
        viewModel.imageFromCamera.observe(viewLifecycleOwner) {
            if(it != null){
                imv_avt.loadAvatar(it.file.absolutePath)
            }
        }
        viewModel.dataService.observe(viewLifecycleOwner) {
            if (::pickerService.isInitialized) {
                pickerService.addValueObject(it, viewModel.serviceId)
                hideLoading()
            }
        }
        viewModel.dataAddress.observe(viewLifecycleOwner) {
            if (::pickerService.isInitialized) {
                pickerService.addValueObject(it, viewModel.addressId)
                hideLoading()
            }
        }

        if (viewModel.optionalServiceData != null) {
            custom_header.setTitle("Chi tiết Dịch")
            btn_create.text = "Cập nhật"
            imv_avt.loadImageUrl(viewModel.optionalServiceData?.data?.image)
            imv_chevron_right1.gone()
            imv_chevron_right2.gone()
            edt_address.setText(viewModel.optionalServiceData?.data?.addressName)
            edt_service.setText(viewModel.optionalServiceData?.data?.serviceName)
            input_optional_service_name.setText(viewModel.optionalServiceData?.data?.name)
            input_optional_service_price.setText(viewModel.optionalServiceData?.data?.price)
            tv_title_service.text = "Dịch vụ"
            tv_title_address.text = "Phòng khám"
        }

    }

    override fun initListener() {
        custom_header.onLeftClick = {
            backPressed()
        }

        btn_create.onAvoidDoubleClick {
            showLoading()
            if (validateCreate(input_optional_service_name.getText(),
                    edt_service.text.toString(), edt_address.text.toString())
            ) {
                if (viewModel.avatarFile != null) {
                    uploadFile()
                } else {
                    viewModel.optionalServiceData?.id?.let { it1 ->
                        val newData = mapOf(
                            "name" to input_optional_service_name.getText(),
                        )
                        addOptionalServiceCollection.document(it1)
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

        edt_service.onAvoidDoubleClick {
            if (viewModel.optionalServiceData == null){
                showLoading()
                pickerService = BottomSheetWheelPicker(requireContext(), isService = true)
                getDataListService()
                pickerService.show(childFragmentManager, "")
                pickerService.onclickProvincesPicker = { data ->
                    if (data is ServiceItem) {
                        edt_service.setText(data.data.name)
                        viewModel.serviceId = data.id
                    }
                }
            }
        }

        edt_address.onAvoidDoubleClick {
            if (viewModel.optionalServiceData == null) {
                showLoading()
                pickerService = BottomSheetWheelPicker(requireContext(), isAddress = true)
                getDataListAddress()
                pickerService.show(childFragmentManager, "")
                pickerService.onclickProvincesPicker = { data ->
                    if (data is AddressItem) {
                        edt_address.setText(data.data?.name)
                        if (viewModel.addressId != data.id){
                            edt_service.text = null
                            viewModel.addressId = data.id.toString()
                        }
                    }
                }
            }

        }
    }

    private fun getDataListAddress() {
        addressCollection.orderBy("name")
            .get().addOnSuccessListener { documents ->
                hideLoading()
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
                hideLoading()
                toast(R.string.error_400)
            }
    }

    private fun getDataListService() {
        servicesCollection.whereEqualTo("barberShopAddressId", viewModel.addressId)
            .orderBy("name").get()
            .addOnSuccessListener { documents ->
                hideLoading()
                if (documents.isEmpty) {
                    toast("Không có dữ liệu Khoa khám")
                } else {
                    val dataList = documents.map { documentSnapshot ->
                        val id = documentSnapshot.id
                        val data = documentSnapshot.toObject(ServiceResponse::class.java)
                        ServiceItem(id, data)
                    }
                    viewModel.dataService.value = dataList
                }
            }.addOnFailureListener {
                hideLoading()
                Log.d("thiss", it.message.toString())
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
                    val data = AddOptionalServiceRequest(
                        name = input_optional_service_name.getText(),
                        image = imageUrl.toString(),
                        serviceId = viewModel.serviceId,
                        serviceName = edt_service.text.toString(),
                        barberShopAddressId = viewModel.addressId,
                        addressName = edt_address.text.toString(),
                        status = 0,
                        price = input_optional_service_price.getText()
                    )
                    if (viewModel.optionalServiceData == null) {
                        addOptionalServiceCollection.add(data)
                            .addOnSuccessListener { documentReference ->
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
                                "name" to input_optional_service_name.getText(),
                                "image" to imageUrl.toString(),
                            )
                            addOptionalServiceCollection.document(it1)
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

    private fun validateCreate(name: String, service: String, address: String): Boolean {
        if (viewModel.avatarFile?.absolutePath == null && viewModel.optionalServiceData == null) {
            toast(R.string.add_optional_service_no_image)
            hideLoading()
            return false
        }
        if (TextUtils.isEmpty(name.trim())) {
            toast(R.string.add_optional_service_no_name)
            hideLoading()
            return false
        }
        if (TextUtils.isEmpty(address.trim())) {
            toast(R.string.add_optional_service_no_address)
            hideLoading()
            return false
        }
        if (TextUtils.isEmpty(service.trim())) {
            toast(R.string.add_optional_service_no_service)
            hideLoading()
            return false
        }
        if (input_optional_service_price.getText().isNullOrEmpty()) {
            toast("Vui lòng nhâp Giá tiền")
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