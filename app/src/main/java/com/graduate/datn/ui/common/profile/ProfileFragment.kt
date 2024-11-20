package com.graduate.datn.ui.common.profile

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.activityViewModels
import com.graduate.datn.R
import com.graduate.datn.adapter.recyclerview.SelectOptionalServiceAdapter
import com.graduate.datn.adapter.recyclerview.SelectServiceAdapter
import com.graduate.datn.base.BaseFragment
import com.graduate.datn.base.permission.PermissionHelper
import com.graduate.datn.custom.dialog.BottomSheetDatePicker
import com.graduate.datn.custom.dialog.PickImageDialog
import com.graduate.datn.entity.OptionalService
import com.graduate.datn.entity.Service
import com.graduate.datn.entity.User
import com.graduate.datn.extension.*
import com.graduate.datn.rx.RxBus
import com.graduate.datn.rx.RxEvent
import com.graduate.datn.ui.common.crop.CropImageFragment
import com.graduate.datn.utils.BundleKey
import com.graduate.datn.utils.Constant
import com.graduate.datn.utils.FileUtil
import com.graduate.datn.utils.RealPathUtil
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.sangcomz.fishbun.FishBun
import kotlinx.android.synthetic.main.fragment_add_docter_name.input_detail_name
import kotlinx.android.synthetic.main.fragment_add_docter_name.tv_detail_name
import kotlinx.android.synthetic.main.fragment_profile.*
import java.io.File

class ProfileFragment : BaseFragment() {
    private val viewModel: ProfileViewModel by activityViewModels()
    private val userCollection = FirebaseFirestore.getInstance().collection(Constant.TABLE_USER)
    private lateinit var mAuth: FirebaseAuth
    private val currentTimeMillis = System.currentTimeMillis()
    private val permissionHelper: PermissionHelper by lazy {
        PermissionHelper()
    }
    private val mAdapter: SelectOptionalServiceAdapter by lazy {
        SelectOptionalServiceAdapter(requireContext())
    }
    private val mServiceAdapter: SelectServiceAdapter by lazy {
        SelectServiceAdapter(requireContext())
    }

    override fun backFromAddFragment() {

    }

    override val layoutId: Int
        get() = R.layout.fragment_profile

    override fun initView() {
        rcv_optional_service.adapter = mAdapter
        rcv_service.adapter = mServiceAdapter
        mAuth = FirebaseAuth.getInstance()
        viewModel.imageFromCamera.value = null
        viewModel.avatarFile = null
    }

    override fun initData() {
        refreshData()
        viewModel.imageFromCamera.observe(viewLifecycleOwner) {
            if (it != null) {
                imv_avt.loadAvatar(it.file.absolutePath)
            }
        }
    }

    override fun initListener() {
        custom_header.onLeftClick = {
            backPressed()
        }
        imv_male.onAvoidDoubleClick {
            setGender(1)
            viewModel.gender = 1
        }

        imv_female.onAvoidDoubleClick {
            setGender(2)
            viewModel.gender = 2
        }

        imv_other.onAvoidDoubleClick {
            setGender(3)
            viewModel.gender = 3
        }

        btn_update.onAvoidDoubleClick {
            showLoading()
            if (!validateData()) {
                hideLoading()
                return@onAvoidDoubleClick
            } else {
                userCollection.whereEqualTo("id", mAuth.currentUser!!.uid).get()
                    .addOnSuccessListener { querySnapshot ->
                        if (querySnapshot.isEmpty) {
                            Log.d("FCM", "Không có id này")
                        } else {
                            if (viewModel.avatarFile == null) {
                                val data = mapOf("name" to input_name.getText(),
                                    "phone" to input_phone.getText(),
                                    "birthday" to edt_birthday.text.toString(),
                                    "gander" to viewModel.gender,
                                    "detailname" to input_detail_name.getText(),
                                    "address" to input_address.text.toString(),
                                    "height" to input_cm.getText(), // Chiều cao
                                    "weight" to input_kg.getText(), // Cân nặng
                                    "blood_type" to input_rh.getText(), // Nhóm máu
                                    "citizen_identity" to input_cnnd.getText(), // CMND
                                    "insurance_id" to input_bhyt.getText()
                                )

                                querySnapshot.map {
                                    it.reference.update(data).addOnSuccessListener {
                                        hideLoading()
                                        toast("Cập nhật thành công!")
                                        backPressed()
                                    }.addOnFailureListener { e ->
                                        toast("Có lỗi xãy ra!")
                                        hideLoading()
                                        Log.d("FCM", "update token fail: $it")
                                    }
                                }
                            } else {
                                val imageReference =
                                    Firebase.storage.reference.child("image_$currentTimeMillis.jpg")
                                imageReference.putFile(Uri.fromFile(viewModel.avatarFile?.absolutePath?.let {
                                    File(it)
                                })).apply {
                                    addOnSuccessListener {
                                        imageReference.downloadUrl.addOnSuccessListener { imageUrl ->
                                            val data = mapOf("avatar" to imageUrl,
                                                "name" to input_name.getText(),
                                                "phone" to input_phone.getText(),
                                                "birthday" to edt_birthday.text.toString(),
                                                "detailname" to input_detail_name.getText(),
                                                "gander" to viewModel.gender,
                                                "address" to input_address.text.toString())
                                            querySnapshot.map {
                                                it.reference.update(data).addOnSuccessListener {
                                                    RxBus.checkChangeInfoSubject.onNext(RxEvent.ChangeInforUser(
                                                        true))
                                                    hideLoading()
                                                    toast("Cập nhật thành công!")
                                                    backPressed()
                                                }.addOnFailureListener { e ->
                                                    toast("Có lỗi xãy ra!")
                                                    hideLoading()
                                                    Log.d("FCM", "update token fail: $it")
                                                }
                                            }
                                        }
                                    }
                                    addOnFailureListener {
                                        hideLoading()
                                        toast("Có lỗi xãy ra!")
                                    }
                                }
                            }
                        }
                    }.addOnFailureListener { e ->
                        toast("Có lỗi xãy ra!")
                        hideLoading()
                        Log.d("FCM", "update token fail: $e")
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
        edt_birthday.onAvoidDoubleClick {
            val bottomSheetDatePicker = BottomSheetDatePicker(requireContext(), maxDate = true)
            val bundle = Bundle().apply {
                putString(BundleKey.KEY_DATE_SELECTED,
                    edt_birthday.text.toString().convertToDate(DATE_FORMAT_2)
                        ?.convertToString(DATE_FORMAT_7))
            }
            bottomSheetDatePicker.arguments = bundle
            bottomSheetDatePicker.show(childFragmentManager, "")
            bottomSheetDatePicker.onclickDatePicker = {
                edt_birthday.setText(it.convertToDate(DATE_FORMAT_7)
                    ?.convertToString(DATE_FORMAT_2))
            }
        }
    }

    private fun validateData(): Boolean {
        return when {
            input_name.getText().isEmpty() -> {
                toast("Vui lòng nhập Họ và tên!")
                false
            }
            input_phone.getText().isEmpty() -> {
                toast("Vui lòng nhập Số điện thoại!")
                false
            }
            !input_phone.getText().validatePhoneInRegister() -> {
                toast(R.string.register_input_email_not_phone)
                false
            }
            edt_birthday.text.toString().isEmpty() -> {
                toast("Vui lòng chọn Ngày sinh!")
                false
            }
            viewModel.gender == -1 -> {
                toast("Vui lòng chọn Giới tính!")
                false
            }
            else -> true
        }
    }

    override fun backPressed(): Boolean {
        getVC().backFromAddFragment()
        return false
    }

    private fun refreshData() {
        showLoading()
        userCollection.whereEqualTo("id", mAuth.currentUser?.uid).get()
            .addOnSuccessListener { documents ->
                hideLoading()
                if (documents.isEmpty) {

                } else {
                    val data = documents.map { documentSnapshot ->
                        documentSnapshot.toObject(User::class.java)
                    }
                    setUpHide(data[0])
                }
            }.addOnFailureListener {
                hideLoading()
                toast("Dữ liệu lỗi!")
            }
    }

    private fun setUpHide(user: User) {
        when (user.account) {
            0 -> {
                setUpView(false)
            }
            1 -> {
                setUpView(true)
            }
            else -> {
                setUpView(false)
            }
        }
        setUpData(user)
    }

    private fun setUpData(user: User) {
        imv_avt.loadImageUrl(user.avatar)
        input_email.setText(user.email)
        input_name.setText(user.name)
        input_phone.setText(user.phone)
        input_cm.setText(user.height)
        input_kg.setText(user.weight)
        input_rh.setText(user.blood_type)
        input_cnnd.setText(user.citizen_identity)
        input_bhyt.setText(user.insurance_id)
        input_detail_name.setText(user.detailname)
        user.gander.let {
            if (it != null) {
                viewModel.gender = it
            }
        }
        input_address.setText(user.address)
        setUpGender(user.gander)
        edt_birthday.setText(user.birthday)
        edt_address.setText(user.addressName)
//        edt_service.setText(user.serviceName)
//        edt_optional_service.setText(user.optionalServiceName)
        val serviceData = user.service?.map { Service(it.serviceId, it.serviceName, true) }
        val optionalServiceData = user.optionalService?.map {
            OptionalService(optionalServiceId = it.optionalServiceId,
                serviceId = it.serviceId,
                optionalServiceName = it.optionalServiceName,
                disable = true)
        }
        optionalServiceData?.let { mAdapter.refresh(it) }
        serviceData?.let { mServiceAdapter.refresh(it) }
    }

    private fun setUpGender(gander: Int?) {
        when (gander) {
            1 -> {
                imv_male.setImageDrawable(requireContext().getDrawable(R.drawable.ic_rbtn_selected))
            }
            2 -> {
                imv_other.setImageDrawable(requireContext().getDrawable(R.drawable.ic_rbtn_selected))
            }
            3 -> {
                imv_female.setImageDrawable(requireContext().getDrawable(R.drawable.ic_rbtn_selected))
            }
            else -> {

            }
        }
    }

    private fun setUpView(show: Boolean) {
        if (show) {
            cl_choose_optional_service.visible()
            tv_choose_optional_service_title.visible()
            cl_choose_service.visible()
            tv_choose_service_title.visible()
            cl_choose_address.visible()
            tv_choose_address_title.visible()
            tv_detail_name_title.visible()
            input_detail_name.visible()
            tv_body_mass.gone()
            layout_cm.gone()
            layout_kg.gone()
            layout_rh.gone()
            tv_cmnd.gone()
            input_cnnd.gone()
            tv_bhyt.gone()
            input_bhyt.gone()
        } else {
            cl_choose_optional_service.gone()
            tv_choose_optional_service_title.gone()
            cl_choose_service.gone()
            tv_choose_service_title.gone()
            cl_choose_address.gone()
            tv_choose_address_title.gone()
            tv_detail_name_title.gone()
            input_detail_name.gone()

            tv_body_mass.visible()
            layout_cm.visible()
            layout_kg.visible()
            layout_rh.visible()
            tv_cmnd.visible()
            input_cnnd.visible()
            tv_bhyt.visible()
            input_bhyt.visible()
        }
    }

    private fun setGender(gender: Int) {
        when (gender) {
            1 -> {
                imv_female.setImageDrawable(requireContext().getDrawable(R.drawable.ic_rbtn_unselected))
                imv_other.setImageDrawable(requireContext().getDrawable(R.drawable.ic_rbtn_unselected))
                imv_male.setImageDrawable(requireContext().getDrawable(R.drawable.ic_rbtn_selected))
                viewModel.gender = 1
            }
            2 -> {
                imv_male.setImageDrawable(requireContext().getDrawable(R.drawable.ic_rbtn_unselected))
                imv_other.setImageDrawable(requireContext().getDrawable(R.drawable.ic_rbtn_unselected))
                imv_female.setImageDrawable(requireContext().getDrawable(R.drawable.ic_rbtn_selected))
                viewModel.gender = 2
            }
            3 -> {
                imv_male.setImageDrawable(requireContext().getDrawable(R.drawable.ic_rbtn_unselected))
                imv_female.setImageDrawable(requireContext().getDrawable(R.drawable.ic_rbtn_unselected))
                imv_other.setImageDrawable(requireContext().getDrawable(R.drawable.ic_rbtn_selected))
                viewModel.gender = 3
            }
            else -> {
                imv_male.setImageDrawable(requireContext().getDrawable(R.drawable.ic_rbtn_unselected))
                imv_female.setImageDrawable(requireContext().getDrawable(R.drawable.ic_rbtn_unselected))
                imv_other.setImageDrawable(requireContext().getDrawable(R.drawable.ic_rbtn_unselected))
                viewModel.gender = -1
            }
        }
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

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray,
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        permissionHelper.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }
}