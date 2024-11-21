package com.graduate.datn.ui.docterName.list_schedule_by_user

import android.os.Bundle
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.graduate.datn.R
import com.graduate.datn.adapter.recyclerview.BookingItem
import com.graduate.datn.adapter.recyclerview.ScheduleAdapter
import com.graduate.datn.base.BaseFragment
import com.graduate.datn.base.adapter.EndlessLoadingRecyclerViewAdapter
import com.graduate.datn.entity.response.BookingResponse
import com.graduate.datn.extension.gone
import com.graduate.datn.extension.onAvoidDoubleClick
import com.graduate.datn.extension.toast
import com.graduate.datn.extension.visible
import com.graduate.datn.ui.docterName.schedule_details.ScheduleDetailsFragment
import com.graduate.datn.ui.user.choose_address.ChooseAddressFragment
import com.graduate.datn.utils.BundleKey
import com.graduate.datn.utils.Constant
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.fragment_list_schdule_user.*

class ListScheduleByUserFragment : BaseFragment() {
    private val viewModel: ListScheduleByUserViewModel by activityViewModels()
    private val mScheduleAdapter: ScheduleAdapter by lazy {
        ScheduleAdapter(requireContext())
    }
    private val db = FirebaseFirestore.getInstance()
    private val bookingCollection = db.collection(Constant.TABLE_BOOKING)
    private lateinit var auth: FirebaseAuth

    override fun backFromAddFragment() {
        refreshData()
    }

    override val layoutId: Int
        get() = R.layout.fragment_list_schdule_user

    override fun initView() {
        setUpView()
        arguments?.let {
            if (it.containsKey(BundleKey.ID_USER)) {
                viewModel.userId = it.getString(BundleKey.ID_USER)
            }
        }

        auth = Firebase.auth
        viewModel.lastVisibleDocument = null
        recycler_view.setAdapter(mScheduleAdapter)
        recycler_view.setListLayoutManager(LinearLayoutManager.VERTICAL)
    }

    private fun setUpView() {
        ll_create.gone()
        header.setTitle("Lịch sử đặt lịch")
    }

    override fun initData() {
        refreshData()
    }

    override fun initListener() {
//        header.onLeftClick = {
//            backPressed()
//        }
        recycler_view.apply {
            setOnRefreshListener {
                recycler_view.enableRefresh(false)
                refreshData()
            }
            setOnLoadingMoreListener(object :
                EndlessLoadingRecyclerViewAdapter.OnLoadingMoreListener {
                override fun onLoadMore() {
                    mScheduleAdapter.showLoadingItem(true)
                    showLoading()
                    bookingCollection
                        .orderBy("date")
                        .whereEqualTo("userId", viewModel.userId)
                        .whereEqualTo("docterId", auth.currentUser?.uid)
                        .startAfter(viewModel.lastVisibleDocument!!)
                        .limit(20)
                        .get().addOnSuccessListener { documents ->
                            mScheduleAdapter.hideLoadingItem()
                            hideLoading()
                            if (documents.isEmpty) {
                                // Không còn dữ liệu để tải thêm
                            } else {
                                val dataList = documents.map { documentSnapshot ->
                                    val id = documentSnapshot.id
                                    val data =
                                        documentSnapshot.toObject(BookingResponse::class.java)
                                    BookingItem(id, data)
                                }
                                viewModel.lastVisibleDocument = documents.documents.lastOrNull()
                                mScheduleAdapter.addModels(dataList, false)
                            }
                        }.addOnFailureListener {
                            hideLoading()
                            toast(R.string.error_400)
                        }
                }
            })
        }

        mScheduleAdapter.onClick = {
            getVC().addFragment(ScheduleDetailsFragment::class.java, Bundle().apply {
                putString(BundleKey.ID_SCHEDULE, it)
            })
        }

        ll_create.onAvoidDoubleClick {
            getVC().addFragment(ChooseAddressFragment::class.java)
        }
    }

    override fun backPressed(): Boolean {
        getVC().backFromAddFragment()
        return false
    }

    private fun refreshData() {
        showLoading()
        mScheduleAdapter.clear()
        bookingCollection
            .whereEqualTo("docterId", auth.currentUser?.uid)
            .whereEqualTo("userId", viewModel.userId)
            .orderBy("date").limit(20)
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
                        val data = documentSnapshot.toObject(BookingResponse::class.java)
                        BookingItem(id, data)
                    }
                    mScheduleAdapter.refresh(dataList)
                }
            }.addOnFailureListener {
                hideLoading()
                tv_show_message_not_result.visible()
                toast(R.string.error_400)
            }
    }
}