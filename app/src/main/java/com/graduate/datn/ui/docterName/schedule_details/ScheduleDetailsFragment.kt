package com.graduate.datn.ui.docterName.schedule_details

import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.fragment.app.viewModels
import com.graduate.datn.R
import com.graduate.datn.base.BaseFragment
import com.graduate.datn.custom.dialog.CancelScheduleDialog
import com.graduate.datn.entity.User
import com.graduate.datn.entity.request.Notification
import com.graduate.datn.entity.request.SendNotificationRequest
import com.graduate.datn.entity.response.BookingResponse
import com.graduate.datn.entity.response.NotificationResponse
import com.graduate.datn.extension.*
import com.graduate.datn.utils.BundleKey
import com.graduate.datn.utils.Constant
import com.graduate.datn.utils.Constant.TYPE_NEW
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_schedule_details.*
import java.text.SimpleDateFormat
import java.time.Duration
import java.time.Instant
import java.util.*

@AndroidEntryPoint
class ScheduleDetailsFragment : BaseFragment() {
    private val viewModel: ScheduleDetailsViewModel by viewModels()
    private val db = FirebaseFirestore.getInstance()
    private val bookingCollection = db.collection(Constant.TABLE_BOOKING)
    private val mUserCollection = db.collection(Constant.TABLE_USER)
    private val mNotificationCollection = db.collection(Constant.TABLE_NOTIFICATION)
    private val mWorkScheduleCollection = db.collection(Constant.TABLE_WORK_SCHEDULE)
    private lateinit var auth: FirebaseAuth
    private lateinit var bookingDate: String
    private val cancelDialog: CancelScheduleDialog by lazy { CancelScheduleDialog(requireContext()) }

    override fun backFromAddFragment() {

    }

    override val layoutId: Int
        get() = R.layout.fragment_schedule_details

    override fun initView() {
        viewModel.phone = null
        auth = Firebase.auth
        viewModel.idSchedule = null
        viewModel.data = null
    }

    override fun initData() {
        arguments?.let {
            if (it.containsKey(BundleKey.ID_SCHEDULE)) {
                viewModel.idSchedule = it.getString(BundleKey.ID_SCHEDULE)
            }
        }
        reloadData()
    }

    override fun initListener() {
        header.onLeftClick = {
            backPressed()
        }
        ll_call_phone.onAvoidDoubleClick {
            if (!viewModel.phone.isNullOrEmpty()) {
                val phone = viewModel.phone
                val intent = Intent(Intent.ACTION_DIAL)
                intent.data = Uri.parse("tel:$phone")
                requireContext().startActivity(intent)
            }
        }
        btn_cancel.onAvoidDoubleClick {
            if (auth.currentUser?.uid != viewModel.data?.userId && auth.currentUser?.uid != viewModel.data?.docterId) {
                viewModel.idSchedule?.let { it1 ->
                    showLoading()
                    val data = mapOf("status" to TYPE_CANCEL,
                        "reason" to "Vì lý do nhân sự, lịch khám bệnh của bạn đã bị huỷ.")
                    bookingCollection.document(it1).update(data)
                        .addOnSuccessListener {
                            toast("Huỷ Lịch thành công!")
                            updateStatusSchedule(viewModel.data?.docterId,
                                viewModel.data?.date,
                                viewModel.data?.timeFrom)
                            reloadData()
                            saveNotification(isCancel = true)
                            getTokenUser()
                            getTokenBarber()
                            hideLoading()
                            ll_complete.gone()
                        }.addOnFailureListener {
                            toast("Huỷ không thành công!")
                            hideLoading()
                        }
                }
            } else if (auth.currentUser?.uid == viewModel.data?.docterId) {
                if (getTimeCurrent() != null) {
                    showLoading()
                    val timeDifference =
                        calculateTimeDifference(getTimeCurrent()!!, viewModel.data?.timeStamp!!)
                    if (timeDifference < Duration.ofMinutes(15)) {
                        viewModel.idSchedule?.let { it1 ->
                            val data = mapOf("status" to TYPE_CANCEL,
                                "reason" to "Bạn không có mặt đúng thời gian đặt lịch.")
                            bookingCollection.document(it1).update(data)
                                .addOnSuccessListener {
                                    toast("Huỷ Lịch thành công!")
                                    updateStatusSchedule(viewModel.data?.docterId,
                                        viewModel.data?.date,
                                        viewModel.data?.timeFrom)
                                    saveNotification(true)
                                    getTokenUser()
                                    reloadData()
                                    hideLoading()
                                    ll_complete.gone()
                                }.addOnFailureListener {
                                    toast("Huỷ không thành công!")
                                    hideLoading()
                                }
                        }
                    } else {
                        hideLoading()
                        toast("Không đúng thời gian, Huỷ sau 15 phút của Lịch!")
                    }
                } else {
                    hideLoading()
                }
            } else {
                cancelDialog.show()
                cancelDialog.onClickBtnYes = {
                    showLoading()
                    viewModel.idSchedule?.let { it1 ->
                        val data = mapOf("status" to TYPE_CANCEL,
                            "reason" to it)
                        bookingCollection.document(it1).update(data)
                            .addOnSuccessListener {
                                toast("Huỷ Lịch thành công!")
                                updateStatusSchedule(viewModel.data?.docterId,
                                    viewModel.data?.date,
                                    viewModel.data?.timeFrom)
                                saveNotification(true)
                                getTokenBarber()
                                reloadData()
                                hideLoading()
                                ll_complete.gone()
                            }.addOnFailureListener {
                                toast("Huỷ không thành công!")
                                hideLoading()
                            }
                    }
                }
            }
        }

        btn_complete.onAvoidDoubleClick {
            if (auth.currentUser?.uid == viewModel.data?.docterId) {
                if (getTimeCurrent() != null) {
                    showLoading()
                    val timeDifference =
                        calculateTimeDifference(getTimeCurrent()!!, viewModel.data?.timeStamp!!)
                    if (timeDifference < Duration.ofMinutes(20)) {
                        viewModel.idSchedule?.let { it1 ->
                            bookingCollection.document(it1).update("status", TYPE_COMPLETE)
                                .addOnSuccessListener {
                                    toast("Cập nhật thành công!")
                                    saveNotification(false)
                                    getTokenUser()
                                    reloadData()
                                    hideLoading()
                                    ll_complete.gone()
                                }.addOnFailureListener {
                                    toast("Cập nhật không thành công!")
                                    hideLoading()
                                }
                        }
                    } else {
                        hideLoading()
                        toast("Không đúng thời gian, hoàn thành sau 20 phút của Lịch!")
                    }
                }
            }
        }

    }

    override fun backPressed(): Boolean {
        getVC().backFromAddFragment()
        return false
    }

    private fun reloadData() {
        showLoading()
        viewModel.idSchedule?.let {
            bookingCollection.document(it).get()
                .addOnSuccessListener { documentSnapshot ->
                    if (documentSnapshot.exists()) {
                        val data = documentSnapshot.toObject(BookingResponse::class.java)
                        setUpShowInfor(true)
                        setUpData(data)
                    } else {
                        setUpShowInfor(false)
                        toast("Dữ liệu không tồn tại!")
                    }
                }
                .addOnFailureListener { e ->
                    toast("Có lỗi xãy ra!")
                    setUpShowInfor(false)
                }
        }
        hideLoading()
    }


    private fun setUpData(data: BookingResponse?) {
        viewModel.data = data
        Log.e("thiss", "${data?.status == TYPE_NEW && data.status == 0}")
        if (auth.currentUser?.uid == data?.docterId) {
            img_avatar.loadImageUrl(data?.avatarUser)
            tv_name.text = data?.nameUser

            tv_detail_name.text = data?.phoneUser
            viewModel.phone = data?.phoneUser

            Log.e("thiss", "${data?.status == TYPE_NEW && data.status == 0}")
            if (data?.status == TYPE_NEW && data.status == 0) {
                Log.e("thiss", " dk 2: ${data.timeStamp?.seconds!! < Timestamp.now().seconds}")
                if (data.timeStamp?.seconds!! < Timestamp.now().seconds) {
                    setUpShowButton(true)
                    btn_complete.visible()
                    btn_complete.setMargins(left = 0)
                    btn_cancel.gone()
                } else {
                    setUpShowButton(true)
                }
            } else {
                setUpShowButton(false)
            }
        } else if (auth.currentUser?.uid == data?.userId) {
            img_avatar.loadImageUrl(data?.docterAvatar)
            tv_name.text = data?.docterName
            tv_detail_name.text = data?.detailNameDocter
            viewModel.phone = data?.docterPhone
            if (data?.timeStamp?.seconds!! > Timestamp.now().seconds && data.status == 0) {
                setUpShowButton(true)
                btn_complete.gone()
                btn_cancel.visible()
            } else {
                setUpShowButton(false) //e thao tác đến lịch vừa r đi e
            }
        } else {
            img_avatar.loadImageUrl(data?.avatarUser)
            tv_name.text = data?.nameUser
            tv_detail_name.text = data?.detailNameDocter
            viewModel.phone = data?.phoneUser
            if (data?.status == 0) {
                setUpShowButton(true)
                btn_complete.gone()
            }
        }
        tv_status.text = getStatus(data?.status)
        tv_date.text = data?.date
        tv_time_ex.text = data?.timeFrom

        tv_time.text = data?.timeFrom + " - " + data?.timeTo
        tv_address_name.text = data?.clinicShopName
        tv_barber_address.text = data?.clinicShopAddress
        tv_money.text = data?.price +" VND"
        if (data?.status == 2) {
            ll_reason.visible()
            tv_reason.text = data.reason
        }
    }


    private fun getTimeCurrent(): Timestamp? {
        val currentDate =
            SimpleDateFormat("dd/MM/yyyy HH:mm",
                Locale.getDefault()).format(Calendar.getInstance().time)
        return SimpleDateFormat("dd/MM/yyyy HH:mm").parse(currentDate)?.let { Timestamp(it) }
    }

    private fun calculateTimeDifference(timestamp1: Timestamp, timestamp2: Timestamp): Duration {
        val instant1 =
            Instant.ofEpochMilli(timestamp1.seconds * 1000 + timestamp1.nanoseconds / 1000000)
        val instant2 =
            Instant.ofEpochMilli(timestamp2.seconds * 1000 + timestamp2.nanoseconds / 1000000)
        return Duration.between(instant1, instant2)
    }

    private fun setUpShowButton(show: Boolean) {
        if (show) {
            ll_complete.visible()
        } else {
            ll_complete.gone()
        }
    }

    private fun setUpShowInfor(show: Boolean) {
        if (show) {
            nsv_container.visible()
        } else {
            nsv_container.gone()
            ll_complete.gone()
        }
    }


    private fun getStatus(status: Int?): String? {
        return when (status) {
            0 -> {
                context?.let { tv_status.setTextColor(it.getColor(R.color.color_591DA3)) }
                "Chưa khám"
            }
            1 -> {
                context?.let { tv_status.setTextColor(it.getColor(R.color.color_0DE11D)) }
                "Đã khám" +
                        ""
            }
            2 -> {
                context?.let { tv_status.setTextColor(it.getColor(R.color.color_F44747)) }
                "Đã huỷ"
            }
            else -> {
                ""
            }
        }
    }

    private fun updateStatusSchedule(docterId: String?, date: String?, timeFrom: String?) {
        mWorkScheduleCollection
            .whereEqualTo("approve", true)
            .whereEqualTo("date", date)
            .whereEqualTo("idDocterName", docterId)
            .limit(1)
            .get()
            .addOnSuccessListener { documentSnapshot ->
                documentSnapshot.map {
                    val timeRanges = it.get("timeRanges") as List<Map<String, Any>>
                    val indexOfTimeRange = timeRanges.indexOfFirst { timeRange ->
                        timeRange["startTime"] == timeFrom
                    }

                    if (indexOfTimeRange != -1) {
                        val timeRange = timeRanges[indexOfTimeRange] as MutableMap<String, Any>
                        timeRange["stater"] = 0
                        mWorkScheduleCollection
                            .document(it.id)
                            .update("timeRanges", timeRanges)
                            .addOnSuccessListener {
                                Log.d("FCM", "Stater updated successfully")
                            }
                            .addOnFailureListener { e ->
                                Log.d("FCM", "Failed to update stater: $e")
                            }
                    }
                }
            }
            .addOnFailureListener { e ->
                Log.d("FCM", "update token fail: $e")
            }

    }

    private fun getTokenUser() {
        mUserCollection
            .whereEqualTo("id", viewModel.data?.userId)
            .limit(1)
            .get()
            .addOnSuccessListener { querySnapshot ->
                if (querySnapshot.isEmpty) {
                    Log.d("FCM", "Không có id này")
                } else {
                    val user = querySnapshot.map {
                        it.toObject(User::class.java)
                    }
                    user.first().token?.let { token ->
                        sendNotification(token)
                    }
                }
            }
            .addOnFailureListener { e ->
                Log.d("FCM", "update token fail: $e")
            }
    }

    private fun getTokenBarber() {
        mUserCollection
            .whereEqualTo("id", viewModel.data?.docterId)
            .limit(1)
            .get()
            .addOnSuccessListener { querySnapshot ->
                if (querySnapshot.isEmpty) {
                    Log.d("FCM", "Không có id này")
                } else {
                    val barber = querySnapshot.map {
                        it.toObject(User::class.java)
                    }
                    barber.first().token?.let { token ->
                        sendNotification(token)
                    }
                }
            }
            .addOnFailureListener { e ->
                Log.d("FCM", "update token fail: $e")
            }
    }

    private fun saveNotification(isCancel: Boolean) {
        val dataNotif =
            if (isCancel) {
                NotificationResponse(
                    userId = viewModel.data?.userId,
                    docterId = if (auth.currentUser?.uid != viewModel.data?.docterId) viewModel.data?.docterId else "",
                    bookingId = viewModel.idSchedule,
                    title = "Huỷ Lịch Khám bệnh!",
                    message = "Lịch Khám bệnh ${viewModel.data?.timeFrom} ${viewModel.data?.date} đã bị Huỷ!",
                    docterName = viewModel.data?.docterName,
                    nameUser = viewModel.data?.nameUser,
                    date = viewModel.data?.date,
                    timeFrom = viewModel.data?.timeFrom,
                    timeTo = viewModel.data?.timeTo,
                    reportedStatus = 1,
                    status = 0,
                    typeNotification = 0,
                    typeCanSendUserAndBarber = 0,
                    timestamp = getTimeCurrent()
                )
            } else {
                NotificationResponse(
                    userId = viewModel.data?.userId,
                    docterId = "",
                    bookingId = viewModel.idSchedule,
                    title = "Xác nhận Khám bệnh!",
                    message = "Lịch Khám bệnh lúc ${viewModel.data?.timeFrom} ${viewModel.data?.date} đã được !",
                    docterName = viewModel.data?.docterName,
                    nameUser = viewModel.data?.nameUser,
                    date = viewModel.data?.date,
                    timeFrom = viewModel.data?.timeFrom,
                    timeTo = viewModel.data?.timeTo,
                    reportedStatus = 1,
                    status = 0,
                    typeNotification = 0,
                    typeCanSendUserAndBarber = 0,
                    timestamp = getTimeCurrent()
                )
            }
        mNotificationCollection.add(dataNotif)
            .addOnSuccessListener { _ ->
                // Xử lý thành công
            }
            .addOnFailureListener { e ->
                // Xử lý lỗi
            }
    }


    private fun sendNotification(token: String) {
        Log.d("FCM", "token: $token")
//        val token =
//            "c9ugj9e4T3iXzZusey1dXp:APA91bF6wvT8LN6rdzaIlwmmSlB3JrFsBNXXFbsGxH-q-chMeLkSRM6RyoUrPXmIONRD3eL4TXlsn18Qf7l9uv9TPWkOwxknpPj2jOeym9bFCeXKRh5tBjBAyMZSW7G9lrgIdjw6ENj9"
        val data = SendNotificationRequest(
            to = "$token",
            notification = Notification(
                body = "Lịch Khám bệnh lúc ${viewModel.data?.timeFrom} ${
                    viewModel.data?.date
                } đã bị Huỷ!",
                title = "Huỷ Lịch Khám bệnh!"
            )
        )
        viewModel.sendNotification(data)
    }


    companion object {
        const val TYPE_CANCEL = 2
        const val TYPE_COMPLETE = 1
    }
}