package com.graduate.datn.ui.common.account

import android.util.Log
import androidx.fragment.app.activityViewModels
import com.graduate.datn.R
import com.graduate.datn.base.BaseFragment
import com.graduate.datn.entity.User
import com.graduate.datn.extension.loadImageUrl
import com.graduate.datn.extension.onAvoidDoubleClick
import com.graduate.datn.extension.toast
import com.graduate.datn.ui.common.changePassword.ChangePasswordFragment
import com.graduate.datn.ui.common.login.LoginFragment
import com.graduate.datn.ui.common.profile.ProfileFragment
import com.graduate.datn.utils.Constant
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.fragment_account.*
import kotlinx.android.synthetic.main.home_fragment.tv_blood_type
import kotlinx.android.synthetic.main.home_fragment.tv_height
import kotlinx.android.synthetic.main.home_fragment.tv_weight

class AccountFragment : BaseFragment() {
    private val viewModel: AccountViewModel by activityViewModels()
    private lateinit var mAuth: FirebaseAuth
    private val db = FirebaseFirestore.getInstance()
    private val userCollection = db.collection(Constant.TABLE_USER)

    override fun backFromAddFragment() {
        getUserInfor()
    }

    override val layoutId: Int
        get() = R.layout.fragment_account

    override fun initView() {
        mAuth = Firebase.auth
    }

    override fun initData() {
        viewModel.logoutData.observe(viewLifecycleOwner) {
            handleObjectResponse(it)
        }
        getUserInfor()
    }

    private fun getUserInfor() {
        showLoading()
        userCollection
            .whereEqualTo("id", mAuth.currentUser?.uid)
            .limit(1)
            .get()
            .addOnSuccessListener {
                hideLoading()
                if (!it.isEmpty) {
                    it.mapNotNull { documentSnapshot ->
                        val data = documentSnapshot.toObject(User::class.java)
                        setUpInforView(data)
                    }
                }
            }
            .addOnFailureListener{
                hideLoading()
            }
    }

    private fun setUpInforView(data: User) {
        cv_avatar.loadImageUrl(data.avatar )
        tv_name.text = data.name 

      /*  if (data.account == 0) {
            data.height?.let {
                val height = it.toFloatOrNull()?.let { String.format("%.1f", it) } ?: "..."
                val weight = data.weight?.toFloatOrNull()?.let { String.format("%.1f", it) } ?: "..."
                tv_height.text = "$height Cm"
                tv_weight.text = "$weight Kg"
                tv_blood_type.text = "${data.blood_type ?: "..."}"
            } ?: run {

                tv_height.text = "..."
                tv_weight.text = "..."
                tv_blood_type.text = "${data.blood_type ?: "..."}"
            }
        }*/
    }

    override fun initListener() {
        btn_profile.onAvoidDoubleClick {
            getVC().addFragment(ProfileFragment::class.java)
        }
        btn_password.onAvoidDoubleClick {
            getVC().addFragment(ChangePasswordFragment::class.java)
        }
        btn_logout.onAvoidDoubleClick {
            clearToken()
            (parentFragment?.parentFragment as BaseFragment).getVC()
                .replaceFragment(LoginFragment::class.java)
            hideLoading()
        }
        btn_question.onAvoidDoubleClick {
            toast("Chức năng này chưa được kích hoạt. Xin thông cảm!")
        }
        btn_language.onAvoidDoubleClick {
            toast("Chức năng này chưa được kích hoạt. Xin thông cảm!")
        }
        btn_notification.onAvoidDoubleClick {
            toast("Chức năng này chưa được kích hoạt. Xin thông cảm!")
        }
        sw_fingerprint.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                toast("Chức năng này chưa được kích hoạt. Xin thông cảm!")
                sw_fingerprint.isChecked = false
            }
        }
    }

    private fun Logout() {
        mAuth.signOut()
    }

    private fun clearToken() {
        mAuth.currentUser?.let {
            FirebaseFirestore.getInstance().collection(Constant.TABLE_USER)
                .whereEqualTo("id", mAuth.currentUser!!.uid)
                .get()
                .addOnSuccessListener { querySnapshot ->
                    if (querySnapshot.isEmpty) {
                        Log.d("FCM", "Không có id này")
                    } else {
                        for (document in querySnapshot.documents) {
                            document.reference.update("token", FieldValue.delete())
                                .addOnSuccessListener {
                                    Logout()
                                    getVC().replaceFragment(LoginFragment::class.java)
                                    Log.d("FCM", "clear token")
                                }
                                .addOnFailureListener { e ->
                                    toast("Đăng xuất thất bại")
                                    Log.d("FCM", "clear token fail: $it")
                                }
                        }
                    }
                }
                .addOnFailureListener { e ->
                    Log.d("FCM", "clear token fail: $e")
                }
        }
    }

    override fun backPressed(): Boolean {
        return false
    }

    override fun <U> getObjectResponse(data: U) {
        super.getObjectResponse(data)
        toast(getString(R.string.account_logout_success))
    }
}