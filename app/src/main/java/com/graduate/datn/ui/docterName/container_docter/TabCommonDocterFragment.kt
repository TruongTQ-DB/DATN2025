package com.graduate.datn.ui.docterName.container_docter

import android.os.Bundle
import android.view.View
import com.graduate.datn.R
import com.graduate.datn.base.BaseViewStubFragment
import com.graduate.datn.base.ViewController
import com.graduate.datn.ui.docterName.container_docter.TabContainerDocterFragment.Companion.KEY_TAB_DOCTER_FRAGMENT
import com.graduate.datn.ui.docterName.container_docter.TabContainerDocterFragment.Companion.TAB_HOME
import com.graduate.datn.ui.docterName.container_docter.TabContainerDocterFragment.Companion.TAB_NOTIFICATION_DOCTER
import com.graduate.datn.ui.docterName.container_docter.TabContainerDocterFragment.Companion.TAB_PROFILE_DOCTER
import com.graduate.datn.ui.docterName.container_docter.TabContainerDocterFragment.Companion.TAB_SCHEDULE_DOCTER
import com.graduate.datn.ui.docterName.container_docter.TabContainerDocterFragment.Companion.TAB_USER_DOCTER
import com.graduate.datn.ui.docterName.home_docter.HomeDocterFragment
import com.graduate.datn.ui.docterName.list_booking_customer.ListBookingCustomerFragment
import com.graduate.datn.ui.docterName.list_schedule_docter.ListScheduleDocterFragment
import com.graduate.datn.ui.common.account.AccountFragment
import com.graduate.datn.ui.common.notification.NotificationFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_tab_common.*

@AndroidEntryPoint
class TabCommonDocterFragment : BaseViewStubFragment() {

    override fun backFromAddFragment() {

    }

    override fun backPressed(): Boolean {
        return true
    }

    override fun onCreateViewAfterViewStubInflated(
        inflatedView: View,
        savedInstanceState: Bundle?,
    ) {
        setVC(ViewController(container_tab.id, childFragmentManager))
        arguments?.let {
            if (it.containsKey(KEY_TAB_DOCTER_FRAGMENT)) {
                when (it.getInt(KEY_TAB_DOCTER_FRAGMENT)) {
                    TAB_HOME -> {
                        getVC().addFragment(HomeDocterFragment::class.java)
                    }
                    TAB_USER_DOCTER -> {
                        getVC().addFragment(ListBookingCustomerFragment::class.java)
                    }
                    TAB_SCHEDULE_DOCTER -> {
                        getVC().addFragment(ListScheduleDocterFragment::class.java)
                    }
                    TAB_NOTIFICATION_DOCTER -> {
                        getVC().addFragment(NotificationFragment::class.java)
                    }
                    TAB_PROFILE_DOCTER -> {
                        getVC().addFragment(AccountFragment::class.java)
                    }
                }
            }
        }
    }

    override fun getViewStubLayoutResource() = R.layout.fragment_tab_common
}