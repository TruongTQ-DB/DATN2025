package com.graduate.datn.network

import com.graduate.datn.entity.patient.ScheduleHealthCheck
import com.graduate.datn.base.entity.BaseListLoadMoreResponse
import com.graduate.datn.base.entity.BaseListResponse
import com.graduate.datn.base.entity.BaseObjectResponse
import com.graduate.datn.base.entity.BaseResponse
import com.graduate.datn.entity.Test.Test
import com.graduate.datn.entity.User
import com.graduate.datn.entity.boss.ListBoss
import com.graduate.datn.entity.calendar.ListCalendar
import com.graduate.datn.entity.dateWorkLog.DateWorkLog
import com.graduate.datn.entity.detailedMedicalExamination.DetailedMedicalExamination
import com.graduate.datn.entity.home.SpecialtysOrService
import com.graduate.datn.entity.profile.Profile
import com.graduate.datn.entity.profile.ProfileReponse
import com.graduate.datn.entity.request.RreateRequest
import com.graduate.datn.entity.request.SendNotificationRequest
import com.graduate.datn.entity.request.UserRequest
import com.graduate.datn.entity.response.LoginResponce
import com.graduate.datn.entity.response.SendNotificationResponse
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class Repository @Inject constructor(val apiInterface: ApiInterface) {
    fun getData(pageIndex:Int): Single<BaseListLoadMoreResponse<User>> {
        return apiInterface.getDataUser("f",pageIndex)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }

    fun login(username:String,password:String): Single<BaseObjectResponse<LoginResponce>> {
        return apiInterface.login(username, password)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }
    fun logout(): Single<BaseResponse> {
        return apiInterface.logout()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }

    fun getUserProfile(): Single<BaseObjectResponse<Profile>> {
        return apiInterface.getUserProfile()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }

    fun updateUserProfile(profile: ProfileReponse): Single<BaseObjectResponse<ProfileReponse>> {
        return apiInterface.updateUserProfile(profile)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }

    fun getDateWorkLog(): Single<BaseListResponse<DateWorkLog>> {
        return apiInterface.getDateWorkLog()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }

    fun getListDateWorkLog(page: Int): Single<BaseObjectResponse<ListCalendar>> {
        return apiInterface.getListDateWorkLog("DESC", page)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }

    fun getDataSpecialtys(): Single<BaseListResponse<SpecialtysOrService>> {
        return apiInterface.getDataSpecialtys()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }

    fun getDataService(): Single<BaseListResponse<SpecialtysOrService>> {
        return apiInterface.getDataServices()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }

    fun getDatamedicalHistory(page: String): Single<BaseListLoadMoreResponse<Test>> {
        return apiInterface.getDatamedicalHistory( page)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }

    fun getScheduleHealthCheck(page: String): Single<BaseListLoadMoreResponse<ScheduleHealthCheck>> {
        return apiInterface.getDataScheduleHealthCheck( page)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }

    fun getDetailedMedicalExamination(id: Int): Single<BaseObjectResponse<DetailedMedicalExamination>> {
        return apiInterface.getDetailedMedicalExamination(id)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }

    fun getListBoss(): Single<BaseListResponse<ListBoss>> {
        return apiInterface.getListBoss()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }

    fun getListRequest(page: Int): Single<BaseObjectResponse<UserRequest>> {
        return apiInterface.getListRequest("DESC", 10, page)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }

    fun createRequest(user_id: Int, type: Int, time: Int, from_date: String, to_date: String, work_off : Double, approved_by: Int, reason: String): Single<BaseObjectResponse<RreateRequest>> {
        return apiInterface.createRequest(user_id, type, time, from_date, to_date, work_off, approved_by, reason, 0)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }
    fun sendNotification(request: SendNotificationRequest): Single<SendNotificationResponse> {
        return apiInterface.sendNotification(request)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }
}