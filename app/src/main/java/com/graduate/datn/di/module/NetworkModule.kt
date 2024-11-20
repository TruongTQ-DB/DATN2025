package com.graduate.datn.di.module

import android.content.Context
import com.graduate.datn.network.ApiInterface
import com.graduate.datn.network.NetworkCheckerInterceptor
import com.graduate.datn.share_preference.SharePreference
import com.graduate.datn.utils.Define

import com.google.gson.GsonBuilder

import java.util.concurrent.TimeUnit

import javax.inject.Singleton

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

@Module
@InstallIn(SingletonComponent::class)
class NetworkModule {

    @Provides
    @Singleton
    fun provideApiInterface(client: OkHttpClient): ApiInterface {
        val gson = GsonBuilder()
            .setLenient()
            .create()

        val retrofit = Retrofit.Builder()
            .baseUrl("https://fcm.googleapis.com/fcm/")
            .client(client)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .build()

        return retrofit.create(ApiInterface::class.java)
    }

    @Provides
    @Singleton
    fun provideHttpClient(context: Context, sharedPref: SharePreference): OkHttpClient {
        val loggingInterceptor = HttpLoggingInterceptor()
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY)

//        val tokenInterceptor = TokenInterceptor()
        val networkCheckerInterceptor = NetworkCheckerInterceptor(context)

        val interceptor = Interceptor { chain ->
            val request: Request = chain.request()
            val builder: Request.Builder = request.newBuilder()
//            Log.d("thiss", "Bearer ${sharedPref.login().token}")
//            builder.addHeader("Authorization", "Bearer ${sharedPref.login().token}")
                builder.addHeader("Authorization", "key=AAAARZJd9r8:APA91bF-PBxxVPpGswXzYbpylxlhn7ykeNj5pnA-EGk1wYRrFD4AXAYAgAqXyQTSsPYB-x6L8UcnjHqWs2-_7baN2fkRFmpeFhJrNYot_XktQvXxHDa5hNXLMQjyjuR_6aoPYq8moIdg")
                chain.proceed(builder.build())
        }
        return OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .addInterceptor(interceptor)
            .addInterceptor(networkCheckerInterceptor)
            .connectTimeout(Define.DEFAULT_TIMEOUT, TimeUnit.SECONDS)
            .readTimeout(Define.DEFAULT_TIMEOUT, TimeUnit.SECONDS)
            .build()
    }


}
