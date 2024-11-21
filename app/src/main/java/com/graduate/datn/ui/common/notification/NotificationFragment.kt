package com.graduate.datn.ui.common.notification

import android.os.Bundle
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.graduate.datn.R
import com.graduate.datn.adapter.recyclerview.NotificationAdapter
import com.graduate.datn.adapter.recyclerview.NotificationItem
import com.graduate.datn.base.BaseFragment
import com.graduate.datn.base.adapter.EndlessLoadingRecyclerViewAdapter
import com.graduate.datn.entity.User
import com.graduate.datn.entity.response.NotificationResponse
import com.graduate.datn.extension.gone
import com.graduate.datn.extension.isNullOrEmpty
import com.graduate.datn.extension.toast
import com.graduate.datn.extension.visible
import com.graduate.datn.ui.docterName.schedule_details.ScheduleDetailsFragment
import com.graduate.datn.utils.BundleKey
import com.graduate.datn.utils.Constant
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.fragment_notification.*

class NotificationFragment : BaseFragment() {
    private val viewModel: NotificationViewModel by activityViewModels()
    private val mAddressAdapter: NotificationAdapter by lazy {
        NotificationAdapter(requireContext())
    }
    private val db = FirebaseFirestore.getInstance()
    private val addressesCollection = db.collection(Constant.TABLE_NOTIFICATION)
    private val userCollection = db.collection(Constant.TABLE_USER)

    private lateinit var auth: FirebaseAuth

    override fun backFromAddFragment() {
        refreshData()
    }

    override val layoutId: Int
        get() = R.layout.fragment_notification

    override fun initView() {
        auth = Firebase.auth
        getUser()
        viewModel.lastVisibleDocument = null
        recycler_view.setAdapter(mAddressAdapter)
        recycler_view.setListLayoutManager(LinearLayoutManager.VERTICAL)
    }

    private fun getUser() {
        userCollection.whereEqualTo("id", auth.currentUser?.uid).limit(1).get()
            .addOnSuccessListener { documents ->
                val userData = documents.mapNotNull { documentSnapshot ->
                    documentSnapshot.toObject(User::class.java)
                }
                if (!userData.isNullOrEmpty()) {
                    viewModel.isUser = userData.first().account == Constant.TYPE_ACCOUNT_CUSTOMER
                    refreshData()
                }
            }
    }

    override fun initData() {

    }

    override fun initListener() {
        header.onLeftClick = {
            backPressed()
        }
        recycler_view.apply {
            setOnRefreshListener {
                recycler_view.enableRefresh(false)
                refreshData()
            }
            setOnLoadingMoreListener(object :
                EndlessLoadingRecyclerViewAdapter.OnLoadingMoreListener {
                override fun onLoadMore() {
                    mAddressAdapter.showLoadingItem(true)
                    showLoading()
                    addressesCollection.whereEqualTo(if (viewModel.isUser) "userId" else "docterId",
                        auth.currentUser?.uid)
                        .orderBy("timestamp", Query.Direction.DESCENDING)
                        .startAfter(viewModel.lastVisibleDocument)
//                        .limit(20)
                        .get()
                        .addOnSuccessListener { documents ->
                            mAddressAdapter.hideLoadingItem()
                            hideLoading()
                            if (documents.isEmpty) {
                                // Không còn dữ liệu để tải thêm
                            } else {
                                val dataList = documents.map { documentSnapshot ->
                                    val id = documentSnapshot.id
                                    val data =
                                        documentSnapshot.toObject(NotificationResponse::class.java)
                                    NotificationItem(id, data)
                                }
                                viewModel.lastVisibleDocument = documents.documents.lastOrNull()
                                mAddressAdapter.addModels(dataList, false)
                            }
                        }.addOnFailureListener {
                            hideLoading()
                            toast(R.string.error_400)
                        }
                }
            })
        }

        mAddressAdapter.onClick = {
            if (it.data.status == 0) {
                addressesCollection.document(it.id).update("status", 1).addOnSuccessListener {
                    // Cập nhật thành công
                }
            }

            getVC().addFragment(ScheduleDetailsFragment::class.java, Bundle().apply {
                putString(BundleKey.ID_SCHEDULE, it.data.bookingId)
            })
        }
    }

    override fun backPressed(): Boolean {
        getVC().backFromAddFragment()
        return false
    }

    private fun refreshData() {
        showLoading()
        mAddressAdapter.clear()
        addressesCollection
            .whereEqualTo(if (viewModel.isUser) "userId" else "docterId", auth.currentUser?.uid)
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .limit(20)
            .get()
            .addOnSuccessListener { documents ->
                hideLoading()
                if (documents.isEmpty) {
                    tv_show_message_not_result.visible()
                } else {
                    tv_show_message_not_result.gone()
                    viewModel.lastVisibleDocument = documents.documents.lastOrNull()
                    val dataList = documents.map { documentSnapshot ->
                        val id = documentSnapshot.id
                        val data = documentSnapshot.toObject(NotificationResponse::class.java)
                        NotificationItem(id, data)
                    }
                    mAddressAdapter.refresh(dataList)
                }
            }.addOnFailureListener {
                hideLoading()
                tv_show_message_not_result.visible()
                toast(R.string.error_400)
            }
    }
}