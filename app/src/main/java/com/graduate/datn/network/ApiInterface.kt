package com.graduate.datn.network


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
import com.graduate.datn.entity.patient.ScheduleHealthCheck
import com.graduate.datn.entity.profile.Profile
import com.graduate.datn.entity.profile.ProfileReponse
import com.graduate.datn.entity.request.RreateRequest
import com.graduate.datn.entity.request.SendNotificationRequest
import com.graduate.datn.entity.request.UserRequest
import com.graduate.datn.entity.response.LoginResponce
import com.graduate.datn.entity.response.SendNotificationResponse
import io.reactivex.Single
import retrofit2.http.*

interface ApiInterface {

    @GET("search1")
    @Headers("Content-Type: application/json", "lang: vi")
    fun getDataUser(
        @Query("s") keyWord: String,
        @Query("page") page: Int,
    ): Single<BaseListLoadMoreResponse<User>>

    //    Login
    @POST("auth/login")
    fun login(
        @Query("email_work") email_work: String,
        @Query("password") password: String,
    )
            : Single<BaseObjectResponse<LoginResponce>>

    //    Logout
    @POST("logout")
    fun logout(): Single<BaseResponse>

    @GET("work_logs/getDateCheckInCurrent")
    fun getDateWorkLog(): Single<BaseListResponse<DateWorkLog>>

    @POST("work_logs/getDateWorkLogByUserId")
    fun getListDateWorkLog(@Query("sort_by") sort_by: String,
                       @Query("page") page: Int): Single<BaseObjectResponse<ListCalendar>>

    @POST("profile/updateProfile")
    fun updateUserProfile(@Body profile: ProfileReponse): Single<BaseObjectResponse<ProfileReponse>>

    @GET("user/getUserProfile")
    fun getUserProfile(): Single<BaseObjectResponse<Profile>>

    @POST("dayoffs/createDayOff")
    fun createRequest(): Single<BaseObjectResponse<Profile>>

//    @GET("dayoffs/getListDayOff")
//    fun getListRequest(@Query("sort_by") sort_by: String,
//                       @Query("page_size") page_size: String,
//                       @Query("address") address: String): Single<BaseListLoadMoreResponse<Profile>>

    @GET("dayoffs/getListBoss")
    fun getListBoss(): Single<BaseListResponse<ListBoss>>

    @POST("dayoffs/getListDateOffByIDUser")
    fun getListRequest(@Query("sort_by") sort_by: String,
                       @Query("page_size") page_size: Int,
                       @Query("page") page: Int): Single<BaseObjectResponse<UserRequest>>

    @POST("dayoffs/createDayOff")
    fun createRequest(@Query("user_id") user_id: Int,
                       @Query("type") type: Int,
                       @Query("time") time: Int,
                      @Query("from_date") from_date: String,
                      @Query("to_date") to_date: String,
                      @Query("work_off") work_off: Double,
                      @Query("approved_by") approved_by: Int,
                      @Query("reason") reason: String,
                      @Query("status") status: Int): Single<BaseObjectResponse<RreateRequest>>











    @GET("user/specialty")
    fun getDataSpecialtys(): Single<BaseListResponse<SpecialtysOrService>>

    //    Data Service
    @GET("user/services")
    fun getDataServices(): Single<BaseListResponse<SpecialtysOrService>>

    //    Get Data Test
    @GET("user/medical-history")
    fun getDatamedicalHistory(@Query("page") page: String): Single<BaseListLoadMoreResponse<Test>>

    //    Get Data Test
    @GET("user/schedule-health-check")
    fun getDataScheduleHealthCheck(@Query("page") page: String): Single<BaseListLoadMoreResponse<ScheduleHealthCheck>>

    @GET("user/schedule-health-check/{id}/detail")
    fun getDetailedMedicalExamination(@Path("id") id: Int): Single<BaseObjectResponse<DetailedMedicalExamination>>

    @POST("send")
    fun sendNotification(@Body request: SendNotificationRequest): Single<SendNotificationResponse>
}
