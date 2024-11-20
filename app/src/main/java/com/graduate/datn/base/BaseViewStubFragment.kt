package com.graduate.datn.base

import android.os.Bundle
import android.view.View
import androidx.annotation.CallSuper
import androidx.annotation.LayoutRes
import com.graduate.datn.R
import kotlinx.android.synthetic.main.stub_fragment.*

abstract class BaseViewStubFragment : BaseFragment() {

    private var hasInflated = false
    private var visible = false

    override fun backFromAddFragment() {

    }

    override val layoutId: Int
        get() = R.layout.stub_fragment

    override fun initListener() {
    }

    override fun backPressed(): Boolean {
        getVC().backFromAddFragment(null)
        return false
    }


    override fun initView() {
        fragmentViewStub!!.layoutResource = getViewStubLayoutResource()
        if (visible && !hasInflated) {
            val inflatedView = fragmentViewStub!!.inflate()
            onCreateViewAfterViewStubInflated(inflatedView, mSavedInstanceState)
            afterViewStubInflated(view)
        }
    }

    override fun initData() {

    }

    protected abstract fun onCreateViewAfterViewStubInflated(
        inflatedView: View,
        savedInstanceState: Bundle?,
    )


    @LayoutRes
    protected abstract fun getViewStubLayoutResource(): Int

    @CallSuper
    protected fun afterViewStubInflated(originalViewContainerWithViewStub: View?) {
        hasInflated = true
    }

    override fun onResume() {
        super.onResume()
        visible = true
        if (fragmentViewStub != null && !hasInflated) {
            val inflatedView = fragmentViewStub!!.inflate()
            onCreateViewAfterViewStubInflated(inflatedView, mSavedInstanceState)
            afterViewStubInflated(view)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        hasInflated = false
    }

    override fun onPause() {
        super.onPause()
        visible = false
    }

    override fun onDetach() {
        super.onDetach()
        hasInflated = false
    }
}