package com.graduate.datn.ui.admin.add_docter_name

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import androidx.fragment.app.activityViewModels
import com.graduate.datn.R
import com.graduate.datn.adapter.recyclerview.*
import com.graduate.datn.base.BaseFragment
import com.graduate.datn.base.entity.BaseError
import com.graduate.datn.base.permission.PermissionHelper
import com.graduate.datn.custom.dialog.BottomSheetDatePicker
import com.graduate.datn.custom.dialog.BottomSheetWheelPicker
import com.graduate.datn.custom.dialog.PickImageDialog
import com.graduate.datn.entity.OptionalService
import com.graduate.datn.entity.Service
import com.graduate.datn.entity.User
import com.graduate.datn.entity.response.ListAddressResponse
import com.graduate.datn.entity.response.OptionalServiceResponse
import com.graduate.datn.entity.response.ServiceResponse
import com.graduate.datn.extension.*
import com.graduate.datn.ui.common.crop.CropImageFragment
import com.graduate.datn.utils.BundleKey
import com.graduate.datn.utils.Constant
import com.graduate.datn.utils.Constant.TYPE_ACCOUNT_BARBER_NAME
import com.graduate.datn.utils.Constant.TYPE_ACCOUNT_CUSTOMER
import com.graduate.datn.utils.FileUtil
import com.graduate.datn.utils.RealPathUtil
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.sangcomz.fishbun.FishBun
import kotlinx.android.synthetic.main.fragment_add_docter_name.*
import java.io.File

class AddDocterNameFragment : BaseFragment() {
    private val viewModel: AddDocterNameViewModel by activityViewModels()

    private val permissionHelper: PermissionHelper by lazy {
        PermissionHelper()
    }
    private val mAdapter: SelectOptionalServiceAdapter by lazy {
        SelectOptionalServiceAdapter(requireContext())
    }
    private val mServiceAdapter: SelectServiceAdapter by lazy {
        SelectServiceAdapter(requireContext())
    }
    private lateinit var pickerService: BottomSheetWheelPicker
    private val currentTimeMillis = System.currentTimeMillis()
    private val servicesCollection =
        FirebaseFirestore.getInstance().collection(Constant.TABLE_SERVICE)
    private val addressCollection =
        FirebaseFirestore.getInstance().collection(Constant.TABLE_BOOKING_CLINIC_ADDRESS)
    private val optionalServiceCollection =
        FirebaseFirestore.getInstance().collection(Constant.TABLE_OPTIONAL_SERVICE)

    private val userCollection = FirebaseFirestore.getInstance().collection(Constant.TABLE_USER)

    private lateinit var mAuth: FirebaseAuth

    override fun backFromAddFragment() {
    }

    override val layoutId: Int
        get() = R.layout.fragment_add_docter_name

    override fun initView() {
        rcv_optional_service.adapter = mAdapter
        rcv_service.adapter = mServiceAdapter
        viewModel.apply {
            isAddBarber = null
            isUpdateBarber = null
            dataUser = null
            serviceSelected.clear()
            optionalServiceSelected.clear()
        }

        arguments?.let {
            if (it.containsKey(BundleKey.KEY_IS_CREATE_BARBER)) {
                viewModel.isAddBarber = it.getBoolean(BundleKey.KEY_IS_CREATE_BARBER)
            }
            if (it.containsKey(BundleKey.KEY_IS_UPDATE_BARBER)) {
                viewModel.isUpdateBarber = it.getBoolean(BundleKey.KEY_IS_UPDATE_BARBER)
            }
            if (it.containsKey(BundleKey.KEY_DATA_UPDATE_BARBER)) {
                viewModel.dataUser = it.getSerializable(BundleKey.KEY_DATA_UPDATE_BARBER) as User?
            }

        }
        mAuth = FirebaseAuth.getInstance()

        viewModel.apply {
            serviceId = ""
            addressId = ""
            optionalServiceId = ""
            imageFromCamera.value = null
            avatarFile = null
        }
        setUpView()
        setDataUpdate()
    }

    private fun setDataUpdate() {
        if (viewModel.isUpdateBarber != null) {
            tv_title_confirm_password.gone()
            tv_title_password.gone()
            input_password.gone()
            input_confirm_password.gone()
            tv_title_email.text = "Email"
            input_username.setUnFocusable()
            if (viewModel.isUpdateBarber == true) {
                header.setTitle("Chi tiết Bác sĩ")
                viewModel.dataUser.apply {
                    imv_chevron_right_1.gone()
//                    imv_chevron_right_2.gone()
//                    imv_chevron_right_3.gone()
                    tv_choose_address_title.text = "Phòng khám"
//                    tv_choose_service_title.text = "Dịch vụ"
//                    tv_choose_optional_service_title.text = "Dịch vụ tuỳ chọn"
                    input_name.setText(this?.name)
                    imv_avt.loadImageUrl(this?.avatar)
                    input_phone.setText(this?.phone)
                    input_detail_name.setText(this?.detailname)
                    edt_birthday.setText(this?.birthday)
                    input_username.setText(this?.email)
                    input_address.setText(this?.address)
                    this?.gander?.let { setGender(gender = it) }
                    edt_choose_address.setText(this?.addressName)
//                    edt_service.setText(this?.serviceName)
//                    edt_optional_service.setText(this?.optionalServiceName)
                    this?.service?.let { viewModel.serviceSelected.addAll(it) }
                    this?.optionalService?.let { viewModel.optionalServiceSelected.addAll(it) }
                    this?.clinicShopAddressId?.let { viewModel.addressId = it }
                    mAdapter.refresh(viewModel.optionalServiceSelected)
                    mServiceAdapter.refresh(viewModel.serviceSelected)
                }
            } else {
                header.setTitle("Chi tiết bệnh nhân")
                viewModel.dataUser.apply {
                    input_name.setText(this?.name)
                    imv_avt.loadImageUrl(this?.avatar)
                    input_phone.setText(this?.phone)
                    edt_birthday.setText(this?.birthday)
                    input_username.setText(this?.email)
                    input_address.setText(this?.address)
                    this?.gander?.let { setGender(gender = it) }
                }
            }
        } else {
            tv_title_confirm_password.visible()
            tv_title_password.visible()
            input_password.visible()
            input_confirm_password.visible()
        }
    }

    private fun setUpView() {
        if (viewModel.isAddBarber == true || viewModel.isUpdateBarber == true) {
            header.setTitle("Thêm Bác sĩ")
            tv_choose_address_title.visible()
            tv_choose_service_title.visible()
            tv_choose_optional_service_title.visible()
            tv_detail_name.visible()
            input_detail_name.visible()
            cl_choose_address.visible()
            cl_choose_service.visible()
            cl_choose_optional_service.visible()
        } else {
            header.setTitle("Thêm Bệnh nhân")
            viewModel.dataUser.apply {
                tv_detail_name.gone()
                input_detail_name.gone()
                tv_choose_address_title.gone()
                tv_choose_service_title.gone()
                tv_choose_optional_service_title.gone()
                cl_choose_address.gone()
                cl_choose_service.gone()
                cl_choose_optional_service.gone()
            }
        }
    }

    override fun initData() {
        viewModel.imageFromCamera.observe(viewLifecycleOwner) {
            if (it != null) {
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

        viewModel.dataOptionalService.observe(viewLifecycleOwner) {
            if (::pickerService.isInitialized) {
                pickerService.addValueObject(it, viewModel.optionalServiceId)
                hideLoading()
            }
        }
    }

    override fun initListener() {
        header.onLeftClick = {
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
                    input_address.text.toString(),
                    edt_choose_address.text.toString(),
//                    edt_service.text.toString()
                    "", " "
//                    edt_optional_service.text.toString()
                )
            ) {
                if (viewModel.isUpdateBarber == null) {
                    val idAccountOld = mAuth.currentUser?.uid
                    mAuth.createUserWithEmailAndPassword(input_username.getText(),
                        input_password.getText())
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                uploadFile(mAuth.currentUser?.uid)
                                userCollection
                                    .whereEqualTo("id", idAccountOld)
                                    .limit(1)
                                    .get()
                                    .addOnSuccessListener {
                                        val data = it.map { documentSnapshot ->
                                            documentSnapshot.toObject(User::class.java)
                                        }
                                        data.first().email?.let { it1 ->
                                            data.first().passWord?.let { it2 ->
                                                mAuth.signInWithEmailAndPassword(it1, it2)
                                                    .addOnCompleteListener {

                                                    }
                                            }
                                        }
                                    }
                            } else {
                                hideLoading()
                                val errorMessage = task.exception?.message
                                println("Lỗi đăng ký: $errorMessage")
                                if (errorMessage == "The email address is already in use by another account.") {
                                    toast("Địa chỉ Email đã tồn tại!")
                                } else {
                                    toast(errorMessage.toString())
                                }
                            }
                        }
                } else {
                    val service = viewModel.serviceSelected.map { data ->
                        Service(data.serviceId, data.serviceName)
                    }
                    val optionalService = viewModel.optionalServiceSelected.map { data ->
                        OptionalService(data.serviceId,
                            data.optionalServiceId,
                            data.optionalServiceName)
                    }
                    if (viewModel.avatarFile != null) {
                        val imageReference =
                            Firebase.storage.reference.child("image_$currentTimeMillis.jpg")
                        imageReference.putFile(Uri.fromFile(viewModel.avatarFile?.absolutePath?.let {
                            File(it)
                        })).apply {
                            addOnSuccessListener {
                                imageReference.downloadUrl.addOnSuccessListener { imageUrl ->
                                    Log.d("thiss", imageUrl.toString())
                                    val userData = mapOf(
                                        "name" to input_name.getText(),
                                        "detailname" to input_detail_name.getText(),
                                        "avatar" to imageUrl.toString(),
                                        "phone" to input_phone.getText(),
                                        "birthday" to edt_birthday.text.toString(),
                                        "email" to input_username.getText(),
                                        "address" to input_address.text.toString(),
                                        "gander" to viewModel.gender,
                                        "service" to service,
                                        "optionalService" to optionalService,
                                    )
                                    viewModel.dataUser?.id?.let { it1 ->
                                        userCollection
                                            .whereEqualTo("id", viewModel.dataUser?.id)
                                            .limit(1)
                                            .get()
                                            .addOnSuccessListener {
                                                it.mapNotNull {
                                                    it.reference.update(userData)
                                                        .addOnSuccessListener { _ ->
                                                            hideLoading()
                                                            toast("Cập nhật thành công!")
                                                            backPressed()
                                                        }
                                                        .addOnFailureListener { e ->
                                                            hideLoading()
                                                            toast("Cập nhật thất bại!")
                                                        }

                                                }
                                            }
                                    }
                                }
                            }
                        }
                    } else {
                        val userData = mapOf(
                            "name" to input_name.getText(),
                            "detailname" to input_detail_name.getText(),
                            "phone" to input_phone.getText(),
                            "birthday" to edt_birthday.text.toString(),
                            "email" to input_username.getText(),
                            "address" to input_address.text.toString(),
                            "gander" to viewModel.gender,
                            "service" to service,
                            "optionalService" to optionalService,
                        )
                        viewModel.dataUser?.id?.let { it1 ->
                            userCollection
                                .whereEqualTo("id", viewModel.dataUser?.id)
                                .limit(1)
                                .get()
                                .addOnSuccessListener {
                                    it.mapNotNull {
                                        it.reference.update(userData)
                                            .addOnSuccessListener { _ ->
                                                hideLoading()
                                                toast("Cập nhật thành công!")
                                                backPressed()
                                            }
                                            .addOnFailureListener { e ->
                                                hideLoading()
                                                toast("Cập nhật thất bại!")
                                            }

                                    }
                                }
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


        tv_add_service.onAvoidDoubleClick {
            showLoading()
            pickerService = BottomSheetWheelPicker(requireContext(), isService = true)
            getDataListService()
            pickerService.show(childFragmentManager, "")
            pickerService.onclickProvincesPicker = { data ->
                if (data is ServiceItem) {
//                        edt_service.setText(data.data.name)
//                        if (viewModel.serviceId != data.id) {
//                            resetSelect(isOptionalService = true)
//                            viewModel.serviceId = data.id
//                        }
                    if (viewModel.serviceSelected.filter { it.serviceId == data.id }
                            .isNullOrEmpty()) {
                        val dataService = Service(data.id, data.data.name ?: "")
                        mServiceAdapter.addModel(dataService, true)
                        viewModel.serviceId = data.id
                        viewModel.serviceSelected.add(dataService)
                    } else {
                        toast("Bạn đã chọn khoa khám này trước đó!")
                    }
                }
                hintKeyBoard()
            }
            pickerService.onclickCancel = {
                hintKeyBoard()
            }
        }

        edt_choose_address.onAvoidDoubleClick {
            if (viewModel.isUpdateBarber == null) {
                showLoading()
                pickerService = BottomSheetWheelPicker(requireContext(), isAddress = true)
                getDataListAddress()
                pickerService.show(childFragmentManager, "")
                pickerService.onclickProvincesPicker = { data ->
                    if (data is AddressItem) {
                        edt_choose_address.setText(data.data?.name)
                        if (viewModel.addressId != data.id) {
                            resetSelect(isService = true, isOptionalService = true)
                            viewModel.addressId = data.id.toString()
                        }
                    }
                    hintKeyBoard()
                }
                pickerService.onclickCancel = {
                    hintKeyBoard()
                }
            }
        }

        tv_add_optional_servive.onAvoidDoubleClick {
            showLoading()
            pickerService = BottomSheetWheelPicker(requireContext(), isOptionalService = true)
            getDataListOptionalService()
            pickerService.show(childFragmentManager, "")
            pickerService.onclickProvincesPicker = { data ->
                if (data is OptionalServiceItem) {
//                        edt_optional_service.setText(data.data.name)
                    if (viewModel.optionalServiceSelected.filter { it.optionalServiceId == data.id }
                            .isNullOrEmpty()) {
                        val dataOptionalService = OptionalService(
                            data.data.serviceId ?: "",
                            data.id,
                            data.data.name ?: "")
                        mAdapter.addModel(dataOptionalService, true)
                        viewModel.optionalServiceId = data.id
                        viewModel.optionalServiceSelected.add(dataOptionalService)
                    } else {
                        toast("Bạn đã chọn Khoa khám tuỳ chọn này trước đó!")
                    }
                }
                hintKeyBoard()
            }
            pickerService.onclickCancel = {
                hintKeyBoard()
            }
        }

        mServiceAdapter.onDeleteService = { data, positon ->
            mServiceAdapter.removeModel(positon)
            viewModel.serviceSelected.remove(data)
            resetSelect(isService = false, isOptionalService = true)
            val dataOptionalService =
                viewModel.optionalServiceSelected.filter { it.serviceId != data.serviceId }.toList()
            mAdapter.clear()
            viewModel.optionalServiceSelected.clear()
            if (!dataOptionalService.isNullOrEmpty()) {
                viewModel.optionalServiceSelected.addAll(dataOptionalService)
                mAdapter.refresh(dataOptionalService)
            }
        }

        mAdapter.onDeleteOptionalService = { data, positon ->
            mAdapter.removeModel(positon)
            viewModel.optionalServiceSelected.remove(data)
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

//        type_service.onClick = {
//            resetSelect(isService = true, isOptionalService = true)
//        }
    }

    private fun getDataListOptionalService() {
        val serviceId = viewModel.serviceSelected.map { it.serviceId }
        if (!serviceId.isNullOrEmpty()) {
            optionalServiceCollection
                .whereEqualTo("barberShopAddressId", viewModel.addressId)
                .whereIn("serviceId", serviceId)
                .orderBy("name").get()
                .addOnSuccessListener { documents ->
                    hideLoading()
                    if (documents.isEmpty) {
                        if (edt_choose_address.text.toString().isNullOrEmpty()) {
                            toast(R.string.chose_cli_address)
                        } else if (viewModel.serviceSelected.isNullOrEmpty()) {
                            toast(R.string.chose_service)
                        } else {
                            toast("Không có Dịch vụ!")
                        }
                    } else {
                        val dataList = documents.map { documentSnapshot ->
                            val id = documentSnapshot.id
                            val data =
                                documentSnapshot.toObject(OptionalServiceResponse::class.java)
                            OptionalServiceItem(id, data)
                        }
                        viewModel.dataOptionalService.value = dataList
                    }
                }.addOnFailureListener {
                    hideLoading()
                    Log.d("thiss", it.message.toString())
                    toast(R.string.error_400)
                }
        } else {
            if (edt_choose_address.text.toString().isNullOrEmpty()) {
                toast(R.string.chose_cli_address)
            } else if (viewModel.serviceSelected.isNullOrEmpty()) {
                toast(R.string.chose_service)
            }
            hideLoading()
        }
    }

    private fun getDataListService() {
        servicesCollection
            .whereEqualTo("barberShopAddressId", viewModel.addressId)
            .orderBy("name").get()
            .addOnSuccessListener { documents ->
                hideLoading()
                if (documents.isEmpty) {
                    if (edt_choose_address.text.toString().isNullOrEmpty()) {
                        toast(R.string.chose_cli_address)
                    } else {
                        toast("Không có Khoa khám!")
                    }
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

    private fun getDataListAddress() {
        addressCollection.orderBy("name")
            .get().addOnSuccessListener { documents ->
                hideLoading()
                if (documents.isEmpty) {
                    toast("Không có Phòng khám!")
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

    private fun uploadFile(id: String?) {
        val imageReference = Firebase.storage.reference.child("image_$currentTimeMillis.jpg")
        imageReference.putFile(Uri.fromFile(viewModel.avatarFile?.absolutePath?.let {
            File(it)
        })).apply {
            addOnSuccessListener {
                imageReference.downloadUrl.addOnSuccessListener { imageUrl ->
                    Log.d("thiss", imageUrl.toString())
                    val service = viewModel.serviceSelected.map { data ->
                        Service(data.serviceId, data.serviceName)
                    }
                    val optionalService = viewModel.optionalServiceSelected.map { data ->
                        OptionalService(data.serviceId,
                            data.optionalServiceId,
                            data.optionalServiceName)
                    }
                    val userData = if (viewModel.isAddBarber == true) {
                        User(
                            id = id,
                            name = input_name.getText(),
                            avatar = imageUrl.toString(),
                            account = TYPE_ACCOUNT_BARBER_NAME,
                            addressName = edt_choose_address.text.toString(),
                            clinicShopAddressId = viewModel.addressId,
//                            serviceName = edt_service.text.toString(),
//                            serviceId = viewModel.serviceId,
                            service = service,
                            optionalService = optionalService,
                            phone = input_phone.getText(),
                            gander = viewModel.gender,
                            birthday = edt_birthday.text.toString(),
                            passWord = input_password.getText(),
                            email = input_username.getText(),
//                            optionalServiceId = viewModel.optionalServiceId,
//                            optionalServiceName = edt_optional_service.text.toString(),
                            address = input_address.text.toString()
                        )
                    } else {
                        User(
                            id = id,
                            name = input_name.getText(),
                            avatar = imageUrl.toString(),
                            account = TYPE_ACCOUNT_CUSTOMER,
                            phone = input_phone.getText(),
                            gander = viewModel.gender,
                            birthday = edt_birthday.text.toString(),
                            passWord = input_password.getText(),
                            email = input_username.getText(),
                            address = input_address.text.toString()
                        )
                    }
                    mAuth.currentUser?.uid?.let {
                        userCollection.add(userData)
                            .addOnSuccessListener { _ ->
                                hideLoading()
                                toast("Thêm bác sĩ thành công!")
                                backPressed()
                            }
                            .addOnFailureListener { e ->
                                hideLoading()
                                toast("Thêm bác sĩ không thành công!")
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
        barberShopAddress: String,
        serviceName: String,
        optionalServiceName: String,
    ): Boolean {
        if (viewModel.avatarFile?.absolutePath == null && viewModel.isUpdateBarber == null) {
            toast(R.string.add_service_no_image)
            hideLoading()
            return false
        }
        if (TextUtils.isEmpty(userName.trim()) && viewModel.isUpdateBarber == null) {
            toast(R.string.input_email)
            hideLoading()
            return false
        }
        if (userName.length > USER_NAME_MAX_LENGTH_VALIDATION && viewModel.isUpdateBarber == null) {
            toast(R.string.register_input_email_max_length)
            hideLoading()
            return false
        }

        if (userName.contains("@") || userName.checkHasContainAlphabetCharacter() && viewModel.isUpdateBarber == null) {
            if (!(userName).isEmailValid()) {
                toast(R.string.register_input_email_not_email)
                hideLoading()
                return false
            }
        }
        if (TextUtils.isEmpty(password.trim()) && viewModel.isUpdateBarber == null) {
            toast(R.string.input_password)
            hideLoading()
            return false
        }
        if (password.length > PASSWORD_MAX_LENGTH_VALIDATION && viewModel.isUpdateBarber == null) {
            toast(R.string.register_input_password_max_length)
            hideLoading()
            return false
        }

        if (password.length < PASSWORD_LENGTH_VALIDATION && viewModel.isUpdateBarber == null) {
            toast(R.string.register_input_password_min_length)
            hideLoading()
            return false
        }
        if (TextUtils.isEmpty(confirmPassword.trim()) && viewModel.isUpdateBarber == null) {
            toast(R.string.confirm_password)
            hideLoading()
            return false
        }
        if (password != (confirmPassword) && viewModel.isUpdateBarber == null) {
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
        if (TextUtils.isEmpty(barberShopAddress.trim()) && viewModel.isAddBarber == true) {
            toast(R.string.chose_cli_address)
            hideLoading()
            return false
        } //        if (TextUtils.isEmpty(serviceName.trim()) && viewModel.isAddBarber == true) {
        if (viewModel.serviceSelected.isNullOrEmpty() && viewModel.isAddBarber == true) {
            toast(R.string.chose_service)
            hideLoading()
            return false
        }
        if (viewModel.optionalServiceSelected.isNullOrEmpty() && viewModel.isAddBarber == true) {
            toast(R.string.chose_optional_service)
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

    private fun resetSelect(
        isService: Boolean = false,
        isOptionalService: Boolean = false,
    ) {
        if (isService) {
            viewModel.apply {
                serviceId = ""
                serviceSelected.clear()
                mServiceAdapter.clear()
            }
        }
        if (isOptionalService) {
            viewModel.apply {
                optionalServiceId = ""
                mAdapter.clear()
                optionalServiceSelected.clear()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        hintKeyBoard()
    }
}