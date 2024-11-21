package com.graduate.datn.ui.docterName.container_docter

import android.os.Bundle
import androidx.fragment.app.Fragment
import com.graduate.datn.R
import com.graduate.datn.adapter.viewPager.ViewPegerBottomNavigation
import com.graduate.datn.base.BaseFragment
import com.graduate.datn.ui.common.profile.ProfileFragment
import kotlinx.android.synthetic.main.fragment_tab_container_user.*

class TabContainerDocterFragment : BaseFragment() {
    private lateinit var mPegerAdapter: ViewPegerBottomNavigation<Fragment>
    private val mFragments = arrayListOf<Fragment>(
        getFragment(TAB_HOME),
        getFragment(TAB_USER_DOCTER),
        getFragment(TAB_SCHEDULE_DOCTER),
        getFragment(TAB_NOTIFICATION_DOCTER),
        getFragment(TAB_PROFILE_DOCTER))

    private fun getFragment(index: Int) = TabCommonDocterFragment().apply {
        arguments = Bundle().apply {
            putInt(KEY_TAB_DOCTER_FRAGMENT, index)
        }
    }

    override fun backFromAddFragment() {

    }

    override val layoutId: Int
        get() = R.layout.fragment_tab_container_docter

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
                R.id.menu_customer -> {
                    setUpFragment(TAB_USER_DOCTER)
                    true
                }
                R.id.menu_schedule -> {
                    setUpFragment(TAB_SCHEDULE_DOCTER)
                    true
                }
                R.id.menu_notification -> {
                    setUpFragment(TAB_NOTIFICATION_DOCTER)
                    true
                }
                R.id.menu_account -> {
                    setUpFragment(TAB_PROFILE_DOCTER)
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
        vp_with_bottom_navigation.setCurrentItem(TAB_PROFILE_DOCTER, false)
        bnv_home.menu.getItem(TAB_PROFILE_DOCTER).isChecked = true
        (mFragments[vp_with_bottom_navigation.currentItem] as? BaseFragment)?.getVC()
            ?.removeAllFragmentExceptFirst()

        if (isDetail) {
            (mFragments[vp_with_bottom_navigation.currentItem] as? BaseFragment)?.getVC()
                ?.addFragment(ProfileFragment::class.java)
        }
    }

    fun moveToTabNotification() {
        vp_with_bottom_navigation.setCurrentItem(TAB_NOTIFICATION_DOCTER, true)
        bnv_home.menu.getItem(TAB_NOTIFICATION_DOCTER).isChecked = true
        (mFragments[vp_with_bottom_navigation.currentItem] as? BaseFragment)?.getVC()
            ?.removeAllFragmentExceptFirst()
    }

    fun moveToTabSchedule() {
        vp_with_bottom_navigation.setCurrentItem(TAB_SCHEDULE_DOCTER, true)
        bnv_home.menu.getItem(TAB_SCHEDULE_DOCTER).isChecked = true
        (mFragments[vp_with_bottom_navigation.currentItem] as? BaseFragment)?.getVC()
            ?.removeAllFragmentExceptFirst()
    }

    fun moveToTabCustomer() {
        vp_with_bottom_navigation.setCurrentItem(TAB_USER_DOCTER, true)
        bnv_home.menu.getItem(TAB_USER_DOCTER).isChecked = true
        (mFragments[vp_with_bottom_navigation.currentItem] as? BaseFragment)?.getVC()
            ?.removeAllFragmentExceptFirst()
    }


    companion object {
        const val KEY_TAB_DOCTER_FRAGMENT = "KEY_TAB_FRAGMENT_DOCTER"
        const val TAB_HOME = 0
        const val TAB_PROFILE_DOCTER = 4
        const val TAB_NOTIFICATION_DOCTER = 3
        const val TAB_SCHEDULE_DOCTER = 2
        const val TAB_USER_DOCTER = 1
    }
}