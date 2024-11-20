package com.graduate.datn.ui.admin.customer

import com.graduate.datn.base.BaseViewModel
import com.graduate.datn.network.Repository
import com.google.firebase.firestore.DocumentSnapshot
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ListCustomerViewModel @Inject constructor(var repo: Repository) : BaseViewModel() {
    var lastVisibleDocument: DocumentSnapshot? = null
}