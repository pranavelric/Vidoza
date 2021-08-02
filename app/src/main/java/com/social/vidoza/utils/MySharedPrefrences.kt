package com.social.vidoza.utils

import android.content.Context
import android.content.SharedPreferences
import com.social.vidoza.R
import com.social.vidoza.utils.Constants
import com.social.vidoza.utils.Constants.APP_THEME
import com.social.vidoza.utils.Constants.BACKGROUND_IMAGE
import com.social.vidoza.utils.Constants.NIGHT_MODE_ENABLED
import com.social.vidoza.utils.Constants.NOT_FOUND
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

    fun setTheme(id: Int) {
        editor.putInt(APP_THEME, id)
        editor.commit()
    }

    fun getAppTheme(): Int {

        return sp.getInt(APP_THEME, R.style.Theme_Vidoza)
    }

    fun setBackgroundImage(path: String) {
        editor.putString(BACKGROUND_IMAGE, path)
        editor.commit()
    }

    fun getBrackgroundImage(): String? {
        return sp.getString(BACKGROUND_IMAGE, NOT_FOUND)
    }


}