package com.graduate.datn.base

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import com.graduate.datn.R

class ViewController(
    private val layoutId: Int = 0,
    private val fragmentManager: FragmentManager,
) {


    private val listFragment: ArrayList<BaseFragment> = arrayListOf()
    var currentFragment: BaseFragment? = null
        private set

    fun <T : BaseFragment> replaceFragment(type: Class<T>, data: Bundle? = null) {
        try {
            currentFragment = type.newInstance()
        } catch (e: InstantiationException) {
            println("Error replace fragment")
        } catch (e: IllegalAccessException) {
            println("Error replace fragment")
        }
        currentFragment?.arguments = (data)
        currentFragment?.setVC(this)
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.setCustomAnimations(R.anim.trans_left_in,
            R.anim.trans_left_out,
            R.anim.trans_right_in,
            R.anim.trans_right_out)
        currentFragment?.let {
            fragmentTransaction.replace(layoutId, it).commitAllowingStateLoss()
            listFragment.clear()
            listFragment.add(it)
        }

    }

    fun <T : BaseFragment> addFragment(
        type: Class<T>,
        data: Bundle? = null,
        hasAnimation: Boolean = true,
        isHideOldFragment: Boolean = true,
    ) {
        val fragmentTransaction = fragmentManager.beginTransaction()
        if (currentFragment != null) {
            if (isHideOldFragment) {
                fragmentTransaction.hide(currentFragment!!)
            }
        }
        if (hasAnimation) {
            fragmentTransaction.setCustomAnimations(R.anim.trans_right_in, R.anim.trans_right_in)
        } else {
            fragmentTransaction.setCustomAnimations(R.anim.animation_none, R.anim.animation_none)
        }
        var newFragment: T? = null
        try {
            newFragment = type.newInstance()

        } catch (e: InstantiationException) {
            println("Error add fragment")
        } catch (e: IllegalAccessException) {
            println("Error add fragment")
        }
        if (newFragment != null && data != null) {
            newFragment.arguments = (data)
        }
        if (newFragment != null) {
            newFragment.setVC(this)
            fragmentTransaction.add(layoutId, newFragment, type.simpleName)
            if (currentFragment != null) {
                if (hasAnimation) {
                    fragmentTransaction.setCustomAnimations(R.anim.animation_in_delay,
                        R.anim.animation_in_delay)
                } else {
                    fragmentTransaction.setCustomAnimations(R.anim.animation_none,
                        R.anim.animation_none)
                }
                fragmentTransaction.setMaxLifecycle(currentFragment!!, Lifecycle.State.STARTED)
            }
            fragmentTransaction.commitAllowingStateLoss()
        }
        newFragment?.let {
            listFragment.add(newFragment)
        }
        currentFragment = newFragment
    }

    fun backFromAddFragment(data: Bundle? = null): Boolean {
        return if (listFragment.size >= 2) {
            listFragment.removeAt(listFragment.size - 1)
            val fragmentTransaction = fragmentManager.beginTransaction()
            fragmentTransaction.setCustomAnimations(R.anim.trans_right_out, R.anim.trans_right_out)
            currentFragment?.let {
                fragmentTransaction.remove(it)
            }
            currentFragment = listFragment.lastOrNull()
            if (data != null) {
                currentFragment?.arguments = (data)
            }
            currentFragment?.apply {
                fragmentTransaction.setMaxLifecycle(this, Lifecycle.State.RESUMED)
                setVC(this@ViewController)
            }
            fragmentTransaction.setCustomAnimations(R.anim.animation_none, R.anim.animation_none)
            fragmentTransaction.show(currentFragment!!)
            fragmentTransaction.commitAllowingStateLoss()
            currentFragment?.backFromAddFragment()
            true
        } else {
            false
        }
    }

    fun removeAllFragment() {
        val fragmentTransaction = fragmentManager.beginTransaction()
        for (i in listFragment.size - 1 downTo 0) {
            fragmentTransaction.remove(listFragment[i])
        }
        listFragment.clear()
        fragmentTransaction.commitAllowingStateLoss()
    }

    fun removeAllFragmentExceptFirst(data: Bundle? = null): Boolean {
        return if (listFragment.size >= 2) {
            val fragmentTransaction = fragmentManager.beginTransaction()
            for (i in listFragment.size - 1 downTo 1) {
                fragmentTransaction.remove(listFragment[i])
            }
            currentFragment = listFragment.firstOrNull()
            listFragment.clear()
            currentFragment?.let {
                listFragment.add(it)
            }
            if (data != null) {
                currentFragment?.arguments = (data)
            }
            currentFragment?.let {
                it.setVC(this)
                fragmentTransaction.setMaxLifecycle(it, Lifecycle.State.RESUMED)
                fragmentTransaction.setCustomAnimations(R.anim.trans_left_in,
                    R.anim.trans_left_out,
                    R.anim.trans_right_in,
                    R.anim.trans_right_out)
                fragmentTransaction.show(it)
                fragmentTransaction.commitAllowingStateLoss()
            }
            currentFragment?.backFromAddFragment()
            true
        } else {
            false
        }
    }

    fun backFromAddFragmentWithCondition(data: Bundle? = null, numFragments: Int): Boolean {
        return if (listFragment.size >= numFragments) {
            val fragmentTransaction = fragmentManager.beginTransaction()
            val listRemoveFragments = java.util.ArrayList<Fragment>()
            for (i in listFragment.size - 1 downTo listFragment.size - numFragments) {
                listRemoveFragments.add(listFragment[i])
                fragmentTransaction.remove(listFragment[i])
            }
            listFragment.removeAll(listRemoveFragments.toSet())
            currentFragment = listFragment.lastOrNull()
            if (data != null) {
                currentFragment?.arguments = (data)
            }
            currentFragment?.setVC(this)
            currentFragment?.userVisibleHint = true
            fragmentTransaction.setCustomAnimations(R.anim.animation_none, R.anim.animation_none)
            currentFragment?.let {
                fragmentTransaction.show(currentFragment!!)
            }
            fragmentTransaction.commitAllowingStateLoss()
            currentFragment!!.backFromAddFragment()
            true
        } else {
            false
        }
    }

    fun getCurrentSize() = listFragment.size
}