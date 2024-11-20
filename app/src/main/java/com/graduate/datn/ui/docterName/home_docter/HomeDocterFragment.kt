package com.graduate.datn.ui.docterName.home_docter

import android.os.Bundle
import com.graduate.datn.R
import com.graduate.datn.base.BaseFragment
import com.graduate.datn.entity.User
import com.graduate.datn.extension.loadImageUrl
import com.graduate.datn.extension.onAvoidDoubleClick
import com.graduate.datn.extension.toast
import com.graduate.datn.ui.docterName.create_time_work_schedule.CreateTimeWorkScheduleFragment
import com.graduate.datn.ui.docterName.container_docter.TabContainerDocterFragment
import com.graduate.datn.utils.BundleKey.KEY_USER_INFOR
import com.graduate.datn.utils.Constant
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.fragment_home_docter.*

class HomeDocterFragment : BaseFragment() {
    private lateinit var mAuth: FirebaseAuth
    private val db = FirebaseFirestore.getInstance()
    private val userCollection = db.collection(Constant.TABLE_USER)
    var userData: User ?= null
    override fun backFromAddFragment() {

    }

    override val layoutId: Int
        get() = R.layout.fragment_home_docter

    override fun initView() {
        mAuth = Firebase.auth
    }

    override fun initData() {
        getUserInfor()
    }

    private fun getUserInfor() {
        showLoading()
        userCollection
            .whereEqualTo("id", mAuth.currentUser?.uid)
            .limit(1)
            .get()
            .addOnSuccessListener {
                if (!it.isEmpty) {
                    hideLoading()
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
        cv_avatar.loadImageUrl(data.avatar)
        tv_name.text = data.name
        userData = data
    }

    override fun initListener() {
        btn_schedule.onAvoidDoubleClick {
            (parentFragment?.parentFragment as? TabContainerDocterFragment)?.moveToTabSchedule()
        }

        btn_customer.onAvoidDoubleClick {
            (parentFragment?.parentFragment as? TabContainerDocterFragment)?.moveToTabCustomer()
        }

        btn_notification.onAvoidDoubleClick {
            (parentFragment?.parentFragment as? TabContainerDocterFragment)?.moveToTabNotification()
        }
        btn_work_schedule.onAvoidDoubleClick {
            getVC().addFragment(CreateTimeWorkScheduleFragment::class.java, Bundle().apply {
                putSerializable(KEY_USER_INFOR, userData)
            })
        }
    }

    override fun backPressed(): Boolean {
        return false
    }

    override fun <U> getObjectResponse(data: U) {
        super.getObjectResponse(data)
        toast(getString(R.string.account_logout_success))
    }

    override fun onResume() {
        super.onResume()
        getUserInfor()
    }
}