package com.graduate.datn.ui.admin.add_customer

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import androidx.fragment.app.activityViewModels
import com.graduate.datn.R
import com.graduate.datn.base.BaseFragment
import com.graduate.datn.base.entity.BaseError
import com.graduate.datn.base.permission.PermissionHelper
import com.graduate.datn.custom.dialog.BottomSheetDatePicker
import com.graduate.datn.custom.dialog.BottomSheetWheelPicker
import com.graduate.datn.custom.dialog.PickImageDialog
import com.graduate.datn.entity.request.AddCustomerRequest
import com.graduate.datn.extension.*
import com.graduate.datn.ui.admin.add_docter_name.AddDocterNameViewModel
import com.graduate.datn.ui.common.crop.CropImageFragment
import com.graduate.datn.utils.BundleKey
import com.graduate.datn.utils.Constant.TABLE_USER
import com.graduate.datn.utils.Constant.TYPE_ACCOUNT_CUSTOMER
import com.graduate.datn.utils.FileUtil
import com.graduate.datn.utils.RealPathUtil
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.sangcomz.fishbun.FishBun
import kotlinx.android.synthetic.main.fragment_add_customer.btn_create
import kotlinx.android.synthetic.main.fragment_add_customer.cl_avt
import kotlinx.android.synthetic.main.fragment_add_customer.custom_header
import kotlinx.android.synthetic.main.fragment_add_customer.edt_birthday
import kotlinx.android.synthetic.main.fragment_add_customer.imv_avt
import kotlinx.android.synthetic.main.fragment_add_customer.imv_female
import kotlinx.android.synthetic.main.fragment_add_customer.imv_male
import kotlinx.android.synthetic.main.fragment_add_customer.imv_other
import kotlinx.android.synthetic.main.fragment_add_customer.input_address
import kotlinx.android.synthetic.main.fragment_add_customer.input_confirm_password
import kotlinx.android.synthetic.main.fragment_add_customer.input_name
import kotlinx.android.synthetic.main.fragment_add_customer.input_password
import kotlinx.android.synthetic.main.fragment_add_customer.input_phone
import kotlinx.android.synthetic.main.fragment_add_customer.input_username
import java.io.File

class AddCustomerFragment : BaseFragment() {
    private val viewModel: AddDocterNameViewModel by activityViewModels()

    private val permissionHelper: PermissionHelper by lazy {
        PermissionHelper()
    }
    private lateinit var pickerService: BottomSheetWheelPicker
    private val currentTimeMillis = System.currentTimeMillis()

    private lateinit var mAuth: FirebaseAuth
    private lateinit var database: FirebaseDatabase
    private lateinit var usersReference: DatabaseReference

    override fun backFromAddFragment() {
    }

    override val layoutId: Int
        get() = R.layout.fragment_add_customer

    override fun initView() {
        mAuth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()
        usersReference = database.reference.child(TABLE_USER)

        viewModel.apply {
            serviceId = ""
            addressId = ""
            optionalServiceId = ""
            imageFromCamera.value = null
            avatarFile = null
        }
    }

    override fun initData() {
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

        btn_create.onAvoidDoubleClick {
            showLoading()
            if (validateCreate(
                    input_username.getText(),
                    input_password.getText(),
                    input_confirm_password.getText(),
                    input_name.getText(),
                    input_phone.getText(),
                    edt_birthday.text.toString(),
                    viewModel.gender,
                    input_address.text.toString())
            ) {
                mAuth.createUserWithEmailAndPassword(input_username.getText(),
                    input_password.getText())
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            uploadFile(mAuth.currentUser?.uid)
                        } else {
                            val errorMessage = task.exception?.message
                            println("Lỗi đăng ký: $errorMessage")
                            if (errorMessage == "The email address is already in use by another account") {
                                toast("Địa chỉ Email đã được sữ dụng")
                            } else {
                                toast(errorMessage.toString())
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

        edt_birthday.onAvoidDoubleClick {
            val bottomSheetDatePicker = BottomSheetDatePicker(requireContext(), maxDate = true)
            val bundle = Bundle().apply {
                putString(
                    BundleKey.KEY_DATE_SELECTED,
                    edt_birthday.text.toString().convertToDate(DATE_FORMAT_2)
                        ?.convertToString(DATE_FORMAT_7)
                )
            }
            bottomSheetDatePicker.arguments = bundle
            bottomSheetDatePicker.show(childFragmentManager, "")
            bottomSheetDatePicker.onclickDatePicker = {
                edt_birthday.setText(
                    it.convertToDate(DATE_FORMAT_7)
                        ?.convertToString(DATE_FORMAT_2)
                )
            }
        }

    }

    private fun uploadFile(id: String?) {
        val imageReference = Firebase.storage.reference.child("image_$currentTimeMillis.jpg")
        imageReference.putFile(Uri.fromFile(viewModel.avatarFile?.absolutePath?.let {
            File(it)
        })).apply {
            addOnSuccessListener {
                imageReference.downloadUrl.addOnSuccessListener { imageUrl ->
                    Log.d("thiss", imageUrl.toString())
                    val data = AddCustomerRequest(
                        id = id,
                        name = input_name.getText(),
                        avatar = imageUrl.toString(),
                        isAccount = TYPE_ACCOUNT_CUSTOMER,
                        phone = input_phone.getText(),
                        gander = viewModel.gender,
                        birthday = edt_birthday.text.toString(),
                        passWord = input_password.getText(),
                        email = input_username.getText()
                    )
                    mAuth.currentUser?.uid?.let { it ->
                        database.reference.child(TABLE_USER).child(it).setValue(data)
                            .addOnSuccessListener {
                                toast("Thêm tài khoản thành công!")
                                hideLoading()
                                backPressed()
                            }
                            .addOnFailureListener { error ->
                                hideLoading()
                                toast("${error.message}!")
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

    private fun validateCreate(
        userName: String,
        password: String,
        confirmPassword: String,
        name: String,
        phone: String,
        birthday: String,
        gender: Int,
        address: String,
    ): Boolean {
        if (viewModel.avatarFile?.absolutePath == null) {
            toast(R.string.add_service_no_image)
            hideLoading()
            return false
        }
        if (TextUtils.isEmpty(userName.trim())) {
            toast(R.string.input_email)
            hideLoading()
            return false
        }
        if (userName.length > USER_NAME_MAX_LENGTH_VALIDATION) {
            toast(R.string.register_input_email_max_length)
            hideLoading()
            return false
        }

        if (userName.contains("@") || userName.checkHasContainAlphabetCharacter()) {
            if (!(userName).isEmailValid()) {
                toast(R.string.register_input_email_not_email)
                hideLoading()
                return false
            }
        }
        if (TextUtils.isEmpty(password.trim())) {
            toast(R.string.input_password)
            hideLoading()
            return false
        }
        if (password.length > PASSWORD_MAX_LENGTH_VALIDATION) {
            toast(R.string.register_input_password_max_length)
            hideLoading()
            return false
        }
        if (password.length < PASSWORD_LENGTH_VALIDATION) {
            toast(R.string.register_input_password_min_length)
            hideLoading()
            return false
        }
        if (TextUtils.isEmpty(confirmPassword.trim())) {
            toast(R.string.confirm_password)
            hideLoading()
            return false
        }
        if (password != (confirmPassword)) {
            toast(R.string.register_input_confirm_password_not_match)
            hideLoading()
            return false
        }
        if (TextUtils.isEmpty(name.trim())) {
            toast(R.string.input_name)
            hideLoading()
            return false
        }
        if (name.length > NAME_MAX_LENGTH_VALIDATION) {
            toast(R.string.register_input_name_max_length)
            hideLoading()
            return false
        }
        if (TextUtils.isEmpty(phone.trim())) {
            toast(R.string.input_phone)
            hideLoading()
            return false
        }
        if (!phone.validatePhoneInRegister()) {
            toast(R.string.register_input_email_not_phone)
            hideLoading()
            return false
        }
        if (TextUtils.isEmpty(birthday.trim())) {
            toast(R.string.chose_birthday)
            hideLoading()
            return false
        }
        if (gender == -1) {
            toast(R.string.chose_gender)
            hideLoading()
            return false
        }
        if (TextUtils.isEmpty(address.trim())) {
            toast(R.string.input_address)
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
}