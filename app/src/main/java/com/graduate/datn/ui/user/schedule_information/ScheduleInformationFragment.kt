package com.graduate.datn.ui.user.schedule_information

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.viewModels
import com.graduate.datn.R
import com.graduate.datn.adapter.recyclerview.BookingItem
import com.graduate.datn.base.BaseFragment
import com.graduate.datn.entity.User
import com.graduate.datn.entity.request.Notification
import com.graduate.datn.entity.request.SendNotificationRequest
import com.graduate.datn.entity.response.BookingResponse
import com.graduate.datn.entity.response.NotificationResponse
import com.graduate.datn.entity.response.SendNotificationResponse
import com.graduate.datn.extension.*
import com.graduate.datn.ui.user.make_appointment_time.BookingDaytime
import com.graduate.datn.ui.user.success_appointment_booking.SuccessAppointmentBookingFragment
import com.graduate.datn.utils.BundleKey
import com.graduate.datn.utils.Constant
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_schedule_information.*
import java.text.SimpleDateFormat
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.*

@AndroidEntryPoint
class ScheduleInformationFragment : BaseFragment() {
    private val viewModel: ScheduleInformationViewModel by viewModels()
    private val db = FirebaseFirestore.getInstance()
    private val mWorkScheduleCollection = db.collection(Constant.TABLE_WORK_SCHEDULE)
    private val mBookingCollection = db.collection(Constant.TABLE_BOOKING)
    private val mUserCollection = db.collection(Constant.TABLE_USER)
    private val mNotificationCollection = db.collection(Constant.TABLE_NOTIFICATION)

    override fun backFromAddFragment() {

    }

    override val layoutId: Int
        get() = R.layout.fragment_schedule_information

    override fun initView() {
        viewModel.idBooking = null
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
            if (it.containsKey(BundleKey.KEY_SERVICE_NAME)) {
                viewModel.serviceName = it.getString(BundleKey.KEY_SERVICE_NAME)
            }
            if (it.containsKey(BundleKey.KEY_OPTIONAL_SERVICE_NAME)) {
                viewModel.optionalServiceName = it.getString(BundleKey.KEY_OPTIONAL_SERVICE_NAME)
            }
            if (it.containsKey(BundleKey.KEY_OPTIONAL_SERVICE_ID)) {
                viewModel.optionalServiceId = it.getString(BundleKey.KEY_OPTIONAL_SERVICE_ID)
            }
            if (it.containsKey(BundleKey.KEY_OPTIONAL_SERVICE_PRICE)) {
                viewModel.optionalServicePrice = it.getString(BundleKey.KEY_OPTIONAL_SERVICE_PRICE)
            }
            if (it.containsKey(BundleKey.KEY_DOCTER_NAME)) {
                viewModel.barberNameId = it.getSerializable(BundleKey.KEY_DOCTER_NAME) as User?
            }
            if (it.containsKey(BundleKey.KEY_BOOK_DATE)) {
                viewModel.date = it.getString(BundleKey.KEY_BOOK_DATE)
            }
            if (it.containsKey(BundleKey.KEY_BOOK_TIME_FROM)) {
                viewModel.timeFrom = it.getString(BundleKey.KEY_BOOK_TIME_FROM)
            }
            if (it.containsKey(BundleKey.KEY_BOOK_TIME_TO)) {
                viewModel.timeTo = it.getString(BundleKey.KEY_BOOK_TIME_TO)
            }
            if (it.containsKey(BundleKey.KEY_BOOK_INTEND_TIME)) {
                viewModel.intendTime = it.getString(BundleKey.KEY_BOOK_INTEND_TIME)
            }
            if (it.containsKey(BundleKey.KEY_INFORMATION_USER)) {
                viewModel.user = it.getSerializable(BundleKey.KEY_INFORMATION_USER) as User?
            }
            if (it.containsKey(BundleKey.KEY_INDEX_TIME)) {
                viewModel.indexTime =
                    it.getSerializable(BundleKey.KEY_INDEX_TIME) as BookingDaytime?
            }
            it.remove(BundleKey.KEY_ADDRESS_ID)
            it.remove(BundleKey.KEY_ADDRESS_NAME)
            it.remove(BundleKey.KEY_SERVICE_ID)
            it.remove(BundleKey.KEY_OPTIONAL_SERVICE_ID)
            it.remove(BundleKey.KEY_OPTIONAL_SERVICE_PRICE)
            it.remove(BundleKey.KEY_DOCTER_NAME)
            it.remove(BundleKey.KEY_BOOK_DATE)
            it.remove(BundleKey.KEY_INFORMATION_USER)
            it.remove(BundleKey.KEY_BOOK_INTEND_TIME)
            it.remove(BundleKey.KEY_INDEX_TIME)
        }

        setUpView()

        viewModel.data.observe(viewLifecycleOwner) {
            if (it is SendNotificationResponse) {
                if (it != null && !it.results[0].messageId.isNullOrEmpty()) {
//                    toast("send thanh coong")
                    Log.d("FCM", "send notification thành công!")
                } else {
                    Log.d("FCM", "send notification that bai!")
                }
            }
        }
    }

    override fun initListener() {
        header.onLeftClick = {
            backPressed()
        }

        btn_confirm.onAvoidDoubleClick {
            checkDuplicateTime()
        }

        btn_pay.onAvoidDoubleClick {
            toast("Chức năng chưa hoàn tất!")
        }
    }

    private fun updateStaterAtPosition() {
        viewModel.indexTime?.id?.let {
            mWorkScheduleCollection.document(it)
                .get()
                .addOnSuccessListener { documentSnapshot ->
                    val timeRanges = documentSnapshot.get("timeRanges") as List<Map<String, Any>>

                    if (viewModel.indexTime!!.index < timeRanges.size) {
                        val timeRange = timeRanges[viewModel.indexTime!!.index].toMutableMap()
                        timeRange["stater"] = 1
                        Log.e ("thiss", timeRange.toString())

                        val updatedTimeRanges = timeRanges.toMutableList()
                        updatedTimeRanges[viewModel.indexTime!!.index] = timeRange
                        Log.e ("thiss","sssssssssssssssss: " + updatedTimeRanges.toString())
                        val updatedData = mapOf("timeRanges" to updatedTimeRanges)
                        mWorkScheduleCollection.document(it).update(updatedData)
                            .addOnSuccessListener {
                                viewModel.apply {
                                    val timestamp =
                                        SimpleDateFormat("dd/MM/yyyy HH:mm").parse(
                                            "${date?.convertDate(DATE_FORMAT_6, DATE_FORMAT_2)} $timeFrom")
                                            ?.let { it1 -> com.google.firebase.Timestamp(it1) }
                                    val dataBooking = BookingResponse(
                                        userId = user?.id,
                                        docterId = barberNameId?.id,
                                        serviceId = serviceId,
                                        optionalServiceId = optionalServiceId,
                                        serviceName = serviceName,
                                        optionalServiceName = optionalServiceName,
                                        docterAvatar = barberNameId?.avatar,
                                        docterName  = barberNameId?.name,
                                        detailNameDocter = barberNameId?.detailname,
                                        docterPhone = barberNameId?.phone,
                                        clinicShopName = addressName,
                                        clinicShopAddress = address,
                                        avatarUser = user?.avatar,
                                        nameUser = user?.name,
                                        phoneUser = user?.phone,
                                        birthdayUser = user?.birthday,
                                        date = date?.convertDate(DATE_FORMAT_6, DATE_FORMAT_2),
                                        timeFrom = timeFrom,
                                        timeTo = timeTo,
                                        price = optionalServicePrice,
                                        timeStamp = timestamp,
                                        timeStampCurrent = getCurrentDaystamp()
                                    )

                                    mBookingCollection.add(dataBooking)
                                        .addOnSuccessListener { documentReference ->
                                            idBooking = documentReference.id
                                            val idBooking = documentReference.id
                                            mUserCollection
                                                .whereEqualTo("id", viewModel.barberNameId?.id)
                                                .get()
                                                .addOnSuccessListener { querySnapshot ->
                                                    if (querySnapshot.isEmpty) {
                                                        hideLoading()
                                                        Log.d("FCM", "Không có id này")
                                                    } else {
                                                        val barber = querySnapshot.map {
                                                            it.toObject(User::class.java)
                                                        }
                                                        barber.first().token?.let { token ->
                                                            sendNotification(token)
                                                        }
                                                        val dataNotif =
                                                            if (barber.first().token.isNullOrEmpty()) {
                                                                NotificationResponse(
                                                                    userId ="",
                                                                    docterId = viewModel.barberNameId?.id,
                                                                    bookingId = idBooking,
                                                                    title = "Đăng ký khám bệnh",
                                                                    message = "Bạn có lịch khám cho Bệnh nhân ${viewModel.user?.name} lúc ${viewModel.timeFrom} ${
                                                                        viewModel.date?.convertDate(
                                                                            DATE_FORMAT_6,
                                                                            DATE_FORMAT_2)
                                                                    }",
                                                                    docterName = viewModel.barberNameId?.name,

                                                                    nameUser = viewModel.user?.name,
                                                                    date = viewModel.date,
                                                                    timeFrom = viewModel.timeFrom,
                                                                    timeTo = viewModel.timeTo,
                                                                    reportedStatus = 0,
                                                                    status = 0,
                                                                    typeNotification = 0,
                                                                    typeCanSendUserAndBarber = 0,
                                                                    timestamp = getTimeCurrent()
                                                                )
                                                            } else {
                                                                NotificationResponse(
                                                                    userId = "",
                                                                    docterId = viewModel.barberNameId?.id,
                                                                    bookingId = idBooking,
                                                                    title = "Đăng ký khám bệnh",
                                                                    message = "Bạn có lịch khám cho Bệnh nhân ${viewModel.user?.name} lúc ${viewModel.timeFrom} ${
                                                                        viewModel.date?.convertDate(
                                                                            DATE_FORMAT_6,
                                                                            DATE_FORMAT_2)
                                                                    }",
                                                                    docterName = viewModel.barberNameId?.name,
                                                                    nameUser = viewModel.user?.name,
                                                                    date = viewModel.date,
                                                                    timeFrom = viewModel.timeFrom,
                                                                    timeTo = viewModel.timeTo,
                                                                    reportedStatus = 1,
                                                                    status = 0,
                                                                    typeNotification = 0,
                                                                    typeCanSendUserAndBarber = 0,
                                                                    timestamp = getTimeCurrent()
                                                                )
                                                            }
                                                        mNotificationCollection.add(dataNotif)
                                                            .addOnSuccessListener { _ ->
                                                                hideLoading()
                                                                bookingSuccess()
                                                            }
                                                            .addOnFailureListener { e ->
                                                                hideLoading()
                                                                Log.d("FCM", "save noti thất bại!")
                                                            }
                                                    }
                                                }
                                                .addOnFailureListener { e ->
                                                    Log.d("FCM", "update token fail: $e")
                                                }
                                        }
                                        .addOnFailureListener { e ->
                                            hideLoading()
                                            toast("Đăng ký lịch khám thất bại, vui lòng chọn thời gian khác!")
                                        }
                                }
                            }
                            .addOnFailureListener { exception ->
                                hideLoading()
                                toast("Vui lòng kiểm tra lại khung giờ!")
                            }
                    }
                }
                .addOnFailureListener { exception ->
                    Log.d("thiss", exception.toString())
                    hideLoading()
                }
        }
    }
    private fun getCurrentDaystamp(): Timestamp? {
        return SimpleDateFormat("dd/MM/yyyy").parse(SimpleDateFormat("dd/MM/yyyy",
            Locale.getDefault()).format(Calendar.getInstance().time))?.let { Timestamp(it) }
    }

    private fun getTimeCurrent(): Timestamp? {
        val currentDate =
            SimpleDateFormat("dd/MM/yyyy HH:mm",
                Locale.getDefault()).format(Calendar.getInstance().time)
        return SimpleDateFormat("dd/MM/yyyy HH:mm").parse(currentDate)?.let { Timestamp(it) }
    }

    private fun checkDuplicateTime() {
        showLoading()
        mBookingCollection
            .whereEqualTo("userId", viewModel.user?.id)
            .whereEqualTo("date", viewModel.date?.convertDate(DATE_FORMAT_6, DATE_FORMAT_2))
            .orderBy("timeFrom")
            .get()
            .addOnSuccessListener { documents ->
                if (documents.isEmpty) {
                    updateStaterAtPosition()
                } else {
                    val dataList = documents.map { documentSnapshot ->
                        val id = documentSnapshot.id
                        val data =
                            documentSnapshot.toObject(BookingResponse::class.java)
                        BookingItem(id, data)
                    }
                    val listCalendar = dataList.mapNotNull { data ->
                        data.data.timeFrom?.let {
                            data.data.timeTo?.let { it1 ->
                                viewModel.timeFrom?.let { it2 ->
                                    viewModel.timeTo?.let { it3 ->
                                        areTimesOverlapping(it,
                                            it1, it2, it3)
                                    }
                                }
                            }
                        }
                    }
                    var checkCalendarExists = false
                    listCalendar.map {
                        if (it) {
                            checkCalendarExists = true
                            return@map
                        }
                    }
                    if (!checkCalendarExists) {
                        updateStaterAtPosition()
                    } else {
                        hideLoading()
                        toast("Bạn đã có Lịch trong khung giờ này!")
                    }
                }
            }
            .addOnFailureListener {
                hideLoading()
                toast("Có lỗi xãy ra!")
            }
    }

    fun areTimesOverlapping(
        timeFromA: String,
        timeToA: String,
        timeFromB: String,
        timeToB: String,
    ): Boolean {
        val formatter = DateTimeFormatter.ofPattern("HH:mm")
        val startTimeA = LocalTime.parse(timeFromA, formatter)
        val endTimeA = LocalTime.parse(timeToA, formatter)
        val startTimeB = LocalTime.parse(timeFromB, formatter)
        val endTimeB = LocalTime.parse(timeToB, formatter)

        if (endTimeA <= startTimeB || startTimeA >= endTimeB) {
            Log.d("thiss",
                "ssssSchedule A: $startTimeA - $endTimeA  Schedule B:  $startTimeB - $endTimeB ")
            return false
        }
        Log.i("thiss", "Schedule A: $startTimeA - $endTimeA  Schedule B:  $startTimeB - $endTimeB ")
        return true
    }

    private fun bookingSuccess() {
        getVC().addFragment(SuccessAppointmentBookingFragment::class.java,
            Bundle().apply {
                putString(BundleKey.KEY_ADDRESS_ID, viewModel.addressId)
                putString(BundleKey.KEY_ADDRESS_NAME, viewModel.addressName)
                putString(BundleKey.KEY_ADDRESS, viewModel.address)
                putString(BundleKey.KEY_SERVICE_ID, viewModel.serviceId)
                putString(BundleKey.KEY_OPTIONAL_SERVICE_ID,
                    viewModel.optionalServiceId)
                putString(BundleKey.KEY_OPTIONAL_SERVICE_PRICE,
                    viewModel.optionalServicePrice)
                putSerializable(BundleKey.KEY_DOCTER_NAME, viewModel.barberNameId)
                putString(BundleKey.KEY_BOOK_DATE,
                    viewModel.date?.convertDate(DATE_FORMAT_6, DATE_FORMAT_2))
                putString(BundleKey.KEY_BOOK_TIME_FROM, viewModel.timeFrom)
                putString(BundleKey.KEY_BOOK_TIME_TO, viewModel.timeTo)
                putString(BundleKey.KEY_BOOK_INTEND_TIME, viewModel.intendTime)
                putSerializable(BundleKey.KEY_INFORMATION_USER, viewModel.user)
                putString(BundleKey.KEY_ID_BOOKING, viewModel.idBooking)
            })
    }

    private fun setUpView() {
        viewModel.apply {
            img_avatar.loadImageUrl(barberNameId?.avatar)
            tv_barber_name.text = barberNameId?.name

            tv_detail_docter.text = barberNameId?.detailname
            tv_user_name.text = user?.name
            tv_user_birthday.text = user?.birthday
            tv_phone.text = user?.phone
            tv_date.text = date?.convertDate(DATE_FORMAT_6, DATE_FORMAT_2)
            tv_time.text = timeFrom
            tv_department_address.text = serviceName
            tv_time_planned.text = intendTime
            tv_address_name.text = addressName
            tv_barber_address.text = address
            tv_money.text = optionalServicePrice +" VND"
        }
    }


    override fun backPressed(): Boolean {
        getVC().backFromAddFragment()
        return false
    }

    private fun sendNotification(token: String) {
        Log.d("FCM", "token: $token")
//        val token =
//            "fvVU5pZXQRunBaBu_dUwEl:APA91bF9hJr0fSnBhVHRlynmrbSc_XxdncJ8FlI_LY3j07jsz9qrTUeqRxSvWdB-ed2-WybUbBQa5m2PewNGjyEl35KHFdNsDq973_DQopn_GaC9CPU4N9Btl7QXX_vQpCliSwXKRGLG"
        val data = SendNotificationRequest(
            to = "$token",
            notification = Notification(
                body = "Bạn có lịch khám lúc ${viewModel.timeFrom} ${
                    viewModel.date?.convertDate(DATE_FORMAT_6,
                        DATE_FORMAT_2)
                }",
                title = "Lịch khám bệnh mới!"
            )
        )
        viewModel.sendNotification(data)
    }
}