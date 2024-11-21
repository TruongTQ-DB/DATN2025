package com.graduate.datn.ui.admin.notification_schedule

import android.os.Bundle
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.graduate.datn.R
import com.graduate.datn.adapter.recyclerview.NotificationScheduleAdapter
import com.graduate.datn.base.BaseFragment
import com.graduate.datn.entity.request.DateToWorkRequest
import com.graduate.datn.extension.gone
import com.graduate.datn.extension.toast
import com.graduate.datn.extension.visible
import com.graduate.datn.ui.admin.List_date_schedule.ListDateScheduleFragment
import com.graduate.datn.ui.admin.list_work_schedule.DateToWorkItem
import com.graduate.datn.ui.common.notification.NotificationViewModel
import com.graduate.datn.utils.BundleKey
import com.graduate.datn.utils.Constant
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.fragment_notification_schedule.*

class NotificationScheduleFragment : BaseFragment() {
    private val viewModel: NotificationViewModel by activityViewModels()
    private val mAdapter: NotificationScheduleAdapter by lazy {
        NotificationScheduleAdapter(requireContext())
    }
    private val db = FirebaseFirestore.getInstance()
    private val mCollection = db.collection(Constant.TABLE_WORK_SCHEDULE)

    private lateinit var auth: FirebaseAuth

    override fun backFromAddFragment() {
        refreshData()
    }

    override val layoutId: Int
        get() = R.layout.fragment_notification_schedule

    override fun initView() {
        auth = Firebase.auth
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
        recycler_view.apply {
            setOnRefreshListener {
                recycler_view.enableRefresh(false)
                refreshData()
            }
        }

        mAdapter.onClick = {
            getVC().addFragment(ListDateScheduleFragment::class.java, Bundle().apply {
                putString(BundleKey.ID_DOCTER, it.data.idDocterName)
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
        mCollection
            .orderBy("name")
            .whereEqualTo("approve", false)
            .get()
            .addOnSuccessListener { documents ->
                mAdapter.hideLoadingItem()
                hideLoading()
                if (documents.isEmpty) {
                    tv_show_message_not_result.visible()
                } else {
                    tv_show_message_not_result.gone()
                    val dateSet = HashSet<DateToWorkItem>()
                    documents.map { documentSnapshot ->
                        val id = documentSnapshot.id
                        val data = documentSnapshot.toObject(DateToWorkRequest::class.java)
                        data.let {
                            val isUnique =
                                dateSet.none { it.data.idDocterName == data.idDocterName }
                            if (isUnique) {
                                dateSet.add(DateToWorkItem(id, data))
                            }
                        }
                    }
                    mAdapter.refresh(dateSet.toList())
                }
            }.addOnFailureListener {
                tv_show_message_not_result.visible()
                hideLoading()
                toast(R.string.error_400)
            }
    }
}