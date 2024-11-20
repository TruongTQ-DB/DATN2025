package com.graduate.datn.base

import androidx.lifecycle.ViewModel
import io.reactivex.disposables.CompositeDisposable


open class BaseViewModel : ViewModel() {

    protected var mDisposable = CompositeDisposable()

    override fun onCleared() {
        super.onCleared()
        mDisposable.clear()
    }
}
