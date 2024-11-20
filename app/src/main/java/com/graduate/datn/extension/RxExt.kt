package com.graduate.datn.extension

import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.TimeUnit

fun <T> Single<T>.backgroundThread(): Single<T> {
    return this.subscribeOn(io.reactivex.schedulers.Schedulers.io())
        .observeOn(io.reactivex.android.schedulers.AndroidSchedulers.mainThread())
}

fun <T> Observable<T>.backgroundThread(): Observable<T> {
    return this.subscribeOn(io.reactivex.schedulers.Schedulers.io())
        .observeOn(io.reactivex.android.schedulers.AndroidSchedulers.mainThread())
}

fun Completable.backgroundThread(): Completable {
    return this.subscribeOn(io.reactivex.schedulers.Schedulers.io())
        .observeOn(io.reactivex.android.schedulers.AndroidSchedulers.mainThread())
}

fun completableTimer(func: () -> Unit, timer: Long = 2000L): Disposable {
    return Completable.timer(timer, TimeUnit.MILLISECONDS)
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe ({
            func()
        },{})
}