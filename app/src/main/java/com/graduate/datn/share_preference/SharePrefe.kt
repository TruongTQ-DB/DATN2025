package com.graduate.datn.share_preference

import android.content.Context
import com.graduate.datn.BaseApplication
import com.graduate.datn.entity.User
import com.graduate.datn.extension.CategotySharePref
import com.graduate.datn.extension.SharePref
import com.graduate.datn.extension.getString
import com.google.gson.Gson
import javax.inject.Inject

class SharePrefe @Inject constructor() : SharePreference {
    override fun login(): User {
        BaseApplication.context.getSharedPreferences(SharePref.MyPref.CategotySharePref,
            Context.MODE_PRIVATE).let {
            return Gson().fromJson(it?.getString(SharePref.KeyPref.CategotySharePref),
                User::class.java) ?: User()
        }
    }

    override fun logout() {
        val sharedPref =
            BaseApplication.context.getSharedPreferences(SharePref.MyPref.CategotySharePref,
                Context.MODE_PRIVATE)
        sharedPref.edit().clear().apply()
    }
}