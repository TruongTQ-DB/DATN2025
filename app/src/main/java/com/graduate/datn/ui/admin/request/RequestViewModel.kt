package com.graduate.datn.ui.barber_name

import com.graduate.datn.base.BaseViewModel
import com.graduate.datn.network.Repository
import com.google.firebase.firestore.DocumentSnapshot
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class RequestViewModel @Inject constructor(var repo: Repository): BaseViewModel(){
    var startIndex: String?= null
    var lastVisibleDocument: DocumentSnapshot? = null
}