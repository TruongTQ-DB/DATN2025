package com.graduate.datn.ui.docterName.list_booking_customer

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.graduate.datn.R
import com.graduate.datn.adapter.recyclerview.BookingItem
import com.graduate.datn.adapter.recyclerview.BookingCustomerAdapter
import com.graduate.datn.base.BaseFragment
import com.graduate.datn.base.adapter.EndlessLoadingRecyclerViewAdapter
import com.graduate.datn.entity.response.BookingResponse
import com.graduate.datn.extension.*
import com.graduate.datn.ui.docterName.customer_information.CustomerInformationFragment
import com.graduate.datn.utils.BundleKey.ID_CUSTOMER_BOOKING
import com.graduate.datn.utils.Constant
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.fragment_list_booking_customer.*

class ListBookingCustomerFragment : BaseFragment() {
    private val viewModel: ListBookingCustomerViewModel by activityViewModels()
    private val mAdapter: BookingCustomerAdapter by lazy {
        BookingCustomerAdapter(requireContext())
    }
    private val db = FirebaseFirestore.getInstance()
    private val bookingCollection = db.collection(Constant.TABLE_BOOKING)
    private lateinit var auth: FirebaseAuth
    var handler = Handler(Looper.getMainLooper())

    override fun backFromAddFragment() {
        refreshData()
    }

    override val layoutId: Int
        get() = R.layout.fragment_list_booking_customer

    override fun initView() {
        auth = Firebase.auth
        viewModel.lastVisibleDocument = null
        recycler_view.setAdapter(mAdapter)
        recycler_view.setListLayoutManager(LinearLayoutManager.VERTICAL)
    }

    override fun initData() {
        refreshData()
    }

    override fun initListener() {
        header.onLeftClick = {
            backPressed()
        }

        tv_show_message_not_result.onAvoidDoubleClick {
            edt_search.setText("")
        }

        edt_search.textChangedListener {
            var delaySearch = 0
            after {
                if (edt_search.text.isNullOrEmpty()) {
                    imv_clear_search.gone()
                    delaySearch = 0
                } else {
                    delaySearch = 800
                    imv_clear_search.visible()
                }
                handler.removeMessages(0)
                handler.postDelayed(Runnable {
                    showLoading()
                   refreshData()
                }, delaySearch.toLong())
            }
        }

        recycler_view.apply {
            setOnRefreshListener {
                recycler_view.enableRefresh(false)
                refreshData()
            }
            setOnLoadingMoreListener(object :
                EndlessLoadingRecyclerViewAdapter.OnLoadingMoreListener {
                override fun onLoadMore() {
                    mAdapter.showLoadingItem(true)
                    showLoading()
                    bookingCollection
                        .whereEqualTo("docterId", auth.currentUser?.uid)
//                        .whereArrayContains("nameUser", edt_search.text.toString())
                        .orderBy("nameUser")
                        .startAfter(viewModel.lastVisibleDocument!!)
                        .limit(20)
                        .get().addOnSuccessListener { documents ->
                            mAdapter.hideLoadingItem()
                            hideLoading()
                            if (documents.isEmpty) {
                                // Không còn dữ liệu để tải thêm
                            } else {
                                val userIdSet = mutableSetOf<String>()
                                val uniqueDataList = mutableListOf<BookingItem>()
                                tv_show_message_not_result.gone()
                                documents.map { documentSnapshot ->
                                    val id = documentSnapshot.id
                                    val data =
                                        documentSnapshot.toObject(BookingResponse::class.java)
                                    val userId = data.userId
                                    if (userId != null && !userIdSet.contains(userId)) {
                                        userIdSet.add(userId)
                                        uniqueDataList.add(BookingItem(id, data))
                                    }
                                }
                                viewModel.lastVisibleDocument = documents.documents.lastOrNull()
                                mAdapter.addModels(uniqueDataList, false)
                            }
                        }.addOnFailureListener {
                            hideLoading()
                            toast(R.string.error_400)
                        }
                }
            })
        }

        mAdapter.onClick = {
            getVC().addFragment(CustomerInformationFragment::class.java, Bundle().apply {
                putString(ID_CUSTOMER_BOOKING, it)
            })
        }
    }

    override fun backPressed(): Boolean {
        getVC().backFromAddFragment()
        return false
    }

    private fun refreshData() {
        showLoading()
        mAdapter.clear()
        bookingCollection
            .whereEqualTo("docterId", auth.currentUser?.uid)
//            .whereArrayContains("nameUser", edt_search.text.toString())
            .orderBy("nameUser").limit(20).get()
            .addOnSuccessListener { documents ->
                hideLoading()
                if (documents.isEmpty) {
                    tv_show_message_not_result.visible()
                } else {
                    val userIdSet = mutableSetOf<String>()
                    val uniqueDataList = mutableListOf<BookingItem>()
                    tv_show_message_not_result.gone()
                    viewModel.lastVisibleDocument = documents.documents.lastOrNull()
                    documents.map { documentSnapshot ->
                        val id = documentSnapshot.id
                        val data = documentSnapshot.toObject(BookingResponse::class.java)
//                        BookingItem(id, data)
                        val userId = data.userId
                        if (userId != null && !userIdSet.contains(userId)) {
                            userIdSet.add(userId)
                            uniqueDataList.add(BookingItem(id, data))
                        }
                    }
                    mAdapter.refresh(uniqueDataList)
                }
            }.addOnFailureListener {
                hideLoading()
                tv_show_message_not_result.visible()
                toast(R.string.error_400)
            }
    }
}