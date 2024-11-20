package com.graduate.datn.ui.common.chatDetail

import com.graduate.datn.base.BaseViewModel
import com.graduate.datn.base.entity.BaseObjectResponse
import com.graduate.datn.extension.ObjectResponse
import com.graduate.datn.network.Repository
import com.graduate.datn.share_preference.SharePreference
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ChatDetailModel @Inject constructor(var repo: Repository, var sharePre: SharePreference) :
    BaseViewModel() {
    val logoutData = ObjectResponse<String>()
    fun logout() {
        sharePre.logout()
        mDisposable.add(
            repo.logout()
                .doOnSubscribe {
                    logoutData.value = BaseObjectResponse<String>().loading()
                }.subscribe({
                    logoutData.value = BaseObjectResponse<String>().success("")
                }, {
                    logoutData.value = BaseObjectResponse<String>().error(it)
                })
        )
    }
}