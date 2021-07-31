package com.social.vidoza.di

import android.app.Application
import android.content.Context
import com.social.vidoza.utils.NetworkConnection
import com.social.vidoza.utils.NetworkHelper
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class AppModules {

//    @Singleton
//    @Provides
//    fun providesAuthRepository(): AuthRepository {
//        return AuthRepository()
//    }


    @Singleton
    @Provides
    fun providesNetworkHelper(@ApplicationContext context: Context): NetworkHelper {
        return NetworkHelper(context)
    }


    @Singleton
    @Provides
    fun providesNetworkConnection(application: Application): NetworkConnection {
        return NetworkConnection(application)
    }



}