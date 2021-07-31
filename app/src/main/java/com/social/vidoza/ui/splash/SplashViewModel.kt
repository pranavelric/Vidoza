package com.social.vidoza.ui.splash

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gaming.earningvalleyadmin.utils.ResponseState
import com.social.vidoza.data.model.User
import com.social.vidoza.data.repository.AuthRepository
import kotlinx.coroutines.launch

class SplashViewModel @ViewModelInject constructor(private val authRepository: AuthRepository) : ViewModel() {

    private var _authenticateUserLiveData: MutableLiveData<ResponseState<User?>> = MutableLiveData()
    private var _userLiveData: MutableLiveData<ResponseState<User?>> = MutableLiveData()


    val authenticatedUserLiveData: LiveData<ResponseState<User?>> get() = _authenticateUserLiveData
    val userLiveData: LiveData<ResponseState<User?>> get() = _userLiveData
    fun checkIfUserIsAuthenticated() {

        _authenticateUserLiveData = authRepository.checkIfUserIsAuthenticatedInFirebase()
    }

    fun setUid(uid: String) = viewModelScope.launch {

        _userLiveData.postValue(authRepository.getUser(uid).value)

    }



}