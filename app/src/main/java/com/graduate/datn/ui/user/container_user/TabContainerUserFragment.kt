package com.graduate.datn.ui.user.container_user

import android.os.Bundle
import androidx.fragment.app.Fragment
import com.graduate.datn.R
import com.graduate.datn.adapter.viewPager.ViewPegerBottomNavigation
import com.graduate.datn.base.BaseFragment
import com.graduate.datn.ui.common.profile.ProfileFragment
import com.graduate.datn.ui.user.choose_address.ChooseAddressFragment
import com.graduate.datn.ui.user.tab_user.TabCommonUserFragment
import kotlinx.android.synthetic.main.fragment_tab_container_user.*
import kotlinx.android.synthetic.main.fragment_tab_container_user.bnv_home
import kotlinx.android.synthetic.main.fragment_tab_container_user.vp_with_bottom_navigation
import kotlinx.android.synthetic.main.home_screen_fragment.*

class TabContainerUserFragment : BaseFragment() {
    private lateinit var mPegerAdapter: ViewPegerBottomNavigation<Fragment>
    private val mFragments = arrayListOf<Fragment>(
        getFragment(TAB_HOME),
        getFragment(TAB_SCHEDULE),
        getFragment(TAB_NOTIFICATION),
        getFragment(TAB_ACCOUNT))

    private fun getFragment(index: Int) = TabCommonUserFragment().apply {
        arguments = Bundle().apply {
            putInt(KEY_TAB_USER_FRAGMENT, index)
        }
    }

    override fun backFromAddFragment() {

    }

    override val layoutId: Int
        get() = R.layout.fragment_tab_container_user

    override fun initView() {
        setUpView()
    }

    private fun setUpView() {
        mPegerAdapter =
            ViewPegerBottomNavigation(requireContext(), mFragments, childFragmentManager, lifecycle)
        vp_with_bottom_navigation.adapter = mPegerAdapter
        vp_with_bottom_navigation.isUserInputEnabled = false
        vp_with_bottom_navigation.offscreenPageLimit = mFragments.size
    }

    override fun initData() {
    }

    override fun initListener() {
        setUpClickItemBottomNavigation()
    }

    private fun setUpClickItemBottomNavigation() {
        bnv_home.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.menu_home -> {
                    setUpFragment(TAB_HOME)
                    true
                }
                R.id.menu_schedule -> {
                    setUpFragment(TAB_SCHEDULE)
                    true
                }
                R.id.menu_notification -> {
                    setUpFragment(TAB_NOTIFICATION)
                    true
                }
                R.id.menu_account -> {
                    setUpFragment(TAB_ACCOUNT)
                    true
                }
                else -> false
            }
        }
    }

    private fun setUpFragment(position: Int) {
        vp_with_bottom_navigation.setCurrentItem(position, false)
        (mFragments[vp_with_bottom_navigation.currentItem] as? BaseFragment)?.getVC()
            ?.removeAllFragmentExceptFirst()
    }

    override fun backPressed(): Boolean {
        val currentPage = (mFragments[vp_with_bottom_navigation.currentItem] as BaseFragment)
        val currentSize = currentPage.getVC().getCurrentSize()
        return if (currentSize < 2) {
            if (vp_with_bottom_navigation.currentItem == TAB_HOME) {
                true
            } else {
                backHome()
                false
            }
        } else {
            currentPage.getVC().currentFragment?.backPressed() ?: true
        }
    }

    fun backHome() {
        setUpFragment(TAB_HOME)
        bnv_home.selectedItemId = R.id.menu_home
    }

    fun moveToTabInfo(isDetail: Boolean = true, id: Int? = null) {
        vp_with_bottom_navigation.setCurrentItem(TAB_ACCOUNT, false)
        bnv_home.menu.getItem(TAB_ACCOUNT).isChecked = true
        (mFragments[vp_with_bottom_navigation.currentItem] as? BaseFragment)?.getVC()
            ?.removeAllFragmentExceptFirst()

        if (isDetail) {
            (mFragments[vp_with_bottom_navigation.currentItem] as? BaseFragment)?.getVC()
                ?.addFragment(ProfileFragment::class.java)
        }
    }

    fun moveToTab(position: Int, ) {
        vp_with_bottom_navigation.setCurrentItem(position, false)
        (mFragments[vp_with_bottom_navigation.currentItem] as? BaseFragment)?.getVC()
            ?.removeAllFragmentExceptFirst()
        bnv_home.menu.getItem(TAB_HOME).isChecked = true
    }

    fun moveToTabNotification() {
        vp_with_bottom_navigation.setCurrentItem(TAB_NOTIFICATION, false)
        bnv_home.menu.getItem(TAB_NOTIFICATION).isChecked = true
        (mFragments[vp_with_bottom_navigation.currentItem] as? BaseFragment)?.getVC()
            ?.removeAllFragmentExceptFirst()
    }
    fun moveToProfile() {
        vp_with_bottom_navigation.setCurrentItem(TAB_ACCOUNT, false)
        bnv_home.menu.getItem(TAB_ACCOUNT).isChecked = true
        (mFragments[vp_with_bottom_navigation.currentItem] as? BaseFragment)?.getVC()
            ?.removeAllFragmentExceptFirst()
        (mFragments[vp_with_bottom_navigation.currentItem] as? BaseFragment)?.getVC()
            ?.addFragment(ProfileFragment::class.java)
    }

    fun moveToCreateSchedule() {
        vp_with_bottom_navigation.setCurrentItem(TAB_SCHEDULE, false)
        bnv_home.menu.getItem(TAB_SCHEDULE).isChecked = true
        (mFragments[vp_with_bottom_navigation.currentItem] as? BaseFragment)?.getVC()
            ?.removeAllFragmentExceptFirst()
        (mFragments[vp_with_bottom_navigation.currentItem] as? BaseFragment)?.getVC()
            ?.addFragment(ChooseAddressFragment::class.java)
    }

    companion object {
        const val KEY_TAB_USER_FRAGMENT = "KEY_TAB_FRAGMENT_USER"
        const val TAB_ACCOUNT = 3
        const val TAB_NOTIFICATION = 2
        const val TAB_SCHEDULE = 1
        const val TAB_HOME = 0
    }
}