package com.graduate.datn.ui.common.changePassword

import com.graduate.datn.base.BaseViewModel
import com.graduate.datn.network.Repository
import com.graduate.datn.share_preference.SharePreference
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ChangePasswordViewModel @Inject constructor(var repo: Repository, var sharePre: SharePreference) :
    BaseViewModel() {
    var isLoginByFacebookOrGoogle :Boolean = false
}