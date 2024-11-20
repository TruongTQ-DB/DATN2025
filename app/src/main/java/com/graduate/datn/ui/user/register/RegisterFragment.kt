package com.graduate.datn.ui.user.register

import android.os.Bundle
import android.text.TextUtils
import androidx.fragment.app.activityViewModels
import com.graduate.datn.R
import com.graduate.datn.base.BaseFragment
import com.graduate.datn.custom.dialog.BottomSheetDatePicker
import com.graduate.datn.entity.User
import com.graduate.datn.extension.*
import com.graduate.datn.ui.user.container_user.TabContainerUserFragment
import com.graduate.datn.utils.BundleKey
import com.graduate.datn.utils.Constant
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.register_fragment.*

@AndroidEntryPoint
class RegisterFragment : BaseFragment() {
    private val viewModel: RegisterViewModel by activityViewModels()

    private lateinit var mAuth: FirebaseAuth
    private val userCollection = FirebaseFirestore.getInstance().collection(Constant.TABLE_USER)

    override fun backFromAddFragment() {

    }

    override val layoutId: Int
        get() = R.layout.register_fragment

    override fun backPressed(): Boolean {
        getVC().backFromAddFragment()
        return false
    }

    override fun initView() {
        mAuth = FirebaseAuth.getInstance()
    }


    override fun initData() {

    }

    override fun initListener() {
        header.onLeftClick = {
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

        btn_register.onAvoidDoubleClick {
            showLoading()
            if (!validateRegister(
                    input_username.getText(),
                    input_password.getText(),
                    input_confirm_password.getText(),
                    input_name.getText(),
                    input_phone.getText(),
                    edt_birthday.text.toString(),
                    viewModel.gender,
                    edt_address.text.toString())
            ) {
                hideLoading()
                return@onAvoidDoubleClick
            }
            mAuth.createUserWithEmailAndPassword(input_username.getText(), input_password.getText())
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        var user = User(id = mAuth.currentUser?.uid,
                            email = input_username.getText(),
                            passWord = input_password.getText(),
                            account = 0,
                            address = edt_address.text.toString(),
                            name = input_name.getText(),
                            birthday = edt_birthday.text.toString(),
                            gander = viewModel.gender,
                            phone = input_phone.getText())
                        mAuth.currentUser?.uid?.let {
                            hideLoading()
                            userCollection.add(user)
                                .addOnSuccessListener { _ ->
                                    getVC().replaceFragment(TabContainerUserFragment::class.java)
                                }
                                .addOnFailureListener { e ->
                                    toast("Đăng ký tài khoản không thành công!")
                                }
                        }
                    } else {
                        val errorMessage = task.exception?.message
                        println("Lỗi đăng ký: $errorMessage")
                        hideLoading()
                        if (errorMessage == "The email address is already in use by another account.") {
                            toast("Địa chỉ Email đã được sữ dụng")
                        } else {
                            toast(errorMessage.toString())
                        }
                    }
                }
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
            bottomSheetDatePicker?.show(childFragmentManager, "")
            bottomSheetDatePicker.onclickDatePicker = {
                edt_birthday.setText(
                    it.convertToDate(DATE_FORMAT_7)
                        ?.convertToString(DATE_FORMAT_2)
                )
            }
        }
    }

    private fun validateRegister(
        username: String,
        password: String,
        confirmPassword: String,
        name: String,
        phone: String,
        birthday: String,
        gender: Int,
        address: String,
    ): Boolean {
        if (TextUtils.isEmpty(username)) {
            toast(R.string.register_input_email)
            return false
        }

        if (username.length > USER_NAME_MAX_LENGTH_VALIDATION) {
            toast(R.string.register_input_email_max_length)
            return false
        }

        if (username.contains("@") || username.checkHasContainAlphabetCharacter()) {
            if (!(username).isEmailValid()) {
                toast(R.string.register_input_email_not_email)
                return false
            }
        }

        if (TextUtils.isEmpty(password)) {
            toast(R.string.register_input_password)
            return false
        }

        if (password.length > PASSWORD_MAX_LENGTH_VALIDATION) {
            toast(R.string.register_input_password_max_length)
            return false
        }

        if (password.length < PASSWORD_LENGTH_VALIDATION) {
            toast(R.string.register_input_password_min_length)
            return false
        }

        if (!TextUtils.isEmpty(password) && TextUtils.isEmpty(password.trim()) || !password.isPassWordValidRegexSpecialChar()) {
            toast(R.string.register_input_password_not_password)
            return false
        }

        if (TextUtils.isEmpty(confirmPassword)) {
            toast(R.string.register_input_confirm_password)
            return false
        }

        if (password != (confirmPassword)) {
            toast(R.string.register_input_confirm_password_not_match)
            return false
        }

        if (TextUtils.isEmpty(name)) {
            toast(R.string.register_input_name)
            return false
        }

        if (name.length > NAME_MAX_LENGTH_VALIDATION) {
            toast(R.string.register_input_name_max_length)
            return false
        }

        if (TextUtils.isEmpty(phone)) {
            toast(R.string.register_input_phone)
            return false
        }

        if (!phone.validatePhoneInRegister()) {
            toast(R.string.register_input_email_not_phone)
            return false
        }

        if (TextUtils.isEmpty(birthday)) {
            toast(R.string.register_chose_birthday)
            return false
        }

        if (gender == -1) {
            toast(R.string.register_chose_gender)
            return false
        }

        if (TextUtils.isEmpty(address)) {
            toast(R.string.register_input_address)
            return false
        }

        if (address.length > NAME_MAX_LENGTH_VALIDATION) {
            toast(R.string.register_input_address_max_length)
            return false
        }
        return true
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