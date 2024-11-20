package com.graduate.datn.ui.common.listChat

import com.graduate.datn.base.BaseViewModel
import com.graduate.datn.network.Repository
import com.graduate.datn.share_preference.SharePreference
import com.google.firebase.firestore.DocumentSnapshot
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ListChatViewModel @Inject constructor(var repo: Repository, var sharePre: SharePreference) :
    BaseViewModel() {
    var lastVisibleDocument: DocumentSnapshot? = null


}