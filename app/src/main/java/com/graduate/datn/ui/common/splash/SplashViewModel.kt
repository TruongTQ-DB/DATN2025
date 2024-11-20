package com.graduate.datn.ui.common.splash

import com.graduate.datn.base.BaseViewModel
import com.graduate.datn.entity.User
import com.graduate.datn.share_preference.SharePreference
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(private val sharePreference: SharePreference) :
    BaseViewModel() {
//    fun getToken(): String {
//        return sharePreference.login().token ?: ""
//    }
        var userData: User?= null
}
