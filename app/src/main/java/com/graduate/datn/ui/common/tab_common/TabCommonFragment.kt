package com.graduate.datn.ui.common.tab_common

import android.os.Bundle
import android.view.View
import com.graduate.datn.R
import com.graduate.datn.base.BaseViewStubFragment
import com.graduate.datn.base.ViewController
import com.graduate.datn.ui.admin.container_service.ContainerServiceFragment
import com.graduate.datn.ui.admin.homeScreen.HomeScreenFragment.Companion.KEY_TAB_FRAGMENT
import com.graduate.datn.ui.admin.homeScreen.HomeScreenFragment.Companion.TAB_ACCOUNT
import com.graduate.datn.ui.admin.homeScreen.HomeScreenFragment.Companion.TAB_HOME
import com.graduate.datn.ui.admin.homeScreen.HomeScreenFragment.Companion.TAB_LIST_ADDRESS
import com.graduate.datn.ui.admin.homeScreen.HomeScreenFragment.Companion.TAB_USER_MANAGEMENT
import com.graduate.datn.ui.admin.homeScreen.HomeScreenFragment.Companion.TAB_SERVICE
import com.graduate.datn.ui.admin.home_admin.HomeAdminFragment
import com.graduate.datn.ui.admin.listSchedule.ListScheduleAdminFragment
import com.graduate.datn.ui.admin.list_address.ListAddressFragment
import com.graduate.datn.ui.admin.user_management.ContainerUserManagementFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_tab_common.*

@AndroidEntryPoint
class TabCommonFragment : BaseViewStubFragment() {

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
            if (it.containsKey(KEY_TAB_FRAGMENT)) {
                when (it.getInt(KEY_TAB_FRAGMENT)) {
                    TAB_HOME -> {
                        getVC().addFragment(HomeAdminFragment::class.java)
                    }
                    TAB_LIST_ADDRESS -> {
                        getVC().addFragment(ListAddressFragment::class.java)
                    }
                    TAB_SERVICE -> {
                        getVC().addFragment(ContainerServiceFragment::class.java)
                    }
                    TAB_USER_MANAGEMENT -> {
                        getVC().addFragment(ContainerUserManagementFragment::class.java)
                    }
                    TAB_ACCOUNT -> {
                        getVC().addFragment(ListScheduleAdminFragment::class.java)
                    }
                }
            }
        }
    }

    override fun getViewStubLayoutResource() = R.layout.fragment_tab_common
}