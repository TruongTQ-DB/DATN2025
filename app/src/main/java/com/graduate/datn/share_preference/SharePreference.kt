package com.graduate.datn.share_preference


import com.graduate.datn.entity.User


interface SharePreference {

    fun login(): User
    fun logout()
}