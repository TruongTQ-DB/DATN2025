package com.graduate.datn.ui.admin.list_work_schedule

import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.graduate.datn.R
import com.graduate.datn.adapter.recyclerview.AddressItem
import com.graduate.datn.adapter.recyclerview.WorkScheduleAdapter
import com.graduate.datn.base.BaseFragment
import com.graduate.datn.base.adapter.EndlessLoadingRecyclerViewAdapter
import com.graduate.datn.base.entity.BaseError
import com.graduate.datn.custom.dialog.BottomSheetCalendar
import com.graduate.datn.entity.request.DateToWorkRequest
import com.graduate.datn.entity.response.ListAddressResponse
import com.graduate.datn.extension.*
import com.graduate.datn.ui.admin.work_schedule.CreateWorkScheduleFragment
import com.graduate.datn.utils.Constant.TABLE_WORK_SCHEDULE
import com.google.firebase.firestore.FirebaseFirestore
import com.prolificinteractive.materialcalendarview.CalendarDay
import kotlinx.android.synthetic.main.fragment_list_work_schedule.*
import java.io.Serializable

class ListWorkScheduleFragment : BaseFragment() {
    private val viewModel: ListWorkScheduleViewModel by activityViewModels()
    private val mAdapter: WorkScheduleAdapter by lazy {
        WorkScheduleAdapter(requireContext())
    }
    private val db = FirebaseFirestore.getInstance()
    private val workScheduleCollection = db.collection(TABLE_WORK_SCHEDULE)
    private val today = CalendarDay.today()
    private var bookingDate: String = "${today.year}-${today.month.degit}-${today.day.degit}"

    override fun backFromAddFragment() {
        refreshData(bookingDate)
    }

    override val layoutId: Int
        get() = R.layout.fragment_list_work_schedule

    override fun initView() {
        rcv_work_schedule.setAdapter(mAdapter)
        rcv_work_schedule.setListLayoutManager(LinearLayoutManager.VERTICAL)
        bookingDate = "${today.year}-${today.month.degit}-${today.day.degit}"
    }

    override fun initData() {
        refreshData(bookingDate)
    }

    override fun initListener() {
        header.onLeftClick = {
            backPressed()
        }
        cl_show_calendar.onAvoidDoubleClick {
            val bottomSheetCalendar = BottomSheetCalendar(requireContext(), bookingDate, isWorkDoctor = true)
            bottomSheetCalendar.show(childFragmentManager, "")
            bottomSheetCalendar.onDateChangedListener = { calendarDay, _ ->
                val dateSelect =
                    "${calendarDay.year}-${calendarDay.month.degit}-${calendarDay.day.degit}"
                if (dateSelect != bookingDate) {
                    refreshData(dateSelect)

                }
                bookingDate = dateSelect
            }
        }
        rcv_work_schedule.apply {
            setOnRefreshListener {
                rcv_work_schedule.enableRefresh(false)
                refreshData(bookingDate)
            }
            setOnLoadingMoreListener(object :
                EndlessLoadingRecyclerViewAdapter.OnLoadingMoreListener {
                override fun onLoadMore() {
                    mAdapter.showLoadingItem(true)
                    showLoading()
                    workScheduleCollection
                        .whereEqualTo("approve", true)
                        .whereEqualTo("date", convertDateFormat(bookingDate, isReturnDay = true))
                        .orderBy("name").startAfter(viewModel.lastVisibleDocument!!)
                        .limit(20)
                        .get().addOnSuccessListener { documents ->
                            mAdapter.hideLoadingItem()
                            hideLoading()
                            if (documents.isEmpty) {
                                // Không còn dữ liệu để tải thêm
                            } else {
                                val dataList = documents.map { documentSnapshot ->
                                    val id = documentSnapshot.id
                                    val data =
                                        documentSnapshot.toObject(ListAddressResponse::class.java)
                                    AddressItem(id, data)
                                }
                                viewModel.lastVisibleDocument = documents.documents.lastOrNull()
                                mAdapter.addModels(dataList, false)
                            }
                        }.addOnFailureListener {
                            hideLoading()
                            toast(R.string.error_400)
                        }
                }
            })
        }

        mAdapter.onClick = {

        }

        ll_add_work_schedule.onAvoidDoubleClick {
            getVC().addFragment(CreateWorkScheduleFragment::class.java)
        }
    }

    override fun backPressed(): Boolean {
        getVC().backFromAddFragment()
        return false
    }

    override fun handleValidateError(throwable: BaseError?) {
        toast(throwable!!.error)
    }

    override fun handleNetworkError(throwable: Throwable?, isShowDialog: Boolean) {
        throwable?.getErrorBody()?.msg?.let { toast(it) }
    }

    private fun refreshData(date: String) {
        tv_date.text = convertDateFormat(date)
        var formatDay = convertDateFormat(date, isReturnDay = true)
        showLoading()
        mAdapter.clear()
        workScheduleCollection
            .whereEqualTo("approve", true)
            .whereEqualTo("date", formatDay)
            .orderBy("name").limit(20).get()
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
                    mAdapter.refresh(dataList)
                }
            }.addOnFailureListener {
                hideLoading()
                tv_show_message_not_result.visible()
                toast(R.string.error_400)
            }
    }
}

data class DateToWorkItem(val id: String, val data: DateToWorkRequest) : Serializable

data class DateResponse(val date: String) {
    constructor() : this("")
}