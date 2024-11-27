package com.graduate.datn.ui.user.success_appointment_booking

import android.os.Bundle
import androidx.fragment.app.activityViewModels
import com.graduate.datn.R
import com.graduate.datn.base.BaseFragment
import com.graduate.datn.entity.User
import com.graduate.datn.extension.loadImageUrl
import com.graduate.datn.extension.onAvoidDoubleClick
import com.graduate.datn.ui.docterName.schedule_details.ScheduleDetailsFragment
import com.graduate.datn.ui.user.container_user.TabContainerUserFragment
import com.graduate.datn.ui.user.container_user.TabContainerUserFragment.Companion.TAB_HOME
import com.graduate.datn.utils.BundleKey
import com.graduate.datn.utils.Constant
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.fragment_success_appoinment_booking.*

class SuccessAppointmentBookingFragment : BaseFragment() {
    private val viewModel: SuccessAppointmentBookingViewModel by activityViewModels()
    private val db = FirebaseFirestore.getInstance()
    private val mCollection = db.collection(Constant.TABLE_WORK_SCHEDULE)
    override fun backFromAddFragment() {

    }

    override val layoutId: Int
        get() = R.layout.fragment_success_appoinment_booking

    override fun initView() {

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
            if (it.containsKey(BundleKey.KEY_INFORMATION_USER)) {
                viewModel.user = it.getSerializable(BundleKey.KEY_INFORMATION_USER) as User?
            }
            if (it.containsKey(BundleKey.KEY_ID_BOOKING)) {
                viewModel.bookingId = it.getString(BundleKey.KEY_ID_BOOKING)
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
        }

        setUpView()
    }

    private fun setUpView() {
        viewModel.apply {
            img_avatar.loadImageUrl(docterNameId?.avatar)
            tv_barber_name.text = docterNameId?.name
            tv_bn_email.text = docterNameId?.email
            tv_date.text = date
            tv_time.text = timeFrom
            tv_time_planned.text = intendTime
            tv_address.text = address
            tv_department.text = service
        }
    }

    override fun initListener() {
        tv_back_home.onAvoidDoubleClick {
            (parentFragment?.parentFragment as? TabContainerUserFragment)?.moveToTab(TAB_HOME)
        }

        btn_view.onAvoidDoubleClick {
            getVC().addFragment(ScheduleDetailsFragment::class.java, Bundle().apply {
                putString(BundleKey.ID_SCHEDULE, viewModel.bookingId)
            })
        }
    }

    override fun backPressed(): Boolean {

        return false
    }
}