package com.graduate.datn.ui.docterName.customer_information

import com.graduate.datn.base.BaseViewModel
import com.graduate.datn.network.Repository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class CustomerInformationViewModel @Inject constructor(var repo: Repository) : BaseViewModel() {
    var idUser: String? = null

    var phone: String? = null
}