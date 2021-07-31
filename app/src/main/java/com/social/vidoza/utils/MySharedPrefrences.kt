package com.social.vidoza.utils

import android.content.Context
import android.content.SharedPreferences
import com.social.vidoza.utils.Constants
import com.social.vidoza.utils.Constants.NIGHT_MODE_ENABLED
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class MySharedPrefrences @Inject constructor(@ApplicationContext context: Context) {

    private val sp: SharedPreferences by lazy {
        context.getSharedPreferences(Constants.SHARED_PREFRENCE, 0)
    }
    private var editor = sp.edit()

    fun clearSession() {
        editor.clear()
        editor.commit()
    }


    fun setNightModeEnabled(nightMode: Boolean) {
        editor.putBoolean(NIGHT_MODE_ENABLED, nightMode)
        editor.commit()
    }

    fun getIsNightModeEnabled(): Boolean {
        return sp.getBoolean(NIGHT_MODE_ENABLED, false)
    }



}