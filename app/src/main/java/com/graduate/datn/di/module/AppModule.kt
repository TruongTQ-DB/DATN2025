package com.graduate.datn.di.module

import android.app.Application
import android.content.Context
import com.graduate.datn.share_preference.SharePrefe
import com.graduate.datn.share_preference.SharePreference
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class AppModule {
    @Binds
    @Singleton
    abstract fun provideContext(application: Application) : Context

    @Binds
    @Singleton
    abstract fun SharePrefer(
        sharePrefe: SharePrefe
    ): SharePreference

}