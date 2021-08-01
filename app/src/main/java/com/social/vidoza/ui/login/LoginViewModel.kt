package com.social.vidoza.ui.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gaming.earningvalleyadmin.utils.ResponseState
import com.google.firebase.auth.PhoneAuthCredential
import com.social.vidoza.data.model.User
import com.social.vidoza.data.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(private val authRepository: AuthRepository) : ViewModel() {

    private var _authenticateUserLiveData: MutableLiveData<ResponseState<User>> = MutableLiveData()
    private var _createdUserLiveData: MutableLiveData<ResponseState<User>> = MutableLiveData()

    val createdUserLiveData: LiveData<ResponseState<User>> get() = _createdUserLiveData
    val authenticateUserLiveData: LiveData<ResponseState<User>> get() = _authenticateUserLiveData

    private var _getUserLiveData: MutableLiveData<ResponseState<User?>> = MutableLiveData()
    val getUserLiveData: LiveData<ResponseState<User?>> get() = _getUserLiveData


    fun signInWithPhoneNumber(credential: PhoneAuthCredential) = viewModelScope.launch {
        _authenticateUserLiveData.postValue(authRepository.firebaseSignInWithPhone(credential).value)
    }

    fun createUser(authenticatedUser: User) {
        _createdUserLiveData = authRepository.createUserInFireStoreIfNotExist(
                authenticatedUser
            )

    }

    fun getUser(uid: String) = viewModelScope.launch {
        _getUserLiveData.postValue(authRepository.getUser(uid).value)
    }


    fun updateUserIsNew(id: User, b: Boolean)=viewModelScope.launch {

        authRepository.updateUserIsNew(id,b)

    }

}