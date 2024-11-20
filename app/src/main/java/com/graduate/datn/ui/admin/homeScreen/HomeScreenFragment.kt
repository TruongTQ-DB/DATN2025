package com.graduate.datn.ui.admin.homeScreen

import android.os.Bundle
import androidx.fragment.app.Fragment
import com.graduate.datn.R
import com.graduate.datn.adapter.viewPager.ViewPegerBottomNavigation
import com.graduate.datn.base.BaseFragment
import com.graduate.datn.ui.common.profile.ProfileFragment
import com.graduate.datn.ui.common.tab_common.TabCommonFragment
import kotlinx.android.synthetic.main.home_screen_fragment.*

class HomeScreenFragment : BaseFragment() {
    private lateinit var mPegerAdapter: ViewPegerBottomNavigation<Fragment>
    private val mFragments = arrayListOf<Fragment>(getFragment(TAB_HOME),
        getFragment(TAB_LIST_ADDRESS),
        getFragment(TAB_SERVICE),
        getFragment(TAB_USER_MANAGEMENT),
        getFragment(TAB_ACCOUNT))

    private fun getFragment(index: Int) = TabCommonFragment().apply {
        arguments = Bundle().apply {
            putInt(KEY_TAB_FRAGMENT, index)
        }
    }

    override fun backFromAddFragment() {

    }

    override val layoutId: Int
        get() = R.layout.home_screen_fragment

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
                R.id.menu_calendar -> {
                    setUpFragment(TAB_LIST_ADDRESS)
                    true
                }
                R.id.menu_service -> {
                    setUpFragment(TAB_SERVICE)
                    true
                }
                R.id.menu_request -> {
                    setUpFragment(TAB_USER_MANAGEMENT)
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
//        when (position) {
//            TAB_HOME -> {
//                getVC().addFragment(HomeAdminFragment::class.java)
//            }
//            TAB_LIST_ADDRESS -> {
//                getVC().addFragment(ListAddressFragment::class.java)
//            }
//            TAB_SERVICE -> {
//                getVC().addFragment(ContainerServiceFragment::class.java)
//            }
//            TAB_USER_MANAGEMENT -> {
//                getVC().addFragment(ContainerUserManagementFragment::class.java)
//            }
//            TAB_ACCOUNT -> {
//                getVC().addFragment(ListScheduleAdminFragment::class.java)
//            }
//        }
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

    fun moveToTabRequest(isDetail: Boolean = true, id: Int? = null) {
        vp_with_bottom_navigation.setCurrentItem(TAB_USER_MANAGEMENT, false)
        bnv_home.menu.getItem(TAB_USER_MANAGEMENT).isChecked = true
        (mFragments[vp_with_bottom_navigation.currentItem] as? BaseFragment)?.getVC()
            ?.removeAllFragmentExceptFirst()
        if (isDetail) {
            (mFragments[vp_with_bottom_navigation.currentItem] as? BaseFragment)?.getVC()
                ?.addFragment(ProfileFragment::class.java)
        }
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

    fun moveToTabCalendar(isDetail: Boolean = true, id: Int? = null) {
        vp_with_bottom_navigation.setCurrentItem(TAB_LIST_ADDRESS, false)
        bnv_home.menu.getItem(TAB_LIST_ADDRESS).isChecked = true
        (mFragments[vp_with_bottom_navigation.currentItem] as? BaseFragment)?.getVC()
            ?.removeAllFragmentExceptFirst()

        if (isDetail) {
            (mFragments[vp_with_bottom_navigation.currentItem] as? BaseFragment)?.getVC()
                ?.addFragment(ProfileFragment::class.java)
        }
    }

    companion object {
        const val KEY_TAB_FRAGMENT = "KEY_TAB_FRAGMENT"
        const val TAB_ACCOUNT = 4
        const val TAB_USER_MANAGEMENT = 3
        const val TAB_SERVICE = 2
        const val TAB_LIST_ADDRESS = 1
        const val TAB_HOME = 0
    }
}