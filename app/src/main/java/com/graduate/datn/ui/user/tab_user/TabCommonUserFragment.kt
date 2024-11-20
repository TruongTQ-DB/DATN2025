package com.graduate.datn.ui.user.tab_user

import android.os.Bundle
import android.view.View
import com.graduate.datn.R
import com.graduate.datn.base.BaseViewStubFragment
import com.graduate.datn.base.ViewController
import com.graduate.datn.ui.admin.home.HomeFragment
import com.graduate.datn.ui.common.account.AccountFragment
import com.graduate.datn.ui.common.notification.NotificationFragment
import com.graduate.datn.ui.user.container_user.TabContainerUserFragment.Companion.KEY_TAB_USER_FRAGMENT
import com.graduate.datn.ui.user.container_user.TabContainerUserFragment.Companion.TAB_ACCOUNT
import com.graduate.datn.ui.user.container_user.TabContainerUserFragment.Companion.TAB_HOME
import com.graduate.datn.ui.user.container_user.TabContainerUserFragment.Companion.TAB_NOTIFICATION
import com.graduate.datn.ui.user.container_user.TabContainerUserFragment.Companion.TAB_SCHEDULE
import com.graduate.datn.ui.user.list_schedule_user.ListScheduleUserFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_tab_common.*

@AndroidEntryPoint
class TabCommonUserFragment : BaseViewStubFragment() {

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
            if (it.containsKey(KEY_TAB_USER_FRAGMENT)) {
                when (it.getInt(KEY_TAB_USER_FRAGMENT)) {
                    TAB_HOME -> {
                        getVC().addFragment(HomeFragment::class.java)
                    }
                    TAB_SCHEDULE -> {
                        getVC().addFragment(ListScheduleUserFragment::class.java)
                    }
                    TAB_NOTIFICATION -> {
                        getVC().addFragment(NotificationFragment::class.java)
                    }
                    TAB_ACCOUNT -> {
                        getVC().addFragment(AccountFragment::class.java)
                    }
                }
            }
        }
    }

    override fun getViewStubLayoutResource() = R.layout.fragment_tab_common
}