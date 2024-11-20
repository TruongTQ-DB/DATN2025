package com.graduate.datn.ui.common.changePassword

import android.util.Log
import androidx.fragment.app.activityViewModels
import com.graduate.datn.R
import com.graduate.datn.base.BaseFragment
import com.graduate.datn.entity.User
import com.graduate.datn.extension.gone
import com.graduate.datn.extension.onAvoidDoubleClick
import com.graduate.datn.extension.toast
import com.graduate.datn.extension.visible
import com.graduate.datn.utils.Constant
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.fragment_change_password.*
import kotlinx.android.synthetic.main.fragment_profile.btn_update
import kotlinx.android.synthetic.main.fragment_profile.custom_header

class ChangePasswordFragment : BaseFragment() {
    private val viewModel: ChangePasswordViewModel by activityViewModels()
    private val userCollection = FirebaseFirestore.getInstance().collection(Constant.TABLE_USER)
    private lateinit var mAuth: FirebaseAuth
    override fun backFromAddFragment() {

    }

    override val layoutId: Int
        get() = R.layout.fragment_change_password

    override fun initView() {
        mAuth = FirebaseAuth.getInstance()
        viewModel.isLoginByFacebookOrGoogle = false
        checkLoginByGoogleOrFacebook()
    }

    private fun checkLoginByGoogleOrFacebook() {
        mAuth.currentUser?.let {
            userCollection
                .whereEqualTo("id", mAuth.currentUser!!.uid)
                .get()
                .addOnSuccessListener { querySnapshot ->
                    if (querySnapshot.isEmpty) {
                        Log.d("FCM", "Không có id này")
                    } else {
                        val data = querySnapshot.map {
                            it.toObject(User::class.java)
                        }
                        if (data[0].passWord.isNullOrEmpty()){
                            setUpView(false)
                            viewModel.isLoginByFacebookOrGoogle = true
                        }else {
                            setUpView(true)
                            viewModel.isLoginByFacebookOrGoogle = false
                        }
                    }
                }
                .addOnFailureListener {
                    toast("Có lỗi xãy ra!")
                }
        }
    }

    private fun setUpView(show: Boolean) {
        if(show){
            ll_change_password.visible()
            btn_update.visible()
            tv_show_message_not_result.gone()
        } else {
            ll_change_password.gone()
            btn_update.gone()
            toast("Tài khoản Google/Facebook!")
            tv_show_message_not_result.visible()
        }
    }

    override fun initData() {

    }

    override fun initListener() {
        custom_header.onLeftClick = {
            backPressed()
        }
        btn_update.onAvoidDoubleClick {
            if (!validateData()) {
                return@onAvoidDoubleClick
            } else {
                showLoading()
                userCollection
                    .whereEqualTo("id", mAuth.currentUser!!.uid)
                    .whereEqualTo("passWord", input_password_old.getText())
                    .get()
                    .addOnSuccessListener { querySnapshot ->
                        if (querySnapshot.isEmpty) {
                            toast("Mật khẩu cũ không đúng!")
                        } else {
                            querySnapshot.map {
                                it.reference.update("passWord", input_password_old)
                                    .addOnSuccessListener {
                                        hideLoading()
                                        toast("Đổi mật khẩu thành công!")
                                        backPressed()
                                    }.addOnFailureListener { e ->
                                    toast("Đổi mật khẩu thất bại!")
                                    hideLoading()
                                }
                            }
                        }
                        hideLoading()
                    }
                    .addOnFailureListener {
                        hideLoading()
                        toast("Có lỗi xãy ra!")
                    }

            }
        }
    }

    private fun validateData(): Boolean {
        if (!viewModel.isLoginByFacebookOrGoogle){
            if (input_password_old.getText().isEmpty()) {
                toast("Vui lòng nhập Mật khẩu cũ!")
                return false
            }
        }
        return if (input_password_new.getText().isEmpty()) {
            toast("Vui lòng nhập Mật khẩu mới!")
            false
        } else if (input_complete_password.getText().isEmpty()) {
            toast("Vui Lòng nhâp Xác nhận mật khẩu!")
            false
        } else if (input_password_new.getText() != input_complete_password.getText()) {
            toast("Xác nhận mật khẩu và Mật khẩu không giống nhau!")
            false
        } else {
            true
        }
    }

    override fun backPressed(): Boolean {
        getVC().backFromAddFragment()
        return false
    }
}