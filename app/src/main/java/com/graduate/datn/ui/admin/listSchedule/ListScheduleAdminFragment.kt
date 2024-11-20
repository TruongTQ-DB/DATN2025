package com.graduate.datn.ui.admin.listSchedule

import android.os.Bundle
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.graduate.datn.R
import com.graduate.datn.adapter.recyclerview.BookingItem
import com.graduate.datn.adapter.recyclerview.ScheduleAdapter
import com.graduate.datn.base.BaseFragment
import com.graduate.datn.base.adapter.EndlessLoadingRecyclerViewAdapter
import com.graduate.datn.custom.dialog.BottomSheetCalendar
import com.graduate.datn.entity.response.BookingResponse
import com.graduate.datn.extension.*
import com.graduate.datn.ui.docterName.schedule_details.ScheduleDetailsFragment
import com.graduate.datn.utils.BundleKey.ID_SCHEDULE
import com.graduate.datn.utils.Constant
import com.google.firebase.firestore.FirebaseFirestore
import com.prolificinteractive.materialcalendarview.CalendarDay
import kotlinx.android.synthetic.main.fragment_list_schdule_docter.*

class ListScheduleAdminFragment : BaseFragment() {
    private val viewModel: ListScheduleAdminViewModel by activityViewModels()
    private val mScheduleAdapter: ScheduleAdapter by lazy {
        ScheduleAdapter(requireContext())
    }
    private val db = FirebaseFirestore.getInstance()
    private val bookingCollection = db.collection(Constant.TABLE_BOOKING)
    private val today = CalendarDay.today()
    private lateinit var bookingDate: String

    override fun backFromAddFragment() {
        refreshData(bookingDate)
    }

    override val layoutId: Int
        get() = R.layout.fragment_list_schdule_docter

    override fun initView() {
        viewModel.lastVisibleDocument = null
        viewModel.fillerALL = false
        bookingDate = "${today.year}-${today.month.degit}-${today.day.degit}"
        recycler_view.setAdapter(mScheduleAdapter)
        recycler_view.setListLayoutManager(LinearLayoutManager.VERTICAL)
        setUpView()
    }

    private fun setUpView() {
        header.setTitle("Quản lý Đặt lịch")
        tv_search_all.visible()
        tv_search_all.text = requireContext().getString(R.string.str_all)
    }

    override fun initData() {
        refreshData(bookingDate)
    }

    override fun initListener() {
        header.onLeftClick = {
            backPressed()
        }

        tv_search_all.onAvoidDoubleClick {
            if (viewModel.fillerALL){
                tv_search_all.text = requireContext().getString(R.string.str_all)
                cl_show_calendar.visible()

            }else {
                tv_search_all.text = requireContext().getString(R.string.str_only_day)
                cl_show_calendar.invisible()
            }
            viewModel.fillerALL = !viewModel.fillerALL
            refreshData(bookingDate)
        }

        cl_show_calendar.onAvoidDoubleClick {
            val bottomSheetCalendar =
                BottomSheetCalendar(requireContext(), bookingDate, isWorkDoctor = true)
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

        recycler_view.apply {
            setOnRefreshListener {
                recycler_view.enableRefresh(false)
                refreshData(bookingDate)
            }
            setOnLoadingMoreListener(object :
                EndlessLoadingRecyclerViewAdapter.OnLoadingMoreListener {
                override fun onLoadMore() {
                    mScheduleAdapter.showLoadingItem(true)
                    showLoading()
                    bookingCollection
//                        .whereEqualTo("date", convertDateFormat(bookingDate, isReturnDay = true))
                        .orderBy("timeFrom").startAfter(viewModel.lastVisibleDocument!!)
                        .limit(20)
                        .get().addOnSuccessListener { documents ->
                            mScheduleAdapter.hideLoadingItem()
                            hideLoading()
                            if (documents.isEmpty) {
                                // Không còn dữ liệu để tải thêm
                            } else {
                                val dataList: List<BookingItem> = documents.mapNotNull { documentSnapshot ->
                                    val id = documentSnapshot.id
                                    val data = documentSnapshot.toObject(BookingResponse::class.java)

                                    if (viewModel.fillerALL || data.date == convertDateFormat(bookingDate, isReturnDay = true)) {
                                        BookingItem(id, data)
                                    } else {
                                        null
                                    }
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
                putString(ID_SCHEDULE, it)
            })
        }
    }

    override fun backPressed(): Boolean {
        getVC().backFromAddFragment()
        return false
    }

    private fun refreshData(date: String) {
        tv_date.text = convertDateFormat(date)
        val formatDay = convertDateFormat(date, isReturnDay = true)
        showLoading()
        mScheduleAdapter.clear()
        bookingCollection
//            .whereEqualTo("date", formatDay)
            .orderBy("timeFrom").limit(20).get()
            .addOnSuccessListener { documents ->
                hideLoading()
                if (documents.isEmpty) {
                    tv_show_message_not_result.visible()
                } else {
                    tv_show_message_not_result.gone()
                    viewModel.lastVisibleDocument = documents.documents.lastOrNull()
                    val dataList: List<BookingItem> = documents.mapNotNull { documentSnapshot ->
                        val id = documentSnapshot.id
                        val data = documentSnapshot.toObject(BookingResponse::class.java)

                        if (viewModel.fillerALL || data.date == formatDay) {
                            BookingItem(id, data)
                        } else {
                            null
                        }
                    }
                    if (dataList.isNullOrEmpty()) {
                        tv_show_message_not_result.visible()
                    }
                    mScheduleAdapter.refresh(dataList)
                }
            }.addOnFailureListener {
                hideLoading()
                tv_show_message_not_result.visible()
                toast(R.string.error_400)
            }
    }

//    private fun refreshData() {
//        showLoading()
//        mScheduleAdapter.clear()
//        bookingCollection
//            .whereEqualTo("userId", auth.currentUser?.uid)
//            .orderBy("date").limit(20)
//            .get()
//            .addOnSuccessListener { documents ->
//                hideLoading()
//                if (documents.isEmpty) {
//                    tv_show_message_not_result.visible()
//                } else {
//                    tv_show_message_not_result.gone()
//                    viewModel.lastVisibleDocument = documents.documents.lastOrNull()
//                    val dataList = documents.map { documentSnapshot ->
//                        val id = documentSnapshot.id
//                        val data = documentSnapshot.toObject(BookingResponse::class.java)
//                        BookingItem(id, data)
//                    }
//                    mScheduleAdapter.refresh(dataList)
//                }
//            }.addOnFailureListener {
//                hideLoading()
//                tv_show_message_not_result.visible()
//                toast(R.string.error_400)
//            }
//    }
}