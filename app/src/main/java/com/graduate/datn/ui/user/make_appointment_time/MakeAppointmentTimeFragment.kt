package com.graduate.datn.ui.user.make_appointment_time

import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.view.View
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.graduate.datn.R
import com.graduate.datn.base.BaseFragment
import com.graduate.datn.base.entity.BaseError
import com.graduate.datn.entity.User
import com.graduate.datn.entity.request.TimeRange
import com.graduate.datn.extension.*
import com.graduate.datn.ui.admin.list_work_schedule.DateResponse
import com.graduate.datn.ui.user.check_infomation.CheckInformationFragment
import com.graduate.datn.utils.BundleKey
import com.graduate.datn.utils.BundleKey.KEY_BOOK_DATE
import com.graduate.datn.utils.BundleKey.KEY_BOOK_INTEND_TIME
import com.graduate.datn.utils.BundleKey.KEY_BOOK_TIME_FROM
import com.graduate.datn.utils.BundleKey.KEY_BOOK_TIME_TO
import com.graduate.datn.utils.BundleKey.KEY_INDEX_TIME
import com.graduate.datn.utils.Constant
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.fragment_make_appointment_time.*
import kotlinx.android.synthetic.main.view_custom_book.view.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.Serializable
import java.text.SimpleDateFormat
import java.time.Duration
import java.time.LocalTime
import java.util.*

class MakeAppointmentTimeFragment : BaseFragment() {
    private val viewModel: MakeAppointmentTimeViewModel by activityViewModels()
    private val db = FirebaseFirestore.getInstance()
    private val mCollection = db.collection(Constant.TABLE_WORK_SCHEDULE)
    private var isHasDate = true
    override fun backFromAddFragment() {

    }

    override val layoutId: Int
        get() = R.layout.fragment_make_appointment_time

    override fun initView() {
        viewModel.lastVisibleDocument = null
    }

    override fun initData() {
        arguments?.let {
            if (it.containsKey(BundleKey.KEY_ADDRESS_ID)) {
                viewModel.addressId = it.getString(BundleKey.KEY_ADDRESS_ID)
            }
            if (it.containsKey(BundleKey.KEY_ADDRESS_NAME)) {
                viewModel.addressName = it.getString(BundleKey.KEY_ADDRESS_NAME)
            }
            if (it.containsKey(BundleKey.KEY_ADDRESS)) {
                viewModel.address = it.getString(BundleKey.KEY_ADDRESS)
            }
            if (it.containsKey(BundleKey.KEY_SERVICE_ID)) {
                viewModel.serviceId = it.getString(BundleKey.KEY_SERVICE_ID)
            }
            if (it.containsKey(BundleKey.KEY_OPTIONAL_SERVICE_ID)) {
                viewModel.optionalServiceId = it.getString(BundleKey.KEY_OPTIONAL_SERVICE_ID)
            }
            if (it.containsKey(BundleKey.KEY_SERVICE_NAME)) {
                viewModel.serviceName = it.getString(BundleKey.KEY_SERVICE_NAME)
            }
            if (it.containsKey(BundleKey.KEY_OPTIONAL_SERVICE_NAME)) {
                viewModel.optionalServiceName = it.getString(BundleKey.KEY_OPTIONAL_SERVICE_NAME)
            }
            if (it.containsKey(BundleKey.KEY_OPTIONAL_SERVICE_PRICE)) {
                viewModel.optionalServicePrice = it.getString(BundleKey.KEY_OPTIONAL_SERVICE_PRICE)
            }
            if (it.containsKey(BundleKey.KEY_DOCTER_NAME)) {
                viewModel.docterNameId = it.getSerializable(BundleKey.KEY_DOCTER_NAME) as User?
            }
        }

        setUpView()
        chooseDate()
    }

    private fun setUpView() {
        viewModel.dateOld = null
        img_barber_name.loadImageUrl(viewModel.docterNameId?.avatar)
        name.text = viewModel.docterNameId?.name
        tv_detail_docter.text = viewModel.docterNameId?.detailname
        tv_address.text = viewModel.address
        tv_price.text = viewModel.optionalServicePrice + " VND"

    }

    override fun initListener() {
        custom_header.onLeftClick = {
            backPressed()
        }
        custom_booking.onDateChangeListener = { date ->
            viewModel.checkClickDate = true
            if (!viewModel.dateOld.isNullOrEmpty()) {
                if (date != viewModel.dateOld) {
                    reloadTime(date)
                    if (tv_time.visibility == View.VISIBLE) {
                        tv_time.gone()
                    }
                    custom_booking.clearTime()
                } else {
                    custom_booking.showTime()
                }
            } else {
                reloadTime(date)
            }
            custom_booking.btn_hour.isEnabled = true
//            setEnabledButton()
        }
        custom_booking.onSelectDate = { date ->
            tv_date.visible()
            tv_date.text = date.formatDateSchedule()
            showTime(true)
            showDate(false)
//            setEnabledButton()
            if (ll_hour.foreground == null) {
                val typedValue = TypedValue()
                requireContext().theme.resolveAttribute(android.R.attr.selectableItemBackgroundBorderless,
                    typedValue,
                    true)
                ll_hour.foreground =
                    resources.getDrawable(typedValue.resourceId, requireContext().theme)
            }
        }
        custom_booking.onSelectTime = { from, to ->
            tv_time.visible()
            tv_time.text = "$from - $to"
            viewModel.timeTo = to
            viewModel.timeFrom = from
            viewModel.intendTime = getIntendTime(from, to)
        }

        ll_date.onAvoidDoubleClick {
            if (!custom_booking.getDate().isNullOrEmpty()) {
                viewModel.dateOld = custom_booking.getDate()
            }
            custom_booking.showDate(isHasDate)
            showTime(false)
            showDate(true)
        }

        ll_hour.onAvoidDoubleClick {
            if (!custom_booking.getDate().isNullOrEmpty()) {
                custom_booking.showTime()
                showTime(true)
                showDate(false)
            }
        }

        btn_create.onAvoidDoubleClick {
            if (checkValidate()) {
                getIdDate()
            }
        }
        layout_kham_ttuyen.onAvoidDoubleClick{
            toast("Chức năng này chưa được kích hoạt. Xin thông cảm!")
        }
    }

    private fun getIdDate() {
        showLoading()
        mCollection
            .whereEqualTo("approve", true)
            .whereEqualTo("idDocterName", viewModel.docterNameId?.id)
            .whereEqualTo("date",
                custom_booking.getDate()?.convertDate(DATE_FORMAT_6, DATE_FORMAT_2))
            .get()
            .addOnSuccessListener { querySnapshot ->
                for (document in querySnapshot.documents) {
                    val timeRanges = document["timeRanges"] as List<Map<String, Any>>

                    for ((index, timeRange) in timeRanges.withIndex()) {
                        val startTime = timeRange["startTime"] as String

                        if (startTime == viewModel.timeFrom) {
                            Log.d("thiss",
                                document.id + "  " + index + "  " + custom_booking.getDate()
                                    ?.convertDate(DATE_FORMAT_6, DATE_FORMAT_2))
                            val indexData = BookingDaytime(id = document.id,
                                date = custom_booking.getDate()
                                    ?.convertDate(DATE_FORMAT_6, DATE_FORMAT_2),
                                index = index)
                            getVC().addFragment(CheckInformationFragment::class.java,
                                Bundle().apply {
                                    putString(BundleKey.KEY_ADDRESS_ID, viewModel.addressId)
                                    putString(BundleKey.KEY_ADDRESS_NAME, viewModel.addressName)
                                    putString(BundleKey.KEY_ADDRESS, tv_address.text.toString())
                                    putString(BundleKey.KEY_SERVICE_ID, viewModel.serviceId)
                                    putString(BundleKey.KEY_SERVICE_NAME, viewModel.serviceName)
                                    putString(BundleKey.KEY_OPTIONAL_SERVICE_NAME,
                                        viewModel.optionalServiceName)
                                    putSerializable(BundleKey.KEY_DOCTER_NAME,
                                        viewModel.docterNameId)
                                    putString(BundleKey.KEY_OPTIONAL_SERVICE_ID,
                                        viewModel.optionalServiceId)
                                    putString(BundleKey.KEY_OPTIONAL_SERVICE_PRICE,
                                        viewModel.optionalServicePrice)
                                    putString(KEY_BOOK_DATE, custom_booking.getDate())
                                    putString(KEY_BOOK_TIME_FROM, viewModel.timeFrom)
                                    putString(KEY_BOOK_TIME_TO, viewModel.timeTo)
                                    putString(KEY_BOOK_INTEND_TIME, viewModel.intendTime)
                                    putSerializable(KEY_INDEX_TIME, indexData)
                                })
                        }
                    }
                }
                hideLoading()
            }
            .addOnFailureListener { exception ->
                Log.d("thiss", exception.toString())
                hideLoading()
            }
    }

    private fun getIntendTime(from: String, to: String): String? {
        val localTime1 = LocalTime.parse(from)
        val localTime2 = LocalTime.parse(to)
        val duration = Duration.between(localTime1, localTime2)
        val hoursDiff = duration.toHours()
        val minutesDiff = duration.toMinutes() % 60

        return if (hoursDiff >= 1) {
            "$hoursDiff giờ"
        } else {
            "$minutesDiff phút"
        }
    }

    private fun checkValidate(): Boolean {
        if (custom_booking.getDate().isNullOrEmpty()) {
            toast("Vui lòng chọn ngày!")
            return false
        }
        if (custom_booking.getTime().isNullOrEmpty()) {
            toast("Vui lòng chọn thòi gian!")
            return false
        }
        return true
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

    private fun reloadTime(date: String) {
        custom_booking.setSelect(false)
        showLoading()
        val timestampCurent = Timestamp(Date(System.currentTimeMillis()))
        mCollection
            .whereEqualTo("approve", true)
            .whereEqualTo("idDocterName", viewModel.docterNameId?.id)
            .whereEqualTo("date", date.convertDate(DATE_FORMAT_6, DATE_FORMAT_2))
            .get()
            .addOnSuccessListener { documents ->
                hideLoading()
                if (documents.isEmpty) {
                    Log.d("thiss", "không có dữ liệu$date")
                } else {
                    val timeRangesList = documents.mapNotNull { document ->
                        val timeRangesData =
                            document.data["timeRanges"] as? List<HashMap<String, TimeRange>>
                        if (timeRangesData != null) {
                            val timeRanges = mutableListOf<TimeRange>()
                            for (dataMap in timeRangesData) {
                                val startTime = dataMap["startTime"] as? String ?: ""
                                val endTime = dataMap["endTime"] as? String ?: ""
                                val selector = dataMap["selector"] as? Boolean ?: false
                                val stater = (dataMap["stater"] as? Long)?.toInt() ?: 0
                                val timeStamp = (dataMap["timeStamp"] as? Timestamp)
                                val timeRange =
                                    TimeRange(startTime, endTime, stater, selector, timeStamp)
                                timeStamp?.let {
                                    if (timestampCurent < timeStamp) timeRanges.add(timeRange)
                                }
                            }
                            timeRanges
                        } else {
                            null
                        }
                    }

                    Log.d("thiss", timeRangesList.first().toString())
                    custom_booking.setTime(timeRangesList.first().filter { it.stater == 0 })
                }
            }.addOnFailureListener {
                hideLoading()

            }
    }

    private fun reloadDate() {
        viewModel.docterNameId?.id?.let { Log.d("thiss", it) }
        showLoading()
        val currentDate =
            SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Calendar.getInstance().time)
        val currentDateTimestamp =
            SimpleDateFormat("dd/MM/yyyy").parse(currentDate)?.let { Timestamp(it) }
        currentDateTimestamp?.let {
            mCollection
                .whereEqualTo("approve", true)
                .whereEqualTo("idDocterName", viewModel.docterNameId?.id)
                .whereGreaterThanOrEqualTo("dateTimestamp", it)
                .get()
                .addOnSuccessListener { documents ->
                    hideLoading()
                    if (documents.isEmpty) {
                        isHasDate = false
                        custom_booking.setListAppoint(listOf())
                        Log.d("thiss", "Không có dữ liệu")
                    } else {
                        val dateSet = HashSet<DateResponse>()
                        documents.map { documentSnapshot ->
                            val data = documentSnapshot.toObject(DateResponse::class.java)
                            val date = data.date
                            date.let {
                                val isUnique = dateSet.none { it.date == date }
                                if (isUnique) {
                                    dateSet.add(data)
                                }
                            }
                        }
                        isHasDate = true
                        custom_booking.setListAppoint(dateSet.toList())
                        custom_booking.isHasToday = dateSet.any { it.date == getToday() }
                        custom_booking.setHighlightToday()
                        Log.d("thiss", dateSet.toString())
                    }
                }.addOnFailureListener {
                    hideLoading()

                }
        }
    }

    private fun chooseDate() {
        custom_booking.clearBookingDate()
        custom_booking.clearData()
        tv_time.gone()
        custom_booking.showDate(isHasDate)
        tv_date.gone()
        showTime(false)
        showDate(true)
        lifecycleScope.launch {
            delay(300)
            reloadDate()
        }
    }

    private fun showDate(isShow: Boolean) {
        if (isShow) {
            ll_date.setBackgroundResource(R.drawable.bg_add_address)
            cv_date.cardElevation = (4 * density)
            tv_date.setTextColor(requireContext().getColor(R.color.white))
            tv_choose_date.setTextColor(requireContext().getColor(R.color.white))

        } else {
            ll_date.setBackgroundResource(0)
            cv_date.cardElevation = 0f
            tv_date.setTextColor(requireContext().getColor(R.color.black))
            tv_choose_date.setTextColor(requireContext().getColor(R.color.black))
        }
    }

    private fun showTime(isShow: Boolean) {
        if (isShow) {
            ll_hour.setBackgroundResource(R.drawable.bg_add_address)
            cv_hour.cardElevation = (4 * density)
            tv_time.setTextColor(requireContext().getColor(R.color.white))
            tv_choose_time.setTextColor(requireContext().getColor(R.color.white))
        } else {
            ll_hour.setBackgroundResource(0)
            cv_hour.cardElevation = 0f
            if (tv_time.visibility == View.VISIBLE) {
                tv_choose_time.setTextColor(requireContext().getColor(R.color.black))
                tv_time.setTextColor(requireContext().getColor(R.color.black))
            } else {
                tv_choose_time.setTextColor(requireContext().getColor(R.color.color_text_grey))
            }
        }
    }

    private val density: Float by lazy {
        requireContext().resources.displayMetrics.density
    }
}

data class BookingDaytime(val id: String? = "", val date: String? = "", val index: Int = 0) :
    Serializable