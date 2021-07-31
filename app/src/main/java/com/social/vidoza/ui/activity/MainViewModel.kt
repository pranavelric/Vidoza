package com.social.vidoza.ui.activity

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gaming.earningvalleyadmin.utils.ResponseState
import com.social.vidoza.data.repository.MainActivityRepository
import kotlinx.coroutines.launch

class MainViewModel @ViewModelInject constructor(private val mainRepository: MainActivityRepository) :
    ViewModel() {


    private var _checkForUpdate: MutableLiveData<ResponseState<Boolean>> = MutableLiveData()
    val checkForUpdate: LiveData<ResponseState<Boolean>> get() = _checkForUpdate


    fun checForUpdates(versionCode: Int) = viewModelScope.launch {
        _checkForUpdate.postValue(mainRepository.checkForUpdate(versionCode).value)
    }

}