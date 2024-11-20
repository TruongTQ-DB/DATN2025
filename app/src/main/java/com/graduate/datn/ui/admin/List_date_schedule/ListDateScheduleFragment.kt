package com.graduate.datn.ui.admin.List_date_schedule

import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.graduate.datn.R
import com.graduate.datn.adapter.recyclerview.ListDateScheduleAdapter
import com.graduate.datn.base.BaseFragment
import com.graduate.datn.entity.request.DateToWorkRequest
import com.graduate.datn.extension.gone
import com.graduate.datn.extension.toast
import com.graduate.datn.extension.visible
import com.graduate.datn.ui.admin.list_work_schedule.DateToWorkItem
import com.graduate.datn.ui.admin.work_schedule_detail.WorkScheduleDetailsFragment
import com.graduate.datn.utils.BundleKey
import com.graduate.datn.utils.BundleKey.ID_BARBER
import com.graduate.datn.utils.Constant
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.fragment_list_date_schdule.*
import kotlinx.android.synthetic.main.fragment_list_date_schdule.header
import kotlinx.android.synthetic.main.fragment_list_date_schdule.recycler_view
import kotlinx.android.synthetic.main.fragment_list_date_schdule.tv_show_message_not_result
import kotlinx.android.synthetic.main.fragment_notification_schedule.*

class ListDateScheduleFragment : BaseFragment() {
    private val mAdapter: ListDateScheduleAdapter by lazy {
        ListDateScheduleAdapter(requireContext())
    }
    private val db = FirebaseFirestore.getInstance()
    private val mCollection = db.collection(Constant.TABLE_WORK_SCHEDULE)
    private var idBarber: String? = ""
    private lateinit var auth: FirebaseAuth

    override fun backFromAddFragment() {
        refreshData()
    }

    override val layoutId: Int
        get() = R.layout.fragment_list_date_schdule

    override fun initView() {
        auth = Firebase.auth
        recycler_view.setAdapter(mAdapter)
        recycler_view.setListLayoutManager(LinearLayoutManager.VERTICAL)
    }

    override fun initData() {
        arguments?.let {
            if (it.containsKey(ID_BARBER)) {
                idBarber = it.getString(ID_BARBER)
            }
        }
        refreshData()
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
        }
        mAdapter.onClick = {
            getVC().addFragment(WorkScheduleDetailsFragment::class.java, Bundle().apply {
                putSerializable(BundleKey.KEY_WORD_SCHEDULE_DETAIL, it)
                putBoolean(BundleKey.KEY_WORD_SCHEDULE_DETAIL_APPROVE, true)
            })
        }

        mAdapter.onApprove = { data, positon ->
            showLoading()
            mCollection.document(data.id).update("approve", true)
                .addOnSuccessListener {
                    hideLoading()
                    toast("Chấp thuần thành công!")
                    mAdapter.removeModel(positon)
                }.addOnFailureListener {
                    hideLoading()
                    toast("Chấp thuần thất bại!")
                }

        }
    }

    override fun backPressed(): Boolean {
        getVC().backFromAddFragment()
        return false
    }

    private fun refreshData() {
        showLoading()
        mAdapter.clear()
        mCollection
            .orderBy("dateTimestamp", Query.Direction.DESCENDING)
            .whereEqualTo("idBarberName", idBarber)
            .whereEqualTo("approve", false)
            .get().addOnSuccessListener { documents ->
                mAdapter.hideLoadingItem()
                hideLoading()
                if (documents.isEmpty) {
                    // Không còn dữ liệu để tải thêm
                    tv_show_message_not_result.visible()
                } else {
                    tv_show_message_not_result.gone()
                    val data = documents.map { documentSnapshot ->
                        val id = documentSnapshot.id
                        val data = documentSnapshot.toObject(DateToWorkRequest::class.java)
                        DateToWorkItem(id, data)
                    }
                    mAdapter.refresh(data)
                }
            }.addOnFailureListener {
                hideLoading()
                tv_show_message_not_result.visible()
                toast(R.string.error_400)
            }
    }
}