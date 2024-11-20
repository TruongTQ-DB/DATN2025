package com.graduate.datn

import android.app.Application
import android.content.Context
import com.facebook.FacebookSdk
import com.facebook.appevents.AppEventsLogger
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class BaseApplication : Application() {


    companion object{
        lateinit var context: Context
    }

    override fun onCreate() {
        super.onCreate()
        FacebookSdk.sdkInitialize(this)
        AppEventsLogger.activateApp(this)
        context = applicationContext;

    }


}
