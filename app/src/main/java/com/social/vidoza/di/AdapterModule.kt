package com.social.vidoza.di

import com.social.vidoza.adapter.UserListAdapter
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent


@Module
@InstallIn(ActivityComponent::class)
class AdapterModule {

    @Provides
    fun providesUserListAdapter(): UserListAdapter {
        return UserListAdapter()
    }
}