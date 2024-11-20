package com.graduate.datn.ui.admin.user_management

import android.os.Bundle
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.graduate.datn.R
import com.graduate.datn.adapter.recyclerview.DocterNameAdapter
import com.graduate.datn.base.BaseFragment
import com.graduate.datn.base.adapter.EndlessLoadingRecyclerViewAdapter
import com.graduate.datn.custom.dialog.BottomSheetCalendar
import com.graduate.datn.entity.User
import com.graduate.datn.entity.request.DateToWorkRequest
import com.graduate.datn.extension.*
import com.graduate.datn.ui.admin.add_docter_name.AddDocterNameFragment
import com.graduate.datn.ui.admin.list_work_schedule.DateToWorkItem
import com.graduate.datn.ui.admin.work_schedule.CreateWorkScheduleFragment
import com.graduate.datn.ui.admin.work_schedule_detail.WorkScheduleDetailsFragment
import com.graduate.datn.utils.BundleKey.KEY_DATA_UPDATE_BARBER
import com.graduate.datn.utils.BundleKey.KEY_IS_CREATE_BARBER
import com.graduate.datn.utils.BundleKey.KEY_IS_UPDATE_BARBER
import com.graduate.datn.utils.BundleKey.KEY_WORD_SCHEDULE_DETAIL
import com.graduate.datn.utils.Constant
import com.graduate.datn.utils.Constant.TYPE_ACCOUNT_BARBER_NAME
import com.graduate.datn.utils.Constant.TYPE_ACCOUNT_CUSTOMER
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.prolificinteractive.materialcalendarview.CalendarDay
import kotlinx.android.synthetic.main.fragment_container_user_management.*

class ContainerUserManagementFragment : BaseFragment() {
    private val viewModel: ContainerUserManagerViewModel by activityViewModels()
    private val db = FirebaseFirestore.getInstance()
    private val userCollection = db.collection(Constant.TABLE_USER)
    private val workScheduleCollection = db.collection(Constant.TABLE_WORK_SCHEDULE)
    private val mAdapter: DocterNameAdapter by lazy { DocterNameAdapter(requireContext()) }
    private val today = CalendarDay.today()
    override fun backFromAddFragment() {
        refreshData()
    }

    override val layoutId: Int
        get() = R.layout.fragment_container_user_management

    override fun initView() {
        viewModel.fillerALL = true
        viewModel.tabLayout = 1
        tv_search_all.text = requireContext().getString(R.string.str_only_day)
        cl_show_calendar.invisible()
        recycler_view.setAdapter(mAdapter)
        recycler_view.setListLayoutManager(LinearLayoutManager.VERTICAL)
        viewModel.bookingDate = "${today.year}-${today.month.degit}-${today.day.degit}"
        refreshData()
    }

    override fun initData() {
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
                    when (viewModel.tabLayout) {
                        TYPE_CUSTOMER -> {
                            userCollection
                                .whereEqualTo("account", Constant.TYPE_ACCOUNT_CUSTOMER)
                                .orderBy("name").startAfter(viewModel.lastVisibleDocument!!)
                                .limit(20)
                                .get().addOnSuccessListener { documents ->
                                    mAdapter.hideLoadingItem()
                                    hideLoading()
                                    if (documents.isEmpty) {
                                        // Không còn dữ liệu để tải thêm
                                    } else {
                                        val dataList = documents.map { documentSnapshot ->
                                            documentSnapshot.toObject(User::class.java)
                                        }
                                        viewModel.lastVisibleDocument =
                                            documents.documents.lastOrNull()
                                        mAdapter.addModels(dataList, false)
                                    }
                                }.addOnFailureListener {
                                    hideLoading()
                                    toast(R.string.error_400)
                                }
                        }
                        TYPE_BARBER -> {
                            userCollection
                                .whereEqualTo("account", Constant.TYPE_ACCOUNT_BARBER_NAME)
                                .orderBy("name").startAfter(viewModel.lastVisibleDocument!!)
                                .limit(20)
                                .get().addOnSuccessListener { documents ->
                                    mAdapter.hideLoadingItem()
                                    hideLoading()
                                    if (documents.isEmpty) {
                                        // Không còn dữ liệu để tải thêm
                                    } else {
                                        val dataList = documents.map { documentSnapshot ->
                                            documentSnapshot.toObject(User::class.java)
                                        }
                                        viewModel.lastVisibleDocument =
                                            documents.documents.lastOrNull()
                                        mAdapter.addModels(dataList, false)
                                    }
                                }.addOnFailureListener {
                                    hideLoading()
                                    toast(R.string.error_400)
                                }
                        }
                        TYPE_WORK_SCHEDULE -> {
                            workScheduleCollection
                                .orderBy("dateTimestamp", Query.Direction.DESCENDING).startAfter(viewModel.lastVisibleDocument!!)
                                .whereEqualTo("approve", true)
                                .limit(50)
                                .get().addOnSuccessListener { documents ->
                                    mAdapter.hideLoadingItem()
                                    hideLoading()
                                    if (documents.isEmpty) {
                                        // Không còn dữ liệu để tải thêm
                                    } else {
                                        val dataList = documents.map { documentSnapshot ->
                                            val id = documentSnapshot.id
                                            val data =
                                                documentSnapshot.toObject(DateToWorkRequest::class.java)
                                            DateToWorkItem(id, data)
                                        }
                                        if (viewModel.fillerALL){
                                            mAdapter.addModels(listOf(dataList), false)
                                        }else {
                                            val formatDay =
                                                viewModel.bookingDate?.let { convertDateFormat(it, isReturnDay = true) }
                                            mAdapter.refresh(dataList.filter{it.data.date == formatDay })
                                        }
                                        viewModel.lastVisibleDocument =
                                            documents.documents.lastOrNull()
                                        mAdapter.addModels(listOf(dataList), false)
                                    }
                                }.addOnFailureListener {
                                    hideLoading()
                                    toast(R.string.error_400)
                                }
                        }
                    }
                }
            })
        }

    }

    override fun initListener() {
        header.onLeftClick = {
            backPressed()
        }

        cl_show_calendar.onAvoidDoubleClick {
            val bottomSheetCalendar = viewModel.bookingDate?.let { it1 ->
                BottomSheetCalendar(requireContext(),
                    it1,
                    isWorkDoctor = true)
            }
            bottomSheetCalendar?.show(childFragmentManager, "")
            bottomSheetCalendar?.onDateChangedListener = { calendarDay, _ ->
                val dateSelect =
                    "${calendarDay.year}-${calendarDay.month.degit}-${calendarDay.day.degit}"
                if (dateSelect != viewModel.bookingDate) {
                    viewModel.bookingDate = dateSelect
                    refreshData()
                }
            }
        }

        tab_layout.onClickLeft = {
            tv_add.text = "Thêm Bệnh nhân"
            cl_show_calendar.gone()
            viewModel.tabLayout = TYPE_CUSTOMER
            refreshData()
            tv_search_all.gone()
        }

        tab_layout.onClickMiddle = {
            tv_add.text = "Thêm Bác Sĩ"
            cl_show_calendar.gone()
            viewModel.tabLayout = TYPE_BARBER
            refreshData()
            tv_search_all.gone()
        }

        tab_layout.onClickRight = {
            if (viewModel.fillerALL) {
                cl_show_calendar.invisible()
            } else {
                cl_show_calendar.visible()
            }
            tv_add.text = "Thêm Lịch làm việc"
            viewModel.tabLayout = TYPE_WORK_SCHEDULE
            refreshData()
            tv_search_all.visible()
        }

        tv_search_all.onAvoidDoubleClick {
            if (viewModel.fillerALL) {
                tv_search_all.text = requireContext().getString(R.string.str_all)
                cl_show_calendar.visible()

            } else {
                tv_search_all.text = requireContext().getString(R.string.str_only_day)
                cl_show_calendar.invisible()
            }
            viewModel.fillerALL = !viewModel.fillerALL
            refreshData()
        }

        ll_add.onAvoidDoubleClick {
            when (viewModel.tabLayout) {
                TYPE_CUSTOMER -> {
                    getVC().addFragment(AddDocterNameFragment::class.java, Bundle().apply {
                        putBoolean(KEY_IS_CREATE_BARBER, false)
                    })
                }
                TYPE_BARBER -> {
                    getVC().addFragment(AddDocterNameFragment::class.java, Bundle().apply {
                        putBoolean(KEY_IS_CREATE_BARBER, true)
                    })
                }
                TYPE_WORK_SCHEDULE -> {
                    getVC().addFragment(CreateWorkScheduleFragment::class.java)
                }
            }
        }

        mAdapter.onClick = { data ->
            if (data is User) {
                if (data.account == TYPE_ACCOUNT_CUSTOMER) {
                    getVC().addFragment(AddDocterNameFragment::class.java, Bundle().apply {
                        putBoolean(KEY_IS_UPDATE_BARBER, false)
                        putSerializable(KEY_DATA_UPDATE_BARBER, data)
                    })
                } else if (data.account == TYPE_ACCOUNT_BARBER_NAME) {
                    getVC().addFragment(AddDocterNameFragment::class.java, Bundle().apply {
                        putBoolean(KEY_IS_UPDATE_BARBER, true)
                        putSerializable(KEY_DATA_UPDATE_BARBER, data)
                    })
                }
            } else if (data is DateToWorkItem) {
                getVC().addFragment(WorkScheduleDetailsFragment::class.java, Bundle().apply {
                    putSerializable(KEY_WORD_SCHEDULE_DETAIL, data)
                })
            }
        }
    }

    private fun refreshData() {
        showLoading()
        mAdapter.clear()
        when (viewModel.tabLayout) {
            TYPE_CUSTOMER -> {
                userCollection
                    .whereEqualTo("account", Constant.TYPE_ACCOUNT_CUSTOMER)
                    .orderBy("name").limit(20).get()
                    .addOnSuccessListener { documents ->
                        hideLoading()
                        if (documents.isEmpty) {
                            tv_show_message_not_result.visible()
                        } else {
                            tv_show_message_not_result.gone()
                            viewModel.lastVisibleDocument = documents.documents.lastOrNull()
                            val dataList = documents.map { documentSnapshot ->
                                documentSnapshot.toObject(User::class.java)
                            }
                            mAdapter.refresh(dataList)
                        }
                    }.addOnFailureListener {
                        hideLoading()
                        tv_show_message_not_result.visible()
                        toast(R.string.error_400)
                    }
            }
            TYPE_BARBER -> {
                userCollection
                    .whereEqualTo("account", Constant.TYPE_ACCOUNT_BARBER_NAME)
                    .orderBy("name").limit(20).get()
                    .addOnSuccessListener { documents ->
                        hideLoading()
                        if (documents.isEmpty) {
                            tv_show_message_not_result.visible()
                        } else {
                            tv_show_message_not_result.gone()
                            viewModel.lastVisibleDocument = documents.documents.lastOrNull()
                            val dataList = documents.map { documentSnapshot ->
                                documentSnapshot.toObject(User::class.java)
                            }
                            mAdapter.refresh(dataList)
                        }
                    }.addOnFailureListener {
                        hideLoading()
                        tv_show_message_not_result.visible()
                        toast(R.string.error_400)
                    }
            }
            TYPE_WORK_SCHEDULE -> {
                tv_date.text = viewModel.bookingDate?.let { convertDateFormat(it) }
                val formatDay =
                    viewModel.bookingDate?.let { convertDateFormat(it, isReturnDay = true) }
                showLoading()
                mAdapter.clear()
                workScheduleCollection
                    .whereEqualTo("approve", true)
//                    .whereEqualTo("date", formatDay)
                    .orderBy("dateTimestamp", Query.Direction.DESCENDING)
                    .limit(if (viewModel.fillerALL) 50 else 500)
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
                                val data = documentSnapshot.toObject(DateToWorkRequest::class.java)
                                DateToWorkItem(id, data)
                            }
                            if (viewModel.fillerALL){
                                mAdapter.refresh(dataList)
                            }else {
                                mAdapter.refresh(dataList.filter{it.data.date == formatDay })
                            }
                        }
                    }.addOnFailureListener {
                        hideLoading()
                        tv_show_message_not_result.visible()
                        toast(R.string.error_400)
                    }
            }
        }
    }

    override fun backPressed(): Boolean {
        getVC().backFromAddFragment()
        return false
    }

    companion object {
        const val TYPE_CUSTOMER = 1
        const val TYPE_BARBER = 2
        const val TYPE_WORK_SCHEDULE = 3
    }

}