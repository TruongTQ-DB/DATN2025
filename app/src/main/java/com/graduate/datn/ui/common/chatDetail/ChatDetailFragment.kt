package com.graduate.datn.ui.common.chatDetail

import androidx.fragment.app.activityViewModels
import com.graduate.datn.R
import com.graduate.datn.base.BaseFragment
import com.graduate.datn.extension.onAvoidDoubleClick
import com.graduate.datn.extension.toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.fragment_chat_detail.*

class ChatDetailFragment : BaseFragment() {
    private val viewModel: ChatDetailModel by activityViewModels()
    private lateinit var mAuth: FirebaseAuth
    override fun backFromAddFragment() {

    }

    override val layoutId: Int
        get() = R.layout.fragment_chat_detail

    override fun initView() {
        mAuth = Firebase.auth
    }

    override fun initData() {

    }

    override fun initListener() {
        imv_back.onAvoidDoubleClick {
            backPressed()
        }

    }

    override fun backPressed(): Boolean {
        getVC().backFromAddFragment()
        return false
    }

    override fun <U> getObjectResponse(data: U) {
        super.getObjectResponse(data)
        toast(getString(R.string.account_logout_success))
    }

}