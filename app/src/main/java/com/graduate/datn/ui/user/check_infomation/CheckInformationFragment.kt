package com.graduate.datn.ui.user.check_infomation

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.viewModels
import com.graduate.datn.R
import com.graduate.datn.base.BaseFragment
import com.graduate.datn.entity.User
import com.graduate.datn.extension.onAvoidDoubleClick
import com.graduate.datn.ui.user.make_appointment_time.BookingDaytime
import com.graduate.datn.ui.user.schedule_information.ScheduleInformationFragment
import com.graduate.datn.utils.BundleKey
import com.graduate.datn.utils.Constant
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.check_information_fragment.*

@AndroidEntryPoint
class CheckInformationFragment : BaseFragment() {
    private val viewModel: CheckInformationViewModel by viewModels()
    private lateinit var auth: FirebaseAuth
    override fun backFromAddFragment() {
        arguments?.let {
//            if (it.containsKey(BundleKey.KEY_UPDATE_PROFILE_MEDICAL)) {
//                viewModel.dataProfileNew = it.getSerializable(BundleKey.KEY_UPDATE_PROFILE_MEDICAL) as UpdateProfileMedicalResponse
//                resetUI(viewModel.dataProfileNew)
//            }
        }
    }

    override val layoutId: Int
        get() = R.layout.check_information_fragment

    override fun initView() {
        auth = Firebase.auth
        auth.currentUser?.let {
            FirebaseFirestore.getInstance().collection(Constant.TABLE_USER)
                .whereEqualTo("id", auth.currentUser!!.uid)
                .get()
                .addOnSuccessListener { querySnapshot ->
                    if (querySnapshot.isEmpty) {
                        Log.d("FCM", "Không có id này")
                    } else {
                        querySnapshot.map {
                            val user = it.toObject(User::class.java)
                            setUpView(user)
                            viewModel.user = user
                        }
                    }
                }
                .addOnFailureListener { e ->
                    Log.d("FCM", "update token fail: $e")
                }
        }
    }

    private fun setUpView(user: User) {
        tv_name.text = user.name
        tv_birthday.text = user.birthday
        tv_email.text = user.email
        tv_phone.text = user.phone
        tv_sex.text = getGander(user.gander)
        tv_address.text = user.address
    }

    private fun getGander(gander: Int?): String {
        return when(gander) {
            1 -> {
                "Nam"
            }
            2 -> {
                "Nữ"
            }
            3 -> {
                "Khác"
            }
            else -> {
                ""
            }
        }
    }


    override fun initData() {
        arguments?.let {
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
                    viewModel.docterNameId = it.getSerializable(BundleKey.KEY_DOCTER_NAME) as User?
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
                if (it.containsKey(BundleKey.KEY_INDEX_TIME)) {
                    viewModel.indexTime = it.getSerializable(BundleKey.KEY_INDEX_TIME) as BookingDaytime?
                }
                it.remove(BundleKey.KEY_ADDRESS_ID)
                it.remove(BundleKey.KEY_ADDRESS_NAME)
                it.remove(BundleKey.KEY_ADDRESS)
                it.remove(BundleKey.KEY_SERVICE_ID)
                it.remove(BundleKey.KEY_OPTIONAL_SERVICE_ID)
                it.remove(BundleKey.KEY_OPTIONAL_SERVICE_PRICE)
                it.remove(BundleKey.KEY_DOCTER_NAME)
                it.remove(BundleKey.KEY_BOOK_DATE)
                it.remove(BundleKey.KEY_INFORMATION_USER)
                it.remove(BundleKey.KEY_BOOK_INTEND_TIME)
                it.remove(BundleKey.KEY_INDEX_TIME)
            }
        }
    }

    override fun initListener() {
        header.onLeftClick = {
            backPressed()
        }

        btn_next.onAvoidDoubleClick {
            Log.d("thiss", viewModel.indexTime.toString())
            getVC().addFragment(ScheduleInformationFragment::class.java, Bundle().apply {
                putString(BundleKey.KEY_ADDRESS_ID, viewModel.addressId)
                putString(BundleKey.KEY_ADDRESS_NAME, viewModel.addressName)
                putString(BundleKey.KEY_ADDRESS, viewModel.address)
                putString(BundleKey.KEY_SERVICE_ID, viewModel.serviceId)
                putString(BundleKey.KEY_OPTIONAL_SERVICE_ID, viewModel.optionalServiceId)
                putString(BundleKey.KEY_SERVICE_NAME, viewModel.serviceName)
                putString(BundleKey.KEY_OPTIONAL_SERVICE_NAME, viewModel.optionalServiceName)
                putString(BundleKey.KEY_OPTIONAL_SERVICE_PRICE, viewModel.optionalServicePrice)
                putSerializable(BundleKey.KEY_DOCTER_NAME, viewModel.docterNameId)
                putString(BundleKey.KEY_BOOK_DATE, viewModel.date)
                putString(BundleKey.KEY_BOOK_TIME_FROM,viewModel.timeFrom)
                putString(BundleKey.KEY_BOOK_TIME_TO,viewModel.timeTo)
                putString(BundleKey.KEY_BOOK_INTEND_TIME,viewModel.intendTime)
                putSerializable(BundleKey.KEY_INDEX_TIME,viewModel.indexTime)
                putSerializable(BundleKey.KEY_INFORMATION_USER,viewModel.user)
            })
        }
    }

    override fun backPressed(): Boolean {
        getVC().backFromAddFragment()
        return false
    }
}