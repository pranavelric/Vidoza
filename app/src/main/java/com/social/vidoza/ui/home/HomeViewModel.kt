package com.social.vidoza.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gaming.earningvalleyadmin.utils.ResponseState
import com.social.vidoza.data.model.User
import com.social.vidoza.data.repository.HomeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(private val homeRepository: HomeRepository) : ViewModel() {


    private var _userLiveData: MutableLiveData<ResponseState<User>> = MutableLiveData()
    val userLiveData: LiveData<ResponseState<User>> get() = _userLiveData

    private var _updateFCMLiveData: MutableLiveData<ResponseState<String>> = MutableLiveData()
    val updateFCMLiveData: LiveData<ResponseState<String>> get() = _updateFCMLiveData


    private var _deleteFCMLiveData: MutableLiveData<ResponseState<String>> = MutableLiveData()
    val deleteFCMLiveData: LiveData<ResponseState<String>> get() = _deleteFCMLiveData



    private var _userListLiveData: MutableLiveData<ResponseState<List<User?>>> = MutableLiveData()
    val userListLiveData: LiveData<ResponseState<List<User?>>> get() = _userListLiveData



    fun getUserFromDataBase(uid: String) = viewModelScope.launch {
        _userLiveData.postValue(homeRepository.getUser(uid).value)
    }

    fun updateFCMToken(token: String, userId: String) = viewModelScope.launch {
        _updateFCMLiveData.postValue(homeRepository.sendFCMTokenToDatabase(token, userId).value)
    }

    fun deleteTokenFromUser(uid: String) =viewModelScope.launch{
_deleteFCMLiveData.postValue(homeRepository.deleteFCMToken(uid).value)
    }



    fun getUserList(userId:String) = viewModelScope.launch {
_userListLiveData.postValue(homeRepository.getUserss(userId).value)
    }

}