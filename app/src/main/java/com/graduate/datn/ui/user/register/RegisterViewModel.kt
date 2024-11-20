package com.graduate.datn.ui.user.register

import com.graduate.datn.base.BaseViewModel
import com.graduate.datn.network.Repository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(var repo: Repository) : BaseViewModel() {
    var gender: Int = -1
}