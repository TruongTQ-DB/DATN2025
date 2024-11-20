package com.graduate.datn.ui.common.login

import android.content.Context
import android.text.TextUtils
import android.util.Log
import com.graduate.datn.BaseApplication
import com.graduate.datn.R
import com.graduate.datn.base.BaseViewModel
import com.graduate.datn.base.entity.BaseError
import com.graduate.datn.base.entity.BaseObjectResponse
import com.graduate.datn.entity.response.LoginResponce
import com.graduate.datn.extension.CategotySharePref
import com.graduate.datn.extension.ObjectResponse
import com.graduate.datn.extension.SharePref
import com.graduate.datn.network.Repository
import com.google.gson.Gson
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(var repo: Repository) : BaseViewModel() {
    val loginData = ObjectResponse<LoginResponce>()
    fun login(userName: String, password: String) {
        if (TextUtils.isEmpty(userName)) {
            loginData.value =
                BaseObjectResponse<LoginResponce>().error(BaseError(R.string.login_validate_user_name),
                    false)
            return
        }
        if (TextUtils.isEmpty(password)) {
            loginData.value =
                BaseObjectResponse<LoginResponce>().error(BaseError(R.string.login_validate_password),
                    false)
            return
        }

        mDisposable.add(repo.login(userName, password)
            .doOnSubscribe {
                loginData.value = BaseObjectResponse<LoginResponce>().loading()
            }.subscribe({
                saveInfoLogin(it.data!!)
                Log.d("this", it.data.toString())
                loginData.value = BaseObjectResponse<LoginResponce>().success(it.data)
            }, {
                Log.d("this", it.message.toString())
                loginData.value = BaseObjectResponse<LoginResponce>().error(it)
            }))
    }

    private fun saveInfoLogin(data: LoginResponce) {
        BaseApplication.context.getSharedPreferences(SharePref.MyPref.CategotySharePref,
            Context.MODE_PRIVATE).let {
            it.edit().apply {
                putString(SharePref.MyPref.CategotySharePref, Gson().toJson(data))
                apply()
            }
        }
    }

}