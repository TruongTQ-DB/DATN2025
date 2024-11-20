package com.graduate.datn.rx

import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.PublishSubject

object RxBus {
    private val publisher = PublishSubject.create<Any>()
    val checkChangeInfoSubject = BehaviorSubject.create<RxEvent.ChangeInforUser>()

    fun publish(event: Any) {
        publisher.onNext(event)
    }

    @JvmStatic fun <T> listen(eventType: Class<T>): Observable<T> = publisher.ofType(eventType)
}
