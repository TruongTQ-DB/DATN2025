package com.graduate.datn.ui.common.notification

import com.graduate.datn.base.BaseViewModel
import com.graduate.datn.network.Repository
import com.google.firebase.firestore.DocumentSnapshot
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class NotificationViewModel @Inject constructor(var repo: Repository) : BaseViewModel(){
    var lastVisibleDocument: DocumentSnapshot? = null

    var isUser: Boolean = false
}