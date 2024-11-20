package com.graduate.datn

import com.graduate.datn.base.BaseActivity
import com.graduate.datn.base.custom.HSBALoadingDialog
import com.graduate.datn.ui.common.splash.SplashFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : BaseActivity() {
    
    override val layoutResId: Int
        get() = R.layout.activity_main
    override val layoutId: Int
        get() = R.id.container

    override fun initListener() {
    }

    override fun initView() {
        getViewController().addFragment(SplashFragment::class.java)
    }

    override fun initData() {
    }

    override fun onDestroy() {
        super.onDestroy()
        HSBALoadingDialog.getInstance(this).destroyLoadingDialog()
    }
}
